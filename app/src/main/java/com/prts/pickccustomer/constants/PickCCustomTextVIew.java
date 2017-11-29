package com.prts.pickccustomer.constants;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Uday on 31-08-2016.
 */
public class PickCCustomTextVIew extends TextView implements Constants{

    public PickCCustomTextVIew(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public PickCCustomTextVIew(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PickCCustomTextVIew(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            //Typeface tf = Typeface.createFromAsset(getContext().getAssets(), CUSTOM_FONT);
            //setTypeface(tf);
            setTextSize(14.0f);
        }
    }

}

