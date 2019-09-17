package com.test.course_haut;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private EditText id, pw, code;
    private Button bt;
    private ImageView rf;
    private Handler handler;
    private ConcurrentHashMap<String, List<Cookie>> cookieStore = new ConcurrentHashMap<>();
    private String __VIEWSTATE;
    OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        client = new OkHttpClient().newBuilder()
                .cookieJar(new CookieJar() {
                    @Override
                    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                        cookieStore.put(url.host(), cookies);
                    }

                    @Override
                    public List<Cookie> loadForRequest(HttpUrl url) {//加载新的cookies
                        List<Cookie> cookies = cookieStore.get(url.host());
                        return cookies != null ? cookies : new ArrayList<Cookie>();
                    }
                }).build();

        id = findViewById(R.id.id);//学号
        pw = findViewById(R.id.pw);//密码
        code = findViewById(R.id.code);//验证码
        bt = findViewById(R.id.bt);//按钮
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String tId = id.getText().toString();
                final String tPw = pw.getText().toString();
                final String tCode = code.getText().toString();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Request request = new Request.Builder()
                                .url("http://172.18.254.101/default2.aspx")
                                .post(new FormBody.Builder()
                                        .add("__VIEWSTATE", __VIEWSTATE)
                                        .add("txtUserName", tId)
                                        .add("TextBox1", "")
                                        .add("TextBox2", tPw)
                                        .add("txtSecretCode", tCode)
                                        .add("RadioButtonList1", "学生")
                                        .add("Button1", "")
                                        .add("lbLanguage", "")
                                        .add("hidPdrs", "")
                                        .add("hidsc", "")
                                        .build())
                                .build();
                        try (Response response = client.newCall(request).execute()) {
                            String resp = response.body().string();
                            System.out.println(resp);
                            if(resp.contains("验证码不正确！！"))
                            {
                                ref();
                                Message m = new Message();
                                m.what = 0x03;
                                m.obj = "验证码不正确";
                                handler.sendMessage(m);

                            }
                            else if(resp.contains("密码错误，如忘记密码，请与教务处联系!"))
                            {
                                ref();
                                Message m = new Message();
                                m.what = 0x03;
                                m.obj = "密码错误，如忘记密码，请与教务处联系!";
                                handler.sendMessage(m);
                            }
                            else if(resp.contains("用户名不存在或未按照要求参加教学活动！！"))
                            {
                                ref();
                                Message m = new Message();
                                m.what = 0x03;
                                m.obj = "用户名不存在或未按照要求参加教学活动！！";
                                handler.sendMessage(m);
                            }
                            else if(resp.contains("用户名不能为空！！"))
                            {
                                ref();
                                Message m = new Message();
                                m.what = 0x03;
                                m.obj = "用户名不能为空！！";
                                handler.sendMessage(m);
                            }
                            else if(resp.contains("密码错误！！"))
                            {
                                ref();
                                Message m = new Message();
                                m.what = 0x03;
                                m.obj = "密码错误！！";
                                handler.sendMessage(m);
                            }
                            else if(resp.contains("密码不能为空！！"))
                            {
                                ref();
                                Message m = new Message();
                                m.what = 0x03;
                                m.obj = "密码不能为空！！";
                                handler.sendMessage(m);
                            }
                            else if(resp.contains("验证码不能为空"))
                            {
                                ref();
                                Message m = new Message();
                                m.what = 0x03;
                                m.obj = "验证码不能为空！！";
                                handler.sendMessage(m);
                            }
                            else if(resp.contains("欢迎您使用正方教务管理系统"))
                            {
                                ref();
                                Message m = new Message();
                                m.what = 0x04;
                                handler.sendMessage(m);
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
        rf = findViewById(R.id.rf);//刷新
        rf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ref();
                    }
                }).start();
            }
        });
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case 0x01:
                        rf.setImageBitmap((Bitmap) msg.obj);
                        break;
                    case 0x02:
                        __VIEWSTATE = (String) msg.obj;
                        break;
                    case 0x03:
                        Toast.makeText(getApplicationContext(),(String)msg.obj,Toast.LENGTH_LONG).show();break;
                    case 0x04:

                }
                return false;
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {

                Request request = new Request.Builder()
                        .url("http://172.18.254.101/default2.aspx#")
                        .build();
                try (Response response = client.newCall(request).execute()) {
                    String resp = response.body().string();
                    String[] resp_sp = resp.split("TE\" value=\"");
                    Message m = new Message();
                    m.what = 0x02;//ViewState传输成功
                    m.obj = resp_sp[1].split("\" />")[0];
                    handler.sendMessage(m);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                ref();//验证码刷新
            }
        }).start();
    }

    //刷新验证码
    void ref() {
        Request request = new Request.Builder()
                .url("http://172.18.254.101/CheckCode.aspx")
                .build();
        try (Response response = client.newCall(request).execute()) {
            InputStream resp = response.body().byteStream();
            Bitmap bitmap = BitmapFactory.decodeStream(resp);
            Message m = new Message();
            m.what = 0x01;//图片传输成功
            m.obj = bitmap;
            handler.sendMessage(m);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
