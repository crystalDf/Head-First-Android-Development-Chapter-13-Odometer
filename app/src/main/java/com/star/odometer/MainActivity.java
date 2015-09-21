package com.star.odometer;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private OdometerService mOdometerService;
    private boolean mBound;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            OdometerService.OdometerBinder odometerBinder =
                    (OdometerService.OdometerBinder) service;
            mOdometerService = odometerBinder.getOdometerService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        watchKilometers();
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = new Intent(this, OdometerService.class);
        bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mBound) {
            unbindService(mServiceConnection);
        }
    }

    private void watchKilometers() {

        final Handler handler = new Handler();

        final TextView distanceTextView = (TextView) findViewById(R.id.distance);

        handler.post(new Runnable() {
            @Override
            public void run() {

                double distance = 0;

                if (mOdometerService != null) {
                    distance = mOdometerService.getKiloMeters();
                }

                distanceTextView.setText(String.format("%1$, .2f kilometers", distance));

                handler.postDelayed(this, 1000);
            }
        });
    }
}
