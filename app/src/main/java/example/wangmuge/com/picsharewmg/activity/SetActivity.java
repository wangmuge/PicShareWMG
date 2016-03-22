package example.wangmuge.com.picsharewmg.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import example.wangmuge.com.picsharewmg.R;
import example.wangmuge.com.picsharewmg.http.MyVolley;
import example.wangmuge.com.picsharewmg.http.util;
import example.wangmuge.com.picsharewmg.widget.BitmapCache;
import example.wangmuge.com.picsharewmg.widget.CircleImageView;

import static example.wangmuge.com.picsharewmg.activity.UploadActivity.getPicFromBytes;
import static example.wangmuge.com.picsharewmg.activity.UploadActivity.readStream;

public class SetActivity extends AppCompatActivity {

    @Bind(R.id.set_iv_header)
    CircleImageView setIvHeader;
    @Bind(R.id.set_header)
    TextView setHeader;
    @Bind(R.id.set_username)
    TextView setUsername;
    @Bind(R.id.set_password)
    TextView setPassword;
    @Bind(R.id.set_info)
    TextView setInfo;
    @Bind(R.id.avloadingIndicatorView_set)
    AVLoadingIndicatorView avloadingIndicatorViewSet;
    private SharedPreferences perfereneces;
    private SharedPreferences.Editor editor;

    private static final int CHOOSE_PICTURE = 1;
    Bitmap myBitmap;
    private byte[] mContent;

    int userId;
    String header;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);
        ButterKnife.bind(this);
        perfereneces = getSharedPreferences("user", 0);

        header = perfereneces.getString("header", "");
        String url = util.server_showPic + "pic=" + header;

        ImageLoader loader = new ImageLoader(MyVolley.getHttpQueues(), new BitmapCache());
        ImageLoader.ImageListener listener = ImageLoader.getImageListener(setIvHeader, 0, 0);
        loader.get(url, listener);
        init();

    }

    private void init() {

        setUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                Bundle b = new Bundle();
                b.putInt("i", 1);
                i.putExtras(b);
                i.setClass(SetActivity.this, UpdateActivity.class);
                startActivity(i);

            }
        });
        setPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                Bundle b = new Bundle();
                b.putInt("i", 2);
                i.putExtras(b);
                i.setClass(SetActivity.this, UpdateActivity.class);
                startActivity(i);
            }
        });
        setInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                Bundle b = new Bundle();
                b.putInt("i", 3);
                i.putExtras(b);
                i.setClass(SetActivity.this, UpdateActivity.class);
                startActivity(i);
            }
        });
        setHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
                openAlbumIntent.setType("image/*");
                startActivityForResult(openAlbumIntent, CHOOSE_PICTURE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {

                case CHOOSE_PICTURE:

                    try {
                        ContentResolver resolver = getContentResolver();
                        // 获得图片的uri
                        Uri originalUri = data.getData();
                        // 将图片内容解析成字节数组
                        mContent = readStream(resolver.openInputStream(Uri
                                .parse(originalUri.toString())));
                        // 将字节数组转换为ImageView可调用的Bitmap对象
                        myBitmap = getPicFromBytes(mContent, null);
                        // //把得到的图片绑定在控件上显示
                        avloadingIndicatorViewSet.setVisibility(View.VISIBLE);
                        postFile();
                        setIvHeader.setImageBitmap(myBitmap);

                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }

                    break;


            }
        }
    }

    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "'IMG'_yyyyMMdd_HHmmss");
        return dateFormat.format(date) + ".jpg";
    }

    public void postFile() throws Exception {


        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        //将bitmap一字节流输出 Bitmap.CompressFormat.PNG 压缩格式，100：压缩率，baos：字节流
        myBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        baos.close();
        byte[] buffer = baos.toByteArray();
        System.out.println("图片的大小：" + buffer.length);

        //将图片的字节流数据加密成base64字符输出
        String photo = Base64.encodeToString(buffer, 0, buffer.length, Base64.DEFAULT);

        perfereneces = getSharedPreferences("user", 0);

        editor = perfereneces.edit();
        userId = perfereneces.getInt("userid", 0);


        RequestParams params = new RequestParams();
        params.put("photo", photo);


        String url = util.server_header + "id=" + userId + "&header=" + getPhotoFileName();

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                editor.putString("header", getPhotoFileName());
                editor.commit();
                Toast.makeText(SetActivity.this, "上传头像成功", Toast.LENGTH_SHORT).show();
               // avloadingIndicatorViewSet.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                Toast.makeText(SetActivity.this, "上传头像失败", Toast.LENGTH_SHORT).show();
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
