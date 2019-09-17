package com.test.course_haut;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        id = findViewById(R.id.id);//学号
        pw = findViewById(R.id.pw);//密码

        for (int i = 0; i < 9; i++) {
           Util.clients[i] =  new OkHttpClient().newBuilder()
                    .cookieJar(new CookieJar() {
                        @Override
                        public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                            Util.getCookieStore().put(url.host(), cookies);
                        }

                        @NotNull
                        @Override
                        public List<Cookie> loadForRequest(HttpUrl url) {//加载新的cookies
                            List<Cookie> cookies = Util.getCookieStore().get(url.host());
                            return cookies != null ? cookies : new ArrayList<Cookie>();
                        }
                    }).build();
        }

       //获取xml存储的账户密码（有风险）
        SharedPreferences sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);;
        id.setText(sharedPreferences.getString("id",""));
        pw.setText(sharedPreferences.getString("pw",""));
        code = findViewById(R.id.code);//验证码
        bt = findViewById(R.id.bt);//按钮
        bt.setOnClickListener(v -> {
            final String tId = id.getText().toString();
            final String tPw = pw.getText().toString();
            final String tCode = code.getText().toString();
            new Thread(() -> {
                Request request = new Request.Builder()
                        .url("http://"+Util.ips[0]+"/default2.aspx")
                        .post(new FormBody.Builder()
                                .add("__VIEWSTATE", Util.get__VIEWSTATE())
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
                try (Response response = Util.getClient(0).newCall(request).execute()) {
                    String resp = response.body().string();
                    if(resp.contains("验证码不正确！！"))
                    {
                        ref(0);
                        Message m = new Message();
                        m.what = 0x03;
                        m.obj = "验证码不正确";
                        handler.sendMessage(m);

                    }
                    else if(resp.contains("密码错误，如忘记密码，请与教务处联系!"))
                    {
                        ref(0);
                        Message m = new Message();
                        m.what = 0x03;
                        m.obj = "密码错误，如忘记密码，请与教务处联系!";
                        handler.sendMessage(m);
                    }
                    else if(resp.contains("用户名不存在或未按照要求参加教学活动！！"))
                    {
                        ref(0);
                        Message m = new Message();
                        m.what = 0x03;
                        m.obj = "用户名不存在或未按照要求参加教学活动！！";
                        handler.sendMessage(m);
                    }
                    else if(resp.contains("用户名不能为空！！"))
                    {
                        ref(0);
                        Message m = new Message();
                        m.what = 0x03;
                        m.obj = "用户名不能为空！！";
                        handler.sendMessage(m);
                    }
                    else if(resp.contains("密码错误！！"))
                    {
                        ref(0);
                        Message m = new Message();
                        m.what = 0x03;
                        m.obj = "密码错误！！";
                        handler.sendMessage(m);
                    }
                    else if(resp.contains("密码不能为空！！"))
                    {
                        ref(0);
                        Message m = new Message();
                        m.what = 0x03;
                        m.obj = "密码不能为空！！";
                        handler.sendMessage(m);
                    }
                    else if(resp.contains("验证码不能为空"))
                    {
                        ref(0);
                        Message m = new Message();
                        m.what = 0x03;
                        m.obj = "验证码不能为空！！";
                        handler.sendMessage(m);
                    }
                    else if(resp.contains("欢迎您使用正方教务管理系统"))
                    {
                        String[] resp_sp = resp.split("id=\"xhxm\">");
                        SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                        editor.putString("id", tId);
                        editor.putString("pw", tPw);
                        editor.apply();
                        Util.setId(tId);
                        Message m = new Message();
                        m.what = 0x04;
                        m.obj = resp_sp[1].split("同学")[0];
                        handler.sendMessage(m);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        });
        rf = findViewById(R.id.rf);//刷新
        rf.setEnabled(false);
        rf.setOnClickListener(v -> new Thread(() -> ref(0)).start());
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case 0x01:
                        rf.setImageBitmap((Bitmap) msg.obj);
                        break;
                    case 0x02:
                        rf.setEnabled(true);
                        Util.set__VIEWSTATE((String) msg.obj);
                        break;
                    case 0x03:
                        Toast.makeText(getApplicationContext(),(String)msg.obj,Toast.LENGTH_LONG).show();break;
                    case 0x04:
                        Toast.makeText(getApplicationContext(),"欢迎使用，"+msg.obj,Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(),SelectActivity.class);
                        startActivity(intent);break;
                    case 0x05:
                        Toast.makeText(getApplicationContext(),"未能连接至河南工业大学教务系统",Toast.LENGTH_LONG).show();break;
                }
                return false;
            }
        });
        getViewState();
    }

    void getViewState()
    {
        new Thread(() -> {

            Request request = new Request.Builder()
                    .url("http://"+Util.ips[0]+"/default2.aspx#")
                    .build();
            try (Response response = Util.getClient(0).newCall(request).execute()) {
                String resp = response.body().string();
                String[] resp_sp = resp.split("TE\" value=\"");
                Message m = new Message();
                m.what = 0x02;//ViewState传输成功
                m.obj = resp_sp[1].split("\" />")[0];
                handler.sendMessage(m);
                ref(0);//验证码刷新
            }
            catch (ConnectException e1)
            {
                Message m = new Message();
                m.what = 0x05;//无网络
                handler.sendMessage(m);
                e1.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            }

        }).start();
    }
    //刷新验证码
    void ref(int index) {
        Request request = new Request.Builder()
                .url("http://172.18.254.101/CheckCode.aspx")
                .build();
        try (Response response = Util.getClient(index).newCall(request).execute()) {
            InputStream resp = response.body().byteStream();
            Bitmap bitmap = BitmapFactory.decodeStream(resp);
            Message m = new Message();
            m.what = 0x01;//图片传输成功
            m.obj = bitmap;
            handler.sendMessage(m);

        }
        catch (ConnectException e1)
        {
            Message m = new Message();
            m.what = 0x05;//无网络
            handler.sendMessage(m);
            e1.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.refresh:
                getViewState();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
