package com.moudle.mydoctor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.moudle.pluginstand.PayInterfaceBroadCast;
import com.moudle.pluginstand.PayInterfaceService;

/**
 * Created by Administrator on 2018/7/31.
 */

public class MyReceiver extends BroadcastReceiver implements PayInterfaceBroadCast {
    @Override
    public void attach(Context context) {
        Toast.makeText(context,"----绑定上下文广播---",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "-----插件收到广播2--->", Toast.LENGTH_SHORT).show();
    }
}
