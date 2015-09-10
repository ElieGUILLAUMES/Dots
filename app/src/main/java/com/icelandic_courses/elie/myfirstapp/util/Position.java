package com.icelandic_courses.elie.myfirstapp.util;

/**
 * Generic class to store Positions with different number units.
 *
 * Created by Endos on 03.09.2015.
 */
public class Position<T extends Number> {

    protected T x,y;

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

    /* Math */

    public Position<Integer> add(int y, int x) {
        return new Position<Integer>(
                y + this.y.intValue(),
                x + this.x.intValue()
        );
    }

    public int manhattenDistance( Position<T> position) {
        return Math.abs(position.x.intValue() - x.intValue()) + Math.abs(position.y.intValue() - y.intValue());
    }

    private static double square(double x) {
        return x * x;
    }

    private static double distance(Position<Float> a, Position<Float> b) {
        return square(a.x - b.x) + square(a.y - b.y);
    }

    private static double distanceToSegmentSquared(Position<Float> p, Position<Float> v, Position<Float> w) {

        double segmentLength = distance(v, w);

        if (segmentLength == 0) {
            return distance(p, v);
        }

        double t = ((p.x - v.x) * (w.x - v.x) + (p.y - v.y) * (w.y - v.y)) / segmentLength;

        if (t < 0) {
            return distance(p, v);
        }

        if (t > 1) {
            return distance(p, w);
        }

        Position<Float> nearestPointOnSegment = new Position<Float>(
                (float) (v.y + t * (w.y - v.y)),
                (float) (v.x + t * (w.x - v.x))
        );

        return distance(p, nearestPointOnSegment);
    }

    public double distanceToSegment(Position<Float> v, Position<Float> w) {
        return Math.sqrt(distanceToSegmentSquared((Position<Float>) this, v, w));
    }

    /* Comparison */

    @Override
    public boolean equals(Object o) {
        if(o == null || !(o instanceof Position))
            return false;

        Position<T> position = (Position<T>) o;
        return x.equals(position.x) && y.equals(position.y);
    }

    @Override
    public int hashCode() {
        return x.hashCode() + y.hashCode();
    }

    public Position<T> clone() {
        return new Position<>(x,y);
    }

    public String toString() {
        return "(y=" + y + "|x=" + x + ")";
    }
}
