package com.xm666.realisticcruelty.math;

public class InverseFunction {
    public float offsetX;
    public float offsetY;
    public float length;

    public InverseFunction(float x, float y, boolean reversed) {
        if (reversed)
            x = 1.0F - x;

        offsetY = x * y / (x + y - 1.0F);
        offsetX = 1.0F / (1.0F - offsetY);
        length = -1.0F / offsetY - offsetX;

        if (reversed) {
            offsetX += length;
            length *= -1.0F;
        }
    }

    public float apply(float x) {
        return 1.0F / (offsetX + length * x) + offsetY;
    }
}
