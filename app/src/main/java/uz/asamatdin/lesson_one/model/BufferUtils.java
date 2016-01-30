package uz.asamatdin.lesson_one.model;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

/**
 * Shama menen 30.01.2016 sag'at 12:47
 * user ta'repten jaratildi. :)
 */
public class BufferUtils {

    public static final int FLOAT_SIZE_IN_BYTE = 4;
    public static final int INT_SIZE_IN_BYTE = 4;
    public static final int SHORT_SIZE_IN_BYTE = 2;


    public static FloatBuffer getFloatBuffer(float[] data) {
        return getFloatBuffer(data, 0);
    }

    public static FloatBuffer getFloatBuffer(float[] data, int position) {
        FloatBuffer buffer = getNativeOrderBuffer(data.length, FLOAT_SIZE_IN_BYTE).asFloatBuffer();
        buffer.put(data).position(position);
        return buffer;
    }

    public static IntBuffer getIntBuffer(int[] data, int position) {
        IntBuffer buffer = getNativeOrderBuffer(data.length, INT_SIZE_IN_BYTE).asIntBuffer();
        buffer.put(data).position(position);
        return buffer;
    }

    public static IntBuffer getIntBuffer(int[] data) {
        return getIntBuffer(data, 0);
    }

    public static ShortBuffer getShortBuffer(short[] data, int position) {
        ShortBuffer buffer = getNativeOrderBuffer(data.length, SHORT_SIZE_IN_BYTE).asShortBuffer();
        buffer.put(data).position(position);
        return buffer;
    }

    public static ShortBuffer getShortBuffer(short[] data) {
        return getShortBuffer(data, 0);
    }

    public static ByteBuffer getNativeOrderBuffer(int lengthOfData, int sizeInByte) {
        return ByteBuffer.allocateDirect(lengthOfData * sizeInByte).order(ByteOrder.nativeOrder());
    }

    public static Buffer getBuffer(Model.ModelTypes type, Object[] data, int position) {
        switch (type) {
            case FLOAT_BUFFER:
                return getFloatBuffer(getFloatArray(data), position);
            case INT_BUFFER:
                return getIntBuffer(getIntArray(data), position);
            case SHORT_BUFFER:
                return getShortBuffer(getShortArray(data), position);
            default:
                return null;
        }
    }


    private static float[] getFloatArray(Object[] data) {
        float[] returnData = new float[data.length];
        for (int i = 0; i < data.length; i++) {
            returnData[i] = (float) data[i];
        }
        return returnData;
    }

    private static int[] getIntArray(Object[] data) {
        int[] returnData = new int[data.length];
        for (int i = 0; i < data.length; i++) {
            returnData[i] = (int) data[i];
        }
        return returnData;
    }

    private static short[] getShortArray(Object[] data) {
        short[] returnData = new short[data.length];
        for (int i = 0; i < data.length; i++) {
            returnData[i] = (short) data[i];
        }
        return returnData;
        //// TODO: 30.01.2016  
    }

    public static int getDataSizeInByte(Model.ModelTypes type) {
        switch (type) {
            case FLOAT_BUFFER:
                return FLOAT_SIZE_IN_BYTE;
            case INT_BUFFER:
                return INT_SIZE_IN_BYTE;
            case SHORT_BUFFER:
                return SHORT_SIZE_IN_BYTE;
            default:
                return FLOAT_SIZE_IN_BYTE;
        }
    }
}
