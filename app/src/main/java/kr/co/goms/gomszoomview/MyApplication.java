package kr.co.goms.gomszoomview;

import android.app.Application;

import com.glidebitmappool.GlideBitmapPool;

public class MyApplication extends Application {
    private static MyApplication sInstance;

    @Override
    public void onCreate() {

        sInstance = this;
        try {
            Class.forName("android.os.AsyncTask");
        } catch (ClassNotFoundException e) {}
        super.onCreate();

        GlideBitmapPool.initialize(10 * 1024 * 1024); // 10mb max memory size
    }

    // Singleton code
    public static MyApplication getInstance() {
        if (null == sInstance) {
            sInstance = new MyApplication();
        }
        return sInstance;
    }


}
