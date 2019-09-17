package com.test.course_haut;

import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

public class F__kActivity extends AppCompatActivity {

    private SeekBar seekBar;
    private TextView serverNum,className,out;
    private Button start;
    private Handler handler;
    private ProgressBar progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_f__k);

        handler = new Handler(msg -> {
       switch (msg.what)
       {
           case 0x00:
               start.setText("START");
               seekBar.setEnabled(true);
               progress.setVisibility(View.INVISIBLE);
           out.setText("课程已加入课表");break;
           case 0x01:out.setText((String)msg.obj);break;
       }
            return false;
        });

        serverNum = findViewById(R.id.serverNum);
        seekBar = findViewById(R.id.seekBar);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                serverNum.setText(""+(1+progress));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        className = findViewById(R.id.className);
        String clzName =Util.list.get(getIntent().getIntExtra("index",0)).getName();
        className.setText(clzName);

        start  = findViewById(R.id.start);
        start.setOnClickListener(v -> {
            out.setText("");
            seekBar.setEnabled(false);
            progress.setVisibility(View.VISIBLE);
            start.setText("STOP");

            Thread f__kThread =  new Thread(() -> {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Request request0 = new Request.Builder()
                        .url("http://"+Util.ips[0]+"/default2.aspx")
                        .post(new FormBody.Builder()
                                .add("__VIEWSTATE", Util.get__VIEWSTATE())
                                .add("Button1", "")
                                .build())
                        .build();
                System.out.println(check(clzName));
                while (start.getText().toString().equals("STOP")&&!check(clzName))//开始并且没有检查到已选这门课
                {
                    try (Response response = Util.getClient(0).newCall(request0).execute()) {
                        String resp = response.body().string();
                        Message m = new Message();
                        m.what = 0x01;
                        m.obj = resp.trim();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        if(start.getText().toString().equals("STOP"))
                        {
                            Thread.sleep(1000);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Message m = new Message();
                m.what = 0x00;//停止
                handler.sendMessage(m);
            });
            f__kThread.start();
        });
        out = findViewById(R.id.out);

        progress = findViewById(R.id.progress);
    }
    boolean check(String className)
    {
        Request request = new Request.Builder()
                .addHeader("Referer","http://"+Util.ips[0]+"/xs_main.aspx?xh="+Util.getId())
                .url("http://"+Util.ips[0]+"/xskbcx.aspx?xh="+Util.getId()+"&xm="+Util.getName()+"&gnmkdm=N121603")
                .build();
        try (Response response = Util.getClient(0).newCall(request).execute()) {
            String resp = response.body().string();
            Document doc = Jsoup.parse(resp);
            Elements e =  doc.select("td[rowspan=\"2\"]");
            for (Element element : e) {
                String [] s =  element.text().split(" ");
                if((s[0]+" | "+s[1]).equals(className))
                {
                    return true;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


}
