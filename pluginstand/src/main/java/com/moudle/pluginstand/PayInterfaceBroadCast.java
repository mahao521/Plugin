package com.moudle.pluginstand;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * Created by Administrator on 2018/7/31.
 */

public interface PayInterfaceBroadCast {

    public void attach(Context context);

    public void onReceive(Context context, Intent intent);
}
