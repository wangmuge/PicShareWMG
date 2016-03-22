package example.wangmuge.com.picsharewmg.widget;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.toolbox.ImageLoader;

/**
 * Created by wangmuge on 15/12/25.
 */
public class BitmapCache implements ImageLoader.ImageCache {

    private LruCache<String,Bitmap> cache;
    public int max=10*1024*10;

    public BitmapCache() {
        cache=new LruCache<String,Bitmap>(max){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return super.sizeOf(key, value);
            }
        };
    }

    @Override
    public Bitmap getBitmap(String url) {
        return cache.get(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {

        cache.put(url,bitmap);
    }
}
