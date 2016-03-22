package example.wangmuge.com.picsharewmg.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
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

public class UpdateActivity extends AppCompatActivity {

    int i;
    @Bind(R.id.update_username)
    EditText updateUsername;
    @Bind(R.id.update_password)
    EditText updatePassword;
    @Bind(R.id.update_info)
    EditText updateInfo;
    @Bind(R.id.update)
    Button update;
    @Bind(R.id.avloadingIndicatorView_update)
    AVLoadingIndicatorView avloadingIndicatorViewUpdate;
    private SharedPreferences perfereneces;
    private SharedPreferences.Editor editor;
    int userId;
    String username;
    String password;
    String info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        ButterKnife.bind(this);
        Bundle b = getIntent().getExtras();
        i = b.getInt("i");
        perfereneces = getSharedPreferences("user", 0);
        editor = perfereneces.edit();
        userId = perfereneces.getInt("userid", 0);
        username = perfereneces.getString("username", "");
        password = perfereneces.getString("password", "");
        info = perfereneces.getString("info", "");

        init();
    }

    private void init() {
        updateUsername.setText(username);
        updatePassword.setText(password);
        updateInfo.setText(info);
        switch (i) {
            case 1:
                updateUsername.setFocusable(true);
                updateUsername.setFocusableInTouchMode(true);
                updateUsername.requestFocus();
                updateUsername.requestFocusFromTouch();
                break;
            case 2:
                updatePassword.setFocusable(true);
                updatePassword.setFocusableInTouchMode(true);
                updatePassword.requestFocus();
                updatePassword.requestFocusFromTouch();
                break;
            case 3:
                updateInfo.setFocusable(true);
                updateInfo.setFocusableInTouchMode(true);
                updateInfo.requestFocus();
                updateInfo.requestFocusFromTouch();
                break;

        }
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                avloadingIndicatorViewUpdate.setVisibility(View.VISIBLE);
                TimerTask task = new TimerTask() {

                    public void run() {


                        Volley_Post();


                    }

                };

                Timer timer = new Timer();

                timer.schedule(task, 2000);
            }
        });

    }

    private void Volley_Post() {


        final String name = updateUsername.getText().toString().trim();
        final String psw = updatePassword.getText().toString().trim();
        final String info = updateInfo.getText().toString().trim();


        String url = util.server_update + "name=" + name + "&id=" + userId + "&pwd=" + psw + "&info=" + info;


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject s) {


                editor.putString("username", name);
                editor.putString("password", psw);
                editor.putString("info", info);
                editor.commit();

                Intent intent = getIntent();

                intent.setClass(UpdateActivity.this, SetActivity.class);

                startActivity(intent);
                Toast.makeText(UpdateActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                avloadingIndicatorViewUpdate.setVisibility(View.INVISIBLE);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                Toast.makeText(UpdateActivity.this,
                        "修改失败", Toast.LENGTH_SHORT).show();


            }
        }


        );
        request.setTag("update");
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
