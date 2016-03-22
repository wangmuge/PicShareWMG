package example.wangmuge.com.picsharewmg.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.hanks.htextview.HTextView;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import example.wangmuge.com.picsharewmg.R;
import example.wangmuge.com.picsharewmg.http.MyVolley;
import example.wangmuge.com.picsharewmg.http.util;
import example.wangmuge.com.picsharewmg.model.User;

public class LoginActivity extends Activity {


    @Bind(R.id.username)
    EditText username;
    @Bind(R.id.password)
    EditText password;
    @Bind(R.id.login)
    Button login;
    @Bind(R.id.textView)
    TextView textView;
    @Bind(R.id.tv_title)
    HTextView tvTitle;
    @Bind(R.id.avloadingIndicatorView)
    AVLoadingIndicatorView avloadingIndicatorView;

    private SharedPreferences perferences;
    private SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);


        perferences = getSharedPreferences("user", 0);
        editor = perferences.edit();
//        tvTitle.setAnimateType(HTextViewType.ANVIL);
//        tvTitle.animateText("Welcome"); // animate


        initView();

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

    private void initView() {

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                avloadingIndicatorView.setVisibility(View.VISIBLE);
                TimerTask task = new TimerTask(){

                    public void run(){


                        Volley_Post();


                    }

                };

                Timer timer = new Timer();

                timer.schedule(task, 2000);

            }
        });

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ii = new Intent();
                ii.setClass(LoginActivity.this, RegisterActivity.class);
                startActivity(ii);
            }
        });
    }

    private void Volley_Post() {


        String name = username.getText().toString().trim();
        String psw = password.getText().toString().trim();

        String url = util.server_login + "username=" + name + "&" + "password=" + psw;


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject s) {

//                tvTitle.setAnimateType(HTextViewType.ANVIL);
//                tvTitle.animateText("Success"); // animate

                User user = getUserFromJSON(s.optJSONArray("result").optJSONObject(0));


                editor.putInt("userid", user.getId());
                editor.putString("username", user.getUsername());
                editor.putString("password", user.getPassword());
                editor.putString("info", user.getInfo());
                editor.putString("header", user.getHeader());

                editor.commit();


                Log.d("TAG", s.toString());

                Intent intent = getIntent();

                intent.setClass(LoginActivity.this, MainActivity.class);
                LoginActivity.this.startActivity(intent);
                Toast.makeText(LoginActivity.this, "恭喜"
                        + "登录成功", Toast.LENGTH_SHORT).show();

                avloadingIndicatorView.setVisibility(View.INVISIBLE);
                finish();




            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                avloadingIndicatorView.setVisibility(View.INVISIBLE);
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        LoginActivity.this);
                builder.setMessage("用户名或密码错误");
                builder.setCancelable(true);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        Toast.makeText(LoginActivity.this, "请重新输入正确的用户名或密码",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }


        );
        request.setTag("Login");
        MyVolley.getHttpQueues().add(request);


    }

    private User getUserFromJSON(
            JSONObject object) {
        User user = new User();
        if (object != null && !object.equals("")) {

            user.setId(object.optInt("id"));
            user.setUsername(object.optString("username"));
            user.setPassword(object.optString("password"));
            user.setInfo(object.optString("info"));
            user.setHeader(object.optString("header"));
        }
        return user;
    }


}
//        username.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i = new Intent();
//                llogin.setText(i.getExtras().getString("name"));
//                password.setText(i.getExtras().getString("psw"));
//
//            }
//        });