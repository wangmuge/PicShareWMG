package example.wangmuge.com.picsharewmg.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import example.wangmuge.com.picsharewmg.R;
import example.wangmuge.com.picsharewmg.activity.ImgDetailActivity;
import example.wangmuge.com.picsharewmg.http.MyVolley;
import example.wangmuge.com.picsharewmg.http.util;
import example.wangmuge.com.picsharewmg.widget.BitmapCache;
import example.wangmuge.com.picsharewmg.widget.CircleImageView;

/**
 * Created by wangmuge on 15/12/21.
 */
public class PicAdapter extends RecyclerView.Adapter<PicAdapter.MyViewHolder> {
   //这里要改成自己定义的viewholder


    private LayoutInflater mInflater;
    private Context mContext;
    private List<Map<String,Object>> mDatas;

    public PicAdapter(Context mContext, List<Map<String,Object>> mDatas) {
        this.mContext = mContext;
        this.mDatas = mDatas;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.pic_item2, parent, false);

        MyViewHolder viewHolder = new MyViewHolder(view);

        return viewHolder;
    }



    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {//改成自定义的viewholder

//        holder.setIsRecyclable(false);

        holder.tv_username.setText(mDatas.get(position).get("username").toString());


        holder.tv_text.setText(mDatas.get(position).get("text").toString());


        final String picname=mDatas.get(position).get("pic").toString();
        final String sid=mDatas.get(position).get("sid").toString();

       // String url="http://192.168.56.1:8080/PicShareWMG/showPic.action";
        String url=util.server_showPic+"pic="+picname;

        ImageLoader loader =new ImageLoader(MyVolley.getHttpQueues(),new BitmapCache());
        ImageLoader.ImageListener listener=ImageLoader.getImageListener(holder.iv_pic,R.drawable.bg_loading,0);
        loader.get(url, listener,300,300);


        String picheader=mDatas.get(position).get("header").toString();

        String url2=util.server_showPic+"pic="+picheader;

        ImageLoader loader2 =new ImageLoader(MyVolley.getHttpQueues(),new BitmapCache());
        ImageLoader.ImageListener listener2=ImageLoader.getImageListener(holder.iv_header, R.drawable.bg_loading, 0);
        loader2.get(url2, listener2);

       holder.ib_like.setText(mDatas.get(position).get("like").toString());
        final String num = mDatas.get(position).get("like").toString();
        final int sum= Integer.parseInt(num)+1;
        final String sum2= String.valueOf(sum);



        holder.iv_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                Bundle b = new Bundle();
                b.putString("imgname", picname);
                intent.putExtras(b);
                intent.setClass(mContext, ImgDetailActivity.class);
                mContext.startActivity(intent);
            }
        });


        holder.ib_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("like","like");

                String url = util.server_updatelike + "sid=" + sid;

                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.i("json", error.toString());
                    }

                });

                request.setTag("like");
                MyVolley.getHttpQueues().add(request);
                holder.ib_like.setText(sum2);
                holder.ib_like.setClickable(false);


            }
        });






//        SpringSystem springSystem = SpringSystem.create();
//      final  Spring spring = springSystem.createSpring();
//        spring.addListener(new SimpleSpringListener() {
//
//            @Override
//            public void onSpringUpdate(Spring spring) {
//                // You can observe the updates in the spring
//                // state by asking its current value in onSpringUpdate.
//                float value = (float) spring.getCurrentValue();
//                float scale = 1f - (value * 0.5f);
//                holder.iv_pic.setScaleX(scale);
//                holder.iv_pic.setScaleY(scale);
//            }
//        });
//        holder.iv_pic.setOnTouchListener(new View.OnTouchListener() {
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//
//                int key = event.getAction();
//
//                switch (key) {
//
//                    case MotionEvent.ACTION_DOWN:
//                        spring.setEndValue(-0.4);
//                        break;
//
//                    case MotionEvent.ACTION_UP:
//                    case MotionEvent.ACTION_CANCEL:
//                        spring.setEndValue(0.0);
//                        break;
//
//                    default:
//                        break;
//                }
//                return true;
//            }
//        });
    }



    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_username;//item里所有的控件
        TextView tv_text;
        CircleImageView iv_pic;
        ImageView iv_header;
        Button ib_like;
        public MyViewHolder(View itemView) {
            super(itemView);
            tv_username = (TextView) itemView.findViewById(R.id.tv_name);
            tv_text= (TextView) itemView.findViewById(R.id.tv_text);

            iv_header= (ImageView) itemView.findViewById(R.id.iv_header);
            iv_pic= (CircleImageView) itemView.findViewById(R.id.iv);
            ib_like = (Button) itemView.findViewById(R.id.bt_like);



        }
    }
}
