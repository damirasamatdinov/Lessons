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
 * Shama menen 20.01.2016 sag'at 14:09
 * user ta'repten jaratildi. :)
 */
public class LessonSixRenderer implements GLSurfaceView.Renderer {

    private final Context mContext;
    private final int perByteForFloat = 4;
    private final FloatBuffer mCubePositionBuffer;
    private final FloatBuffer mCubeNormalBuffer;
    private final FloatBuffer mCubeTexCoordinatesBuffer;
    private final FloatBuffer mCubeTexCoordinatesForPlaneBuffer;


    private float[] mViewMatrix = new float[16];
    private float[] mProjectionMatrix = new float[16];
    private float[] mAccumulatedRotationMatrix = new float[16];
    private float[] mLightModelMatrix = new float[16];
    private float[] mModelMatrix = new float[16];
    private float[] mCurrentRotation = new float[16];
    private float[] mTemporaryMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];
    private float[] mLightPosInModelSpace = {0.0f, 0.0f, 0.0f, 1.0f};
    private float[] mLightPosInWorldSpace = new float[4];
    private float[] mLightPosInEyeSpace = new float[4];
    private int mProgramHandle;
    private int mLightProgramHandle;
    private int mBrinkDataHandle;
    private int mGrassDataHandle;
    private int mMVPMatrixHandle;
    private int mLightPosHandle;
    private int mMVMatrixHandle;
    private int mTextureUniformHandle;
    private int mNormalHandle;
    private int mTexCoordinateHandle;
    private int mPositionHandle;
    public volatile float mDeltaX = 0.0f;
    public volatile float mDeltaY = 0.0f;
    private int mTexCoordinateDataSize = 2;
    private int mCubePositionDataSize = 3;
    private int mQueuedMinFilter;
    private int mQueuedMagFilter;

    public LessonSixRenderer(Context context) {
        this.mContext = context;

        // X, Y, Z
        final float[] cubePositionData =
                {
                        // In OpenGL counter-clockwise winding is default. This means that when we look at a triangle,
                        // if the points are counter-clockwise we are looking at the "front". If not we are looking at
                        // the back. OpenGL has an optimization where all back-facing triangles are culled, since they
                        // usually represent the backside of an object and aren't visible anyways.

                        // Front face
                        -1.0f, 1.0f, 1.0f,
                        -1.0f, -1.0f, 1.0f,
                        1.0f, 1.0f, 1.0f,
                        -1.0f, -1.0f, 1.0f,
                        1.0f, -1.0f, 1.0f,
                        1.0f, 1.0f, 1.0f,

                        // Right face
                        1.0f, 1.0f, 1.0f,
                        1.0f, -1.0f, 1.0f,
                        1.0f, 1.0f, -1.0f,
                        1.0f, -1.0f, 1.0f,
                        1.0f, -1.0f, -1.0f,
                        1.0f, 1.0f, -1.0f,

                        // Back face
                        1.0f, 1.0f, -1.0f,
                        1.0f, -1.0f, -1.0f,
                        -1.0f, 1.0f, -1.0f,
                        1.0f, -1.0f, -1.0f,
                        -1.0f, -1.0f, -1.0f,
                        -1.0f, 1.0f, -1.0f,

                        // Left face
                        -1.0f, 1.0f, -1.0f,
                        -1.0f, -1.0f, -1.0f,
                        -1.0f, 1.0f, 1.0f,
                        -1.0f, -1.0f, -1.0f,
                        -1.0f, -1.0f, 1.0f,
                        -1.0f, 1.0f, 1.0f,

                        // Top face
                        -1.0f, 1.0f, -1.0f,
                        -1.0f, 1.0f, 1.0f,
                        1.0f, 1.0f, -1.0f,
                        -1.0f, 1.0f, 1.0f,
                        1.0f, 1.0f, 1.0f,
                        1.0f, 1.0f, -1.0f,

                        // Bottom face
                        1.0f, -1.0f, -1.0f,
                        1.0f, -1.0f, 1.0f,
                        -1.0f, -1.0f, -1.0f,
                        1.0f, -1.0f, 1.0f,
                        -1.0f, -1.0f, 1.0f,
                        -1.0f, -1.0f, -1.0f,
                };

        final float[] cubeNormalData =
                {
                        // Front face
                        0.0f, 0.0f, 1.0f,
                        0.0f, 0.0f, 1.0f,
                        0.0f, 0.0f, 1.0f,
                        0.0f, 0.0f, 1.0f,
                        0.0f, 0.0f, 1.0f,
                        0.0f, 0.0f, 1.0f,

                        // Right face
                        1.0f, 0.0f, 0.0f,
                        1.0f, 0.0f, 0.0f,
                        1.0f, 0.0f, 0.0f,
                        1.0f, 0.0f, 0.0f,
                        1.0f, 0.0f, 0.0f,
                        1.0f, 0.0f, 0.0f,

                        // Back face
                        0.0f, 0.0f, -1.0f,
                        0.0f, 0.0f, -1.0f,
                        0.0f, 0.0f, -1.0f,
                        0.0f, 0.0f, -1.0f,
                        0.0f, 0.0f, -1.0f,
                        0.0f, 0.0f, -1.0f,

                        // Left face
                        -1.0f, 0.0f, 0.0f,
                        -1.0f, 0.0f, 0.0f,
                        -1.0f, 0.0f, 0.0f,
                        -1.0f, 0.0f, 0.0f,
                        -1.0f, 0.0f, 0.0f,
                        -1.0f, 0.0f, 0.0f,

                        // Top face
                        0.0f, 1.0f, 0.0f,
                        0.0f, 1.0f, 0.0f,
                        0.0f, 1.0f, 0.0f,
                        0.0f, 1.0f, 0.0f,
                        0.0f, 1.0f, 0.0f,
                        0.0f, 1.0f, 0.0f,

                        // Bottom face
                        0.0f, -1.0f, 0.0f,
                        0.0f, -1.0f, 0.0f,
                        0.0f, -1.0f, 0.0f,
                        0.0f, -1.0f, 0.0f,
                        0.0f, -1.0f, 0.0f,
                        0.0f, -1.0f, 0.0f
                };

        // S, T (or X, Y)
        // Texture coordinate data.
        // Because images have a Y axis pointing downward (values increase as you move down the image) while
        // OpenGL has a Y axis pointing upward, we adjust for that here by flipping the Y axis.
        // What's more is that the texture coordinates are the same for every face.
        final float[] cubeTextureCoordinateData =
                {
                        // Front face
                        0.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 1.0f,
                        1.0f, 0.0f,

                        // Right face
                        0.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 1.0f,
                        1.0f, 0.0f,

                        // Back face
                        0.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 1.0f,
                        1.0f, 0.0f,

                        // Left face
                        0.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 1.0f,
                        1.0f, 0.0f,

                        // Top face
                        0.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 1.0f,
                        1.0f, 0.0f,

                        // Bottom face
                        0.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 1.0f,
                        1.0f, 0.0f
                };

        // S, T (or X, Y)
        // Texture coordinate data.
        // Because images have a Y axis pointing downward (values increase as you move down the image) while
        // OpenGL has a Y axis pointing upward, we adjust for that here by flipping the Y axis.
        // What's more is that the texture coordinates are the same for every face.
        final float[] cubeTextureCoordinateDataForPlane =
                {
                        // Front face
                        0.0f, 0.0f,
                        0.0f, 25.0f,
                        25.0f, 0.0f,
                        0.0f, 25.0f,
                        25.0f, 25.0f,
                        25.0f, 0.0f,

                        // Right face
                        0.0f, 0.0f,
                        0.0f, 25.0f,
                        25.0f, 0.0f,
                        0.0f, 25.0f,
                        25.0f, 25.0f,
                        25.0f, 0.0f,

                        // Back face
                        0.0f, 0.0f,
                        0.0f, 25.0f,
                        25.0f, 0.0f,
                        0.0f, 25.0f,
                        25.0f, 25.0f,
                        25.0f, 0.0f,

                        // Left face
                        0.0f, 0.0f,
                        0.0f, 25.0f,
                        25.0f, 0.0f,
                        0.0f, 25.0f,
                        25.0f, 25.0f,
                        25.0f, 0.0f,

                        // Top face
                        0.0f, 0.0f,
                        0.0f, 25.0f,
                        25.0f, 0.0f,
                        0.0f, 25.0f,
                        25.0f, 25.0f,
                        25.0f, 0.0f,

                        // Bottom face
                        0.0f, 0.0f,
                        0.0f, 25.0f,
                        25.0f, 0.0f,
                        0.0f, 25.0f,
                        25.0f, 25.0f,
                        25.0f, 0.0f
                };

        mCubePositionBuffer = ByteBuffer.allocateDirect(cubePositionData.length * perByteForFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mCubePositionBuffer.put(cubePositionData).position(0);

        mCubeNormalBuffer = ByteBuffer.allocateDirect(cubeNormalData.length * perByteForFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mCubeNormalBuffer.put(cubeNormalData).position(0);


        mCubeTexCoordinatesBuffer = ByteBuffer.allocateDirect(cubeTextureCoordinateData.length * perByteForFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mCubeTexCoordinatesBuffer.put(cubeTextureCoordinateData).position(0);

        //cube Texture Coordinates for plane buffer
        mCubeTexCoordinatesForPlaneBuffer = ByteBuffer.allocateDirect(cubeTextureCoordinateDataForPlane.length * perByteForFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mCubeTexCoordinatesForPlaneBuffer.put(cubeTextureCoordinateDataForPlane).position(0);
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        GLES20.glEnable(GLES20.GL_CULL_FACE);

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        final float eyeX = 0.0f;
        final float eyeY = 0.0f;
        final float eyeZ = -0.5f;

        final float centerX = 0.0f;
        final float centerY = 0.0f;
        final float centerZ = -5.0f;

        final float upX = 0.0f;
        final float upY = 1.0f;
        final float upZ = 0.0f;

        Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);


        int vertexShaderHandle = ShaderHelper.compileShader(mContext, GLES20.GL_VERTEX_SHADER, R.raw.lesson_six_vertex_shader);
        int fragmentShaderHandle = ShaderHelper.compileShader(mContext, GLES20.GL_FRAGMENT_SHADER, R.raw.lesson_six_fragment_shader);

        mProgramHandle = ShaderHelper.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle, "a_Position", "a_Normal", "a_TexCoordinate");

        int pointLightVertexShaderHandle = ShaderHelper.compileShader(mContext, GLES20.GL_VERTEX_SHADER, R.raw.light_point_vertex_shader);
        int pointLightFragmentShaderHandle = ShaderHelper.compileShader(mContext, GLES20.GL_FRAGMENT_SHADER, R.raw.light_point_fragment_shader);

        mLightProgramHandle = ShaderHelper.createAndLinkProgram(pointLightVertexShaderHandle, pointLightFragmentShaderHandle, "a_Position");

        mBrinkDataHandle = TextureUtils.loadTexture(mContext, R.drawable.stone_wall_public_domain);
        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);

        mGrassDataHandle = TextureUtils.loadTexture(mContext, R.drawable.noisy_grass_public_domain);
        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);

        //// TODO: 20.01.2016
        if (mQueuedMinFilter != 0) {
            setMinFilter(mQueuedMinFilter);
        }

        if (mQueuedMagFilter != 0) {
            setMagFilter(mQueuedMagFilter);
        }


        Matrix.setIdentityM(mAccumulatedRotationMatrix, 0);

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;
        final float left = -ratio;
        final float right = ratio;
        final float top = 1.0f;
        final float bottom = -1.0f;
        final float near = 1.0f;
        final float far = 1000.0f;

        Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);

    }

    @Override
    public void onDrawFrame(GL10 gl) {

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        long time = SystemClock.uptimeMillis() % 10000L;
        long slowTime = SystemClock.uptimeMillis() % 100000L;

        float angleInDegrees = (360.0f / 10000.0f) * ((int) time);
        float slowAngleInDegrees = (360.0f / 100000.0f) * ((int) slowTime);

        GLES20.glUseProgram(mProgramHandle);

        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_MVPMatrix");
        mMVMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_MVMatrix");
        mLightPosHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_LightPos");
        mTextureUniformHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_Texture");
        mPositionHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Position");
        mTexCoordinateHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_TexCoordinate");
        mNormalHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Normal");

        Matrix.setIdentityM(mLightModelMatrix, 0);
        Matrix.translateM(mLightModelMatrix, 0, 0.0f, 0.0f, -2.0f);
        Matrix.rotateM(mLightModelMatrix, 0, angleInDegrees, 0.0f, 1.0f, 0.0f);
        Matrix.translateM(mLightModelMatrix, 0, 0.0f, 0.0f, 3.5f);

        Matrix.multiplyMV(mLightPosInWorldSpace, 0, mLightModelMatrix, 0, mLightPosInModelSpace, 0);
        Matrix.multiplyMV(mLightPosInEyeSpace, 0, mViewMatrix, 0, mLightPosInWorldSpace, 0);

        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, 0.0f, 0.8f, -3.5f);

        Matrix.setIdentityM(mCurrentRotation, 0);
        Matrix.rotateM(mCurrentRotation, 0, mDeltaX, 0.0f, 1.0f, 0.0f);
        Matrix.rotateM(mCurrentRotation, 0, mDeltaY, 1.0f, 0.0f, 0.0f);

        mDeltaX = 0.0f;
        mDeltaY = 0.0f;

        Matrix.multiplyMM(mTemporaryMatrix, 0, mCurrentRotation, 0, mAccumulatedRotationMatrix, 0);
        System.arraycopy(mTemporaryMatrix, 0, mAccumulatedRotationMatrix, 0, 16);

        Matrix.multiplyMM(mTemporaryMatrix, 0, mModelMatrix, 0, mAccumulatedRotationMatrix, 0);
        System.arraycopy(mTemporaryMatrix, 0, mModelMatrix, 0, 16);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mBrinkDataHandle);

        GLES20.glUniform1i(mTextureUniformHandle, 0);

        mCubeTexCoordinatesBuffer.position(0);
        GLES20.glVertexAttribPointer(mTexCoordinateHandle, mTexCoordinateDataSize, GLES20.GL_FLOAT, false
                , 0, mCubeTexCoordinatesBuffer);

        GLES20.glEnableVertexAttribArray(mTexCoordinateHandle);

        drawCube();

        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, 0.0f, -2.0f, -5.0f);
        Matrix.scaleM(mModelMatrix, 0, 25.0f, 1.0f, 25.0f);
        Matrix.rotateM(mModelMatrix, 0, slowAngleInDegrees, 0.0f, 1.0f, 0.0f);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mGrassDataHandle);

        GLES20.glUniform1i(mTextureUniformHandle, 0);

        mCubeTexCoordinatesForPlaneBuffer.position(0);

        GLES20.glVertexAttribPointer(mTexCoordinateHandle, mTexCoordinateDataSize, GLES20.GL_FLOAT, false,
                0, mCubeTexCoordinatesForPlaneBuffer);

        GLES20.glEnableVertexAttribArray(mTexCoordinateHandle);

        drawCube();

        GLES20.glUseProgram(mLightProgramHandle);
        drawLight();
    }

    public void setMinFilter(final int filter) {
        if (mBrinkDataHandle != 0 && mGrassDataHandle != 0) {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mBrinkDataHandle);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, filter);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mGrassDataHandle);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, filter);
        } else {
            mQueuedMinFilter = filter;
        }
    }

    public void setMagFilter(final int filter) {
        if (mBrinkDataHandle != 0 && mGrassDataHandle != 0) {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mBrinkDataHandle);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, filter);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mGrassDataHandle);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, filter);
        } else {
            mQueuedMagFilter = filter;
        }
    }

    private void drawLight() {
        int mLightPositionHandle = GLES20.glGetAttribLocation(mLightProgramHandle, "a_Position");
        int mLightMVPMatrixHandle = GLES20.glGetUniformLocation(mLightProgramHandle, "u_MVPMatrix");

        GLES20.glVertexAttrib3f(mLightPositionHandle, mLightPosInModelSpace[0], mLightPosInModelSpace[1], mLightPosInModelSpace[2]);

        GLES20.glDisableVertexAttribArray(mLightPositionHandle);

        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mLightModelMatrix, 0);
        Matrix.multiplyMM(mTemporaryMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
        System.arraycopy(mTemporaryMatrix, 0, mMVPMatrix, 0, 16);
        GLES20.glUniformMatrix4fv(mLightMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 1);
    }

    private void drawCube() {
        mCubePositionBuffer.position(0);
        GLES20.glVertexAttribPointer(mPositionHandle, mCubePositionDataSize, GLES20.GL_FLOAT, false,
                0, mCubePositionBuffer);
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        mCubeNormalBuffer.position(0);
        GLES20.glVertexAttribPointer(mNormalHandle, mCubePositionDataSize, GLES20.GL_FLOAT, false
                , 0, mCubeNormalBuffer);

        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        GLES20.glUniformMatrix4fv(mMVMatrixHandle, 1, false, mMVPMatrix, 0);

        Matrix.multiplyMM(mTemporaryMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
        System.arraycopy(mTemporaryMatrix, 0, mMVPMatrix, 0, 16);

        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        GLES20.glUniform3f(mLightPosHandle, mLightPosInEyeSpace[0], mLightPosInEyeSpace[1], mLightPosInEyeSpace[2]);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 36);

    }


}
