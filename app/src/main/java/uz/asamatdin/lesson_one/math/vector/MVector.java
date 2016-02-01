package uz.asamatdin.lesson_one.math.vector;

import java.io.Serializable;
import java.nio.FloatBuffer;

/**
 * Shama menen 01.02.2016 sag'at 10:49
 * user ta'repten jaratildi. :)
 */
public abstract class MVector implements Serializable, LengthVector {

    public final float length() {
        return (float) Math.sqrt(lengthSquared());
    }

    public abstract MVector load(FloatBuffer buffer1);

    public abstract MVector negate();

    public final MVector normalize() {
        final float len = length();
        if (len != 0.0f) {
            float l = 1.0f / len;
            return this.scale(l);
        }else {
            throw new IllegalStateException("Vektordin' uzinlig'i no'll.");
        }
    }

    protected abstract MVector scale(float l);

}
