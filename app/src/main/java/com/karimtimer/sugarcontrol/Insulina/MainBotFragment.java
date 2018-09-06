package com.karimtimer.sugarcontrol.Insulina;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.*;
import com.ibm.mobilefirstplatform.clientsdk.android.analytics.api.*;
import com.ibm.mobilefirstplatform.clientsdk.android.logger.api.*;
import com.ibm.watson.developer_cloud.android.library.audio.MicrophoneHelper;
import com.ibm.watson.developer_cloud.android.library.audio.MicrophoneInputStream;
import com.ibm.watson.developer_cloud.android.library.audio.StreamPlayer;
import com.ibm.watson.developer_cloud.android.library.audio.utils.ContentType;
import com.ibm.watson.developer_cloud.conversation.v1.Conversation;
import com.ibm.watson.developer_cloud.conversation.v1.model.InputData;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageOptions;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechResults;
import com.ibm.watson.developer_cloud.speech_to_text.v1.websocket.RecognizeCallback;
import com.ibm.watson.developer_cloud.text_to_speech.v1.TextToSpeech;
import com.ibm.watson.developer_cloud.text_to_speech.v1.model.Voice;
import com.karimtimer.sugarcontrol.R;
import com.karimtimer.sugarcontrol.Statistics.GraphActivity;
import com.karimtimer.sugarcontrol.History.HistoryActivity;
import com.karimtimer.sugarcontrol.Treatment.MedicationChooseType;
import com.karimtimer.sugarcontrol.models.Message;


import org.json.*;

import java.util.ArrayList;


public class MainBotFragment extends android.support.v4.app.Fragment {

    private ArrayList<Double> inRangeAL;
    private ArrayList<Double> belowRangeAL;
    private ArrayList<Double> aboveRangeAL;
    private RecyclerView recyclerView;
    private ChatAdapter mAdapter;
    private ArrayList messageArrayList;
    private EditText inputMessage;
    private ImageButton btnSend;
    private ImageButton btnRecord;
    //private Map<String,Object> context = new HashMap<>();
    private com.ibm.watson.developer_cloud.conversation.v1.model.Context context = null;
    StreamPlayer streamPlayer;
    private boolean initialRequest;
    private boolean permissionToRecordAccepted = false;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static String TAG = "MainActivity";
    private static String TAG2 = "BotActivity";
    private static final int RECORD_REQUEST_CODE = 101;
    private boolean listening = false;
    private SpeechToText speechService;
    private TextToSpeech textToSpeech;
    private MicrophoneInputStream capture;
    private Context mContext, recorddd;
    private String workspace_id;
    private String conversation_username;
    private String conversation_password;
    private String STT_username;
    private String STT_password;
    private String TTS_username;
    private String TTS_password;
    private String analytics_APIKEY;
    private SpeakerLabelsDiarization.RecoTokens recoTokens;
    private MicrophoneHelper microphoneHelper;
    private Logger myLogger;
    private Toolbar toolbar;
    private StorageReference storage;
    private FirebaseDatabase database, mFirebaseDatabase;
    private DatabaseReference databaseRef1, databaseRef2;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseUsers, myRef, myRef2, myRef3, myRefHba1c;
    private FirebaseUser mCurrentUser;
    private ArrayList<Double> avgToday;
    private int emonth, eday, eyear;
    private int lowerRange, upperRange;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.main_bot_activity, parent, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getActivity().getApplicationContext();
        conversation_username = mContext.getString(R.string.conversation_username);
        conversation_password = mContext.getString(R.string.conversation_password);
        workspace_id = mContext.getString(R.string.workspace_id);
        STT_username = mContext.getString(R.string.STT_username);
        STT_password = mContext.getString(R.string.STT_password);
        TTS_username = mContext.getString(R.string.TTS_username);
        TTS_password = mContext.getString(R.string.TTS_password);
        analytics_APIKEY = mContext.getString(R.string.mobileanalytics_apikey);



        //IBM Cloud Mobile Analytics
        BMSClient.getInstance().initialize(getActivity().getApplicationContext(), BMSClient.REGION_SYDNEY);
        //Analytics is configured to record lifecycle events.
        Analytics.init(getActivity().getApplication(), "Sugar Control", analytics_APIKEY, false,false, Analytics.DeviceEvent.ALL);
        //Analytics.send();
        myLogger = Logger.getLogger("myLogger");
        // Send recorded usage analytics to the Mobile Analytics Service
        Analytics.send(new ResponseListener() {
            @Override
            public void onSuccess(Response response) {
                // Handle Analytics send success here.
            }

            @Override
            public void onFailure(Response response, Throwable throwable, JSONObject jsonObject) {
                // Handle Analytics send failure here.
            }
        });

