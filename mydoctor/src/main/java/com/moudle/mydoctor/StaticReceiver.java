package com.moudle.mydoctor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Administrator on 2018/7/31.
 */

public class StaticReceiver extends BroadcastReceiver {

    static  final String ACTION = "com.mahao.receiver.ACTION";
    private static final String TAG = "StaticReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: ");
        Toast.makeText(context, "我是插件   收到宿主的消息  静态注册的广播  收到宿主的消息----->", Toast.LENGTH_SHORT).show();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Toast.makeText(context, "休眠之后---->", Toast.LENGTH_SHORT).show();
    }
}
