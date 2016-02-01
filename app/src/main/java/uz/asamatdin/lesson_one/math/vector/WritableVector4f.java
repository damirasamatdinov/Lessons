package uz.asamatdin.lesson_one.math.vector;

/**
 * Shama menen 01.02.2016 sag'at 11:17
 * user ta'repten jaratildi. :)
 */
public interface WritableVector4f extends WritableVector3f {
    void setW(float w);


    void set(float x, float y, float z, float w);
}
