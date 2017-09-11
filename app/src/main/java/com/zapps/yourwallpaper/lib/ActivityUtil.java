package com.zapps.yourwallpaper.lib;

import android.content.Context;
import android.content.Intent;

/**
 * Created by Zimincom on 2017. 9. 11..
 */

public class ActivityUtil {


    public static void newActivity(Context context, Class targetActivity) {
        Intent intent = new Intent(context, targetActivity);
        context.startActivity(intent);
    }
}
