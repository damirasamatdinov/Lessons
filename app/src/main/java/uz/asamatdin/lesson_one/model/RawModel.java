package uz.asamatdin.lesson_one.model;

import android.opengl.GLES20;

import com.badlogic.gdx.graphics.GL20;

/**
 * Shama menen 30.01.2016 sag'at 16:40
 * user ta'repten jaratildi. :)
 */
public class RawModel extends Model implements IRenderable {


    public int handle = -1;
    public int stride;
    public int offset = 0;


    public RawModel(int target, int size, int usage, ModelTypes type, int stride, int offset, boolean useVbo) {
        super(target, size, usage, type, useVbo);
        this.stride = stride;
        this.offset = offset;
    }

    public RawModel(int target, int size, ModelTypes type, int stride, int offset, boolean useVbo) {
        this(target, size, GLES20.GL_STATIC_DRAW, type, stride, offset, useVbo);
    }


    @Override
    public void release() {
        if (vboIdx > 0 && handle > 0) {
            GLES20.glDisableVertexAttribArray(handle);
            unbindVbo();
        }
    }

    @Override
    public void render() {
        if (vboIdx > 0 && handle > 0) {
            GLES20.glBindBuffer(target, vboIdx);
            GLES20.glEnableVertexAttribArray(handle);
            if (isUseVbo()) {
                getGl20().glVertexAttribPointer(handle, size, getType(), false, stride, offset);
            } else {
                if (getBuffer() == null) {
                    GLES20.glDisableVertexAttribArray(handle);
                    unbindVbo();
                    return;
                }
                GLES20.glVertexAttribPointer(handle, size, getType(), false, stride, getBuffer());
            }
        }
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
