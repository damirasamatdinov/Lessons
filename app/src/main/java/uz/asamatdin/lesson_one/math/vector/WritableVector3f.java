package uz.asamatdin.lesson_one.math.vector;

/**
 * Shama menen 01.02.2016 sag'at 11:12
 * user ta'repten jaratildi. :)
 */
public interface WritableVector3f extends WritableVector2f {
    void setZ(float z);

    void set(float x, float y, float z);
}
