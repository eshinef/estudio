package com.es.flashlight;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    View background_view;
    TextView switch_btn;
    View sos_btn;

    FlashLight flashLight;

    Handler lightHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    flashLight.turnOn();
                    break;

                case 0:
                    flashLight.turnOff();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        background_view = findViewById(R.id.background_view);
        switch_btn = findViewById(R.id.switch_btn);
        switch_btn.setOnClickListener(view -> {
            flashLight = getFlashLight();
            if (!flashLight.on) {
                flashLight.turnOn();
            } else {
                flashLight.turnOff();
            }
        });

        sos_btn = findViewById(R.id.sos_btn);
        sos_btn.setOnClickListener(view -> {
            flashLight = getFlashLight();
            boolean sos = flashLight.triggerSOS();
            if(sos) {
                startSOS();
            } else {
                stopSOS();
            }
        });
    }
    private Timer sosTimer = null;
    private SOSTask sosTask = null;

    void startSOS() {
        if (sosTimer == null) {
            sosTimer = new Timer();
            sosTask = new SOSTask();
            sosTimer.schedule(sosTask, 500, 200);
        }
    }

    void stopSOS() {
        if (sosTimer != null) {
            sosTimer.cancel();
            sosTimer.purge();
        }
        sosTimer = null;
    }


    private FlashLight getFlashLight() {
        if (flashLight == null) {
            flashLight = new FlashLight();
        }
        return flashLight;
    }

    private boolean hasFlashlight() {
        return getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    class FlashLight {
        private boolean on = false;
        private boolean sos = false;

        public FlashLight() {
        }

        void turnOn() {
            on = true;
            switch_btn.setText("On");
            background_view.setBackgroundColor(getColor(R.color.red));
        }

        void turnOff() {
            on = false;
            switch_btn.setText("Off");
            background_view.setBackgroundColor(getColor(R.color.yellow));
        }

        boolean isOn() {
            return on;
        }

        boolean triggerSOS() {
            if(sos) {
                sos = false;
            } else {
                sos = true;
            }
            return sos;
        }
    }

    class SOSTask extends TimerTask {
        @Override
        public void run() {
            flashLight = getFlashLight();
            if(flashLight.isOn()) {
                lightHandler.sendEmptyMessage(0);
            } else {
                lightHandler.sendEmptyMessage(1);
            }
        }
    }
}