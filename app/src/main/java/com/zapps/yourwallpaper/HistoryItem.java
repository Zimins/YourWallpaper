package com.zapps.yourwallpaper;

import java.io.File;

/**
 * Created by Zimincom on 2017. 9. 20..
 */

public class HistoryItem {

    File image;
    String name;

    public HistoryItem(File image, String name) {
        this.image = image;
        this.name = name;
    }
}
