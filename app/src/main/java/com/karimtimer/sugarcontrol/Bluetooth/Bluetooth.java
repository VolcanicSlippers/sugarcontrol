package com.karimtimer.sugarcontrol.Bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.karimtimer.sugarcontrol.Main.MainActivity;
import com.karimtimer.sugarcontrol.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;


/**
 * Bluetooth with client UI
 *
 * @author Abdikariim Timer
 */
public class Bluetooth extends AppCompatActivity{
    private static final String TAG = "Bluetooth client";

    private Button listen, send, listDevice, btnBluetoothSaveEntry;
    private ListView listView;
    private TextView msg_box, msg_box_2,msg_box_3, status;
    private EditText writeMsg;
    private Toolbar toolbar;

    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice[] btArray;
    SendReceive sendReceive;

    static final int STATE_LISTENING = 1;
    static final int STATE_CONNECTING = 2;
    static final int STATE_CONNECTED = 3;
    static final int STATE_CONNECTION_FAILED = 4;
    static final int STATE_MESSAGE_RECEIVED = 5;

    int REQUEST_ENABLE_BLUETOOTH = 1;
    private long mLastClickTime = 0;

    private static final String APP_NAME = "BTChat";
    private static final UUID MY_UUID = UUID.fromString("67c1b0dc-4518-4f0d-a712-5f4abacebf2b");

    private FirebaseUser mCurrentUser;
    private FirebaseDatabase  mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef, myRef2;

    private String emonth, eday, eyear, time, srgLvl, date;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth);

        //Toolbar for saving items
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar_bluetooth);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        listen = findViewById(R.id.bluetooth_btn_listen);
        listDevice = findViewById(R.id.btn_list_devices);
//        send = findViewById(R.id.bluetooth_message_send);
        listView = findViewById(R.id.bluetooth_devices_list);
        msg_box = findViewById(R.id.bluetooth_message_view);
        msg_box_2 = findViewById(R.id.bluetooth_message_view_2);
        msg_box_3 = findViewById(R.id.bluetooth_message_view_3);
        status  = findViewById(R.id.bluetooth_status);
//        writeMsg = findViewById(R.id.bluetooth_text_message);
        btnBluetoothSaveEntry = findViewById(R.id.btn_save_entry);

        bluetoothAdapter = bluetoothAdapter.getDefaultAdapter();


        if(!bluetoothAdapter.isEnabled()){
            Intent enableIntent = new Intent(bluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent,REQUEST_ENABLE_BLUETOOTH);
        }


        implementListeners();
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference().child("Record").child("SugarLevel").child(mAuth.getUid());
        myRef2 = mFirebaseDatabase.getReference().child("Current Record").child("SugarLevel").child(mAuth.getUid());

//        Thread2 t = new Thread2();
//        t.start();
        btnBluetoothSaveEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Attempting to add object to database.");


                    String fullTxt = msg_box.getText().toString();

                    String[] syllables = fullTxt.split(",");




                    //    String recordNumber = counter;
                    FirebaseUser user = mAuth.getCurrentUser();
                    String userID = mCurrentUser.getUid();
                    final String time = getTime();
                    final String date = getDate();
                    final String sugarLevel = getSrgLvl();

                    String[] dateSplit = getDate().split("/");
                    for (int i = 0; i < dateSplit.length; i++) {
                        if(i==0){
                            setEday(dateSplit[0]);
                        }if(i==1){
                            setEmonth(dateSplit[1]);
                        }if(i==2){
                            setEyear(dateSplit[2]);
                        }

                    }


                    //if(checkTimeFormat(date) == true) {
                    if (!time.equals("Select Time")) {
                        if (!date.equals("Select Date")) {
                            if (!sugarLevel.equals("")) {

                                @SuppressWarnings("VisibleForTests") final DatabaseReference newPost = myRef.push();
                                @SuppressWarnings("VisibleForTests") final DatabaseReference currentRecordPost = myRef2;


                                newPost.child("time").setValue(time);
                                newPost.child("date").setValue(date);
                                newPost.child("day").setValue(getEday());
                                newPost.child("month").setValue(getEmonth());
                                newPost.child("year").setValue(getEyear());
                                // newPost.child("carbs").setValue(carbs);
                                newPost.child("sugarLevel").setValue(sugarLevel);

                                currentRecordPost.child("time").setValue(time);
                                currentRecordPost.child("date").setValue(date);
                                currentRecordPost.child("day").setValue(getEday());
                                currentRecordPost.child("month").setValue(getEmonth());
                                currentRecordPost.child("year").setValue(getEyear());
                                currentRecordPost.child("Recording").setValue(sugarLevel);




                                //post the stuff
                                Log.e(TAG, "numer 1");
                                startActivity(new Intent(Bluetooth.this, MainActivity.class));

                                finish();


                            } else {
                                Toast.makeText(Bluetooth.this, "incorrect Blood Glucose reading!", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(Bluetooth.this, "incorrect date.", Toast.LENGTH_SHORT).show();
                        }


                    } else {
                        Toast.makeText(Bluetooth.this, "incorrect time.", Toast.LENGTH_SHORT).show();

                    }

                }


            //   }
        });
    }

    private void implementListeners() {

        listDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Set<BluetoothDevice> bt = bluetoothAdapter.getBondedDevices();
                String[] strings = new String[bt.size()];
                btArray=new BluetoothDevice[bt.size()];
                int index = 0;


                if(bt.size()>0){
                    for(BluetoothDevice device : bt){
                        btArray[index] = device;
                        strings[index] = device.getName();
                        index++;
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,strings);
                    listView.setAdapter(arrayAdapter);
                }
            }
        });

        listen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ServerClass serverClass = new ServerClass();
                serverClass.start();
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ClientClass clientClass = new ClientClass(btArray[i]);
                clientClass.start();

                status.setText("Connecting");
            }
        });


