package com.prts.pickccustomer.Payment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;

import com.prts.pickccustomer.R;
import com.prts.pickccustomer.constants.PickCCustomTextVIew;

/**
 * Created by LOGICON3 on 5/2/2017.
 */

public class CashPaymentInvoice extends AppCompatActivity {
    PickCCustomTextVIew eMailIDET;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cash_invoice);
        eMailIDET = (PickCCustomTextVIew)findViewById(R.id.email_txt_et);
    }


    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.checkbox_email:
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (!checked)
                {
                   eMailIDET.requestFocus();
                     eMailIDET.setFocusableInTouchMode(true);
                   imm.showSoftInput(eMailIDET, InputMethodManager.SHOW_FORCED);
                }

            else
                {


                }
                // Remove the meat
                break;

        }
    }
}
