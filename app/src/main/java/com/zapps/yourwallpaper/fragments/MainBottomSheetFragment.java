package com.zapps.yourwallpaper.fragments;

import android.app.Dialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.View;

import com.zapps.yourwallpaper.R;

/**
 * Created by Zimincom on 2017. 9. 22..
 */

public class MainBottomSheetFragment extends BottomSheetDialogFragment {

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View view = View.inflate(getContext(), R.layout.fragment_main_bottom_sheet, null);
        dialog.setContentView(view);
    }
}
