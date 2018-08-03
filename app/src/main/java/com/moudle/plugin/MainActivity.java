package com.moudle.plugin;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.moudle.hooks.HookActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 *   利用插装模式，实现加载其他的APK；
 *
 *   1 ：加载文件
 *
 *   2 ： 代理生命周期，that 代表当前环境
 *
 *   3 ： broardcast的静态广播特殊，导致需要在发送广播之前，必须先注册，后使用。
 *        所以利用反射获取了系统的applicationInfor信息。
 *
 *   4 ; dexClassLoader 加载外部的class 。Class.forName可以加载本APP的Class;
 */
public class MainActivity extends AppCompatActivity {

    static  final String ACTION = "com.mahao.receiver.ACTION";
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_load_plugin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPlugin();
            }
        });

        findViewById(R.id.btn_jump_plugin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,ProxyActivity.class);
                intent.putExtra("className",PluginManager.getInstance().getPackageInfo().activities[0].name);
                startActivity(intent);
            }
        });

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Toast.makeText(context,"我是宿主，我已经收到消息，握手完成",Toast.LENGTH_SHORT).show();
            }
        };
      //  registerReceiver(receiver, new IntentFilter(ACTION));

        findViewById(R.id.btn_hook_replace).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ComponentName componentName = new ComponentName("com.moudle.plugin","com.moudle.hooks.HookActivity");
                Intent intent = new Intent();
                intent.setComponent(componentName);
                startActivity(intent);
            }
        });
    }

    private void loadPlugin() {
        File fileDir  = this.getDir("plugin", Context.MODE_PRIVATE);
        String name = "mahao.apk";
        String filePath = new File(fileDir,name).getAbsolutePath();
        File file = new File(filePath);
        if(file.exists()){
            file.delete();
        }
        InputStream is = null;
        FileOutputStream os = null;
        try{
            Log.d(TAG, "loadPlugin: " + new File(Environment.getExternalStorageDirectory(),name).getAbsolutePath());
            is = new FileInputStream(new File(Environment.getExternalStorageDirectory(),name));
            os = new FileOutputStream(filePath);
            int len = 0;
            byte[] buff = new byte[1024];
            while ((len = is.read(buff)) != -1){
                os.write(buff,0,len);
            }
            File ff = new File(filePath);
            if(ff.exists()){
                Toast.makeText(this,"dex overWrite",Toast.LENGTH_SHORT).show();
            }
            PluginManager.getInstance().loadPath(this);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                if(os != null){
                    os.close();
                }
                if(is != null){
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendBroadCast(View view){
        Intent intent = new Intent();
        intent.setAction(ACTION);
        sendBroadcast(intent);
    }

}
