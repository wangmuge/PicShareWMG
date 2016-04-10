package example.wangmuge.com.picsharewmg.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import example.wangmuge.com.picsharewmg.R;
import example.wangmuge.com.picsharewmg.activity.ImgDetailActivity;
import example.wangmuge.com.picsharewmg.http.MyVolley;
import example.wangmuge.com.picsharewmg.http.util;
import example.wangmuge.com.picsharewmg.widget.BitmapCache;

/**
 * Created by wangmuge on 15/12/21.
 */
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder> implements View.OnClickListener {
    //这里要改成自己定义的viewholder


    private LayoutInflater mInflater;
    private Context mContext;
    private List<Map<String,Object>> mDatas;
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;
    int sid;
    private String id;
    private String picname;


    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(View view , List<Map<String,Object>> data);
    }
    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
    public UserAdapter(Context mContext, List<Map<String,Object>> mDatas) {
        this.mContext = mContext;
        this.mDatas = mDatas;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.me_item, parent, false);

        MyViewHolder viewHolder = new MyViewHolder(view);

        return viewHolder;
    }



    @Override
    public void onBindViewHolder(final MyViewHolder holder,int position) {//改成自定义的viewholder

        holder.itemView.setTag(mDatas.get(position));
        holder.ib_del.setTag(position);
        holder.tv_text.setText(mDatas.get(position).get("text").toString());
//        holder.ib_del.setTag(mDatas.get(position));
//        holder.itemView.setTag(position);
//         id= mDatas.get(position).get("sid").toString();
        picname=mDatas.get(position).get("pic").toString();
        String url=util.server_showPic+"pic="+picname;
        ImageLoader loader =new ImageLoader(MyVolley.getHttpQueues(),new BitmapCache());
        ImageLoader.ImageListener listener=ImageLoader.getImageListener(holder.iv_img,0,0);
        loader.get(url, listener,600,600);


        holder.iv_img.setOnClickListener(new View.OnClickListener() {
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

        holder.ib_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int tag = (int) view.getTag();
                new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("确定删除吗?")
                        .setContentText("删除后将看不到这张美美的照片喔!")
                        .setConfirmText("好！残忍删了")
                        .setCancelText("不！只是手贱")

                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog
                                        .setTitleText("已删除！")
                                        .setContentText("Bye ~ 过去")
                                        .setConfirmText("OK")
                                        .showCancelButton(false)
                                        .setConfirmClickListener(null)
                                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);

                String ids = mDatas.get(tag).get("id").toString();
                String url = util.server_del + "id=" + ids;
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(mContext,
                                 "删除成功", Toast.LENGTH_SHORT).show();
                    }


                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.i("json", error.toString());
                    }

                });
                request.setTag("del");
                MyVolley.getHttpQueues().add(request);
                            }
                        })
                        .show();


            }
        });

    }



    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    @Override
    public void onClick(View view) {

        mOnItemClickListener.onItemClick(view,(List<Map<String,Object>>)view.getTag());
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_text;

        ImageView iv_img;

        ImageButton ib_del;
        public MyViewHolder(View itemView) {
            super(itemView);

            tv_text= (TextView) itemView.findViewById(R.id.tv_me_title);

            iv_img= (ImageView) itemView.findViewById(R.id.iv_me_img);

            ib_del = (ImageButton) itemView.findViewById(R.id.ib_del);




        }
    }
}
