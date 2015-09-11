package com.icelandic_courses.elie.myfirstapp.transformation;

import com.icelandic_courses.elie.myfirstapp.util.Position;

/**
 * Created by Endos on 09.09.2015.
 */
public class PixelToPitchConverter {

    PixelToPitchConverterDescription description;

    public PixelToPitchConverter(PixelToPitchConverterDescription description) {
        this.description = description;
    }

    public Position<Integer> transformPixelToPitch(Position<Float> pixelPosition) {
        int y = transformPixelToPitch(pixelPosition.getY());
        int x = transformPixelToPitch(pixelPosition.getX());
        return new Position<Integer>(y,x);
    }

    private int transformPixelToPitch(float value) {
        return Math.max(
                0,
                Math.min(
                    description.getPitchSize(),
                    (int) ((value - description.padding) / description.segmentSize)
                )
        );
    }

    public Position<Float> transformPitchToPixel(Position<Integer> pitchPosition) {
        float y = transformPitchToPixel(pitchPosition.getY());
        float x = transformPitchToPixel(pitchPosition.getX());
        return new Position<Float>(y,x);
    }

    private float transformPitchToPixel(int value) {
        float result = (value * description.segmentSize) + description.padding;

        //set it in the center
        result += description.segmentSize / 2;

        return result;
    }

    public PixelToPitchConverterDescription getDescription() {
        return description;
    }
}
