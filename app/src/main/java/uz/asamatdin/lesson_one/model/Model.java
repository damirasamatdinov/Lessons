package uz.asamatdin.lesson_one.model;

import android.opengl.GLES20;

import com.badlogic.gdx.backends.android.AndroidGL20;

import java.nio.Buffer;

/**
 * Shama menen 30.01.2016 sag'at 12:39
 * user ta'repten jaratildi. :)
 */
public abstract class Model implements IModel {


    public enum ModelTypes {
        FLOAT_BUFFER,
        INT_BUFFER,
        SHORT_BUFFER,
        BYTE_BUFFER
    }


    public final int target;
    public final int size;
    private int usage = GLES20.GL_STATIC_DRAW;
    protected int vboIdx = 0;
    protected ModelTypes mType = ModelTypes.FLOAT_BUFFER;
    protected AndroidGL20 gl20;
    protected final boolean useVbo;
    protected Buffer buffer;


    public Model(int target, int size, int usage, ModelTypes type, boolean useVbo) {
        this.target = target;
        this.size = size;
        this.usage = usage;
        this.mType = type;
        this.useVbo = useVbo;
        gl20 = new AndroidGL20();
    }


    public Model(int target, int size, ModelTypes type, boolean useVbo) {
        this(target, size, GLES20.GL_STATIC_DRAW, type, useVbo);
    }

    @Override
    public int createVbo() {
        int[] idx = new int[1];
        GLES20.glGenBuffers(1, idx, 0);
        vboIdx = idx[0];
        return vboIdx;
    }

    @Override
    public void storeDataInAttributeList(Object[] data) {

        GLES20.glBindBuffer(target, vboIdx);

        buffer = BufferUtils.getBuffer(mType, data, 0);

        if (buffer == null) {
            clear();
            return;
        }
        GLES20.glBufferData(target, buffer.capacity() * BufferUtils.getDataSizeInByte(mType), buffer, usage);
    }

    @Override
    public void unbindVbo() {
        GLES20.glBindBuffer(target, 0);
    }

    @Override
    public void deleteVbo(int vboIdx) {
        int deletedVboIdx[] = new int[]{vboIdx};
        GLES20.glDeleteBuffers(deletedVboIdx.length, deletedVboIdx, 0);
    }

    @Override
    public void clear() {
        unbindVbo();
        deleteVbo(vboIdx);
        this.vboIdx = 0;
    }

    public int getVboIdx() {
        return vboIdx;
    }

    public AndroidGL20 getGl20() {
        return gl20;
    }

    public boolean isUseVbo() {
        return useVbo;
    }

    public int getType(ModelTypes type) {
        switch (type) {
            case FLOAT_BUFFER:
                return GLES20.GL_FLOAT;
            case INT_BUFFER:
                return GLES20.GL_UNSIGNED_INT;
            case SHORT_BUFFER:
                return GLES20.GL_UNSIGNED_SHORT;
            case BYTE_BUFFER:
                return GLES20.GL_UNSIGNED_BYTE;
            default:
                return GLES20.GL_FLOAT;
        }
    }

    protected int getType() {
        return getType(mType);
    }

    public Buffer getBuffer() {
        return buffer;
    }


}
