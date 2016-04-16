package example.wangmuge.com.picsharewmg.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import example.wangmuge.com.picsharewmg.R;
import example.wangmuge.com.picsharewmg.http.MyVolley;
import example.wangmuge.com.picsharewmg.http.util;

public class RegisterActivity extends Activity {

    @Bind(R.id.reg_username)
    EditText regUsername;
    @Bind(R.id.reg_password)
    EditText regPassword;
    @Bind(R.id.reg_info)
    EditText regInfo;
    @Bind(R.id.reg)
    Button reg;
    @Bind(R.id.avloadingIndicatorView_reg)
    AVLoadingIndicatorView avloadingIndicatorViewReg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
                WindowManager.LayoutParams. FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register);

        ButterKnife.bind(this);
        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                avloadingIndicatorViewReg.setVisibility(View.VISIBLE);
                TimerTask task = new TimerTask(){

                    public void run(){


                        Volley_Post();


                    }

                };

                Timer timer = new Timer();

                timer.schedule(task, 2000);
            }
        });

    }

    private void Volley_Post() {


        final String name = regUsername.getText().toString().trim();
        final String psw = regPassword.getText().toString().trim();
        String info = regInfo.getText().toString().trim();

        String url = util.server_register + "username=" + name + "&" + "password=" + psw + "&info=" + info;


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject s) {


                Intent intent = getIntent();
//                Bundle b = new Bundle();
//                b.putString("name",name);
//                b.putString("psw",psw);
//                intent.putExtras(b);
                intent.setClass(RegisterActivity.this, LoginActivity.class);

                RegisterActivity.this.startActivity(intent);
                Toast.makeText(RegisterActivity.this, "恭喜"
                        + "注册成功", Toast.LENGTH_SHORT).show();
                avloadingIndicatorViewReg.setVisibility(View.INVISIBLE);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                Toast.makeText(RegisterActivity.this,
                        "注册失败", Toast.LENGTH_SHORT).show();
                avloadingIndicatorViewReg.setVisibility(View.INVISIBLE);


            }
        }


        );
        request.setTag("reg");
        MyVolley.getHttpQueues().add(request);


    }
    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        switch (level) {
            case TRIM_MEMORY_UI_HIDDEN:
                // 进行资源释放操作
                break;
        }
    }
}
