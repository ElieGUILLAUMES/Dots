package com.icelandic_courses.elie.myfirstapp.logic;

import com.icelandic_courses.elie.myfirstapp.util.Position;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Endos on 03.09.2015.
 */
public class LogicDot {

    private static AtomicInteger counter = new AtomicInteger(0);

    private final int id;
    private final DotColor color;
    private Position<Integer> position;

    public LogicDot(DotColor color, Position<Integer> position) {
        id = counter.getAndIncrement();
        this.color = color;
        this.position = position;
    }

    public int getId() {
        return id;
    }

    public Position<Integer> getPosition() {
        return position;
    }

    public DotColor getColor() {
        return color;
    }
}
