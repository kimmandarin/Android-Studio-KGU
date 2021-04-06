package com.tester.apptest1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class LoginPage extends AppCompatActivity {
    EditText userId, userPassword;
    Button loginBtn, joinBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        userId = (EditText) findViewById(R.id.userId);
        userPassword = (EditText) findViewById(R.id.userPassword);
        loginBtn = (Button) findViewById(R.id.loginBtn);
        joinBtn = (Button) findViewById(R.id.joinBtn);
        loginBtn.setOnClickListener(btnListener);
        joinBtn.setOnClickListener(btnListener);
        Button button = (Button) findViewById(R.id.loginBtn);
    }

    /**
     * json -> {"result":"1"} =>class Result{ int result }
     * ObjectMapper => json -> Class
     *
     *
     */
    class CustomTask extends AsyncTask<String, Void, String> {
        String sendMsg, receiveMsg;
        @Override
        protected String doInBackground(String... strings) {
            try {
                String str;
                URL url = new URL("http://118.67.130.73/capston/login/server.jsp");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
                sendMsg = "id="+strings[0]+"&psd="+strings[1]+"&type="+strings[2];
                osw.write(sendMsg);
                System.out.println(sendMsg);
                osw.flush();
                System.out.println(conn.getResponseCode());
                if(conn.getResponseCode() == conn.HTTP_OK) {
                    InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                    BufferedReader reader = new BufferedReader(tmp);
                    StringBuffer buffer = new StringBuffer();
                    while ((str = reader.readLine()) != null) {
                        buffer.append(str);
                    }
                    System.out.println(receiveMsg);
                    receiveMsg = buffer.toString();

                } else {
                    Log.i("통신 결과", conn.getResponseCode()+"에러");
                    Log.i("통신 결과", String.valueOf(conn.getErrorStream()));


                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            return receiveMsg;
        }
    }

    View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.loginBtn : // 로그인 버튼 눌렀을 경우
                    String loginId = userId.getText().toString();
                    String loginPwd = userPassword.getText().toString();
                    try {
                        //AsyncTask at=new LoginPage.CustomTask().execute(loginId,loginPwd,"login");
                        AsyncTask at=new LoginPage.CustomTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,loginId,loginPwd,"login");
                        //String result=new LoginPage.CustomTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,loginId,loginPwd,"login").get().trim();
                        String result  =at.get().toString().trim();
                        System.out.println(result);
                        if(result.equals("realtrue")) {
                            Toast.makeText(LoginPage.this,"로그인",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginPage.this, MainHome.class);
                            startActivity(intent);
                            finish();
                            if (at.getStatus()==AsyncTask.Status.RUNNING)
                                at.cancel(true);
                        } else if(result.equals("false")) {
                            Toast.makeText(LoginPage.this,"아이디 또는 비밀번호가 틀렸음",Toast.LENGTH_SHORT).show();
                            userId.setText("");
                            userPassword.setText("");
                        } else if(result.equals("true")){
                            Toast.makeText(LoginPage.this,"이메일 인증이 필요함",Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception e) {}
                    break;
                case R.id.joinBtn : // 회원가입
                    try {
                            Toast.makeText(LoginPage.this,"회원가입",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginPage.this, JoinPage.class);
                            startActivity(intent);
                            finish();
                        
                    }catch (Exception e) {}
                    break;
            }
        }
    };
}