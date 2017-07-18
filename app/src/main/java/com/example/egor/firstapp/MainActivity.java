package com.example.egor.firstapp;

/**
 * Created by egor on 19.6.17.
 */

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    BluetoothAdapter mBluetoothAdapter;
    BluetoothLeScanner mBleutoothScanner;
    BluetoothManager mBluetoothManager;
    public ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();
    int numBeacons = 4;
    public Beacon[] beacons = (Beacon[]) new Beacon[numBeacons];
    public int[] used = new int[3];

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBTDevices = new ArrayList<>();
        setCoord();

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBleutoothScanner = mBluetoothAdapter.getBluetoothLeScanner();
    }

    public void startDiscovery(View view)
    {
        Timer tim = new Timer();
        TimerTask bthh = new MyTimerTask();
        tim.schedule(bthh, 200, 500);
    }

    public void setCoord()
    {
        beacons[0] = new Beacon();
        beacons[0].setBeacon(0, 0, "id1", -65);

        beacons[1] = new Beacon();
        beacons[1].setBeacon(4, 0, "id2", -65);

        beacons[2] = new Beacon();
        beacons[2].setBeacon(0, 3, "id3", -65);

        beacons[3] = new Beacon();
        beacons[3].setBeacon(4, 3, "id4", -65);
    }

    class MyTimerTask extends TimerTask
    {
        @Override
        public void run()
        {
            AsyncTask.execute(new Runnable()
            {
                @Override
                public void run()
                {
                    mBleutoothScanner.startScan(leScanCallback);
                }
            });
        }
    }

    private ScanCallback leScanCallback = new ScanCallback()
    {
        @Override
        public void onScanResult(int callbackType, ScanResult result)
        {
            for (int i = 0; i < numBeacons; i++)
            {
                if (result.getDevice().getName().equals(beacons[i].getName()))
                {
                    beacons[i].addMid(result.getRssi());

                    TextView text3 = (TextView) findViewById(R.id.textView3);
                    text3.setText(String.valueOf(beacons[0].getDist()));

                    TextView text4 = (TextView) findViewById(R.id.textView4);
                    text4.setText(String.valueOf(beacons[1].getDist()));

                    TextView text5 = (TextView) findViewById(R.id.textView5);
                    text5.setText(String.valueOf(beacons[2].getDist()));

                    if (result.getDevice().getName().equals("id4"))
                    {
                        TextView text6 = (TextView) findViewById(R.id.textView6);
                        text6.setText(String.valueOf(result.getRssi()));
                    }

                    if (getClosest() == 1)
                    {
                        double x1 = Math.abs((Math.pow(beacons[used[0]].getDist(), 2) - Math.pow(beacons[used[1]].getDist(), 2) + Math.pow(beacons[used[1]].getX(), 2)) / (2 * beacons[used[1]].getX()));
                        double y1 = Math.abs((Math.pow(beacons[used[0]].getDist(), 2) - Math.pow(beacons[used[2]].getDist(), 2) - Math.pow(x1, 2) + Math.pow(x1 - beacons[used[2]].getX(), 2) + Math.pow(beacons[used[2]].getY(), 2)) / (2 * beacons[used[2]].getY()));

                        ImageView person = (ImageView) findViewById(R.id.imageView6);
                        ConstraintLayout.LayoutParams params1 = (ConstraintLayout.LayoutParams) (person.getLayoutParams());
                        params1.horizontalBias = ((float) 0.9 / 4) * (float) x1 + 0.05f;
                        params1.verticalBias = 1 - (((float) 0.88 / 6) * (float) y1 + 0.02f);
                        person.setLayoutParams(params1);

                        TextView text = (TextView) findViewById(R.id.textView);
                        text.setText(String.valueOf(x1));

                        TextView text2 = (TextView) findViewById(R.id.textView2);
                        text2.setText(String.valueOf(y1));
                    }
                }
            }
        }
    };

    public int getClosest()
    {
        double minim = 100;

        for (int i = 0; i < 3; i++)
        {
            used[i] = -1;
        }

        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < numBeacons; j++)
            {
                if ((beacons[j].getDist() <= minim) && (used[0] != j) && (used[1] != j) && (used[2] != j) && (beacons[j].getDist() != 0))
                {
                    used[i] = j;
                    minim = beacons[j].getDist();
                }
            }
            minim = 100;
        }

        for (int i = 0; i < 3; i++)
        {
            if (used[i] == -1)
            {
                return 0;
            }
        }

        for (int i = 0; i < 3; i++)
        {
            if (beacons[used[i]].getX() == 0 && beacons[used[i]].getY() == 0)
            {
                int x = used[0];
                used[0] = used[i];
                used[i] = x;
            }
            else if (beacons[used[i]].getY() == 0)
            {
                int x = used[1];
                used[1] = used[i];
                used[i] = x;
            }
            else if (beacons[used[i]].getX() == 0)
            {
                int x = used[2];
                used[2] = used[i];
                used[i] = x;
            }
        }

        return 1;
    }
}