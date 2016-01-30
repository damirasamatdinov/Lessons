package uz.asamatdin.lesson_one;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.badlogic.gdx.backends.android.AndroidGL20;
import com.badlogic.gdx.graphics.GL20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Shama menen 21.01.2016 sag'at 10:25
 * user ta'repten jaratildi. :)
 */
public class LessonSevenRenderer implements GLSurfaceView.Renderer {


    private final LessonSevenActivity mContext;
    private final GLSurfaceView mGlSurfaceView;
    private final AndroidGL20 gl20;
    private Cubes mCubes;

    private boolean mUseVBOs = true;

    private boolean mUseStride = true;
    private int perByteForFloat = 4;

    private int mCubePositionDataSize = 3;
    private int mCubeNormalDataSize = 3;
    private int mCubeTexCoordinateDataSize = 2;

    private int programHandle;
    private int lightProgramHandle;
    private int mTextureHandle;
    private int mPositionHandle;
    private int mNormalHandle;
    private int mMVPMatrixHandle;
    private int mMVMatrixHandle;
    private int mTextureUniformHandle;
    private int mLightPosHandle;
    private int mTexCoordinateHandle;
    private int mActualCubeFactor;
    private int mLastRequestedCubesFactor;
    private float[] mProjectionMatrix = new float[16];
    private float[] mViewMatrix = new float[16];
    private float[] mAccumulatedRotation = new float[16];
    private float[] mModelMatrix = new float[16];
    private float[] mTemporaryMatrix = new float[16];
    private float[] mCurrentRotationMatrix = new float[16];
    private float[] mLightModelMatrix = new float[16];
    private float[] mLightPosInModelSpace = {0.0f, 0.0f, 0.0f, 1.0f};
    private float[] mLightPosInWorldSpace = new float[4];
    private float[] mLightPosInEyeSpace = new float[4];
    private float[] mMVPMatrix = new float[16];
    public volatile float mDeltaX;
    public volatile float mDeltaY;

    private ExecutorService singleThreadedExecutor = Executors.newSingleThreadExecutor();

    public LessonSevenRenderer(LessonSevenActivity context, GLSurfaceView glSurfaceView) {
        this.mContext = context;
        this.mGlSurfaceView = glSurfaceView;
        gl20 = new AndroidGL20();
    }

    private void generateCubes(int cubeFactor, boolean toggleVBO, boolean toggleStride) {
        singleThreadedExecutor.submit(new GetDataRunnable(cubeFactor, toggleVBO, toggleStride));
    }


    private class GetDataRunnable implements Runnable {

        private int mRequestedCubeFactor;
        private boolean toggleVBOs;
        private boolean toggleStrike;

        public GetDataRunnable(int requestedCubeFactor, boolean toggleVBOs, boolean toggleStrike) {
            this.mRequestedCubeFactor = requestedCubeFactor;
            this.toggleVBOs = toggleVBOs;
            this.toggleStrike = toggleStrike;
        }

