package example.wangmuge.com.picsharewmg.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
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
import example.wangmuge.com.picsharewmg.Nav.ArcMenu;
import example.wangmuge.com.picsharewmg.Nav.ArcMenu.OnMenuItemClickListener;
import example.wangmuge.com.picsharewmg.R;
import example.wangmuge.com.picsharewmg.adapter.PicAdapter;
import example.wangmuge.com.picsharewmg.http.MyVolley;
import example.wangmuge.com.picsharewmg.http.util;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.swiperefresh)
    SwipeRefreshLayout swiperefresh;

//    private SharedPreferences perferences;


    private PicAdapter mAdpater;
    private List<Map<String, Object>> mDatas;
    boolean isSwitch = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatas = new ArrayList<Map<String, Object>>();
        initData();
        ButterKnife.bind(this);


        ArcMenu view = (ArcMenu) findViewById(R.id.arcmenu);

        swiperefresh.setOnRefreshListener(this);

        swiperefresh.setColorSchemeResources( android.R.color.holo_green_light,
                android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);



        view.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            @Override
            public void onClick(View view, int pos) {
                // TODO Auto-generated method stub
                String tag = (String) view.getTag();
                Toast.makeText(MainActivity.this, tag, Toast.LENGTH_SHORT).show();
                if (pos == 1) {
//                    perferences = getSharedPreferences("user", 0);
//                    perferences.edit().clear().commit();
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else if (pos == 2) {
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, UploadActivity.class);
                    startActivity(intent);

                } else if (pos == 3) {
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, MeActivity.class);
                    startActivity(intent);
                } else if (pos == 4) {
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, SetActivity.class);
                    startActivity(intent);
                }
                else if (pos == 5) {
                    if(isSwitch == true) {
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false);
                        recyclerView.setLayoutManager(linearLayoutManager);
                        isSwitch = false;
                    }else {
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);
                        recyclerView.setLayoutManager(linearLayoutManager);
                    }
                }
            }
        });



        // initViews();
        mAdpater = new PicAdapter(this, mDatas);
        recyclerView.setAdapter(mAdpater);

        recyclerView.setHasFixedSize(true);
        //设置recycleview布局管理
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        //设置recycleview分割线
        swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initData2();
               // mAdpater.notifyDataSetChanged();
                swiperefresh.setRefreshing(false);
            }
        });


    }
    public void onRefresh() {

            }

    private void initData2() {
        mDatas.clear();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, util.server_showList, new Response.Listener<JSONObject>() {
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


                    for (int i = 0; i < array.length(); i++) {
                        Map<String, Object> map = new HashMap<String, Object>();
                        JSONObject json = array.getJSONObject(i);


                        map.put("username", json.optString("username"));
                        map.put("id", json.optInt("id"));
                        map.put("sid", json.optInt("sid"));
                        map.put("header", json.optString("header"));
                        map.put("pic", json.optString("pic"));
                        map.put("text", json.optString("text"));
                        map.put("like", json.optInt("like"));
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
        request.setTag("showlist2");
        MyVolley.getHttpQueues().add(request);


    }

    private void initData() {

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, util.server_showList, new Response.Listener<JSONObject>() {
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

                    //    response.getJSONObject("result");
                    for (int i = 0; i < array.length(); i++) {
                        Map<String, Object> map = new HashMap<String, Object>();
                        JSONObject json = array.getJSONObject(i);

                        map.put("username", json.optString("username"));
                        map.put("id", json.optInt("id"));
                        map.put("sid", json.optInt("sid"));
                        map.put("header", json.optString("header"));
                        map.put("pic", json.optString("pic"));
                        map.put("text", json.optString("text"));
                        map.put("like", json.optInt("like"));
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


        request.setTag("showlist");
        MyVolley.getHttpQueues().add(request);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
