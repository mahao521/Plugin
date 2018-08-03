package com.moudle.mydoctor;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;

import com.moudle.pluginstand.PayInterfaceActivity;

/**
 * Created by Administrator on 2018/7/30.
 */

public class BaseActivity extends Activity implements PayInterfaceActivity{

    private static final String TAG = "BaseActivity";
    protected Activity that;

    @Override
    public void attach(Activity proxyActivity) {
        this.that = proxyActivity;
        Log.d(TAG, "attach: "  + that);
    }

    @Override
    public void setContentView(View view) {
        Log.d(TAG, "setContentView: " + that);
        if(that != null){
            that.setContentView(view);
        }else {
            super.setContentView(view);
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        Log.d(TAG, "setContentView: ");
        that.setContentView(layoutResID);
    }

    @Override
    public ComponentName startService(Intent service) {
        Log.d(TAG, "startService: ");
        Intent intent = new Intent();
        intent.putExtra("serviceName",service.getComponent().getClassName());
        return that.startService(intent);
    }

    @Override
    public void startActivity(Intent intent) {
        Intent intent1 = new Intent();
        intent1.putExtra("className",intent.getComponent().getClassName());
        that.startActivity(intent1);
    }

    @NonNull
    @Override
    public LayoutInflater getLayoutInflater() {
        return that.getLayoutInflater();
    }

    @Override
    public ApplicationInfo getApplicationInfo() {
        return that.getApplicationInfo();
    }

    @Override
    public Window getWindow() {
        return that.getWindow();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart: ");

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

    }

    @Override
    public View findViewById(int id) {
        return that.findViewById(id);
    }

    @Override
    public Intent getIntent() {
        if(that != null){
            return that.getIntent();
        }
        return super.getIntent();
    }

    @Override
    public ClassLoader getClassLoader() {
        return that.getClassLoader();
    }

    @Override
    public WindowManager getWindowManager() {
        return that.getWindowManager();
    }

    @Override
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        return that.registerReceiver(receiver, filter);
    }

    @Override
    public void unregisterReceiver(BroadcastReceiver receiver) {
        that.unregisterReceiver(receiver);
    }

    @Override
    public void sendBroadcast(Intent intent) {
        that.sendBroadcast(intent);
    }

}
