package com.example.gqqqqq.lorastop.utils;

import android.app.Activity;
import android.content.Intent;

import com.example.gqqqqq.lorastop.ui.NaviSettingActivity;

public class NormalUtils {
    public static void gotoSettings(Activity activity) {
        Intent it = new Intent(activity, NaviSettingActivity.class);
        activity.startActivity(it);
    }

    public static String getTTSAppID() {
        return "14968598";
    }
}
