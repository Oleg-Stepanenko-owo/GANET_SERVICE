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
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import com.ganet.catfish.GANET.GaNetManager;
import com.ganet.catfish.GANET.ReadFromFile;
//TODO: added  psebilyty for logging in file.
public class StartActivity extends AppCompatActivity {

    boolean isServiceStart;
    RadioButton rb1, rb2, rb3;
    BroadcastReceiver service;
    TextView tvServInfo, timeTextView;
    Button btStart, btStartReadFile;
    boolean isReading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        isServiceStart = false;

        IntentFilter filter = new IntentFilter();
        filter.addAction(GaNetService.SERVICESTATUS_RES);
        filter.addAction(GaNetService.TIMEINFO);
        filter.addAction(GaNetService.READFILE);

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
                    case GaNetService.READFILE:
                        if(isReading && intent.hasExtra("AllSize") ) {
                            long allSize = intent.getLongExtra("AllSize", 0);
                            long readedSize = intent.getLongExtra("ReadedSize", 0);
                            updateFileReadUi( allSize, readedSize );
                        }
                        break;
                    default:
                        break;
                }
            }
        };
        registerReceiver(service, filter);

        ((RadioButton) findViewById(R.id.rb1)).setText("yam_fm1.txt");
        ((RadioButton) findViewById(R.id.rb2)).setText("yamGANET1.log");
        ((RadioButton) findViewById(R.id.rb3)).setText("yam_fm2.txt");
    }

    private void updateFileReadUi(long allSize, long readedSize) {
        ((ProgressBar) findViewById(R.id.pbReading)).setMax((int) allSize);
        ((ProgressBar) findViewById(R.id.pbReading)).setProgress((int)readedSize);
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        Intent in = new Intent(GaNetService.SERVICESTATUS_REQ);
//        sendBroadcast(in);
//    }

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

        if( rb1.isChecked() ) fileName = "/data/data/com.ganet.catfish.ganet_service/Log/yam_fm1.txt";
        else if( rb2.isChecked() ) fileName = "/data/data/com.ganet.catfish.ganet_service/Log/yamGANET1.log";
        else if( rb3.isChecked() ) fileName = "/data/data/com.ganet.catfish.ganet_service/Log/yam_fm2.txt";

        if( !isReading ) {
            ((Button)findViewById(R.id.btStartReadFile)).setText("Stop reading");
            Intent in = new Intent(GaNetService.READFILE);
            in.putExtra( "File", fileName );
            in.putExtra( "Read", true );
            sendBroadcast(in);
            isReading = true;
        } else {
            ((Button)findViewById(R.id.btStartReadFile)).setText("Start reading");
            isReading = false;
            Intent in = new Intent(GaNetService.READFILE);
            in.putExtra( "Read", false );
            sendBroadcast(in);
            updateFileReadUi(0, 0);
        }
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
