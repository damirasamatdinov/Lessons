package uz.asamatdin.lesson_one.model;

import android.opengl.GLES20;


/**
 * Shama menen 30.01.2016 sag'at 15:44
 * user ta'repten jaratildi. :)
 */
public abstract class MTexture implements IModel {

    public static final String PNG_TEXTURE = ".png";
    public static final String JPG_TEXTURE = ".jpg";

    public MTexture() {
        this(TextureType.TEXTURE_2D);
    }

    public MTexture(TextureType target) {
        this(target, TextureMinFilterType.NEAREST, TextureMagFilterType.NEAREST);
    }

    public MTexture(TextureType target, TextureMinFilterType minFilterType, TextureMagFilterType magFilterType) {
        this.mTarget = target;
        this.mMinFilterType = minFilterType;
        this.mMagFilterType = magFilterType;
    }

    public enum TextureType {
        TEXTURE_1D,
        TEXTURE_2D,
        TEXTURE_3D,
        TEXTURE_RECTANGLE,
        TEXTURE_BUFFER,
        TEXTURE_CUBE_MAP,
        TEXTURE_1D_ARRAY,
        TEXTURE_2D_ARRAY,
        TEXTURE_CUBE_MAP_ARRAY,
        TEXTURE_2D_MULTISAMPLE,
        TEXTURE_2D_MULTISAMPLE_ARRAY
    }

    public enum TextureMinFilterType {
        NEAREST,
        LINEAR,
        NEAREST_MIPMAP_NEAREST,
        NEAREST_MIPMAP_LINEAR,
        LINEAR_MIPMAP_NEAREST,
        LINEAR_MIPMAP_LINEAR
    }

    public enum TextureMagFilterType {
        NEAREST,
        LINEAR
    }

    public enum TextureSuffix {
        PNG,
        JPG
    }

    @Override
    public void storeDataInAttributeList(Object[] data) {

    }

    protected TextureType mTarget;
    protected TextureMinFilterType mMinFilterType;
    protected TextureMagFilterType mMagFilterType;

    public int getTextureTarget(TextureType type) {
        switch (type) {
            case TEXTURE_2D:
                return GLES20.GL_TEXTURE_2D;
            case TEXTURE_CUBE_MAP:
                return GLES20.GL_TEXTURE_CUBE_MAP;
            default:
                return GLES20.GL_TEXTURE_2D;
        }
    }

    public int getMinFilter(TextureMinFilterType type) {
        switch (type) {
            case NEAREST:
                return GLES20.GL_NEAREST;
            case LINEAR:
                return GLES20.GL_LINEAR;
            case NEAREST_MIPMAP_LINEAR:
                return GLES20.GL_NEAREST_MIPMAP_LINEAR;
            case NEAREST_MIPMAP_NEAREST:
                return GLES20.GL_NEAREST_MIPMAP_NEAREST;
            case LINEAR_MIPMAP_LINEAR:
                return GLES20.GL_LINEAR_MIPMAP_LINEAR;
            case LINEAR_MIPMAP_NEAREST:
                return GLES20.GL_LINEAR_MIPMAP_NEAREST;
            default:
                return GLES20.GL_NEAREST;
        }
    }

    public int getMagFilter(TextureMagFilterType type) {
        switch (type) {
            case NEAREST:
                return GLES20.GL_NEAREST;
            case LINEAR:
                return GLES20.GL_LINEAR;
            default:
                return GLES20.GL_NEAREST;
        }
    }

    protected int getTextureTarget() {
        return getTextureTarget(mTarget);
    }

    protected int getMinFilter() {
        return getMinFilter(mMinFilterType);
    }

    protected int getMagFilter() {
        return getMagFilter(mMagFilterType);
    }

    public String getFileSuffix(TextureSuffix suffix) {
        switch (suffix) {
            case PNG:
                return PNG_TEXTURE;
            case JPG:
                return JPG_TEXTURE;
            default:
                return PNG_TEXTURE;
        }
    }
}
