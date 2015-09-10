package com.icelandic_courses.elie.myfirstapp.logic;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by Endos on 08.09.2015.
 */
public enum DotColor {

    RED,
    GREEN,
    BLUE,
    VIOLET,
    YELLOW;

    private static final List<DotColor> VALUES = Collections.unmodifiableList(Arrays.asList(values()));
    private static final Random RANDOM = new Random();


    public static DotColor randomColor(int numberColors)  {
        numberColors = Math.min(VALUES.size(), numberColors);
        return VALUES.get(RANDOM.nextInt(numberColors));
    }
}
