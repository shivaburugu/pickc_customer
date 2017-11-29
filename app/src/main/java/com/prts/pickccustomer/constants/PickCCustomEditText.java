package com.prts.pickccustomer.constants;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by Uday on 31-08-2016.
 */
public class PickCCustomEditText extends EditText implements Constants {

    public PickCCustomEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public PickCCustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PickCCustomEditText(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            /*Typeface tf = Typeface.createFromAsset(getContext().getAssets(), CUSTOM_FONT);
            setTypeface(tf);*/
            setTextSize(14.0f);
        }
    }

}

