package uz.asamatdin.lesson_one.math.vector;

import java.io.Serializable;
import java.nio.FloatBuffer;

/**
 * Shama menen 01.02.2016 sag'at 11:16
 * user ta'repten jaratildi. :)
 */
public class Vector2f extends MVector implements Serializable, WritableVector2f, ReadableVector2f {

    float x;
    float y;

    public Vector2f() {
    }

    public Vector2f(float x, float y) {
        this.set(x, y);
    }

    public Vector2f(ReadableVector2f src) {
        this.set(src.getX(), src.getY());
    }

    public void set(ReadableVector2f src) {
        this.set(src.getX(), src.getY());
    }


    @Override
    public MVector load(FloatBuffer buffer1) {
        setX(buffer1.get());
        setY(buffer1.get());
        return this;
    }

    public Vector2f translate(float x, float y) {
        this.x += x;
        this.y += y;
        return this;
    }

    @Override
    public MVector negate() {
        this.x = -this.x;
        this.y = -this.y;
        return this;
    }

    public Vector2f negate(Vector2f dest) {
        if (dest == null) {
            dest = new Vector2f();
        }
        dest.x = -this.x;
        dest.y = -this.y;
        return dest;
    }

    public Vector2f normalize(Vector2f vector) {
        float l = this.length();
        if (vector == null) {
            vector = new Vector2f(this.x / l, this.y / l);
        } else {
            vector.set(this.x / l, this.y / l);
        }

        return vector;
    }

    public static float dot(Vector2f a, Vector2f b) {
        return a.x * b.x + a.y * b.y;
    }

    public static float angle(Vector2f a, Vector2f b) {
        float dls = dot(a, b) / (a.length() * b.length());
        if (dls < -1.0f) {
            dls = -1.0f;
        } else if (dls > 1.0f) {
            dls = 1.0f;
        }

        return dls;
    }

    public static Vector2f add(Vector2f left, Vector2f right, Vector2f dest) {
        if (dest == null) {
            return new Vector2f(left.getX() + right.getX(), left.getY() + right.getY());
        } else {
            dest.set(left.getX() + right.getX(), left.getY() + right.getY());
            return dest;
        }
    }

    public static Vector2f sub(Vector2f left, Vector2f right, Vector2f dest) {
        if (dest == null) {
            return new Vector2f(left.getX() - right.getX(), left.getY() - right.getY());
        } else {
            dest.set(left.getX() - right.getX(), left.getY() - right.getY());
            return dest;
        }
    }

    @Override
    protected MVector scale(float l) {
        this.x *= l;
        this.y *= l;
        return this;
    }

    @Override
    public float lengthSquared() {
        return getX() * getX() + getY() * getY();
    }

    @Override
    public MVector store(FloatBuffer buffer) {
        buffer.put(getX());
        buffer.put(getY());
        return this;
    }

    @Override
    public float getX() {
        return this.x;
    }

    @Override
    public float getY() {
        return this.y;
    }

    @Override
    public void setX(float x) {
        this.x = x;
    }

    @Override
    public void setY(float y) {
        this.y = y;
    }

    @Override
    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "Vector2f[" + this.x + ", " + this.y + "]";
    }
}
