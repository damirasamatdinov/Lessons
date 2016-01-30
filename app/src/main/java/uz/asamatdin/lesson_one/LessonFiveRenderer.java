package uz.asamatdin.lesson_one;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Shama menen 19.01.2016 sag'at 11:31
 * user ta'repten jaratildi. :)
 */
public class LessonFiveRenderer implements GLSurfaceView.Renderer {

    private final FloatBuffer mPositionBuffer;
    private final FloatBuffer mColorBuffer;
    private float[] mViewMatrix = new float[16];
    private int mVertexShaderHandle;
    private final Context mContext;
    private int mFragmentShaderHandle;
    private int mProgramHandle;
    private float[] mProjectionMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];
    private int mPositionHandle;
    private int mMVPMatrixHandle;
    private int mColorHandle;
    private float[] mModelMatrix = new float[16];
    private int mPositionDataSize = 3;
    private int mColorDataSize = 4;

    private boolean mBlending;

    public LessonFiveRenderer(Context context) {
        this.mContext = context;

        final float[] p1p = {-1.0f, 1.0f, 1.0f};
        final float[] p2p = {1.0f, 1.0f, 1.0f};
        final float[] p3p = {-1.0f, -1.0f, 1.0f};
        final float[] p4p = {1.0f, -1.0f, 1.0f};
        final float[] p5p = {-1.0f, 1.0f, -1.0f};
        final float[] p6p = {1.0f, 1.0f, -1.0f};
        final float[] p7p = {-1.0f, -1.0f, -1.0f};
        final float[] p8p = {1.0f, -1.0f, -1.0f};

        final float[] cubePositionData = ShapeBuilder.generateCubeData(p1p, p2p, p3p, p4p, p5p,
                p6p, p7p, p8p, p1p.length);

        mPositionBuffer = ByteBuffer.allocateDirect(cubePositionData.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mPositionBuffer.put(cubePositionData).position(0);

        // Points of the cube: color information
        // R, G, B, A
        final float[] p1c = {1.0f, 0.0f, 0.0f, 1.0f};        // red
        final float[] p2c = {1.0f, 0.0f, 1.0f, 1.0f};        // magenta
        final float[] p3c = {0.0f, 0.0f, 0.0f, 1.0f};        // black
        final float[] p4c = {0.0f, 0.0f, 1.0f, 1.0f};        // blue
        final float[] p5c = {1.0f, 1.0f, 0.0f, 1.0f};        // yellow
        final float[] p6c = {1.0f, 1.0f, 1.0f, 1.0f};        // white
        final float[] p7c = {0.0f, 1.0f, 0.0f, 1.0f};        // green
        final float[] p8c = {0.0f, 1.0f, 1.0f, 1.0f};        // cyan

        final float[] cubeColorData = ShapeBuilder.generateCubeData(p1c, p2c, p3c, p4c, p5c, p6c, p7c, p8c, p1c.length);

        mColorBuffer = ByteBuffer.allocateDirect(cubeColorData.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mColorBuffer.put(cubeColorData).position(0);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES20.glDisable(GLES20.GL_CULL_FACE);
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);

        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE);

        final float eyeX = 0.0f;
        final float eyeY = 0.0f;
        final float eyeZ = -0.5f;

        final float lookX = 0.0f;
        final float lookY = 0.0f;
        final float lookZ = -5.0f;

        final float upX = 0.0f;
        final float upY = 1.0f;
        final float upZ = 0.0f;

        Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);

        mVertexShaderHandle = ShaderHelper.compileShader(mContext, GLES20.GL_VERTEX_SHADER, R.raw.lesson_five_vertex_shader);
        mFragmentShaderHandle = ShaderHelper.compileShader(mContext, GLES20.GL_FRAGMENT_SHADER, R.raw.lesson_five_fragment_shader);

        mProgramHandle = ShaderHelper.createAndLinkProgram(mVertexShaderHandle, mFragmentShaderHandle, "a_Position", "a_Color");

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        final float ratio = (float) width / height;
        final float left = -ratio;
        final float right = ratio;
        final float bottom = -1.0f;
        final float top = 1.0f;
        final float near = 1.0f;
        final float far = 10.0f;

        Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
    }


    @Override
    public void onDrawFrame(GL10 gl) {
        if (mBlending) {
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        } else
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        long time = SystemClock.uptimeMillis() % 10000L;
        float angleInDegrees = (360.0f / 10000.0f) * (int) time;

        GLES20.glUseProgram(mProgramHandle);

        mPositionHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Position");
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_MVPMatrix");
        mColorHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Color");

        // Draw some cubes.
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, 4.0f, 0.0f, -7.0f);
        Matrix.rotateM(mModelMatrix, 0, angleInDegrees, 1.0f, 0.0f, 0.0f);
        drawCube();

        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, -4.0f, 0.0f, -7.0f);
        Matrix.rotateM(mModelMatrix, 0, angleInDegrees, 0.0f, 1.0f, 0.0f);
        drawCube();

        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, 0.0f, 4.0f, -7.0f);
        Matrix.rotateM(mModelMatrix, 0, angleInDegrees, 0.0f, 0.0f, 1.0f);
        drawCube();

        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, 0.0f, -4.0f, -7.0f);
        drawCube();

        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, 0.0f, 0.0f, -5.0f);
        Matrix.rotateM(mModelMatrix, 0, angleInDegrees, 1.0f, 1.0f, 0.0f);
        drawCube();
    }

    private void drawCube() {
        mPositionBuffer.position(0);
        GLES20.glVertexAttribPointer(mPositionHandle, mPositionDataSize,
                GLES20.GL_FLOAT, false, 0, mPositionBuffer);
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        mColorBuffer.position(0);
        GLES20.glVertexAttribPointer(mColorHandle, mColorDataSize,
                GLES20.GL_FLOAT, false, 0, mColorBuffer);
        GLES20.glEnableVertexAttribArray(mColorHandle);

        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);

        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 36);
    }

    public void switchMode() {
        mBlending = !mBlending;
        if (mBlending) {
            GLES20.glDisable(GLES20.GL_CULL_FACE);
            GLES20.glDisable(GLES20.GL_DEPTH_TEST);

            GLES20.glEnable(GLES20.GL_BLEND);
            GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE);
        } else {
            GLES20.glEnable(GLES20.GL_CULL_FACE);
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
            GLES20.glDisable(GLES20.GL_BLEND);
        }
    }
}
