package com.prts.pickccustomer.constants;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.widget.TextView;

import com.prts.pickccustomer.R;

/**
 * Created by Uday on 12-02-2017.
 */

public class ProgressDialog {

    Context context;
    int style;
    String title;
    String message;
    Dialog dialog = null;
    boolean cancelable = false;
    private boolean canceledOnTouchOutside;

    public void setCancelable(boolean cancelable) {
        this.cancelable = cancelable;
    }

    public ProgressDialog(Context context) {
        this.context = context;
    }

    public ProgressDialog(Context context, int style) {
        this.context = context;
        this.style = style;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void show(){
        createProgressDialog();
    }

    public void dismiss(){
        dialog.dismiss();
    }

    private void createProgressDialog(){
        if (style != 0){
            dialog = new Dialog(context,style);
        }else {
            dialog = new Dialog(context);
        }
        dialog.setContentView(R.layout.custom_progress_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(cancelable);
        dialog.setCanceledOnTouchOutside(canceledOnTouchOutside);
        dialog.show();
        TextView titleTv = (TextView)dialog.findViewById(R.id.titleTV);
        TextView messageTv = (TextView)dialog.findViewById(R.id.messageTV);
        titleTv.setText(title);
        messageTv.setText(message);
    }

    public boolean isShowing() {
        return dialog.isShowing();
    }

    public void setCanceledOnTouchOutside(boolean canceledOnTouchOutside) {
        this.canceledOnTouchOutside = canceledOnTouchOutside;
    }
}
