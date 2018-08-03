package com.moudle.plugin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


import com.moudle.pluginstand.PayInterfaceBroadCast;

import java.lang.reflect.Constructor;

/**
 * Created by Administrator on 2018/7/31.
 */

public class ProxyBroardCast  extends BroadcastReceiver{

    private String className;
    PayInterfaceBroadCast mPayInterfaceBroadcast;

    @Override
    public void onReceive(Context context, Intent intent) {
        mPayInterfaceBroadcast.onReceive(context,intent);
    }

    public  ProxyBroardCast(String className,Context context){
        this.className = className;
        try {
            Class loadClass = PluginManager.getInstance().getDexClassLoader().loadClass(className);
            Constructor<?> loadConstructor = loadClass.getConstructor(new Class[]{});
            Object instance = loadConstructor.newInstance(new Object[]{});
            mPayInterfaceBroadcast = (PayInterfaceBroadCast) instance;
            mPayInterfaceBroadcast.attach(context);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
