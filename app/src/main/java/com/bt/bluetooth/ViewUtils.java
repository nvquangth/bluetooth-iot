package com.bt.bluetooth;

import android.view.View;

/**
 * Created by quangnv on 25/03/2019
 */

public final class ViewUtils {

    public static void eneble(View view) {
        view.setEnabled(true);
    }

    public static void disable(View view) {
        view.setEnabled(false);
    }

    public static void visible(View view) {
        view.setVisibility(View.VISIBLE);
    }

    public static void invisible(View view) {
        view.setVisibility(View.INVISIBLE);
    }

    public static void gone(View view) {
        view.setVisibility(View.GONE);
    }
}
