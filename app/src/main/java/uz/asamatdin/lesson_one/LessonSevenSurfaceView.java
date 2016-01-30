package uz.asamatdin.lesson_one;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Shama menen 21.01.2016 sag'at 14:37
 * user ta'repten jaratildi. :)
 */
public class LessonSevenSurfaceView extends GLSurfaceView {

    private LessonSevenRenderer mRenderer;
    private float previuosX;
    private float mDensity;
    private float previuosY;

    public LessonSevenSurfaceView(Context context) {
        super(context);
    }

    public LessonSevenSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event != null) {
            float x = event.getX();
            float y = event.getY();
            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                if (mRenderer != null) {
                    float deltaX = (x - previuosX) / mDensity / 2f;
                    float deltaY = (y - previuosY) / mDensity / 2f;

                    mRenderer.mDeltaX += deltaX;
                    mRenderer.mDeltaY += deltaY;
                }
            }

            previuosX = x;
            previuosY = y;

            return true;
        } else
            return super.onTouchEvent(event);
    }

    public void setRenderer(LessonSevenRenderer renderer, float mDensity) {
        this.mRenderer = renderer;
        this.mDensity = mDensity;
        super.setRenderer(renderer);
    }
}
