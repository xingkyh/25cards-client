package com.example.a25cards;

import android.content.Intent;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Xml;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class RegisterActivity extends AppCompatActivity {
    private EditText et_username, et_password,et_nickname,et_password_check;
    private String username, password, nickname,password_check;
    private Button rebt_register;
    private Intent it;
    private Bundle bundle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        // 全屏、隐藏状态栏
        setContentView(R.layout.activity_register);
        et_username =(EditText) findViewById(R.id.reet_username);
        et_password =(EditText) findViewById(R.id.reet_password);
        et_nickname =(EditText) findViewById(R.id.reet_nickname);
        rebt_register =(Button) findViewById(R.id.rebt_register);
        et_password_check = (EditText) findViewById(R.id.reet_password_check);
        rebt_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = et_username.getText().toString();
                password = et_password.getText().toString();
                nickname = et_nickname.getText().toString();
                password_check = et_password_check.getText().toString();
                if(!password.equals(password_check)){
                    Toast.makeText(RegisterActivity.this,"密码不一致",Toast.LENGTH_LONG).show();
                }else{
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Looper.prepare();
                            try {
                                String path = "http://172.21.9.1:8080/25Cards/Register?username="+username+"&password="+password;
                                URL url = new URL(path);
                                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                                connection.setDoOutput(true); //使用URL进行输出
                                connection.setDoInput(true); //使用URL进行输入
                                connection.setUseCaches(false); //忽略使用缓存
                                connection.setRequestMethod("POST");//获取服务器数据
                                connection.setReadTimeout(10000);//设置读取超时的毫秒数
                                connection.setConnectTimeout(10000);//设置连接超时的毫秒数
                                connection.setRequestProperty("Content-type", "text/html;charset=utf-8");
                                connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                                connection.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
                                connection.setRequestProperty("Charset", "UTF-8");
                                connection.connect();

                                DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
                                String content = "nickname="+URLEncoder.encode(nickname,"utf-8");
                                ArrayList<Integer> a = new ArrayList();
                                outputStream.writeBytes(content);
                                outputStream.flush();
                                outputStream.close();

                                InputStream in = connection.getInputStream();
                                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                                String result = reader.readLine();//读取服务器进行逻辑处理后页面显示的数据
                                if(result.equals("Register Successful")){
                                    it = new Intent(RegisterActivity.this,LoginActivity.class);
                                    bundle = new Bundle();
                                    bundle.putString("userName",username);
                                    it.putExtras(bundle);
                                    startActivity(it);
                                    RegisterActivity.this.finish();
                                }
                                else{
                                    Toast.makeText(RegisterActivity.this,result,Toast.LENGTH_LONG).show();
                                }
                                System.out.println(result.toString());
                                Looper.loop();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            }
        });

    }
}
