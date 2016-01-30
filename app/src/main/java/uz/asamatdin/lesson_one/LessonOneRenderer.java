package uz.asamatdin.lesson_one;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Date;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Shama menen 14.01.2016 sag'at 16:19
 * user ta'repten jaratildi. :)
 */
public class LessonOneRenderer implements GLSurfaceView.Renderer {

    FloatBuffer mTriangleVerticesOne;
    FloatBuffer mTriangleVerticesTwo;
    FloatBuffer mTriangleVerticesThree;
    private float[] mViewMatrix = new float[16];
    private float[] mProjectionMatrix = new float[16];
    private float[] mModelMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];

    private int mMVPMatrixHandle;

    private int mPositionHandle;

    private int mColorHandle;
    private int mPositionOffset = 0;
    private int mPositionDataSize = 3;
    /**
     * How many bytes per float.
     */
    private final int mBytesPerFloat = 4;

    /**
     * How many elements per vertex.
     */
    private final int mStrideBytes = 7 * mBytesPerFloat;
    private int mColorPosition = 3;
    private int mColorDataSize = 4;


    public LessonOneRenderer() {

        // This triangle is red, green, and blue.
        final float[] triangle1VerticesData = {
                // X, Y, Z,
                // R, G, B, A
                -0.5f, -0.25f, 0.0f,
                1.0f, 0.0f, 0.0f, 1.0f,

                0.5f, -0.25f, 0.0f,
                0.0f, 0.0f, 1.0f, 1.0f,

                0.0f, 0.559016994f, 0.0f,
                0.0f, 1.0f, 0.0f, 1.0f};

        // This triangle is yellow, cyan, and magenta.
        final float[] triangle2VerticesData = {
                // X, Y, Z,
                // R, G, B, A
                -0.5f, -0.25f, 0.0f,
                1.0f, 1.0f, 0.0f, 1.0f,

                0.5f, -0.25f, 0.0f,
                0.0f, 1.0f, 1.0f, 1.0f,

                0.0f, 0.559016994f, 0.0f,
                1.0f, 0.0f, 1.0f, 1.0f};

        // This triangle is white, gray, and black.
        final float[] triangle3VerticesData = {
                // X, Y, Z,
                // R, G, B, A
                -0.5f, -0.25f, 0.0f,
                1.0f, 1.0f, 1.0f, 1.0f,

                0.5f, -0.25f, 0.0f,
                0.5f, 0.5f, 0.5f, 1.0f,

                0.0f, 0.559016994f, 0.0f,
                0.0f, 0.0f, 0.0f, 1.0f};


        mTriangleVerticesOne = ByteBuffer.allocateDirect(triangle1VerticesData.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mTriangleVerticesTwo = ByteBuffer.allocateDirect(triangle2VerticesData.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mTriangleVerticesThree = ByteBuffer.allocateDirect(triangle3VerticesData.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();

        mTriangleVerticesOne.put(triangle1VerticesData);
        mTriangleVerticesTwo.put(triangle2VerticesData);
        mTriangleVerticesThree.put(triangle3VerticesData);

        mTriangleVerticesOne.position(0);
        mTriangleVerticesTwo.position(0);
        mTriangleVerticesThree.position(0);
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 0.5f);

        float eyeX = 0.0f;
        float eyeY = 0.0f;
        float eyeZ = 1.5f;

        float lookX = 0.0f;
        float lookY = 0.0f;
        float lookZ = -5.0f;

        float upX = 0.0f;
        float upY = 1.0f;
        float upZ = 0.0f;

        Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);

        int vertexShaderHandle = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);

        if (vertexShaderHandle != 0) {
            GLES20.glShaderSource(vertexShaderHandle, vertex_shader);

            GLES20.glCompileShader(vertexShaderHandle);

            int[] status = new int[1];
            GLES20.glGetShaderiv(vertexShaderHandle, GLES20.GL_COMPILE_STATUS, status, 0);

            if (status[0] == 0) {
                GLES20.glDeleteShader(vertexShaderHandle);
                vertexShaderHandle = 0;
            }
        }

        if (vertexShaderHandle == 0) {
            throw new RuntimeException("Error creating vertex shader.");
        }

        int fragmentShaderHandle = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        if (fragmentShaderHandle != 0) {
            GLES20.glShaderSource(fragmentShaderHandle, fragment_shader);
            GLES20.glCompileShader(fragmentShaderHandle);

            int status[] = new int[1];
            GLES20.glGetShaderiv(fragmentShaderHandle, GLES20.GL_COMPILE_STATUS, status, 0);
            if (status[0] == 0) {
                Log.d("fragment_shader", GLES20.glGetShaderInfoLog(fragmentShaderHandle));
                GLES20.glDeleteShader(fragmentShaderHandle);
                fragmentShaderHandle = 0;
            }
        }

        if (fragmentShaderHandle == 0) {
            throw new RuntimeException("Error creating vertex shader.");

        }

        int programHandle = GLES20.glCreateProgram();
        if (programHandle != 0) {
            GLES20.glAttachShader(programHandle, vertexShaderHandle);
            GLES20.glAttachShader(programHandle, fragmentShaderHandle);

            GLES20.glBindAttribLocation(programHandle, 0, "a_Position");
            GLES20.glBindAttribLocation(programHandle, 1, "a_Color");

            GLES20.glLinkProgram(programHandle);

            int programStatus[] = new int[1];
            GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, programStatus, 0);

            if (programStatus[0] == 0) {
                Log.d("program_handle", GLES20.glGetProgramInfoLog(programHandle));
                GLES20.glDeleteProgram(programHandle);
                programHandle = 0;

            }
        }


        if (programHandle == 0) {
            throw new RuntimeException("Error creating vertex shader.");
        }


        mMVPMatrixHandle = GLES20.glGetUniformLocation(programHandle, "u_MPMatrix");
        mPositionHandle = GLES20.glGetAttribLocation(programHandle, "a_Position");
        mColorHandle = GLES20.glGetAttribLocation(programHandle, "a_Color");

        GLES20.glUseProgram(programHandle);
    }


    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;
        float left = -ratio;
        float right = ratio;
        float bottom = -1.0f;
        float top = 1.0f;
        float near = 1.0f;
        float far = 10.0f;

        Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
    }

    @Override
    public void onDrawFrame(GL10 gl) {

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        long time = SystemClock.uptimeMillis() % 10000L;
        float angleInDegrees = (360.0f / 10000.0f) * ((int) time);

        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.rotateM(mModelMatrix, 0, angleInDegrees, 0.0f, 0.0f, 1.0f);
        drawTriangle(mTriangleVerticesOne);

        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, 0.0f, -1.0f, 0.0f);
        Matrix.rotateM(mModelMatrix, 0, 90.0f, 1.0f, 0.0f, 0.0f);
        Matrix.rotateM(mModelMatrix, 0, angleInDegrees, 0.0f, 0.0f, 1.0f);
        drawTriangle(mTriangleVerticesTwo);

        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, 1.0f, 0.0f, 0.0f);
        Matrix.rotateM(mModelMatrix, 0, 90.0f, 0.0f, 1.0f, 0.0f);
        Matrix.rotateM(mModelMatrix, 0, angleInDegrees, 0.0f, 0.0f, 1.0f);
        drawTriangle(mTriangleVerticesThree);

    }

    private void drawTriangle(FloatBuffer buffer) {

        buffer.position(mPositionOffset);

        GLES20.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES20.GL_FLOAT, false, mStrideBytes, buffer);
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        buffer.position(mColorPosition);
        GLES20.glVertexAttribPointer(mColorHandle, mColorDataSize, GLES20.GL_FLOAT, false, mStrideBytes, buffer);
        GLES20.glEnableVertexAttribArray(mColorHandle);

        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);

        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
    }

    private String vertex_shader = "" +
            "uniform mat4 u_MPMatrix; \n" +
            "attribute vec4 a_Position; \n" +
            "attribute vec4 a_Color; \n" +
            "varying vec4 v_Color; \n" +
            "void main(){ \n" +
            "\n" +
            "v_Color = a_Color; \n" +
            "gl_Position = u_MPMatrix * a_Position; \n" +
            "}";

    private String fragment_shader = "" +
            "precision mediump float; \n" +
            "varying vec4 v_Color; \n" +
            "void main(){ \n" +
            "gl_FragColor = v_Color; \n" +
            "}";
}
