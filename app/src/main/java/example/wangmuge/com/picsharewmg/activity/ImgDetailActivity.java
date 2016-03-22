package example.wangmuge.com.picsharewmg.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.graphics.Bitmap.CompressFormat;

import com.android.volley.toolbox.ImageLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import example.wangmuge.com.picsharewmg.R;
import example.wangmuge.com.picsharewmg.http.MyVolley;
import example.wangmuge.com.picsharewmg.http.util;
import example.wangmuge.com.picsharewmg.widget.BitmapCache;

public class ImgDetailActivity extends AppCompatActivity {

    @Bind(R.id.iv_detail)
    ImageView ivDetail;
    @Bind(R.id.bt_share)
    Button btShare;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_img_detail);
        ButterKnife.bind(this);

        Intent i = getIntent();

        String picname = i.getExtras().getString("imgname");

        String url = util.server_showPic + "pic=" + picname;

        ImageLoader loader = new ImageLoader(MyVolley.getHttpQueues(), new BitmapCache());
        ImageLoader.ImageListener listener = ImageLoader.getImageListener(ivDetail, R.drawable.bg_loading, 0);
        loader.get(url, listener);

        ivDetail.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {


                ivDetail.setDrawingCacheEnabled(true);
                bitmap = Bitmap.createBitmap(ivDetail.getDrawingCache());
                MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "", "");

                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + Environment.getExternalStorageDirectory())));

                Toast.makeText(ImgDetailActivity.this, "保存图片成功", Toast.LENGTH_LONG).show();
                return true;
            }
        });

        btShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ivDetail.setDrawingCacheEnabled(true);
                bitmap = Bitmap.createBitmap(ivDetail.getDrawingCache());

                File appDir = new File(Environment.getExternalStorageDirectory(), "wangmuge");
                if (!appDir.exists()) {
                    appDir.mkdir();
                }
                String fileName = System.currentTimeMillis() + ".jpg";
                File file = new File(appDir, fileName);
                try {
                    FileOutputStream fos = new FileOutputStream(file);
                    bitmap.compress(CompressFormat.JPEG, 100, fos);
                    fos.flush();
                    fos.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                ShareSDK.initSDK(ImgDetailActivity.this);
                OnekeyShare oks = new OnekeyShare();
                //关闭sso授权
                oks.disableSSOWhenAuthorize();

                // title标题：微信、QQ（新浪微博不需要标题）
                oks.setTitle("我是分享标题");  //最多30个字符

                // text是分享文本：所有平台都需要这个字段
                oks.setText("说点什么吧！~");  //最多40个字符


                // imagePath是图片的本地路径：除Linked-In以外的平台都支持此参数
                oks.setImagePath(Environment.getExternalStorageDirectory() + "/wangmuge/"+fileName);//确保SDcard下面存在此张图片

                //网络图片的url：所有平台
                //    oks.setImageUrl("http://7sby7r.com1.z0.glb.clouddn.com/CYSJ_02.jpg");//网络图片rul


                // 启动分享GUI
                oks.show(ImgDetailActivity.this);

            }
        });


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