        @Override
        public void run() {
            try {


                final float[] cubeNormalData = {
                        //frond face
                        0.0f, 0.0f, 1.0f,
                        0.0f, 0.0f, 1.0f,
                        0.0f, 0.0f, 1.0f,
                        0.0f, 0.0f, 1.0f,
                        0.0f, 0.0f, 1.0f,
                        0.0f, 0.0f, 1.0f,

                        //right face
                        1.0f, 0.0f, 0.0f,
                        1.0f, 0.0f, 0.0f,
                        1.0f, 0.0f, 0.0f,
                        1.0f, 0.0f, 0.0f,
                        1.0f, 0.0f, 0.0f,
                        1.0f, 0.0f, 0.0f,

                        //back face
                        0.0f, 0.0f, -1.0f,
                        0.0f, 0.0f, -1.0f,
                        0.0f, 0.0f, -1.0f,
                        0.0f, 0.0f, -1.0f,
                        0.0f, 0.0f, -1.0f,
                        0.0f, 0.0f, -1.0f,

                        //left face
                        -1.0f, 0.0f, 0.0f,
                        -1.0f, 0.0f, 0.0f,
                        -1.0f, 0.0f, 0.0f,
                        -1.0f, 0.0f, 0.0f,
                        -1.0f, 0.0f, 0.0f,
                        -1.0f, 0.0f, 0.0f,

                        //top face
                        0.0f, 1.0f, 0.0f,
                        0.0f, 1.0f, 0.0f,
                        0.0f, 1.0f, 0.0f,
                        0.0f, 1.0f, 0.0f,
                        0.0f, 1.0f, 0.0f,
                        0.0f, 1.0f, 0.0f,

                        //bottom face
                        0.0f, -1.0f, 0.0f,
                        0.0f, -1.0f, 0.0f,
                        0.0f, -1.0f, 0.0f,
                        0.0f, -1.0f, 0.0f,
                        0.0f, -1.0f, 0.0f,
                        0.0f, -1.0f, 0.0f,
                };

                final float[] cubeTexCoordinateData = {
                        //Frond face
                        0.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 1.0f,
                        1.0f, 0.0f,

                        //Right face
                        0.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 1.0f,
                        1.0f, 0.0f,

                        //Back face
                        0.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 1.0f,
                        1.0f, 0.0f,

                        //Left face
                        0.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 1.0f,
                        1.0f, 0.0f,

                        //Top face
                        0.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 1.0f,
                        1.0f, 0.0f,

                        //Bottom face
                        0.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 1.0f,
                        1.0f, 0.0f,
                };

                final float[] cubePositionData = new float[108 * mRequestedCubeFactor * mRequestedCubeFactor * mRequestedCubeFactor];
                int cubePositionDataOffset = 0;

                float minPosition = -1.0f;
                float maxPosition = 1.0f;
                float positionRange = maxPosition - minPosition;

                float segments = mRequestedCubeFactor + (mRequestedCubeFactor - 1);

                for (int x = 0; x < mRequestedCubeFactor; x++) {
                    for (int y = 0; y < mRequestedCubeFactor; y++) {
                        for (int z = 0; z < mRequestedCubeFactor; z++) {

                            final float x1 = minPosition + ((positionRange / segments) * (x * 2));
                            final float x2 = minPosition + ((positionRange / segments) * ((x * 2) + 1));

                            final float y1 = minPosition + ((positionRange / segments) * (y * 2));
                            final float y2 = minPosition + ((positionRange / segments) * ((y * 2) + 1));

                            final float z1 = minPosition + ((positionRange / segments) * (z * 2));
                            final float z2 = minPosition + ((positionRange / segments) * ((z * 2) + 1));

                            final float[] p1p = {x1, y2, z2};
                            final float[] p2p = {x2, y2, z2};
                            final float[] p3p = {x1, y1, z2};
                            final float[] p4p = {x2, y1, z2};
                            final float[] p5p = {x1, y2, z1};
                            final float[] p6p = {x2, y2, z1};
                            final float[] p7p = {x1, y1, z1};
                            final float[] p8p = {x2, y1, z1};

                            final float[] currentCubePositionData = ShapeBuilder.generateCubeData(p1p,
                                    p2p, p3p, p4p, p5p, p6p, p7p, p8p, p1p.length);

                            System.arraycopy(currentCubePositionData, 0, cubePositionData,
                                    cubePositionDataOffset, currentCubePositionData.length);
                            cubePositionDataOffset += currentCubePositionData.length;
                        }
                    }
                }

                mGlSurfaceView.queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        if (mCubes != null) {
                            mCubes.release();
                            mCubes = null;
                        }

                        boolean useVBOs = mUseVBOs;
                        boolean useStride = mUseStride;

                        System.gc();
                        try {

                            if (toggleVBOs)
                                useVBOs = !useVBOs;

                            if (toggleStrike)
                                useStride = !useStride;

                            if (useStride) {
                                if (useVBOs) {
                                    mCubes = new CubeWithVBOWithStride(cubePositionData, cubeNormalData, cubeTexCoordinateData, mRequestedCubeFactor);
                                } else {
                                    mCubes = new CubeClientSideWithStride(cubePositionData, cubeNormalData, cubeTexCoordinateData, mRequestedCubeFactor);
                                }
                            } else {
                                if (useVBOs) {
                                    mCubes = new CubeWithVBO(cubePositionData, cubeNormalData, cubeTexCoordinateData, mRequestedCubeFactor);
                                } else {
                                    mCubes = new CubeClientSide(cubePositionData, cubeNormalData, cubeTexCoordinateData, mRequestedCubeFactor);
                                }
                            }

                            mActualCubeFactor = mRequestedCubeFactor;

                            mUseVBOs = useVBOs;
                            mContext.updateVboStatus(mUseVBOs);

                            mUseStride = useStride;
                            mContext.updateStrideStatus(mUseStride);

                        } catch (OutOfMemoryError error) {
                            if (mCubes != null) {
                                mCubes.release();
                                mCubes = null;
                            }

                            System.gc();

                            // TODO: 21.01.2016 update ui Crashed Heap
                        }


                    }
                });

            } catch (OutOfMemoryError error) {
                System.gc();

                // TODO: 21.01.2016 update ui Crashed Heap
            }
        }
    }

    public void decreaseCubeCount() {
        if (mLastRequestedCubesFactor > 1) {
            generateCubes(--mLastRequestedCubesFactor, false, false);
        }
    }

    public void increaseCubeCount() {
        if (mLastRequestedCubesFactor < 16) {
            generateCubes(++mLastRequestedCubesFactor, false, false);
        }
    }

    public void toggleVBO() {
        generateCubes(mLastRequestedCubesFactor, true, false);
    }

    public void toggleStride() {
        generateCubes(mLastRequestedCubesFactor, false, true);
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {


        mLastRequestedCubesFactor = mActualCubeFactor = 3;
        generateCubes(mLastRequestedCubesFactor, false, false);

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

        int vertexShaderHandle = ShaderHelper.compileShader(mContext, GLES20.GL_VERTEX_SHADER, R.raw.lesson_seven_vertex_shader);
        int fragmentShaderHandle = ShaderHelper.compileShader(mContext, GLES20.GL_FRAGMENT_SHADER, R.raw.lesson_seven_fragment_shader);

        programHandle = ShaderHelper.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle, "a_Position", "a_Normal", "a_TexCoordinate");

        mTextureHandle = TextureUtils.loadTexture(mContext, R.drawable.usb_android);

        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureHandle);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureHandle);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);

        Matrix.setIdentityM(mAccumulatedRotation, 0);

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
        final float far = 1000.0f;
        Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        GLES20.glUseProgram(programHandle);

        mMVPMatrixHandle = GLES20.glGetUniformLocation(programHandle, "u_MVPMatrix");
        mMVMatrixHandle = GLES20.glGetUniformLocation(programHandle, "u_MVMatrix");
        mTextureUniformHandle = GLES20.glGetUniformLocation(programHandle, "u_Texture");
        mLightPosHandle = GLES20.glGetUniformLocation(programHandle, "u_LightPos");
        mPositionHandle = GLES20.glGetAttribLocation(programHandle, "a_Position");
        mNormalHandle = GLES20.glGetAttribLocation(programHandle, "a_Normal");
        mTexCoordinateHandle = GLES20.glGetAttribLocation(programHandle, "a_TexCoordinate");

        Matrix.setIdentityM(mLightModelMatrix, 0);
        Matrix.translateM(mLightModelMatrix, 0, 0.0f, 0.0f, -1.0f);

        Matrix.multiplyMV(mLightPosInWorldSpace, 0, mLightModelMatrix, 0, mLightPosInModelSpace, 0);
        Matrix.multiplyMV(mLightPosInEyeSpace, 0, mViewMatrix, 0, mLightPosInWorldSpace, 0);

        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, 0.0f, 0.0f, -3.5f);

        Matrix.setIdentityM(mCurrentRotationMatrix, 0);
        Matrix.rotateM(mCurrentRotationMatrix, 0, mDeltaX, 0.0f, 1.0f, 0.0f);
        Matrix.rotateM(mCurrentRotationMatrix, 0, mDeltaY, 1.0f, 0.0f, 0.0f);

        mDeltaX = 0.0f;
        mDeltaY = 0.0f;

        Matrix.multiplyMM(mTemporaryMatrix, 0, mCurrentRotationMatrix, 0, mAccumulatedRotation, 0);
        System.arraycopy(mTemporaryMatrix, 0, mAccumulatedRotation, 0, 16);

        Matrix.multiplyMM(mTemporaryMatrix, 0, mModelMatrix, 0, mAccumulatedRotation, 0);
        System.arraycopy(mTemporaryMatrix, 0, mModelMatrix, 0, 16);

        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);

        GLES20.glUniformMatrix4fv(mMVMatrixHandle, 1, false, mMVPMatrix, 0);

        Matrix.multiplyMM(mTemporaryMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
        System.arraycopy(mTemporaryMatrix, 0, mMVPMatrix, 0, 16);

        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        GLES20.glUniform3f(mLightPosHandle, mLightPosInEyeSpace[0], mLightPosInEyeSpace[1], mLightPosInEyeSpace[2]);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureHandle);

        GLES20.glUniform1i(mTextureUniformHandle, 0);

        if (mCubes != null)
            mCubes.render();
    }

    private abstract class Cubes {
        abstract void release();

        abstract void render();

        FloatBuffer[] getBuffers(float[] cubePositionData, float[] cubeNormalData, float[] cubeTexCoordinateData, int generatedCubeFactor) {

            FloatBuffer cubePosBuffer = ByteBuffer.allocateDirect(cubePositionData.length * perByteForFloat)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();
            cubePosBuffer.put(cubePositionData).position(0);

            int totalCubes = generatedCubeFactor * generatedCubeFactor * generatedCubeFactor;

            FloatBuffer cubeNorBuffer = ByteBuffer.allocateDirect(cubeNormalData.length *
                    totalCubes * perByteForFloat)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();

            for (int i = 0; i < totalCubes; i++) {
                cubeNorBuffer.put(cubeNormalData);
            }
            cubeNorBuffer.position(0);

            FloatBuffer cubeTexBuffer = ByteBuffer.allocateDirect(cubeTexCoordinateData.length * perByteForFloat * totalCubes)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();
            for (int i = 0; i < totalCubes; i++) {
                cubeTexBuffer.put(cubeTexCoordinateData);
            }
            cubeTexBuffer.position(0);

            return new FloatBuffer[]{cubePosBuffer, cubeNorBuffer, cubeTexBuffer};
        }

        FloatBuffer getStrideBuffer(float[] cubePositionData, float[] cubeNormalData, float[] cubeTexCoordinateData, int generatedCubeFactor) {
            int totalCubes = generatedCubeFactor * generatedCubeFactor * generatedCubeFactor;
            int cubeStrideLength = cubePositionData.length +
                    (cubeNormalData.length * totalCubes) +
                    (cubeTexCoordinateData.length * totalCubes);

            int cubePosOffset = 0;
            int cubeNorOffset = 0;
            int cubeTexOffset = 0;

            FloatBuffer cubeBuffer = ByteBuffer.allocateDirect(cubeStrideLength * perByteForFloat)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();

            for (int i = 0; i < totalCubes; i++) {
                for (int j = 0; j < 36; j++) {
                    cubeBuffer.put(cubePositionData, cubePosOffset, mCubePositionDataSize);
                    cubePosOffset += mCubePositionDataSize;
                    cubeBuffer.put(cubeNormalData, cubeNorOffset, mCubeNormalDataSize);
                    cubeNorOffset += mCubeNormalDataSize;
                    cubeBuffer.put(cubeTexCoordinateData, cubeTexOffset, mCubeTexCoordinateDataSize);
                    cubeTexOffset += mCubeTexCoordinateDataSize;
                }
                cubeNorOffset = 0;
                cubeTexOffset = 0;
            }

            cubeBuffer.position(0);
            return cubeBuffer;
        }
    }

    private class CubeClientSide extends Cubes {
        private FloatBuffer mCubePositionBuffer;
        private FloatBuffer mCubeNormalBuffer;
        private FloatBuffer mCubeTexCoordinateBuffer;

        public CubeClientSide(float[] cubePositionData, float[] cubeNormalData, float[] cubeTexCoordinateData, int generatedCubeFactor) {
            FloatBuffer[] buffers = getBuffers(cubePositionData, cubeNormalData, cubeTexCoordinateData, generatedCubeFactor);
            mCubePositionBuffer = buffers[0];
            mCubeNormalBuffer = buffers[1];
            mCubeTexCoordinateBuffer = buffers[2];
        }


        @Override
        void release() {
            mCubePositionBuffer.limit(0);
            mCubePositionBuffer = null;
            mCubeNormalBuffer.limit(0);
            mCubeNormalBuffer = null;
            mCubeTexCoordinateBuffer.limit(0);
            mCubeTexCoordinateBuffer = null;
        }

        @Override
        void render() {

            GLES20.glEnableVertexAttribArray(mPositionHandle);
            GLES20.glVertexAttribPointer(mPositionHandle, mCubePositionDataSize, GLES20.GL_FLOAT,
                    false, 0, mCubePositionBuffer);

            GLES20.glEnableVertexAttribArray(mNormalHandle);
            GLES20.glVertexAttribPointer(mNormalHandle, mCubeNormalDataSize, GLES20.GL_FLOAT,
                    false, 0, mCubeNormalBuffer);

            GLES20.glEnableVertexAttribArray(mTexCoordinateHandle);
            GLES20.glVertexAttribPointer(mTexCoordinateHandle, mCubeTexCoordinateDataSize, GLES20.GL_FLOAT,
                    false, 0, mCubeTexCoordinateBuffer);

            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 36 * mActualCubeFactor * mActualCubeFactor * mActualCubeFactor);
        }
    }

    private class CubeClientSideWithStride extends Cubes {

        FloatBuffer cubeBuffer;

        public CubeClientSideWithStride(float[] cubePositionData, float[] cubeNormalData, float[] cubeTexCoordinateData, int generatedCubeFactor) {
            cubeBuffer = getStrideBuffer(cubePositionData, cubeNormalData, cubeTexCoordinateData, generatedCubeFactor);
        }

        @Override
        void release() {
            cubeBuffer.limit(0);
            cubeBuffer = null;
        }

        @Override
        void render() {
            final int stride = (mCubeNormalDataSize + mCubePositionDataSize + mCubeTexCoordinateDataSize) * perByteForFloat;

            cubeBuffer.position(0);
            GLES20.glEnableVertexAttribArray(mPositionHandle);
            GLES20.glVertexAttribPointer(mPositionHandle, mCubePositionDataSize, GLES20.GL_FLOAT, false, stride, cubeBuffer);

            cubeBuffer.position(mCubePositionDataSize);
            GLES20.glEnableVertexAttribArray(mNormalHandle);
            GLES20.glVertexAttribPointer(mNormalHandle, mCubeNormalDataSize, GLES20.GL_FLOAT, false,
                    stride, cubeBuffer);

            cubeBuffer.position(mCubePositionDataSize + mCubeNormalDataSize);
            GLES20.glEnableVertexAttribArray(mTexCoordinateHandle);
            GLES20.glVertexAttribPointer(mTexCoordinateHandle, mCubeTexCoordinateDataSize, GLES20.GL_FLOAT,
                    false, stride, cubeBuffer);

            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 36 * mActualCubeFactor * mActualCubeFactor * mActualCubeFactor);
        }
    }

    private class CubeWithVBO extends Cubes {

        private final int mCubePositionBufferIdx;
        private final int mCubeNormalBufferIdx;
        private final int mCubeTexCoordinateBufferIdx;

        public CubeWithVBO(float[] cubePositionData, float[] cubeNormalData, float[] cubeTexCoordinateData, int generatedCubeFactor) {
            FloatBuffer[] buffers = getBuffers(cubePositionData, cubeNormalData, cubeTexCoordinateData, generatedCubeFactor);
            FloatBuffer mCubePositionBuffer = buffers[0];
            FloatBuffer mCubeNormalBuffer = buffers[1];
            FloatBuffer mCubeTexCoordinateBuffer = buffers[2];

            final int[] bufferIdx = new int[3];
            GLES20.glGenBuffers(3, bufferIdx, 0);

            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, bufferIdx[0]);
            GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, mCubePositionBuffer.capacity() * perByteForFloat,
                    mCubePositionBuffer, GLES20.GL_STATIC_DRAW);

            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, bufferIdx[1]);
            GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, mCubeNormalBuffer.capacity() * perByteForFloat,
                    mCubeNormalBuffer, GLES20.GL_STATIC_DRAW);


            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, bufferIdx[2]);
            GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, mCubeTexCoordinateBuffer.capacity() * perByteForFloat,
                    mCubeTexCoordinateBuffer, GLES20.GL_STATIC_DRAW);

            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

            mCubePositionBufferIdx = bufferIdx[0];
            mCubeNormalBufferIdx = bufferIdx[1];
            mCubeTexCoordinateBufferIdx = bufferIdx[2];

            mCubeNormalBuffer.limit(0);
            mCubeNormalBuffer = null;
            mCubePositionBuffer.limit(0);
            mCubePositionBuffer = null;
            mCubeTexCoordinateBuffer.limit(0);
            mCubeTexCoordinateBuffer = null;
        }

        @Override
        void release() {
            int[] deletedBuffers = {mCubePositionBufferIdx, mCubeNormalBufferIdx, mCubeTexCoordinateBufferIdx};
            GLES20.glDeleteBuffers(deletedBuffers.length, deletedBuffers, 0);
        }

        @Override
        void render() {
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mCubePositionBufferIdx);
            GLES20.glEnableVertexAttribArray(mPositionHandle);
            gl20.glVertexAttribPointer(mPositionHandle, mCubePositionDataSize, GL20.GL_FLOAT, false,
                    0, 0);

            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mCubeNormalBufferIdx);
            GLES20.glEnableVertexAttribArray(mNormalHandle);
            gl20.glVertexAttribPointer(mNormalHandle, mCubeNormalDataSize, GL20.GL_FLOAT, false, 0, 0);

            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mCubeTexCoordinateBufferIdx);
            GLES20.glEnableVertexAttribArray(mTexCoordinateHandle);
            gl20.glVertexAttribPointer(mTexCoordinateHandle, mCubeTexCoordinateDataSize, GL20.GL_FLOAT,
                    false, 0, 0);

            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 36 * mActualCubeFactor * mActualCubeFactor * mActualCubeFactor);
        }
    }

    private class CubeWithVBOWithStride extends Cubes {

        private final int mCubeBufferIdx;

        public CubeWithVBOWithStride(float[] cubePositionData, float[] cubeNormalData, float[] cubeTexCoordinateData, int generatedCubeFactor) {
            FloatBuffer buffer = getStrideBuffer(cubePositionData, cubeNormalData, cubeTexCoordinateData, generatedCubeFactor);

            int[] buffersIdx = new int[1];
            GLES20.glGenBuffers(1, buffersIdx, 0);

            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffersIdx[0]);
            GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, buffer.capacity() * perByteForFloat, buffer,
                    GLES20.GL_STATIC_DRAW);

            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

            mCubeBufferIdx = buffersIdx[0];

            buffer.limit(0);
            buffer = null;
        }

        @Override
        void release() {
            int[] deletedBuffers = {mCubeBufferIdx};
            GLES20.glDeleteBuffers(deletedBuffers.length, deletedBuffers, 0);
        }

        @Override
        void render() {
            int stride = (mCubeNormalDataSize + mCubeTexCoordinateDataSize + mCubePositionDataSize) * perByteForFloat;

            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mCubeBufferIdx);
            GLES20.glEnableVertexAttribArray(mPositionHandle);
            gl20.glVertexAttribPointer(mPositionHandle, mCubePositionDataSize, GL20.GL_FLOAT,
                    false, stride, 0);

            GLES20.glEnableVertexAttribArray(mNormalHandle);
            gl20.glVertexAttribPointer(mNormalHandle, mCubeNormalDataSize, GL20.GL_FLOAT,
                    false, stride, mCubePositionDataSize * perByteForFloat);

            GLES20.glEnableVertexAttribArray(mTexCoordinateHandle);
            gl20.glVertexAttribPointer(mTexCoordinateHandle, mCubeTexCoordinateDataSize, GL20.GL_FLOAT,
                    false, stride, (mCubePositionDataSize + mCubeNormalDataSize) * perByteForFloat);

            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 36 * mActualCubeFactor * mActualCubeFactor * mActualCubeFactor);
        }
    }
}
