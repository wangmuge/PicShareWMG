package example.wangmuge.com.picsharewmg.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
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
import example.wangmuge.com.picsharewmg.model.User;

public class LoginActivity extends Activity  {


    @Bind(R.id.username)
    EditText username;
    @Bind(R.id.password)
    EditText password;
    @Bind(R.id.login)
    Button login;
    @Bind(R.id.textView)
    TextView textView;
//    @Bind(R.id.tv_title)
//    HTextView tvTitle;
    @Bind(R.id.avloadingIndicatorView)
    AVLoadingIndicatorView avloadingIndicatorView;
    @Bind(R.id.tv_clear_name)
    ImageButton tvClearName;
    @Bind(R.id.tv_clear_pwd)
    ImageButton tvClearPwd;
    private SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
                WindowManager.LayoutParams. FLAG_FULLSCREEN);
        SharedPreferences perferences = getSharedPreferences("user", 0);
        editor = perferences.edit();
        if (perferences.getBoolean("firststart", true)) {
            editor = perferences.edit();
            //将登录标志位设置为false，下次登录时不在显示首次登录界面
            editor.putBoolean("firststart", false);
            editor.apply();//
            Intent intent = new Intent();
            intent.setClass(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }

        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        initView();
//      tvTitle.setAnimateType(HTextViewType.ANVIL);
//      tvTitle.animateText("Welcome"); // animate
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
                TimerTask task = new TimerTask() {

                    public void run() {
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
        EditWatcher();
        tvClearName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                username.setText("");
                tvClearName.setVisibility(View.GONE);
            }
        });
        tvClearPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                password.setText("");
                tvClearPwd.setVisibility(View.GONE);
            }
        });

    }

    private void EditWatcher() {
        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence != null ){
                    tvClearName.setVisibility(View.VISIBLE);
                }else{
                    tvClearName.setVisibility(View.GONE);
                }
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence != null){
                    tvClearPwd.setVisibility(View.VISIBLE);
                }else{
                    tvClearPwd.setVisibility(View.GONE);
                }
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
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
