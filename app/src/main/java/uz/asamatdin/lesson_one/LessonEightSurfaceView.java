package uz.asamatdin.lesson_one;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Shama menen 22.01.2016 sag'at 10:55
 * user ta'repten jaratildi. :)
 */
public class LessonEightSurfaceView extends GLSurfaceView {

    LessonEightRenderer mRenderer;
    private float mDensity;
    private float previousY;
    private float previousX;

    public LessonEightSurfaceView(Context context) {
        super(context);
    }

    public LessonEightSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event != null) {
            float x = event.getX();
            float y = event.getY();
            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                if (mRenderer != null) {
                    final float deltaX = (x - previousX) / mDensity / 2f;
                    final float deltaY = (y - previousY) / mDensity / 2f;

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

    public void setRenderer(LessonEightRenderer renderer, float density) {
        this.mRenderer = renderer;
        this.mDensity = density;
        super.setRenderer(renderer);
    }
}
