package com.moudle.plugin;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.moudle.pluginstand.PayInterfaceService;

import java.lang.reflect.Constructor;

/**
 * Created by Administrator on 2018/7/30.
 */

public class ProxyService extends Service {

    private static final String TAG = "ProxyService";
    String mServiceName;
    PayInterfaceService mPayInterfaceService;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        init(intent);
        return null;
    }

    private void init(Intent intent) {
        mServiceName = intent.getStringExtra("serviceName");
        Log.d(TAG, "init: " + mServiceName);
        try {
            Class loadClass = PluginManager.getInstance().getDexClassLoader().loadClass(mServiceName);
            Constructor<?> localConstrutor = loadClass.getConstructor(new Class[]{});
            Object instance = localConstrutor.newInstance(new Object[]{});
            mPayInterfaceService = (PayInterfaceService)instance;
            mPayInterfaceService.attach(this);
            Bundle bundle = new Bundle();
            bundle.putInt("from",1);
            mPayInterfaceService.onCreate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(mPayInterfaceService == null){
            init(intent);
        }
        return mPayInterfaceService.onStartCommand(intent,flags,startId);
    }

    @Override
    public void onDestroy() {
        mPayInterfaceService.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        mPayInterfaceService.onLowMemory();
        super.onLowMemory();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mPayInterfaceService.onUnbind(intent);
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        mPayInterfaceService.onRebind(intent);
        super.onRebind(intent);
    }
}



