package uz.asamatdin.lesson_one.model;

import android.opengl.GLES20;


/**
 * Shama menen 30.01.2016 sag'at 17:08
 * user ta'repten jaratildi. :)
 */
public class IndexModel extends Model implements IRenderable {


    public int handle = -1;
    public int stride;
    public int offset = 0;


    public IndexModel(int target, int size, int usage, int stride, int offset, boolean useVbo) {
        super(target, size, usage, ModelTypes.INT_BUFFER, useVbo);
        this.stride = stride;
        this.offset = offset;
    }

    public IndexModel(int target, int size, int stride, int offset, boolean useVbo) {
        this(target, size, GLES20.GL_STATIC_DRAW, stride, offset, useVbo);
    }


    @Override
    public void release() {
        if (vboIdx > 0) {
            unbindVbo();
        }
    }

    @Override
    public void render() {
        if (vboIdx > 0) {
            GLES20.glBindBuffer(target, vboIdx);
        }
    }

    public void drawElements(int drawableType, int count) {
        GLES20.glBindBuffer(target, vboIdx);
        if (isUseVbo())
            getGl20().glDrawElements(drawableType, count, getType(), 0);
        else
            GLES20.glDrawElements(drawableType, count, getType(), 0);
    }

    public int getHandle() {
        return handle;
    }

    public void setHandle(int handle) {
        this.handle = handle;
    }

    public int getStride() {
        return stride;
    }

    public void setStride(int stride) {
        this.stride = stride;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}
