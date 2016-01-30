package uz.asamatdin.lesson_one;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

/**
 * Shama menen 19.01.2016 sag'at 13:56
 * user ta'repten jaratildi. :)
 */
public class LessonFiveSurfaceView extends GLSurfaceView {

    LessonFiveRenderer renderer;

    public LessonFiveSurfaceView(Context context) {
        super(context);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event!=null){
            if (event.getAction() == MotionEvent.ACTION_DOWN){
                if (renderer!=null){
                    queueEvent(new Runnable() {
                        @Override
                        public void run() {
                            renderer.switchMode();
                        }
                    });
                    return true;
                }
            }
        }
        return super.onTouchEvent(event);
    }

    public void setRenderer(LessonFiveRenderer renderer) {
        this.renderer = renderer;
        super.setRenderer(renderer);
    }
}
