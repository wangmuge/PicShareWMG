package example.wangmuge.com.picsharewmg.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import example.wangmuge.com.picsharewmg.R;
import example.wangmuge.com.picsharewmg.adapter.UserAdapter;
import example.wangmuge.com.picsharewmg.http.MyVolley;
import example.wangmuge.com.picsharewmg.http.util;
import example.wangmuge.com.picsharewmg.widget.BitmapCache;
import example.wangmuge.com.picsharewmg.widget.CircleImageView;

public class MeActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    @Bind(R.id.iv_me)
    CircleImageView ivMe;
    @Bind(R.id.tv_me_name)
    TextView tvMeName;
    @Bind(R.id.recyclerView_me)
    RecyclerView recyclerViewMe;
    @Bind(R.id.tv_me_info)
    TextView tvMeInfo;
    @Bind(R.id.swiperefreshme)
    SwipeRefreshLayout swiperefreshme;
    private UserAdapter mAdpater;
    private List<Map<String, Object>> mDatas;
    private boolean dialog = true;
    String userName;
    String userHeader;
    String userInfo;

    private SharedPreferences perfereneces;
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
                WindowManager.LayoutParams. FLAG_FULLSCREEN);
        setContentView(R.layout.activity_me);
        ButterKnife.bind(this);
        mDatas = new ArrayList<Map<String, Object>>();
        initData();
        initView();
    }

    private void initView() {
        swiperefreshme.setOnRefreshListener(this);

        swiperefreshme.setColorSchemeResources( android.R.color.holo_green_light,android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        mAdpater = new UserAdapter(this, mDatas);
        recyclerViewMe.setAdapter(mAdpater);
        recyclerViewMe.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewMe.setLayoutManager(linearLayoutManager);


        swiperefreshme.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                initData2();

                showTips();
                mDatas.clear();
                initData();
                mAdpater.notifyDataSetChanged();
                swiperefreshme.setRefreshing(false);
//                showTips();

            }
        });
    }

    private void showTips() {

        if(dialog == true) {
            if (mDatas.size() == 0) {
                new SweetAlertDialog(MeActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("你还没添加心情噢！")
                        .setContentText("去发表说说去")
                        .setCancelText("我再逛逛。")
                        .setConfirmText("Let's Go!")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                Intent intent = new Intent();
                                intent.setClass(MeActivity.this, UploadActivity.class);
                                startActivity(intent);
                                sweetAlertDialog.dismiss();

                            }
                        })
                        .show();
                dialog = false;
            } else {

            }
        }else{
            return;
        }
    }


//    private void initData2() {
//        mDatas.clear();
//        perfereneces = getSharedPreferences("user", 0);
//        userId = perfereneces.getInt("userid", 0);
//
//        String url = util.server_showUser + "id=" + userId;
//
//
//        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//
//                try {
//                    response = response.getJSONObject("result");
//                    Log.i("json", "json = " + response);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//
//                try {
//
//
//                    JSONArray array = response.getJSONArray("result");
//
//                    for (int i = 0; i < array.length(); i++) {
//                        Map<String, Object> map = new HashMap<String, Object>();
//
//                        JSONObject jsons = array.getJSONObject(i);
//                        map.put("pic", jsons.optString("pic"));
//                        map.put("text", jsons.optString("text"));
//                        map.put("sid", jsons.optInt("sid"));
//                        mDatas.add(map);
//                    }
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//                Log.i("json", error.toString());
//            }
//
//        });
//
//        request.setTag("showuser2");
//        MyVolley.getHttpQueues().add(request);
//    }


    private void initData() {

        perfereneces = getSharedPreferences("user", 0);
        userId = perfereneces.getInt("userid", 0);

        String url = util.server_showUser + "id=" + userId;


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    response = response.getJSONObject("result");
                    Log.i("json", "json = " + response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                try {


                    JSONArray array = response.getJSONArray("result");

                    JSONObject json = array.getJSONObject(0);
                    userName = json.optString("username");
                    userHeader = json.optString("header");
                    userInfo = json.optString("info");
                    tvMeName.setText(userName);
                    tvMeInfo.setText(userInfo);
                    String url = util.server_showPic + "pic=" + userHeader;

                    ImageLoader loader = new ImageLoader(MyVolley.getHttpQueues(), new BitmapCache());
                    ImageLoader.ImageListener listener = ImageLoader.getImageListener(ivMe, 0, 0);
                    loader.get(url, listener);


                    //    response.getJSONObject("result");
                    for (int i = 0; i < array.length(); i++) {
                        Map<String, Object> map = new HashMap<String, Object>();

                        JSONObject js = array.getJSONObject(i);
                        map.put("pic", js.optString("pic"));
                        map.put("text", js.optString("text"));
                        map.put("id", js.optInt("sid"));
                        mDatas.add(map);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
     Log.i("json", error.toString());
            }

        });

        request.setTag("showuser");
        MyVolley.getHttpQueues().add(request);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mDatas.size() > 0){
            mAdpater.notifyDataSetChanged();
        }
//        showTips();
    }

    @Override
    public void onRefresh() {

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
