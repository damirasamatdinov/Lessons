package uz.asamatdin.lesson_one;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

/**
 * Shama menen 19.01.2016 sag'at 9:59
 * user ta'repten jaratildi. :)
 */
public class ShaderHelper {
    private static final String TAG = "ShaderHelper";

    public static int compileShader(Context context, final int shader_type, final int shaderResId) {
        String shaderSource = RawResourceReader.readTextFileFromRawResource(context, shaderResId);
        return compileShader(shader_type, shaderSource);
    }

    public static int compileShader(final int shader_type, final String shader_source) {
        int shaderHandle = GLES20.glCreateShader(shader_type);
        if (shaderHandle != 0) {
            GLES20.glShaderSource(shaderHandle, shader_source);
            GLES20.glCompileShader(shaderHandle);

            int[] status = new int[1];
            GLES20.glGetShaderiv(shaderHandle, GLES20.GL_COMPILE_STATUS, status, 0);
            if (status[0] == 0) {
                Log.e(TAG, GLES20.glGetShaderInfoLog(shaderHandle));
                GLES20.glDeleteShader(shaderHandle);
                shaderHandle = 0;
            }
        }

        if (shaderHandle == 0) {
            throw new RuntimeException("Error creating shader.");
        }

        return shaderHandle;
    }


    public static int createAndLinkProgram(final int vertexShaderHandle, final int fragmentShaderHandle, final String... attributes) {
        int programHandle = GLES20.glCreateProgram();
        if (programHandle != 0) {
            GLES20.glAttachShader(programHandle, vertexShaderHandle);
            GLES20.glAttachShader(programHandle, fragmentShaderHandle);

            if (attributes != null) {
                final int size = attributes.length;
                for (int i = 0; i < size; i++) {
                    GLES20.glBindAttribLocation(programHandle, i, attributes[i]);
                }
            }

            GLES20.glLinkProgram(programHandle);

            int[] status = new int[1];
            GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, status, 0);
            if (status[0] == 0) {
                Log.e(TAG, "Error compiling shader: " + GLES20.glGetProgramInfoLog(programHandle));
                GLES20.glDeleteProgram(programHandle);
                programHandle = 0;
            }
        }

        if (programHandle == 0) {
            throw new RuntimeException("Error creating shader.");
        }
        return programHandle;
    }
}
