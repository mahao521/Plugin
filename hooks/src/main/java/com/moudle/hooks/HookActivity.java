package com.moudle.hooks;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class HookActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hook);

        findViewById(R.id.btn_jump_1).setOnClickListener(this);
        findViewById(R.id.btn_jump_2).setOnClickListener(this);
        findViewById(R.id.btn_jump_3).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_jump_1) {
            Intent intent = new Intent(HookActivity.this, SecondActivity.class);
            startActivity(intent);
        } else if (id == R.id.btn_jump_2) {
            Intent intent = new Intent(this,LogInActivity.class);
            startActivity(intent);
        } else if (id == R.id.btn_jump_3) {
            SharedPreferences share = this.getSharedPreferences("mahao", MODE_PRIVATE);//实例化
            SharedPreferences.Editor editor = share.edit(); //使处于可编辑状态
            editor.putBoolean("login",false);   //设置保存的数据
            Toast.makeText(this, "退出登录成功",Toast.LENGTH_SHORT).show();
            editor.commit();    //提交数据保存
        }
    }
}
