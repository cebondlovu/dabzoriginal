package com.softcrypt.deepkeysmusic.common;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.snackbar.Snackbar;
import com.softcrypt.deepkeysmusic.R;

public class DisplayableError {
    private static DisplayableError instance;
    private final Context context;

    public DisplayableError(Context context) {
        this.context = context;
    }

    public static DisplayableError getInstance(Context context) {
        if(instance == null)
            instance = new DisplayableError(context);
        return instance;
    }

    public void createToastMessage(String message, int type) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        ConstraintLayout toastLayout = (ConstraintLayout) inflater.inflate(R.layout.toast_layout, null);
        TextView messageTxt = toastLayout.findViewById(R.id.toast_topic);

        switch (type) {
            case 0:
                createFailToast(toastLayout, messageTxt, message);
                break;
            case 1:
                createSuccessToast(toastLayout, messageTxt, message);
                break;
            case 2:
                createWarningToast(toastLayout, messageTxt, message);
                break;
            default:
                createFailToast(toastLayout, messageTxt, message);
        }
    }

    private void createWarningToast(ConstraintLayout toastLayout, TextView messageTxt, String message) {
        toastLayout.setBackground(context.getResources().getDrawable(R.drawable.custom_toast_warning));
        messageTxt.setText(message);
        messageTxt.setTextColor(context.getResources().getColor(R.color.white));
        showToast(toastLayout);
    }

    private void createSuccessToast(ConstraintLayout toastLayout, TextView messageTxt, String message) {
        toastLayout.setBackground(context.getResources().getDrawable(R.drawable.custom_toast_success));
        messageTxt.setText(message);
        messageTxt.setTextColor(context.getResources().getColor(R.color.primaryDark));
        showToast(toastLayout);
    }

    private void createFailToast(ConstraintLayout toastLayout, TextView messageTxt, String message) {
        toastLayout.setBackground(context.getResources().getDrawable(R.drawable.custom_toast_fail));
        messageTxt.setText(message);
        messageTxt.setTextColor(context.getResources().getColor(R.color.white));
        showToast(toastLayout);
    }

    private void showToast(ConstraintLayout toastLayout) {
        Toast toast = new Toast(context);
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(toastLayout);
        toast.show();
    }
}
