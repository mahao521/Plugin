package com.moudle.mydoctor;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class OneService extends BaseService {

    private static final String TAG = "OneService";
    int i = 0;
    public OneService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        new Thread(){

            @Override
            public void run() {
                Log.d(TAG, "run: " + (i++));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
