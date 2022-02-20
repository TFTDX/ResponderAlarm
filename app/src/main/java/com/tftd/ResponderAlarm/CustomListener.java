package com.tftd.ResponderAlarm;

import android.app.Dialog;
import android.view.View;

import java.text.BreakIterator;

class CustomListener implements View.OnClickListener {
    private final Dialog dialog;
    private BreakIterator mEdtText;

    public CustomListener(Dialog dialog) {
        this.dialog = dialog;
    }
    @Override
    public void onClick(View v) {
        // put your code here
        String mValue = mEdtText.getText().toString();
        if(mValue.equals("Ok")){
            dialog.dismiss();
        }else{

        }
    }
}