        // Send logs to the Mobile Analytics Service
        Logger.send(new ResponseListener() {
            @Override
            public void onSuccess(Response response) {
                // Handle Logger send success here.
            }

            @Override
            public void onFailure(Response response, Throwable throwable, JSONObject jsonObject) {
                // Handle Logger send failure here.
            }
        });

        String customFont = "Montserrat-Regular.ttf";
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), customFont);

        inputMessage = (EditText) view.findViewById(R.id.messages);
        inputMessage.setTypeface(typeface);
        btnSend = (ImageButton) view.findViewById(R.id.btn_send);
        btnRecord= (ImageButton) view.findViewById(R.id.btn_record);




        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        messageArrayList = new ArrayList<>();
        mAdapter = new ChatAdapter(messageArrayList);
        microphoneHelper = new MicrophoneHelper(getActivity());

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        this.inputMessage.setText("");
        this.initialRequest = true;
        sendMessage();


        //Watson Text-to-Speech Service on Bluemix
        textToSpeech = new TextToSpeech();
        textToSpeech.setUsernameAndPassword(TTS_username, TTS_password);


        int permission = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.RECORD_AUDIO);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission to record denied");
            makeRequest();
        }


        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity().getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                Thread thread = new Thread(new Runnable() {
                    public void run() {
                        Message audioMessage;
                        try {

                            audioMessage =(Message) messageArrayList.get(position);
                            streamPlayer = new StreamPlayer();
                            if(audioMessage != null && !audioMessage.getMessage().isEmpty())
                                //Change the Voice format and choose from the available choices
                                streamPlayer.playStream(textToSpeech.synthesize(audioMessage.getMessage(), Voice.EN_LISA).execute());
                            else
                                streamPlayer.playStream(textToSpeech.synthesize("No Text Specified", Voice.EN_LISA).execute());

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
            }

            @Override
            public void onLongClick(View view, int position) {
                recordMessage();

            }
        }));

        btnSend.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(checkInternetConnection()) {
                    sendMessage();
                }
            }
        });

        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                recordMessage();
            }
        });
    };

    // Speech-to-Text Record Audio permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
            case RECORD_REQUEST_CODE: {

                if (grantResults.length == 0
                        || grantResults[0] !=
                        PackageManager.PERMISSION_GRANTED) {

                    Log.i(TAG, "Permission has been denied by user");
                } else {
                    Log.i(TAG, "Permission has been granted by user");
                }
                return;
            }

            case MicrophoneHelper.REQUEST_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getActivity(), "Permission to record audio denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
        // if (!permissionToRecordAccepted ) finish();

    }

    protected void makeRequest() {
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.RECORD_AUDIO},
                MicrophoneHelper.REQUEST_PERMISSION);
    }

    // Sending a message to Watson Conversation Service
    private void sendMessage() {

        final String inputmessage = this.inputMessage.getText().toString().trim();
        if(!this.initialRequest) {
            Message inputMessage = new Message();
            inputMessage.setMessage(inputmessage);
            inputMessage.setId("1");
            messageArrayList.add(inputMessage);
            myLogger.info("Sending a message to Watson Conversation Service");

        }
        else
        {
            Message inputMessage = new Message();
            inputMessage.setMessage(inputmessage);
            inputMessage.setId("100");
            this.initialRequest = false;
            Toast.makeText(getActivity().getApplicationContext(),"Tap on the message for Voice",Toast.LENGTH_LONG).show();

        }

        this.inputMessage.setText("");
        mAdapter.notifyDataSetChanged();

        Thread thread = new Thread(new Runnable(){
            public void run() {
                try {



                    //Message request.
                    Conversation service = new Conversation(Conversation.VERSION_DATE_2017_05_26);
                    service.setUsernameAndPassword(conversation_username, conversation_password);
                    //InputData is an input object that includes the input text.
                    InputData input = new InputData.Builder(inputmessage).build();
                    MessageOptions options = new MessageOptions.Builder(workspace_id).input(input).context(context).build();
                    MessageResponse response = service.message(options).execute();
                    //Map hell = new HashMap<String, String>();
                  //  System.out.println(inputmessage);

                    //Passing Context of last conversation
                    if(response.getContext() !=null)
                    {
                        JSONObject obj = new JSONObject(response.getContext());
                        //Has record entity for uploading a bgl been correctly identified?
                        if(obj.optString("upload").equals("true")){
                            String recordNumber = obj.optString("RecordNumber");
                            String timeSubmitted = obj.optString("time");
                            String dateSubmitted = obj.optString("date");
                           // Has the date, time and blood glucose levels been received?
                            if(dateSubmitted.length() != 0){
                                String[] units = timeSubmitted.split(":"); //will break the string up into an array
                                int hourOfDay = Integer.parseInt(units[0]); //first element
                                int minute = Integer.parseInt(units[1]); //second element
                                int seconds = Integer.parseInt(units[2]);

                                String[] dayUnits = dateSubmitted.split("-");
                                int yearSubmitted = Integer.parseInt(dayUnits[0]); //first element
                                int monthSubmitted = Integer.parseInt(dayUnits[1]); //second element
                                int daySubmitted = Integer.parseInt(dayUnits[2]);

                                setEday(daySubmitted);
                                setEmonth(monthSubmitted);
                                setEyear(yearSubmitted);
                                mAuth = FirebaseAuth.getInstance();
                                mCurrentUser = mAuth.getCurrentUser();
                                mFirebaseDatabase = FirebaseDatabase.getInstance();
                                myRef = mFirebaseDatabase.getReference().child("Record").child("SugarLevel").child(mAuth.getUid());
                                myRef2 = mFirebaseDatabase.getReference().child("Current Record").child("SugarLevel").child(mAuth.getUid());

                                storage = FirebaseStorage.getInstance().getReference();

                                FirebaseUser user = mAuth.getCurrentUser();
                                String userID = mCurrentUser.getUid();
                                final String timeFinal = timeSubmitted;
                                final String dateFinal = dateSubmitted;
                                final String recordNumberFinal = recordNumber;

                                if (!timeFinal.equals("Select Time")) {
                                    if (!dateFinal.equals("Select Date")) {
                                        if (!recordNumber.equals("")) {

                                            @SuppressWarnings("VisibleForTests") final DatabaseReference newPost = myRef.push();
                                            @SuppressWarnings("VisibleForTests") final DatabaseReference currentRecordPost = myRef2;
                                            @SuppressWarnings("VisibleForTests") final DatabaseReference rangePost = myRef3;


                                            newPost.child("time").setValue(timeFinal);
                                            newPost.child("date").setValue(dateFinal);
                                            newPost.child("day").setValue(getEday());
                                            newPost.child("month").setValue(getEmonth());
                                            newPost.child("year").setValue(getEyear());
                                            newPost.child("sugarLevel").setValue(recordNumber);

                                            currentRecordPost.child("time").setValue(timeFinal);
                                            currentRecordPost.child("date").setValue(dateFinal);
                                            currentRecordPost.child("day").setValue(getEday());
                                            currentRecordPost.child("month").setValue(getEmonth());
                                            currentRecordPost.child("year").setValue(getEyear());
                                            currentRecordPost.child("Recording").setValue(recordNumber);
                                            //post the stuff
                                            closeKeyboard();
                                            android.support.v4.app.Fragment homeFragment = new ClickListener.HomeFragment();
                                            FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                            transaction.replace(R.id.fragment_container, homeFragment);
                                            transaction.addToBackStack(null);
                                            transaction.commit();


                                        } else {
                                            Toast.makeText(getActivity(), "Enter a Blood Glucose reading!", Toast.LENGTH_SHORT).show();
                                        }

                                    } else {
                                        Toast.makeText(getActivity(), "Enter a valid date.", Toast.LENGTH_SHORT).show();
                                    }


                                } else {
                                    Toast.makeText(getActivity(), "Enter a valid time.", Toast.LENGTH_SHORT).show();

                                }

                            }

                            }
                            //add functionality for hba1c
                        if(obj.optString("uploadHba1c").equals("true")){
                            String recordTheHba1c = obj.optString("RecordHbA1c");
                            String timeSubmitted = obj.optString("time");
                            String dateSubmitted = obj.optString("date");
                            if(dateSubmitted.length() != 0){
                                String[] units = timeSubmitted.split(":"); //will break the string up into an array
                                int hourOfDay = Integer.parseInt(units[0]); //first element
                                int minute = Integer.parseInt(units[1]); //second element
                                int seconds = Integer.parseInt(units[2]);

                                String[] dayUnits = dateSubmitted.split("-");
                                int yearSubmitted = Integer.parseInt(dayUnits[0]); //first element
                                int monthSubmitted = Integer.parseInt(dayUnits[1]); //second element
                                int daySubmitted = Integer.parseInt(dayUnits[2]);

                                setEday(daySubmitted);
                                setEmonth(monthSubmitted);
                                setEyear(yearSubmitted);
                                mAuth = FirebaseAuth.getInstance();
                                mCurrentUser = mAuth.getCurrentUser();

                                mFirebaseDatabase = FirebaseDatabase.getInstance();
                                myRefHba1c = mFirebaseDatabase.getReference().child("Record").child("HbA1c").child(mAuth.getUid());

                                storage = FirebaseStorage.getInstance().getReference();

                                FirebaseUser user = mAuth.getCurrentUser();
                                String userID = mCurrentUser.getUid();
                                final String timeFinal = timeSubmitted;
                                final String dateFinal = dateSubmitted;
                                final String recordHba1cFInal = recordTheHba1c;
                                if (!timeFinal.equals("Select Time")) {
                                    if (!dateFinal.equals("Select Date")) {
                                        if (!recordHba1cFInal.equals("")) {

                                            @SuppressWarnings("VisibleForTests") final DatabaseReference newPost = myRefHba1c.push();




                                            newPost.child("time").setValue(timeFinal);
                                            newPost.child("date").setValue(dateFinal);
                                            newPost.child("HbA1cValue").setValue(recordHba1cFInal);
                                            newPost.child("HbA1cNotes").setValue("From Insulina!");


                                            //post the stuff
                                            closeKeyboard();
                                            android.support.v4.app.Fragment homeFragment = new ClickListener.HomeFragment();
                                            FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                            transaction.replace(R.id.fragment_container, homeFragment);
                                            transaction.addToBackStack(null);
                                            transaction.commit();


                                        } else {
                                            Toast.makeText(getActivity(), "Enter a Blood Glucose reading!", Toast.LENGTH_SHORT).show();
                                        }

                                    } else {
                                        Toast.makeText(getActivity(), "Enter a valid date.", Toast.LENGTH_SHORT).show();
                                    }


                                } else {
                                    Toast.makeText(getActivity(), "Enter a valid time.", Toast.LENGTH_SHORT).show();

                                }

                            }

                        }
                        //TODO: redirect for history
                        if (obj.optString("takeTo").equals("history")) {
                            startActivity(new Intent(getActivity(), HistoryActivity.class));
                            getActivity().finish();
                        }
                        //TODO: redirect for graph
                        if (obj.optString("takeTo").equals("stats")) {
                            startActivity(new Intent(getActivity(), GraphActivity.class));
                            getActivity().finish();
                        }
                        //TODO: redirect for medicine
                        if (obj.optString("takeTo").equals("medicine")) {
                            startActivity(new Intent(getActivity(), MedicationChooseType.class));
                            getActivity().finish();
                        }
                        context = response.getContext();


                    }
                    final Message outMessage=new Message();
                    if(response!=null)
                    {
                        if(response.getOutput()!=null && response.getOutput().containsKey("text"))
                        {

                            ArrayList responseList = (ArrayList) response.getOutput().get("text");
                            if(null !=responseList && responseList.size()>0){
                                outMessage.setMessage((String)responseList.get(0));
                                outMessage.setId("2");
                            }
                            messageArrayList.add(outMessage);
                            Thread thread = new Thread(new Runnable() {
                                public void run() {
                                    Message audioMessage;
                                    try {

                                        audioMessage = outMessage;
                                        streamPlayer = new StreamPlayer();
//                                        if(audioMessage != null && !audioMessage.getMessage().isEmpty())
//                                            //Change the Voice format and choose from the available choices
//                                            streamPlayer.playStream(textToSpeech.synthesize(audioMessage.getMessage(), Voice.EN_LISA).execute());
//                                        else
//                                            streamPlayer.playStream(textToSpeech.synthesize("No Text Specified", Voice.EN_LISA).execute());

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            thread.start();
                        }

                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                mAdapter.notifyDataSetChanged();
                                if (mAdapter.getItemCount() > 1) {
                                    recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount()-1);

                                }

                            }
                        });


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();

    }

    private void closeKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }



    //Record a message via Watson Speech to Text
    private void recordMessage() {
        //mic.setEnabled(false);
        speechService = new SpeechToText();
        speechService.setUsernameAndPassword(STT_username, STT_password);


        if(listening != true) {
            capture = microphoneHelper.getInputStream(true);
            new Thread(new Runnable() {
                @Override public void run() {
                    try {
                        speechService.recognizeUsingWebSocket(capture, getRecognizeOptions(), new MicrophoneRecognizeDelegate());
                    } catch (Exception e) {
                        showError(e);
                    }
                }
            }).start();
            listening = true;
            Toast.makeText(getActivity(),"Listening....Click to Stop", Toast.LENGTH_LONG).show();

        } else {
            try {
                microphoneHelper.closeInputStream();
                listening = false;
                Toast.makeText(getActivity(),"Stopped Listening....Click to Start", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * Check Internet Connection
     * @return
     */
    private boolean checkInternetConnection() {
        // get Connectivity Manager object to check connection
        ConnectivityManager cm =
                (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        // Check for network connections
        if (isConnected){
            return true;
        }
        else {
            Toast.makeText(getActivity(), " No Internet Connection available ", Toast.LENGTH_LONG).show();
            return false;
        }

    }

    //Private Methods - Speech to Text
    private RecognizeOptions getRecognizeOptions() {
        return new RecognizeOptions.Builder()
                //.continuous(true)
                .contentType(ContentType.OPUS.toString())
                //.model("en-UK_NarrowbandModel")
                .interimResults(true)
                .inactivityTimeout(2000)
                //TODO: Uncomment this to enable Speaker Diarization
                //.speakerLabels(true)
                .build();
    }

    //Watson Speech to Text Methods.
    private class MicrophoneRecognizeDelegate implements RecognizeCallback {
        @Override
        public void onTranscription(SpeechResults speechResults) {
            //TODO: Uncomment this to enable Speaker Diarization
            /*recoTokens = new SpeakerLabelsDiarization.RecoTokens();
            if(speechResults.getSpeakerLabels() !=null)
            {
                recoTokens.add(speechResults);
                Log.i("SPEECHRESULTS",speechResults.getSpeakerLabels().get(0).toString());


            }*/
            if(speechResults.getResults() != null && !speechResults.getResults().isEmpty()) {
                String text = speechResults.getResults().get(0).getAlternatives().get(0).getTranscript();
                showMicText(text);
            }
        }

        @Override public void onConnected() {

        }

        @Override public void onError(Exception e) {
            showError(e);
            enableMicButton();
        }

        @Override public void onDisconnected() {
            enableMicButton();
        }

        @Override
        public void onInactivityTimeout(RuntimeException runtimeException) {

        }

        @Override
        public void onListening() {

        }

        @Override
        public void onTranscriptionComplete() {

        }
    }

    private void showMicText(final String text) {
        getActivity().runOnUiThread(new Runnable() {
            @Override public void run() {
                inputMessage.setText(text);
            }
        });
    }

    private void enableMicButton() {
        getActivity().runOnUiThread(new Runnable() {
            @Override public void run() {
                btnRecord.setEnabled(true);
            }
        });
    }

    private void showError(final Exception e) {
        getActivity().runOnUiThread(new Runnable() {
            @Override public void run() {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });
    }

    public ArrayList<Double> getInRangeAL() {
        return inRangeAL;
    }

    public void setInRangeAL(ArrayList<Double> inRangeAL) {
        this.inRangeAL = inRangeAL;
    }

    public ArrayList<Double> getBelowRangeAL() {
        return belowRangeAL;
    }

    public void setBelowRangeAL(ArrayList<Double> belowRangeAL) {
        this.belowRangeAL = belowRangeAL;
    }

    public ArrayList<Double> getAboveRangeAL() {
        return aboveRangeAL;
    }

    public void setAboveRangeAL(ArrayList<Double> aboveRangeAL) {
        this.aboveRangeAL = aboveRangeAL;
    }
    public int getLowerRange() {
        return lowerRange;
    }

    public void setLowerRange(int lowerRange) {
        this.lowerRange = lowerRange;
    }

    public int getUpperRange() {
        return upperRange;
    }

    public void setUpperRange(int upperRange) {
        this.upperRange = upperRange;
    }
    private int getEmonth() {
        return emonth;
    }

    private void setEmonth(int emonth) {
        this.emonth = emonth;
    }

    private int getEday() {
        return eday;
    }

    public void setEday(int eday) {
        this.eday = eday;
    }

    private int getEyear() {
        return eyear;
    }

    private void setEyear(int eyear) {
        this.eyear = eyear;
    }

}



