package com.hado.facebookemotion;

/**
 * Created by Hado on 27-Nov-16.
 */

public class EaseOutBack {

    private final float s = 1.70158f;
    private final long duration;
    private final float begin;
    private final float change;

    public EaseOutBack(long duration, float begin, float end) {
        this.duration = duration;
        this.begin = begin;
        this.change = end - begin;
    }

    public static EaseOutBack newInstance(long duration, float beginValue, float endValue) {
        return new EaseOutBack(duration, beginValue, endValue);
    }

    public float getCoordinateYFromTime(float currentTime) {
        return change * ((currentTime = currentTime / duration - 1) * currentTime * ((s + 1) * currentTime + s) + 1) + begin;
    }
}
