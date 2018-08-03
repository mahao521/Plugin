package com.moudle.hooks;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LogInActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mEdtName,mEdtPassword;
    private Button mBtnLogIn;
    private String className ;
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        mEdtName = findViewById(R.id.edt_name);
        mEdtPassword = findViewById(R.id.edt_password);
        mBtnLogIn = findViewById(R.id.btn_login);
        mBtnLogIn.setOnClickListener(this);
        className = getIntent().getStringExtra("extraIntent");
        sharedPreferences = getSharedPreferences("mahao",MODE_PRIVATE);
    }

    @Override
    public void onClick(View v) {
        String name = mEdtName.getText().toString().trim();
        String passWord = mEdtPassword.getText().toString().trim();
        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(passWord)){

            if("mahao".equals(name) && passWord.equals("1234")){

                SharedPreferences.Editor edit = sharedPreferences.edit();
                edit.putString("name",name);
                edit.putString("password",passWord);
                edit.putBoolean("login",true);
                edit.commit();
                if(!TextUtils.isEmpty(className)){
                    ComponentName componentName = new ComponentName(this,className);
                    Intent intent = new Intent();
                    intent.setComponent(componentName);
                    startActivity(intent);
                    finish();
                }
            }else {
                SharedPreferences.Editor edit = sharedPreferences.edit();
                edit.putBoolean("login",false);
                edit.commit();
                Toast.makeText(this,"登录失败",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
