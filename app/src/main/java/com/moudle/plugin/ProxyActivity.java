package com.moudle.plugin;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.moudle.pluginstand.PayInterfaceActivity;

import java.lang.reflect.Constructor;

/**
 * Created by Administrator on 2018/7/30.
 */

public class ProxyActivity extends Activity {

    private static final String TAG = "ProxyActivity";
    //需要加载的目标的类名
    private String className;
    PayInterfaceActivity mPayInterfaceActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        className = getIntent().getStringExtra("className");
        Log.d(TAG, "onCreate: " + className);
        try {
            Class activityClass = getClassLoader().loadClass(className);
            Constructor constructor = activityClass.getConstructor(new Class[]{});
            Object instance = constructor.newInstance(new Object[]{});
            mPayInterfaceActivity = (PayInterfaceActivity) instance;
            mPayInterfaceActivity.attach(this);
            Bundle bundle = new Bundle();
            mPayInterfaceActivity.onCreate(bundle);
            Log.d(TAG, "onCreate: ");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startActivity(Intent intent) {
        String className = intent.getStringExtra("className");
        Intent intent1 = new Intent(this,ProxyActivity.class);
        intent1.putExtra("className",className);
        super.startActivity(intent1);
    }

    @Override
    public ComponentName startService(Intent service) {
        String serviceName = service.getStringExtra("serviceName");
        Intent intent1 = new Intent(this,ProxyService.class);
        intent1.putExtra("serviceName",serviceName);
        return super.startService(intent1);
    }

    @Override
    public ClassLoader getClassLoader(){
        return PluginManager.getInstance().getDexClassLoader();
    }

    @Override
    public Resources getResources() {
        return PluginManager.getInstance().getResources();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPayInterfaceActivity.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPayInterfaceActivity.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPayInterfaceActivity.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPayInterfaceActivity.onResume();
    }

    @Override
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        IntentFilter newFilter = new IntentFilter();
        for (int i = 0 ; i < filter.countActions();i++){
            newFilter.addAction(filter.getAction(i));
        }
        return super.registerReceiver(new ProxyBroardCast(receiver.getClass().getName(),this), newFilter);
    }

}














