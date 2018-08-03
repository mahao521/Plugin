package com.moudle.hooks;

import android.app.Application;

/**
 * Created by Administrator on 2018/8/3.
 */

public class HookApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //使用that插件化时候，需要注释掉
        HookUtil hookUtil = new HookUtil();
        hookUtil.hookHookMh(this);
        hookUtil.hookStartActivity(this);
    }
}
