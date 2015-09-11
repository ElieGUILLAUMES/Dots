package com.icelandic_courses.elie.myfirstapp.transformation;

import com.icelandic_courses.elie.myfirstapp.util.Position;

/**
 * Created by Endos on 09.09.2015.
 */
public class PixelToPitchConverterDescription {

    // pitch unit
    int pitchSize;

    // pixel unit
    float pixelSize;
    float segmentSize;
    float padding;

    public PixelToPitchConverterDescription(int pitchSize, float pixelSize, float padding) {
        this.pitchSize = pitchSize;
        this.pixelSize = pixelSize;
        this.padding = padding;

        //calculate other attributes
        setSegmentSize();
    }

    private void setSegmentSize() {
        segmentSize = (pixelSize - 2 * padding) / pitchSize;
    }

    public int getPitchSize() {
        return pitchSize;
    }

    public float getPixelSize() {
        return pixelSize;
    }

    public float getSegmentSize() {
        return segmentSize;
    }

    public float getPadding() {
        return padding;
    }
}