//        send.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String string = String.valueOf(writeMsg.getText());
//                sendReceive.write(string.getBytes());
//            }
//        });
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            switch(msg.what){
                case STATE_LISTENING:
                    status.setText("Listening");
                    break;
                case STATE_CONNECTING:
                    status.setText("Connecting");
                    break;
                case STATE_CONNECTED:
                    status.setText("Connected");
                    break;
                case STATE_CONNECTION_FAILED:
                    status.setText("Connection failed");
                    break;
                case STATE_MESSAGE_RECEIVED:
                    byte[] readBuff = (byte[]) msg.obj;
                    String tempMsg = new String(readBuff,0,msg.arg1);

                    String[] syllables = tempMsg.split("-");
                    String time = syllables[0];
                    String date = syllables[1];
                    String sgrLvl = syllables[2];
                    setDate(date);
                    setTime(time);
                    setSrgLvl(sgrLvl);
                    msg_box.setText("A bgl of "+sgrLvl +"mmol/L" +", taken at:");
                    msg_box_2.setText(time+", "+date+", Was sent.");
                    msg_box_3.setText("If correct, please press save.");

                    break;
            }


            return true;
        }
    });

    public String getEmonth() {
        return emonth;
    }

    public void setEmonth(String emonth) {
        this.emonth = emonth;
    }

    public String getEday() {
        return eday;
    }

    public void setEday(String eday) {
        this.eday = eday;
    }

    public String getEyear() {
        return eyear;
    }

    public void setEyear(String eyear) {
        this.eyear = eyear;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSrgLvl() {
        return srgLvl;
    }

    public void setSrgLvl(String srgLvl) {
        this.srgLvl = srgLvl;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


    private class ServerClass extends Thread{

        private BluetoothServerSocket serverSocket;


        public ServerClass(){
            try {
                serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(APP_NAME,MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run(){
            BluetoothSocket socket = null;

            while(socket == null){
                try {
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTING;
                    handler.sendMessage(message);


                    socket = serverSocket.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTION_FAILED;
                    handler.sendMessage(message);
                }

                //if the connection has been established, break the loop
                if(socket != null){
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTED;
                    handler.sendMessage(message);

                    sendReceive= new SendReceive(socket);
                    sendReceive.start();

                    break;
                }
            }
        }

    }


    private class ClientClass extends Thread{

        private BluetoothDevice device;
        private BluetoothSocket socket;

        public ClientClass (BluetoothDevice device1){
            device = device1;

            try {
                socket = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        public void run(){
            try {
                socket.connect();

                Message message = Message.obtain();
                message.what = STATE_CONNECTED;
                handler.sendMessage(message);

                sendReceive = new SendReceive(socket);
                sendReceive.start();

            } catch (IOException e) {
                e.printStackTrace();
                Message message = Message.obtain();
                message.what = STATE_CONNECTION_FAILED;
                handler.sendMessage(message);
            }
        }

    }

    private class SendReceive extends Thread{

        private final BluetoothSocket bluetoothSocket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public SendReceive(BluetoothSocket socket){
            bluetoothSocket = socket;
            InputStream tempIn = null;
            OutputStream tempOut = null;

            try {
                tempIn = bluetoothSocket.getInputStream();
                tempOut = bluetoothSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            inputStream = tempIn;
            outputStream = tempOut;

        }

        public void run(){

            byte[] buffer= new byte[1024];
            int bytes;
            while(true){
                try {
                    bytes=inputStream.read(buffer);
                    handler.obtainMessage(STATE_MESSAGE_RECEIVED, bytes, -1,buffer).sendToTarget();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        public void write(byte[] bytes){
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(Bluetooth.this, MainActivity.class));

        finish();
    }
//
//    private class Thread2 extends Thread{
//        public void run(){
//            for(int i = 0; i < 50; i++){
//
//                Message message = Message.obtain();
//                message.arg1 = i;
//                handler.sendMessage(message);
//
//                try {
//                    sleep(500);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }


}
