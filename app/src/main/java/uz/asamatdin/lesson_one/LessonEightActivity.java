package uz.asamatdin.lesson_one;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.pm.ConfigurationInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;

/**
 * Shama menen 22.01.2016 sag'at 11:02
 * user ta'repten jaratildi. :)
 */
public class LessonEightActivity extends Activity {
    LessonEightSurfaceView mGlSurfaceView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        ConfigurationInfo info = manager.getDeviceConfigurationInfo();
        boolean hasGLES = info.reqGlEsVersion > 0x20000;
        mGlSurfaceView = new LessonEightSurfaceView(this);
        setContentView(mGlSurfaceView);
        if (hasGLES) {

            mGlSurfaceView.setEGLContextClientVersion(2);
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);

            LessonEightRenderer renderer = new LessonEightRenderer(this);

            mGlSurfaceView.setRenderer(renderer, metrics.density);


        } else
            return;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGlSurfaceView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGlSurfaceView.onResume();
    }
}
