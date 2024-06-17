package ru.alex.vector.util;

import org.springframework.stereotype.Component;

@Component
public class FloatToArray {
    // Метод для преобразования массива double в массив float
    public float[] toFloatArray(double[] doubles) {
        float[] floats = new float[doubles.length];
        for (int i = 0; i < doubles.length; i++) {
            floats[i] = (float) doubles[i];
        }
        return floats;
    }
}
