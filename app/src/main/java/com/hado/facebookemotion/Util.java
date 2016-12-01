package com.hado.facebookemotion;

import android.content.res.Resources;

/**
 * Created by Hado on 27-Nov-16.
 */

public class Util {
    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }
}
