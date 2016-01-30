package uz.asamatdin.lesson_one.model;

import android.content.Context;
import android.opengl.GLES20;

import uz.asamatdin.lesson_one.TextureUtils;

/**
 * Shama menen 30.01.2016 sag'at 15:12
 * user ta'repten jaratildi. :)
 */
public class TextureModel extends MTexture {


    private final Context mContext;
    private int textureResId = -1;
    private String assetFileName;
    private String textureSuffix;
    private int textureHandle = -1;
    private int textureUniformHandle = -1;
    private final int target;

    public TextureModel(Context context, TextureType type, TextureMinFilterType minFilterType, TextureMagFilterType magFilterType, String fileName) {
        super(type, minFilterType, magFilterType);
        this.mContext = context;
        this.target = getTextureTarget();
        this.assetFileName = fileName;
        this.textureSuffix = getFileSuffix(TextureSuffix.PNG);

    }


    public TextureModel(Context context, TextureType type, TextureMinFilterType minFilterType, TextureMagFilterType magFilterType, int resId) {
        super(type, minFilterType, magFilterType);
        this.mContext = context;
        this.target = getTextureTarget();
        this.textureResId = resId;
        this.textureSuffix = getFileSuffix(TextureSuffix.PNG);

    }

    @Override
    public int createVbo() {
        if (assetFileName != null && !assetFileName.isEmpty())
            textureHandle = TextureUtils.loadTexture(mContext, assetFileName, textureSuffix);

        else if (textureResId > 0)
            textureHandle = TextureUtils.loadTexture(mContext, textureResId);

        return textureHandle;
    }

    @Override
    public void storeDataInAttributeList(Object[] data) {
        bindTexture();
    }

    public void bindTexture() {
        if (textureHandle > 0) {
            GLES20.glBindTexture(target, textureHandle);
            GLES20.glTexParameteri(target, GLES20.GL_TEXTURE_MIN_FILTER, getMinFilter());
            GLES20.glTexParameteri(target, GLES20.GL_TEXTURE_MAG_FILTER, getMagFilter());
            unbindVbo();
        }
    }

    public void activeTexture(int texturePosition) {
        if (textureUniformHandle > 0 && textureHandle > 0) {
            GLES20.glActiveTexture(texturePosition);
            GLES20.glBindTexture(target, textureHandle);
            GLES20.glUniform1i(textureUniformHandle, 0);
            unbindVbo();
        }
    }

    @Override
    public void unbindVbo() {
        GLES20.glBindTexture(target, 0);
    }

    @Override
    public void deleteVbo(int vboIdx) {
        int deletedVboIdx[] = new int[]{vboIdx};
        GLES20.glDeleteTextures(deletedVboIdx.length, deletedVboIdx, 0);
    }

    public int getTextureUniformHandle() {
        return textureUniformHandle;
    }

    public void setTextureUniformHandle(int textureUniformHandle) {
        this.textureUniformHandle = textureUniformHandle;
    }

    @Override
    public void clear() {
        unbindVbo();
        deleteVbo(textureHandle);
        textureHandle = 0;
        textureUniformHandle = 0;
    }
}
