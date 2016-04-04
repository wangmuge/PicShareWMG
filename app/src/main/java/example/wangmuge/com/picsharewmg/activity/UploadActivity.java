package example.wangmuge.com.picsharewmg.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import example.wangmuge.com.picsharewmg.R;
import example.wangmuge.com.picsharewmg.http.util;

public class UploadActivity extends Activity {

    @Bind(R.id.et_text)
    EditText etText;
    @Bind(R.id.ib_img)
    ImageButton ibImg;
    @Bind(R.id.upload)
    Button upload;
    @Bind(R.id.lv_img)
    ImageView lvImg;


    private static final int CHOOSE_PICTURE = 1;
    private static final int TAKE_PICTURE = 0;
    @Bind(R.id.avloadingIndicatorView_upload)
    AVLoadingIndicatorView avloadingIndicatorViewUpload;

    private File mPhotoFile;
    private String mPhotoPath;

    int userId;
    String title;

    Bitmap myBitmap;
    private byte[] mContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        ButterKnife.bind(this);
        initview();
    }

    private void initview() {


        ibImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
                openAlbumIntent.setType("image/*");
                startActivityForResult(openAlbumIntent, CHOOSE_PICTURE);


            }
        });

        lvImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {


                try {
                    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                    mPhotoPath = "mnt/sdcard/DCIM/Camera/" + getPhotoFileName();
                    mPhotoFile = new File(mPhotoPath);
                    if (!mPhotoFile.exists()) {
                        mPhotoFile.createNewFile();
                    }
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(mPhotoFile));
                    startActivityForResult(intent, TAKE_PICTURE);
                } catch (Exception e) {
                }

            }
        });

        upload.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {


                avloadingIndicatorViewUpload.setVisibility(View.VISIBLE);

                try {
                    postFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "'IMG'_yyyyMMdd_HHmmss");
        return dateFormat.format(date) + ".jpg";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case TAKE_PICTURE:

                    Bitmap bitmap = BitmapFactory.decodeFile(mPhotoPath, null);

                    myBitmap = bitmap;
                    lvImg.setImageBitmap(bitmap);
//                    try {
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }

                    ibImg.setVisibility(View.INVISIBLE);


                    break;
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
                        lvImg.setImageBitmap(myBitmap);
                        // postFile();
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    ibImg.setVisibility(View.INVISIBLE);
                    break;


            }
        }
    }

    public static Bitmap getPicFromBytes(byte[] bytes,
                                         BitmapFactory.Options opts) {
        if (bytes != null)
            if (opts != null)
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length,
                        opts);
            else
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return null;
    }

    public static byte[] readStream(InputStream inStream) throws Exception {
        byte[] buffer = new byte[1024];
        int len = -1;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        byte[] data = outStream.toByteArray();
        outStream.close();
        inStream.close();
        return data;

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


        SharedPreferences perfereneces = getSharedPreferences("user", 0);
        userId = perfereneces.getInt("userid", 0);
        title = etText.getText().toString().trim();

        RequestParams params = new RequestParams();
        params.put("photo", photo);


        String url = util.server_upload + "id=" + userId + "&pic=" + getPhotoFileName() + "&text=" + title;

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//                Intent intent = getIntent();
//
//                intent.setClass(UploadActivity.this, MainActivity.class);
//
//                startActivity(intent);
                Toast.makeText(UploadActivity.this, "发表成功", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                Toast.makeText(UploadActivity.this, "发表失败", Toast.LENGTH_SHORT).show();
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
//
//        String path = mPhotoPath;
//        File file = new File(path);
//        if (file.exists() && file.length() > 0) {
//            AsyncHttpClient client = new AsyncHttpClient();
//            RequestParams params = new RequestParams();
//            params.put("img", file);
//            client.post("http://172.16.154.41:8080/PicShareWMG/uploadimg.action", params, new AsyncHttpResponseHandler() {
//
//
//                @Override
//                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//
//
//                }
//
//                @Override
//                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable eerror) {
//
//                    Log.i("eerror",eerror.toString());
//                }
//            });
//
//        }

}