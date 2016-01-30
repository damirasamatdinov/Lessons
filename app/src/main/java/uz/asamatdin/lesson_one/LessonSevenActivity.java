package uz.asamatdin.lesson_one;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.pm.ConfigurationInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

/**
 * Shama menen 21.01.2016 sag'at 14:36
 * user ta'repten jaratildi. :)
 */
public class LessonSevenActivity extends Activity {

    LessonSevenSurfaceView sevenSurfaceView;
    LessonSevenRenderer mRenderer;
    Button btn_use_vbo;
    private Button btn_use_stride;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lesson_seven);

        sevenSurfaceView = (LessonSevenSurfaceView) findViewById(R.id.gl_surface_view);
        btn_use_vbo = (Button) findViewById(R.id.button_toggle_vbo);
        btn_use_stride = (Button) findViewById(R.id.button_toggle_stride);

        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        ConfigurationInfo info = manager.getDeviceConfigurationInfo();
        boolean hasOPES2 = info.reqGlEsVersion > 0x20000;
        if (hasOPES2) {
            sevenSurfaceView.setEGLContextClientVersion(2);

            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);


            mRenderer = new LessonSevenRenderer(this, sevenSurfaceView);
            sevenSurfaceView.setRenderer(mRenderer, metrics.density);
        } else
            return;

        btn_use_vbo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleVBOs();
            }
        });

        btn_use_stride.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleStride();
            }
        });

        findViewById(R.id.button_increment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increaseCubeCount();
            }
        });

        findViewById(R.id.button_decrement).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decreaseCubeCount();
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        sevenSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        sevenSurfaceView.onPause();
    }

    private void decreaseCubeCount() {
        sevenSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                mRenderer.decreaseCubeCount();
            }
        });
    }

    private void increaseCubeCount() {
        sevenSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                mRenderer.increaseCubeCount();
            }
        });
    }

    private void toggleVBOs() {
        sevenSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                mRenderer.toggleVBO();
            }
        });
    }

    protected void toggleStride() {
        sevenSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                mRenderer.toggleStride();
            }
        });
    }

    public void updateVboStatus(final boolean usingVbos) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (usingVbos) {
                    ((Button) findViewById(R.id.button_toggle_vbo)).setText(R.string.use_vbo);
                } else {
                    ((Button) findViewById(R.id.button_toggle_vbo)).setText(R.string.not_use_vbo);
                }
            }
        });
    }

    public void updateStrideStatus(final boolean useStride) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (useStride) {
                    ((Button) findViewById(R.id.button_toggle_stride)).setText(R.string.use_stride);
                } else {
                    ((Button) findViewById(R.id.button_toggle_stride)).setText(R.string.not_use_stride);
                }
            }
        });
    }
}
