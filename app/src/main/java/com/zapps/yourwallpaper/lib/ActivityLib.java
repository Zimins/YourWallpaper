package com.zapps.yourwallpaper.lib;

import android.content.Context;
import android.content.Intent;

import com.zapps.yourwallpaper.Constants;
import com.zapps.yourwallpaper.activities.WaitingActivity;

/**
 * Created by Zimincom on 2017. 9. 11..
 */

public class ActivityLib {


    private static ActivityLib ourInstance;

    public static ActivityLib getInstance() {
        if (ourInstance == null) {
            ourInstance = new ActivityLib();
        }
        return ourInstance;
    }

    private ActivityLib() {}

    public void goActvity(Context context, Class targetActivity) {
        Intent intent = new Intent(context, targetActivity);
        context.startActivity(intent);
    }

    public void goWaitinActivity(Context context, String userPhone, String partnerPhone) {
        Intent intent = new Intent(context, WaitingActivity.class);
        intent.putExtra(Constants.KEY_PHONENUMBER, userPhone);
        intent.putExtra(Constants.KEY_PARTNERNUMBER, partnerPhone);
        context.startActivity(intent);
    }
}
