package com.karimtimer.sugarcontrol.Bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.karimtimer.sugarcontrol.R;

import java.util.ArrayList;
import java.util.Set;

public class MainBluetooth extends AppCompatActivity {

    private Toolbar toolbar;
    private Button btnOn, btnOff, btnScan;
    private ListView scanListView;
    ArrayList<String> stringArrayList = new ArrayList<String>();
    ArrayAdapter<String> arrayAdapter;
    BluetoothAdapter myBluetoothAdapter;
    Intent btEnablingIntent;
    int requestCodeForEnable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_main);

        //Toolbar for saving items
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar_done);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        btnOn = (Button) findViewById(R.id.btON);
        btnOff = (Button) findViewById(R.id.btnOFFF);
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        btEnablingIntent = new Intent (BluetoothAdapter.ACTION_REQUEST_ENABLE);
        requestCodeForEnable =1;

        bluetoothONMethod();
        bluetoothOFFMethod();

        btnScan = (Button) findViewById(R.id.btnShowBluetoothDevices);
        scanListView = (ListView) findViewById(R.id.bluetooth_list);

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myBluetoothAdapter.startDiscovery();
            }
        });

        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(myReceiver, intentFilter);

        arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, stringArrayList);
        scanListView.setAdapter(arrayAdapter);

    }

    BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                stringArrayList.add(device.getName());
                arrayAdapter.notifyDataSetChanged();
            }
        }
    };

//    private void executeButton() {
//        btnShow.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Set<BluetoothBgl> bt = myBluetoothAdapter.getBondedDevices();
//                String[] strings = new String[bt.size()];
//                int index = 0;
//
//                if(bt.size()>0){
//                    for(BluetoothBgl device:bt){
//                        strings[index] = device.getName();
//                        index++;
//                    }
//
//                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,strings);
//                    listOfBluetoothDevices.setAdapter(arrayAdapter);
//                }
//            }
//        });
//    }

    private void bluetoothOFFMethod() {
    btnOff.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(myBluetoothAdapter.isEnabled()){
                myBluetoothAdapter.disable();
            }
        }
    });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==requestCodeForEnable){
            if(resultCode== RESULT_OK){
                Toast.makeText(getApplicationContext(),"Bluetooth has been Enabled", Toast.LENGTH_SHORT).show();
            }else if(resultCode == RESULT_CANCELED){
                Toast.makeText(getApplicationContext(), "Bluetooth Enabling Cancelled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void bluetoothONMethod() {

        btnOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(myBluetoothAdapter == null){
                    Toast.makeText(getApplicationContext(),"Bluetooth is not supported this device",Toast.LENGTH_SHORT).show();

                }else{
                    if(!myBluetoothAdapter.isEnabled()){
                        startActivityForResult(btEnablingIntent,requestCodeForEnable);
                    }
                }
            }
        });
    }

}
