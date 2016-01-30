package uz.asamatdin.lesson_one;

import java.nio.FloatBuffer;

/**
 * Shama menen 14.01.2016 sag'at 17:38
 * user ta'repten jaratildi. :)
 */
public class Triangle {

    private FloatBuffer buffer;
    private float vertices[] = {-0.75f, -0.75f, 0.0f,  // первая вершина
            0.0f, 0.75f, 0.0f,      // вторая
            0.75f, -0.75f, 0.0f}; // третья

    private float colors[] = {0.8f, 0.0f, 1.0f, 1.0f};


    private String vertex_shader = " [vertex]" +
            "   highp attribute vec4 a_Position;" +
            "   void main(){" +
            "       gl_Position = a_Position;" +
            "}";

    private String fragment_shader = "[fragment]" +
            "   void main(){" +
            "       gl_FragColor = vec4(1.0);" +
            "}";

}
