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
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Shama menen 21.01.2016 sag'at 17:09
 * user ta'repten jaratildi. :)
 */
public class LessonEightRenderer implements GLSurfaceView.Renderer {

    private static final int POSITION_DATA_SIZE_IN_ELEMENTS = 3;
    private static final int NORMAL_DATA_SIZE_IN_ELEMENTS = 3;
    private static final int COLOR_DATA_SIZE_IN_ELEMENTS = 4;
    private static final int PER_BYTE_SIZE_IN_FLOAT = 4;
    private static final int PER_BYTE_SIZE_IN_SHORT = 2;
    private static final int STRIDE =
            (POSITION_DATA_SIZE_IN_ELEMENTS + NORMAL_DATA_SIZE_IN_ELEMENTS + COLOR_DATA_SIZE_IN_ELEMENTS) * PER_BYTE_SIZE_IN_FLOAT;

    private Context mContext;
    private AndroidGL20 gl20;
    private HeightMap heightMap;

    private int mProgramHandle;
    private int mPositionHandle;
    private int mNormalHandle;
    private int mMVPMatrixHandle;
    private int mMVMatrixHandle;
    private int mLightPosHandle;
    private int mColorHandle;
    private float[] mViewMatrix = new float[16];
    private float[] mAccumulatedRotationMatrix = new float[16];
    private float[] mProjectionMatrix = new float[16];
    private float[] mLightModelMatrix = new float[16];
    private float[] mLightPosInModelSpace = new float[]{0.0f, 0.0f, 0.0f, 1.0f};
    private float[] mLightPosInWorldSpace = new float[4];
    private float[] mLightPosInEyeSpace = new float[4];
    private float[] mCurrentRotationMatrix = new float[16];
    private float[] mTemporaryMatrix = new float[16];
    private float[] mModelMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];
    public volatile float mDeltaX;
    public volatile float mDeltaY;

    public LessonEightRenderer(Context context) {
        this.mContext = context;
        gl20 = new AndroidGL20();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        heightMap = new HeightMap();

        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

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

        int vertexShaderHandle = ShaderHelper.compileShader(mContext, GLES20.GL_VERTEX_SHADER, R.raw.lesson_eight_vertex_shader);
        int fragmentShaderHandle = ShaderHelper.compileShader(mContext, GLES20.GL_FRAGMENT_SHADER, R.raw.lesson_eight_fragment_shader);

        mProgramHandle = ShaderHelper.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle, "a_Position", "a_Normal", "a_Color");

        Matrix.setIdentityM(mAccumulatedRotationMatrix, 0);

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

        // Set our per-vertex lighting program.
        GLES20.glUseProgram(mProgramHandle);

        // Set program handles for cube drawing.
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_MVPMatrix");
        mMVMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_MVMatrix");
        mLightPosHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_LightPos");
        mPositionHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Position");
        mNormalHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Normal");
        mColorHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Color");

        // Calculate position of the light. Push into the distance.
        Matrix.setIdentityM(mLightModelMatrix, 0);
        Matrix.translateM(mLightModelMatrix, 0, 0.0f, 7.5f, -8.0f);

        Matrix.multiplyMV(mLightPosInWorldSpace, 0, mLightModelMatrix, 0, mLightPosInModelSpace, 0);
        Matrix.multiplyMV(mLightPosInEyeSpace, 0, mViewMatrix, 0, mLightPosInWorldSpace, 0);

        // Draw the heightmap.
        // Translate the heightmap into the screen.
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, 0.0f, 0.0f, -12f);

        // Set a matrix that contains the current rotation.
        Matrix.setIdentityM(mCurrentRotationMatrix, 0);
        Matrix.rotateM(mCurrentRotationMatrix, 0, mDeltaX, 0.0f, 1.0f, 0.0f);
        Matrix.rotateM(mCurrentRotationMatrix, 0, mDeltaY, 1.0f, 0.0f, 0.0f);
        mDeltaX = 0.0f;
        mDeltaY = 0.0f;

        // Multiply the current rotation by the accumulated rotation, and then
        // set the accumulated rotation to the result.
        Matrix.multiplyMM(mTemporaryMatrix, 0, mCurrentRotationMatrix, 0, mAccumulatedRotationMatrix, 0);
        System.arraycopy(mTemporaryMatrix, 0, mAccumulatedRotationMatrix, 0, 16);

        // Rotate the cube taking the overall rotation into account.
        Matrix.multiplyMM(mTemporaryMatrix, 0, mModelMatrix, 0, mAccumulatedRotationMatrix, 0);
        System.arraycopy(mTemporaryMatrix, 0, mModelMatrix, 0, 16);

        // This multiplies the view matrix by the model matrix, and stores
        // the result in the MVP matrix
        // (which currently contains model * view).
        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);

        // Pass in the modelview matrix.
        GLES20.glUniformMatrix4fv(mMVMatrixHandle, 1, false, mMVPMatrix, 0);

        // This multiplies the modelview matrix by the projection matrix,
        // and stores the result in the MVP matrix
        // (which now contains model * view * projection).
        Matrix.multiplyMM(mTemporaryMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
        System.arraycopy(mTemporaryMatrix, 0, mMVPMatrix, 0, 16);

        // Pass in the combined matrix.
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        // Pass in the light position in eye space.
        GLES20.glUniform3f(mLightPosHandle, mLightPosInEyeSpace[0], mLightPosInEyeSpace[1], mLightPosInEyeSpace[2]);

        heightMap.render();
    }

    private class HeightMap {

        private final int SIZE_PER_SIDE = 32;
        private final float MIN_POSITION = -5f;
        private final float POSITION_RANGE = 10f;

        int[] vboIdx = new int[1];
        int[] iboIdx = new int[1];


        int indexCount;

        public HeightMap() {

                final int floatsPerVertex = POSITION_DATA_SIZE_IN_ELEMENTS + NORMAL_DATA_SIZE_IN_ELEMENTS
                        + COLOR_DATA_SIZE_IN_ELEMENTS;
                final int xLength = SIZE_PER_SIDE;
                final int yLength = SIZE_PER_SIDE;

                final float[] heightMapVertexData = new float[xLength * yLength * floatsPerVertex];

                int offset = 0;

                // First, build the data for the vertex buffer
                for (int y = 0; y < yLength; y++) {
                    for (int x = 0; x < xLength; x++) {
                        final float xRatio = x / (float) (xLength - 1);

                        // Build our heightmap from the top down, so that our triangles are counter-clockwise.
                        final float yRatio = 1f - (y / (float) (yLength - 1));

                        final float xPosition = MIN_POSITION + (xRatio * POSITION_RANGE);
                        final float yPosition = MIN_POSITION + (yRatio * POSITION_RANGE);

                        // Position
                        heightMapVertexData[offset++] = xPosition;
                        heightMapVertexData[offset++] = yPosition;
                        heightMapVertexData[offset++] = ((xPosition * xPosition) + (yPosition * yPosition)) / 10f;

                        // Cheap normal using a derivative of the function.
                        // The slope for X will be 2X, for Y will be 2Y.
                        // Divide by 10 since the position's Z is also divided by 10.
                        final float xSlope = (2 * xPosition) / 10f;
                        final float ySlope = (2 * yPosition) / 10f;

                        // Calculate the normal using the cross product of the slopes.
                        final float[] planeVectorX = {1f, 0f, xSlope};
                        final float[] planeVectorY = {0f, 1f, ySlope};
                        final float[] normalVector = {
                                (planeVectorX[1] * planeVectorY[2]) - (planeVectorX[2] * planeVectorY[1]),
                                (planeVectorX[2] * planeVectorY[0]) - (planeVectorX[0] * planeVectorY[2]),
                                (planeVectorX[0] * planeVectorY[1]) - (planeVectorX[1] * planeVectorY[0])};

                        // Normalize the normal
                        final float length = Matrix.length(normalVector[0], normalVector[1], normalVector[2]);

                        heightMapVertexData[offset++] = normalVector[0] / length;
                        heightMapVertexData[offset++] = normalVector[1] / length;
                        heightMapVertexData[offset++] = normalVector[2] / length;

                        // Add some fancy colors.
                        heightMapVertexData[offset++] = xRatio;
                        heightMapVertexData[offset++] = yRatio;
                        heightMapVertexData[offset++] = 0.5f;
                        heightMapVertexData[offset++] = 1f;
                    }
                }

                // Now build the index data
                final int numStripsRequired = yLength - 1;
                final int numDegensRequired = 2 * (numStripsRequired - 1);
                final int verticesPerStrip = 2 * xLength;

                final short[] heightMapIndexData = new short[(verticesPerStrip * numStripsRequired) + numDegensRequired];

                offset = 0;

                for (int y = 0; y < yLength - 1; y++) {
                    if (y > 0) {
                        // Degenerate begin: repeat first vertex
                        heightMapIndexData[offset++] = (short) (y * yLength);
                    }

                    for (int x = 0; x < xLength; x++) {
                        // One part of the strip
                        heightMapIndexData[offset++] = (short) ((y * yLength) + x);
                        heightMapIndexData[offset++] = (short) (((y + 1) * yLength) + x);
                    }

                    if (y < yLength - 2) {
                        // Degenerate end: repeat last vertex
                        heightMapIndexData[offset++] = (short) (((y + 1) * yLength) + (xLength - 1));
                    }
                }
                indexCount = heightMapIndexData.length;

                FloatBuffer vertexBuffer = ByteBuffer.allocateDirect(heightMapVertexData.length * PER_BYTE_SIZE_IN_FLOAT)
                        .order(ByteOrder.nativeOrder()).asFloatBuffer();
                vertexBuffer.put(heightMapVertexData).position(0);

                ShortBuffer indexBuffer = ByteBuffer.allocateDirect(heightMapIndexData.length * PER_BYTE_SIZE_IN_SHORT)
                        .order(ByteOrder.nativeOrder()).asShortBuffer();
                indexBuffer.put(heightMapIndexData).position(0);

                GLES20.glGenBuffers(1, vboIdx, 0);
                GLES20.glGenBuffers(1, iboIdx, 0);

                if (iboIdx[0] > 0 && vboIdx[0] > 0) {
                    GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboIdx[0]);
                    GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexBuffer.capacity() * PER_BYTE_SIZE_IN_FLOAT,
                            vertexBuffer, GLES20.GL_STATIC_DRAW);

                    GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, iboIdx[0]);
                    GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, indexBuffer.capacity() * PER_BYTE_SIZE_IN_SHORT,
                            indexBuffer, GLES20.GL_STATIC_DRAW);

                    GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
                    GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
                } else {
                    // TODO: 22.01.2016 update ui
                }




        }

        public void render() {
            if (vboIdx[0] > 0 && iboIdx[0] > 0) {
                GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboIdx[0]);
                gl20.glVertexAttribPointer(mPositionHandle, POSITION_DATA_SIZE_IN_ELEMENTS, GL20.GL_FLOAT, false,
                        STRIDE, 0);
                GLES20.glEnableVertexAttribArray(mPositionHandle);

                gl20.glVertexAttribPointer(mNormalHandle, NORMAL_DATA_SIZE_IN_ELEMENTS, GL20.GL_FLOAT, false,
                        STRIDE, POSITION_DATA_SIZE_IN_ELEMENTS * PER_BYTE_SIZE_IN_FLOAT);
                GLES20.glEnableVertexAttribArray(mNormalHandle);

                gl20.glVertexAttribPointer(mColorHandle, COLOR_DATA_SIZE_IN_ELEMENTS, GL20.GL_FLOAT, false,
                        STRIDE, (POSITION_DATA_SIZE_IN_ELEMENTS + NORMAL_DATA_SIZE_IN_ELEMENTS) * PER_BYTE_SIZE_IN_FLOAT);
                GLES20.glEnableVertexAttribArray(mColorHandle);

                GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, iboIdx[0]);
                gl20.glDrawElements(GL20.GL_TRIANGLE_STRIP, indexCount, GL20.GL_UNSIGNED_SHORT, 0);

                GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
                GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
            }
        }

        public void release() {
            if (vboIdx[0] > 0) {
                GLES20.glDeleteBuffers(vboIdx.length, vboIdx, 0);
                vboIdx[0] = 0;
            }

            if (iboIdx[0] > 0) {
                GLES20.glDeleteBuffers(iboIdx.length, iboIdx, 0);
                iboIdx[0] = 0;
            }
        }


    }
}
