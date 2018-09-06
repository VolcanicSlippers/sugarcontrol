package com.karimtimer.sugarcontrol.Bluetooth;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.bluetooth.BluetoothAdapter;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.karimtimer.sugarcontrol.Main.MainActivity;
import com.karimtimer.sugarcontrol.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Set;
import java.util.UUID;

public class BluetoothBgl extends AppCompatActivity {
    private static final String TAG = "bluetoothBgl server";


    private Button btnDatePicker, btnTimePicker, listen, send, listDevice;
    private ListView listView;
    private TextView msg_box, status;
    private EditText writeMsg;
    private Toolbar toolbar;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private int emonth, eday, eyear;
    private String sgrLvl;

    BluetoothAdapter bluetoothAdapter;
    android.bluetooth.BluetoothDevice[] btArray;
    SendReceive sendReceive;

    static final int STATE_LISTENING = 1;
    static final int STATE_CONNECTING = 2;
    static final int STATE_CONNECTED = 3;
    static final int STATE_CONNECTION_FAILED = 4;
    static final int STATE_MESSAGE_RECEIVED = 5;

    int REQUEST_ENABLE_BLUETOOTH = 1;

    private static final String APP_NAME = "BTChat";
    private static final UUID MY_UUID = UUID.fromString("67c1b0dc-4518-4f0d-a712-5f4abacebf2b");



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_bgl_vers);

//Toolbar for saving items
        toolbar = (Toolbar) findViewById(R.id.toolbar_bluetooth);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        btnDatePicker = (Button) findViewById(R.id.btn_date_bluetooth);
        btnTimePicker = (Button) findViewById(R.id.btn_time_bluetooth);
        listen = findViewById(R.id.bluetooth_btn_listen);
        listDevice = findViewById(R.id.btn_list_devices);
        send = findViewById(R.id.bluetooth_message_send);
        listView = findViewById(R.id.bluetooth_devices_list);
        msg_box = findViewById(R.id.bluetooth_message_view);
        status  = findViewById(R.id.bluetooth_status);
        writeMsg = findViewById(R.id.bluetooth_bgl_text_message);

        bluetoothAdapter = bluetoothAdapter.getDefaultAdapter();

        btnDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Get Current Date
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(BluetoothBgl.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                btnDatePicker.setText(String.format("%02d/%02d/%04d", dayOfMonth, (monthOfYear+1), year));

                                setEday(dayOfMonth);
                                setEmonth(monthOfYear + 1);
                                setEyear(year);
                            }
                        }, mYear, mMonth, mDay);


                datePickerDialog.show();
            }
        });
        btnTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    // Get Current Time
                    final Calendar c = Calendar.getInstance();
                    mHour = c.get(Calendar.HOUR_OF_DAY);
                    mMinute = c.get(Calendar.MINUTE);

                    // Launch Time Picker Dialog
                    TimePickerDialog timePickerDialog = new TimePickerDialog(BluetoothBgl.this,
                            new TimePickerDialog.OnTimeSetListener() {

                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay,
                                                      int minute) {

                                    btnTimePicker.setText(String.format("%02d:%02d", hourOfDay, minute));                        }
                            }, mHour, mMinute, false);
                    timePickerDialog.show();
            }
        });

        if(!bluetoothAdapter.isEnabled()){
            Intent enableIntent = new Intent(bluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent,REQUEST_ENABLE_BLUETOOTH);
        }


        implementListeners();


//        Thread2 t = new Thread2();
//        t.start();

    }

    private void implementListeners() {

        listDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Set<android.bluetooth.BluetoothDevice> bt = bluetoothAdapter.getBondedDevices();
                String[] strings = new String[bt.size()];
                btArray=new android.bluetooth.BluetoothDevice[bt.size()];
                int index = 0;


                if(bt.size()>0){
                    for(android.bluetooth.BluetoothDevice device : bt){
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


                    send.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String time = btnTimePicker.getText().toString();
                            String date = btnDatePicker.getText().toString();
                            String sugarLevel = writeMsg.getText().toString();
                            setSgrLvl(sugarLevel);
                            Log.e(TAG, time+date+sugarLevel);
                            if (getSgrLvl().matches("[0-9]+")) {
                                String toSend = time + "-" + date + "-" + sugarLevel;

                                String string = String.valueOf(toSend);
                                sendReceive.write(string.getBytes());
                            }else{
                                Toast.makeText(BluetoothBgl.this, "Incorrect bgl value. Please try again.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


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
                    msg_box.setText(tempMsg);
                    break;
            }


            return true;
        }
    });

    public int getEmonth() {
        return emonth;
    }

    public void setEmonth(int emonth) {
        this.emonth = emonth;
    }

    public int getEday() {
        return eday;
    }

    public void setEday(int eday) {
        this.eday = eday;
    }

    public int getEyear() {
        return eyear;
    }

    public void setEyear(int eyear) {
        this.eyear = eyear;
    }
    public String getSgrLvl() {
        return sgrLvl;
    }

    public void setSgrLvl(String sgrLvl) {
        this.sgrLvl = sgrLvl;
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

        private android.bluetooth.BluetoothDevice device;
        private BluetoothSocket socket;

        public ClientClass (android.bluetooth.BluetoothDevice device1){
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
        startActivity(new Intent(BluetoothBgl.this, MainActivity.class));

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
