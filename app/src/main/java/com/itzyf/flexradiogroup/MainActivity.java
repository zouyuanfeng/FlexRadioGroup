package com.itzyf.flexradiogroup;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.google.android.flexbox.FlexboxLayout;

public class MainActivity extends AppCompatActivity {
    private String filterPrices[] = {"0-15万", "15万-25万", "25万-35万", "35万-50万", "50万-不限"};
    private FlexRadioGroup frgLabel;

    private boolean mProtectFromCheckedChange = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        frgLabel = (FlexRadioGroup) findViewById(R.id.frg_label);
        for (String price : filterPrices) {
            RadioButton rb = (RadioButton) getLayoutInflater().inflate(R.layout.item_label, null);
            rb.setText(price);
            FlexboxLayout.LayoutParams lp = new FlexRadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(15, 15, 15, 15);
            rb.setLayoutParams(lp);

            frgLabel.addView(rb);

            frgLabel.setOnCheckedChangeListener(new FlexRadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(@IdRes int checkedId) {
                    mProtectFromCheckedChange = true;
                }
            });
            rb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!mProtectFromCheckedChange && ((RadioButton) v).isChecked()) {
                        frgLabel.clearCheck();
                    } else mProtectFromCheckedChange = false;
                }
            });
        }
    }
}
