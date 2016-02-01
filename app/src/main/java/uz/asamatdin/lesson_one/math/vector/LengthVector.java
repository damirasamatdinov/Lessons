package uz.asamatdin.lesson_one.math.vector;

import java.nio.FloatBuffer;

import uz.asamatdin.lesson_one.math.vector.MVector;

/**
 * Shama menen 01.02.2016 sag'at 10:50
 * user ta'repten jaratildi. :)
 */
public interface LengthVector {
    float length();

    float lengthSquared();

    MVector store(FloatBuffer buffer);

}
