package com.moudle.hooks;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by Administrator on 2018/8/1.
 */

public class HookUtil {

    private static final String TAG = "HookUtil";
    private Context mContext;
    public void hookHookMh(Context context){
        mContext = context;
        Log.d(TAG, "hookHookMh: " + mContext.getPackageName());
        try {
            Class<?> forName = Class.forName("android.app.ActivityThread");
            Field currentField = forName.getDeclaredField("sCurrentActivityThread");
            currentField.setAccessible(true);
            //还原系统的Activity的mh
            Object activityThreadObj = currentField.get(null);
            Field handlerField = forName.getDeclaredField("mH");
            handlerField.setAccessible(true);
            //找到静态hook点
            Handler mH = (Handler) handlerField.get(activityThreadObj);
            Field callbackField = Handler.class.getDeclaredField("mCallback");
            callbackField.setAccessible(true);
            callbackField.set(mH,new ActivityMh(mH));
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    class ActivityMh implements Handler.Callback{

        private Handler mH;
        public ActivityMh(Handler mh){
            this.mH = mh;
        }
        @Override
        public boolean handleMessage(Message msg) {
            if(msg.what == 100){
                handleLaunchActivity(msg);
            }
            mH.handleMessage(msg);
            return true;
        }

        /**
         *    这一个类，也可以实现，依据sharePreference和Intent()判断
         * @param msg
         */
        private void handleLaunchActivity(Message msg) {
            Object object = msg.obj;
            try {
                Field intentField = object.getClass().getDeclaredField("intent");
                intentField.setAccessible(true);
                Intent realyIntent = (Intent) intentField.get(object);
                Intent oldIntent = realyIntent.getParcelableExtra("oldIntent");
                if(oldIntent != null){
                    String packageName = oldIntent.getComponent().getPackageName();
                    String className = oldIntent.getComponent().getClassName();
                    Log.d(TAG, "handleLaunchActivity: " + packageName +" class " + className);
                    SharedPreferences sharedPreferences = mContext.getSharedPreferences("mahao", Context.MODE_PRIVATE);
                    if(sharedPreferences.getBoolean("login",false)){
                        realyIntent.setComponent(oldIntent.getComponent());
                    }else {
                        ComponentName componentName = new ComponentName(mContext,LogInActivity.class);
                        realyIntent.putExtra("extraIntent",oldIntent.getComponent().getClassName());
                        realyIntent.setComponent(componentName);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void hookStartActivity(Context context){
        this.mContext = context;
        Log.d(TAG, "hookStartActivity: " + mContext.getPackageName());
        try{
            Class<?> activityManagerClass = Class.forName("android.app.ActivityManagerNative");
            Field gDefault = activityManagerClass.getDeclaredField("gDefault");
            gDefault.setAccessible(true);
            Object defaultValue  = gDefault.get(null);
            Class<?> singleTon = Class.forName("android.util.Singleton");
            Field mInstance = singleTon.getDeclaredField("mInstance");
            mInstance.setAccessible(true);
            Object iActivityManager = mInstance.get(defaultValue);
            Class<?> iActivityManagerInter = Class.forName("android.app.IActivityManager");
            StartActivity startMethod = new StartActivity(iActivityManager);
            //第二个参数，是返回的对象需要实现那写接口
            Object oldActivityManager = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader()
            ,new Class[]{iActivityManagerInter, View.OnClickListener.class},startMethod);
            //将系统iActivityManager 替换成 自己通过动态代理实现的对象，
            //oldActivityManager 实现了iActivitynanagerI这个接口
            mInstance.set(defaultValue,oldActivityManager);
        }catch (Exception e){
           e.printStackTrace();
        }
    }

    //需要增加一个标志
    public class StartActivity implements InvocationHandler{
        private Object iActivityManagerObject;
        public StartActivity(Object object){
            this.iActivityManagerObject = object;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

            Log.i(TAG, "invoke: "  + method.getName());
            if("startActivity".equals(method.getName())){
                Log.i(TAG, "invoke:-------startActivity-------" );
                //寻找传进来的intent
                Intent intent = null;
                int index = 0;
                for(int i = 0;i < args.length; i++){
                    Object arg = args[i];
                    if (arg instanceof Intent){
                        intent = (Intent) args[i];
                        index = i;
                        break;
                    }
                }
                if(intent != null){
                    String packageName = intent.getComponent().getPackageName();
                    String className = intent.getComponent().getClassName();
                    Log.d(TAG, "StartActivity: " + packageName +" class " + className);
                }
                //目的  --- 载入activity 将它还原
                Intent newIntent = new Intent();
                ComponentName componentName = new ComponentName(mContext,ProxyActivity.class);
                newIntent.setComponent(componentName);
                //真是意图 被隐藏到了， 键值对
                newIntent.putExtra("oldIntent",intent);
                args[index] = newIntent;
            }
            return method.invoke(iActivityManagerObject,args);
        }
    }
}
