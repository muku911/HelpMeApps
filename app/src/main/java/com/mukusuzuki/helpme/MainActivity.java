package com.mukusuzuki.helpme;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    //deklarasi class atau fungsi
    TextView testing,battre,help,settet,forsend;
    GpsTracker gpsTracker;
    ImageButton gotosetting;
    Button testbtn;

    BroadcastReceiver receiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Widget
        testing = (TextView)findViewById(R.id.tv_cgh_location);
        battre = (TextView)findViewById(R.id.textView2);
        help =(TextView)findViewById(R.id.tx_help);
        gotosetting = (ImageButton)findViewById(R.id.setting_btn);
        testbtn = (Button)findViewById(R.id.btn_test);
        settet = (TextView)findViewById(R.id.set);
        forsend = (TextView)findViewById(R.id.tumbal);

        gotosetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a = new Intent(MainActivity.this,SettingPreference.class);
                startActivity(a);
            }
        });

        SharedPreferences setphone, setmassage;
        setphone = PreferenceManager.getDefaultSharedPreferences(this);
        final String settedphonenumber = setphone.getString("setnumphone", "082298664546");

        setmassage =PreferenceManager.getDefaultSharedPreferences(this);
        final String settedhelpmassage = setmassage.getString("setmassage", "Help Me.. find me please, copy and paste this coordinate on your maps \n");

        settet.setText("Now Set Number "+settedphonenumber);


        testbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gpsTracker = new GpsTracker(MainActivity.this);
                if (gpsTracker.canGetLocation()) {
                    double longitude = gpsTracker.getLongtitude();
                    double latitude = gpsTracker.getLatitude();
                    testing.setText("Latitude = " + latitude+"\nLongitude = " + longitude);
                    forsend.setText(latitude+","+longitude);
                } else {
                    gpsTracker.showSettingAlert();
                }

                String phoneNumber = settedphonenumber;
                String plusMassage = settedhelpmassage+"\n"+forsend.getText().toString()+"\n find that on your maps";
                sendMassage(phoneNumber,plusMassage);

                Intent b = new Intent(Intent.ACTION_MAIN);
                b.setType("vnd.android-dir/mms-sms");
                startActivity(b);
            }
        });



        //addfilter
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.ACTION_POWER_CONNECTED");
        filter.addAction("android.intent.action.ACTION_POWER_DISCONNECTED");

        this.registerReceiver(this.BatInfoReiciver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));


        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if(intent.getAction().equals(Intent.ACTION_POWER_CONNECTED)){
                    Toast.makeText(MainActivity.this,"Power Connected",Toast.LENGTH_LONG).show();
                }
                if(intent.getAction().equals(Intent.ACTION_POWER_DISCONNECTED)){
                    Toast.makeText(MainActivity.this,"Power DisConnected", Toast.LENGTH_LONG).show();
                }
            }
        };
        registerReceiver(receiver,filter);


        gpsTracker = new GpsTracker(MainActivity.this);
        if (gpsTracker.canGetLocation()) {
            double longitude = gpsTracker.getLongtitude();
            double latitude = gpsTracker.getLatitude();
            testing.setText("Latitude = " + latitude+"\nLongitude = " + longitude);
        } else {
            gpsTracker.showSettingAlert();
        }
    }

    @Override
    protected void onDestroy() {
        if ( receiver != null){
            unregisterReceiver(receiver);
        }
        super.onDestroy();
    }

    private BroadcastReceiver BatInfoReiciver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            battre.setText("Now Your Percent Battery Is "+String.valueOf(level)+"%");

            SharedPreferences setphone, setmassage, setbatre;
            setphone = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            final String settedphonenumber = setphone.getString("setnumphone", "082298664546");

            setmassage = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            final String settedhelpmassage = setmassage.getString("setmassage", "Help Me.. find me please, copy and paste this coordinate on your maps \n");

            setbatre = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            int levelset = setbatre.getInt("setbattery", Integer.parseInt("10"));

            if (level < levelset){
                gpsTracker = new GpsTracker(MainActivity.this);
                if (gpsTracker.canGetLocation()) {
                    double longitude = gpsTracker.getLongtitude();
                    double latitude = gpsTracker.getLatitude();
                    testing.setText("Latitude = " + latitude+"\nLongitude = " + longitude);
                } else {
                    gpsTracker.showSettingAlert();
                }


                String phoneNumber = settedphonenumber;
                String plusMassage = settedhelpmassage+"\n"+testing.getText().toString();
                sendMassage(phoneNumber,plusMassage);

                help.setVisibility(View.VISIBLE);

                Intent s = new Intent(Intent.ACTION_MAIN);
                s.setType("vnd.android-dir/mms-sms");
                startActivity(s);

            }
        }
    };

    private void sendMassage(String phoneNumber, String plusMassage) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, plusMassage, null, null);

    }

}
