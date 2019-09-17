package com.test.course_haut;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import okhttp3.Request;
import okhttp3.Response;

public class SelectActivity extends AppCompatActivity {

    private ListView courses;
    private SelectAdapter selectAdapter;
    private Handler handler;
    @Override
    //http://"+Util.ips[0]+"/xskbcx.aspx?xh=201616010409&xm=%D5%C5%D1%A9%C4%EA&gnmkdm=N121603
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);
        handler = new Handler(msg -> {
            switch (msg.what)
            {
                case 0x01:selectAdapter.notifyDataSetChanged();
            }
            return false;
        });
        courses = findViewById(R.id.courses);
        selectAdapter =  new SelectAdapter(this);
        courses.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(this,F__kActivity.class);
            intent.putExtra("index",position);
            startActivity(intent);
            System.out.println(position);
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                Request request = new Request.Builder()
                        .addHeader("Referer","http://"+Util.ips[0]+"/xs_main.aspx?xh="+Util.getId())
                        .url("http://"+Util.ips[0]+"/xskbcx.aspx?xh="+Util.getId()+"&xm="+Util.getName()+"&gnmkdm=N121603")
                        .build();
                try (Response response = Util.getClient(0).newCall(request).execute()) {
                    String resp = response.body().string();
                    Document doc = Jsoup.parse(resp);
                    Elements e =  doc.select("td[rowspan=\"2\"]");
                    for (Element element : e) {
//                        System.out.println(element.text());
                        String [] s =  element.text().split(" ");
                        F__kClass f = new F__kClass();
                        f.setName(s[0]+" | "+s[1]);
                        Util.list.add(f);
                    }
                    Message m = new Message();
                    m.what = 0x01;//完成
                    handler.sendMessage(m);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        courses.setAdapter(selectAdapter);
    }
}
