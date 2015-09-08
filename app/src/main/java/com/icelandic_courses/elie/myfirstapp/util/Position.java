package com.icelandic_courses.elie.myfirstapp.util;

/**
 * Created by Endos on 03.09.2015.
 */
public class Position<T> {

    private T x,y;

    public Position(T y, T x) {
        this.y = y;
        this.x = x;
    }

    public T getX() {
        return x;
    }

    public void setX(T x) {
        this.x = x;
    }

    public T getY() {
        return y;
    }

    public void setY(T y) {
        this.y = y;
    }

    public Position<T> clone() {
        return new Position<>(x,y);
    }
}
