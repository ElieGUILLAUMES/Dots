package com.icelandic_courses.elie.myfirstapp.util;

/**
 * A segment is defined by its (upper, left) starting point and it's orientation.
 *
 *      *----   Horizontal
 *
 *      *       Vertical
 *      |
 *      |
 *
 *      (* is the starting point)
 *
 * Created by Endos on 09.09.2015.
 */
public class Segment extends Position<Integer> {

    protected Orientation orientation;

    public Segment(Integer y, Integer x, Orientation orientation) {
        super(y,x);
        this.orientation = orientation;
    }

    public Segment(Position<Integer> segmentStart, Position<Integer> segmentEnd) {
        this(Math.min(segmentStart.getY(), segmentEnd.getY()),
                Math.min(segmentStart.getX(), segmentEnd.getX()),
                segmentStart.getY().intValue() == segmentEnd.getY().intValue() ? Orientation.Horizontal : Orientation.Veritcal);
    }

    @Override
    public boolean equals(Object o) {
        if(o == null || !(o instanceof Segment))
            return false;

        Segment segment = (Segment) o;
        return x.equals(segment.x) && y.equals(segment.y) && orientation == segment.orientation;
    }

    @Override
    public int hashCode() {
        return x.hashCode() + y.hashCode() + orientation.hashCode();
    }
}
