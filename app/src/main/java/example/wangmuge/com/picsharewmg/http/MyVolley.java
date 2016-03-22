package example.wangmuge.com.picsharewmg.http;

import android.app.Application;

import com.android.volley.RequestQueue;

/**
 * Created by wangmuge on 15/12/23.
 */
public class MyVolley extends Application {

    public static RequestQueue queues;


    public MyVolley() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        queues= com.android.volley.toolbox.Volley.newRequestQueue(getApplicationContext());

    }


    public static RequestQueue getHttpQueues() {


        return queues;
    }


}
