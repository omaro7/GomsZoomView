package kr.co.goms.gomszoomview;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.glidebitmappool.GlideBitmapPool;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import kr.co.goms.gomszoomview.constant.AppConstant;
import kr.co.goms.gomszoomview.util.DisplayUtil;
import kr.co.goms.gomszoomview.util.GomsLog;
import kr.co.goms.gomszoomview.util.StringUtil;
import kr.co.goms.gomszoomview.view.GomsZoomView;

public class MainActivity extends AppCompatActivity  implements GomsZoomView.ZoomListener{
    private static final String TAG = MainActivity.class.getSimpleName();
    private GomsZoomView mGomsZoomView;

    public GomsZoomView.ZoomListener mZoomListener;

    private int mPermissionReadExternalStorage;
    private boolean isFirstPermission = true;
    private boolean isAskPermissionStorage = false;

    public static final int RequestPermissionCode = 1;
    public static final int RequestPermissionCodeSecond = 2;

    private int mScreenWidth = 0;

    int mTargetWidth = 1080;
    int mTargetHeight = 1440;
    Bitmap mBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GomsLog.mDebugLog = true;
        mZoomListener = this;

        /** Permission 관련 처리 */
        mPermissionReadExternalStorage = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);

        if (mPermissionReadExternalStorage != PackageManager.PERMISSION_GRANTED) {
            checkAndRequestPermissions();
        }

        mScreenWidth = getWindowManager().getDefaultDisplay().getWidth();

        mGomsZoomView = findViewById(R.id.iv_zoom);

        String imageFolder = Environment.getExternalStorageDirectory().getAbsolutePath() + "/StoreCamera/StoreCameraPhoto";
        String imageName = "storecamera_20170612164120_0.jpg";  //1440x1920
        //imageName = "storecamera_20170626174825_01.jpg";  //1440x1440
        //imageName = "780_1040.jpg";  //1440x1440
        String fileFullPath = imageFolder  + File.separator + imageName;


        Glide.with(this)
            .load(fileFullPath)
            .asBitmap()
            .into(new SimpleTarget<Bitmap>() {
                  @Override
                  public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                      // you can do something with loaded bitmap here
                      GomsLog.d(TAG, "bitmap.getWidth() : " + bitmap.getWidth());
                      GomsLog.d(TAG, "bitmap.getHeight() : " + bitmap.getHeight());

                      String aspectRadio = DisplayUtil.getAspectRatioFromWH(bitmap.getWidth(), bitmap.getHeight());
                      GomsLog.d(TAG, "aspectRadio : " + aspectRadio);

                      int first = StringUtil.stringToInt(DisplayUtil.getAspectRatioSplitValue(aspectRadio, 0));
                      int second = StringUtil.stringToInt(DisplayUtil.getAspectRatioSplitValue(aspectRadio, 1));

                      //1대 1 모드 처리
                      if(mTargetWidth == mTargetHeight) {

                          if (first < second) {
                              bitmap = Bitmap.createBitmap(bitmap, 0, bitmap.getHeight() / 2 - bitmap.getWidth() / 2, bitmap.getWidth(), bitmap.getWidth()); // h > w 일 때, w기준으로 1:1 중앙 크롭
                          } else {
                              bitmap = Bitmap.createBitmap(bitmap, bitmap.getWidth() / 2 - bitmap.getHeight() / 2, 0, bitmap.getHeight(), bitmap.getHeight()); // h < w 일 때, h기준으로 1:1 중앙 크롭
                          }

                          GomsLog.d(TAG, "bitmap.getWidth() : " + bitmap.getWidth());
                          GomsLog.d(TAG, "bitmap.getHeight() : " + bitmap.getHeight());

                          /* 해당 사진보다 스크린 폭이 더 크면, 스크린 폭에 사진을 맞추어서 디스플레이 */
                          if (mScreenWidth > bitmap.getWidth()) {
                              mTargetWidth = mScreenWidth;
                              mTargetHeight = mScreenWidth;
                          } else {
                              mTargetWidth = 1080;
                              mTargetHeight = 1080;
                          }
                      }

                      GomsLog.d(TAG, "mTargetWidth : " + mTargetWidth);
                      GomsLog.d(TAG, "mTargetHeight : " + mTargetHeight);

                      mBitmap = GlideBitmapPool.getBitmap(mTargetWidth, mTargetHeight, Bitmap.Config.ARGB_8888);
                      mBitmap = Bitmap.createScaledBitmap(bitmap, mTargetWidth, mTargetHeight, true);
                      GlideBitmapPool.putBitmap(mBitmap);

                      mGomsZoomView.setImageBitmap(mBitmap);
                      mGomsZoomView.setZoomListener(mZoomListener);
                      mGomsZoomView.setScaleType(ImageView.ScaleType.MATRIX);

                      GomsLog.d(TAG, "mBitmap.getWidth() : " + mBitmap.getWidth());
                      GomsLog.d(TAG, "mBitmap.getHeight() : " + mBitmap.getHeight());
                  }
              });

    }

    private boolean checkAndRequestPermissions() {
        GomsLog.d(TAG, "checkAndRequestPermissions()");

        int storagePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        List<String> listPermissionsNeeded = new ArrayList<>();
        if (storagePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            GomsLog.d(TAG, "checkAndRequestPermissions() > 개발 체크");

            GomsLog.d(TAG, "isAskPermissionStorage : " + isAskPermissionStorage);
            GomsLog.d(TAG, "isFirstPermission : " + isFirstPermission);

            if(isAskPermissionStorage){
                goPermissionSetting();
            }else{
                if(isFirstPermission) {
                    ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), RequestPermissionCode);
                    isFirstPermission = false;
                }else{
                    ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), RequestPermissionCodeSecond);
                }
            }
            return false;
        }else{
            goPermissionSetting();
        }
        return true;
    }

    private void goPermissionSetting(){
        GomsLog.d(TAG, "goPermissionSetting()");
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.parse("package:" + BuildConfig.APPLICATION_ID));
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_NO_HISTORY|Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivityForResult(intent, AppConstant.ACTIVITY_REQUEST_CODE_PERMISSION_SETTING);
    }

    /**------------------------------------ GomsZoomView.Listener ------------------------------------------------*/
    @Override
    public void onOneTap(int type, MotionEvent motionEvent, float scale, float dx, float dy) {

    }

    @Override
    public void onOriginalMove(Matrix matrix) {

    }

    @Override
    public void onOriginalReset(float[] matrixValues, float[] startValues) {

    }

    @Override
    public void onOriginalCenter(float[] matrixValues, RectF mBounds) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Glide.get(this).clearMemory();
    }
}
