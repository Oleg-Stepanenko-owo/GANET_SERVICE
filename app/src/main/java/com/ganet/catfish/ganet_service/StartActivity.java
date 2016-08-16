package com.ganet.catfish.ganet_service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import com.ganet.catfish.GANET.GaNetManager;
import com.ganet.catfish.GANET.ReadFromFile;

public class StartActivity extends AppCompatActivity {

    boolean isServiceStart;
    RadioButton rb1, rb2, rb3;
    BroadcastReceiver service;
    TextView tvServInfo, timeTextView;
    Button btStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        isServiceStart = false;

        IntentFilter filter = new IntentFilter();
        filter.addAction(GaNetService.SERVICESTATUS_RES);
        filter.addAction(GaNetService.TIMEINFO);

        service = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent) {

                switch ( intent.getAction() )
                {
                    case GaNetService.SERVICESTATUS_RES:
                        isServiceStart = intent.getBooleanExtra( "Start", false );
                        ((TextView)findViewById(R.id.tv_servinfo)).setText( intent.getStringExtra("Data") );
                        break;
                    case GaNetService.TIMEINFO:
                        updateTimeUi( intent.getStringExtra("Time") );
                        break;
                    default:
                        break;
                }
            }
        };
        registerReceiver(service, filter);

        ((RadioButton) findViewById(R.id.rb1)).setText("myLOG1-fm.log");
        ((RadioButton) findViewById(R.id.rb2)).setText("Mylog.txt");
        ((RadioButton) findViewById(R.id.rb3)).setText("yam_fm1.txt");
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent in = new Intent(GaNetService.SERVICESTATUS_REQ);
        sendBroadcast(in);
    }

    public void onStartService(View v) {
        if(!isServiceStart)
            startService(new Intent( this, GaNetService.class));
    }

    public void onStopGaNetService(View v) {
        stopService(new Intent( this, GaNetService.class ));
        tvServInfo = (TextView)findViewById(R.id.tv_servinfo);
        tvServInfo.setText("Service stop!");
    }

    public void onStartReadFile( View v ) {
        String fileName = "";

        rb1 = (RadioButton) findViewById(R.id.rb1);
        rb2 = (RadioButton) findViewById(R.id.rb2);
        rb3 = (RadioButton) findViewById(R.id.rb3);

        if( rb1.isChecked() ) fileName = "/data/data/com.ganet.catfish.ganet_service/Log/myLOG1-fm.log";
        else if( rb2.isChecked() ) fileName = "/data/data/com.ganet.catfish.ganet_service/Log/Mylog.txt";
        else if( rb3.isChecked() ) fileName = "/data/data/com.ganet.catfish.ganet_service/Log/yam_fm1.txt";

        Intent in = new Intent(GaNetService.READFILE);
        in.putExtra( "StartRead", fileName );
        sendBroadcast(in);
    }

    public void updateTimeUi( final String timeUI ) {
        timeTextView = (TextView) findViewById(R.id.timeTextView);
        runOnUiThread(new Runnable() {
                          @Override
                          public void run() {
                              timeTextView.setText( timeUI );
                          }
                      }
        );
    }
}
