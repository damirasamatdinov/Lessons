package uz.asamatdin.lesson_one;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Shama menen 20.01.2016 sag'at 16:44
 * user ta'repten jaratildi. :)
 */
public class LessonSixSurfaceView extends GLSurfaceView {


    private LessonSixRenderer mRenderer;

    private float mDensity;

    private float previousX;

    private float previousY;


    public LessonSixSurfaceView(Context context) {
        super(context);
    }

    public LessonSixSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event != null) {
            float x = event.getX();
            float y = event.getY();
            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                if (mRenderer != null) {
                    float deltaX = (x - previousX) / mDensity / 2f;
                    float deltaY = (y - previousY) / mDensity / 2f;

                    mRenderer.mDeltaX += deltaX;
                    mRenderer.mDeltaY += deltaY;
                }
            }
            previousX = x;
            previousY = y;

            return true;
        }
        return super.onTouchEvent(event);
    }

    public void setRenderer(LessonSixRenderer renderer, float density) {
        this.mRenderer = renderer;
        this.mDensity = density;
        super.setRenderer(renderer);
    }
}
