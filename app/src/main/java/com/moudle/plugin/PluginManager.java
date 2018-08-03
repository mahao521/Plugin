package com.moudle.plugin;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import dalvik.system.DexClassLoader;

/**
 * Created by Administrator on 2018/7/30.
 */

public class PluginManager {

    private static final String TAG = "PluginManager";
    private PackageInfo mPackageInfo;
    private Resources mResources;
    private DexClassLoader mDexClassLoader;
    private static final PluginManager instance = new PluginManager();

    public static PluginManager getInstance() {
        return instance;
    }

    private PluginManager(){}

    public PackageInfo getPackageInfo(){
        return mPackageInfo;
    }

    public void loadPath(Context context){
        File fileDir = context.getDir("plugin",Context.MODE_PRIVATE);
        Log.d(TAG, "loadPath: " + fileDir.getAbsolutePath());
        String name = "mahao.apk";
        String path = new File(fileDir,name).getAbsolutePath();
        PackageManager packageManager = context.getPackageManager();
        mPackageInfo =packageManager.getPackageArchiveInfo(path,PackageManager.GET_ACTIVITIES);
        File dexOutFile = context.getDir("dex",Context.MODE_PRIVATE);
        mDexClassLoader = new DexClassLoader(path,dexOutFile.getAbsolutePath(),null,context.getClassLoader());
        try {
            AssetManager assetManager = AssetManager.class.newInstance();
            Method addAessetPath = AssetManager.class.getMethod("addAssetPath",String.class);
            addAessetPath.invoke(assetManager,path);
            mResources = new Resources(assetManager,context.getResources().getDisplayMetrics(),context.getResources().getConfiguration());
        } catch (Exception e) {
            e.printStackTrace();
        }
        parseReceivers(path,context);
    }

    public Resources getResources(){
        return mResources;
    }

    public DexClassLoader getDexClassLoader(){
        return mDexClassLoader;
    }

    public void parseReceivers(String path,Context context){

        try {
            Class packageParseClass = Class.forName("android.content.pm.PackageParser");
            Method parsePackageMethod = packageParseClass.getDeclaredMethod("parsePackage",File.class,int.class);
            Object packageParse = packageParseClass.newInstance();
            Object parseObj = parsePackageMethod.invoke(packageParse,new File(path),PackageManager.GET_ACTIVITIES);
            Field  received = parseObj.getClass().getDeclaredField("receivers");
            List receivers = (List) received.get(parseObj);

            Class<?> componentClass = Class.forName("android.content.pm.PackageParser$Component");
            Field intentsField  = componentClass.getDeclaredField("intents");
            //调用generateActivityInfo 方法，
            Class<?> packageParse$ActivityClass = Class.forName("android.content.pm.PackageParser$Activity");
            Class<?> packageUserState = Class.forName("android.content.pm.PackageUserState");
            Object defaultUserState =  packageUserState.newInstance();
            Method generateReceiverInfo = packageParseClass.getDeclaredMethod("generateActivityInfo",packageParse$ActivityClass
            ,int.class,packageUserState,int.class);
            Class<?> userHandler = Class.forName("android.os.UserHandle");
            Method getCallingUserMethod = userHandler.getDeclaredMethod("getCallingUserId");
            int userId = (int) getCallingUserMethod.invoke(null);

            for(Object activity : receivers){
                ActivityInfo info  = (ActivityInfo) generateReceiverInfo.invoke(packageParse,activity,0,defaultUserState,userId);
                BroadcastReceiver broadcastReceiver = (BroadcastReceiver) mDexClassLoader.loadClass(info.name).newInstance();
                Log.d(TAG, "parseReceivers: " + info.name);
                //因为activity继承自Component，所以可以这样反射
                List<? extends IntentFilter> intentFilters = (List<? extends IntentFilter>) intentsField.get(activity);
                Log.d(TAG, "parseReceivers: " + intentFilters.size());
                for(IntentFilter intentFilter : intentFilters){
                    context.registerReceiver(broadcastReceiver,intentFilter);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

















