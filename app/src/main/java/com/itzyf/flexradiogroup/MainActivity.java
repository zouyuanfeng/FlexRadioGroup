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
        int margin = DensityUtils.dp2px(this, 10);
        for (String price : filterPrices) {
            RadioButton rb = (RadioButton) getLayoutInflater().inflate(R.layout.item_label, null);
            rb.setText(price);
            FlexboxLayout.LayoutParams lp = new FlexRadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(margin, margin / 2, margin, margin / 2);
            rb.setLayoutParams(lp);

            frgLabel.addView(rb);

            /**
             * 下面两个监听器用于点击两次可以清除当前RadioButton的选中
             * 点击RadioButton后，{@link FlexRadioGroup#OnCheckedChangeListener}先回调，然后再回调{@link View#OnClickListener}
             * 如果当前的RadioButton已经被选中时，不会回调OnCheckedChangeListener方法，故判断没有回调该方法且当前RadioButton确实被选中时清除掉选中
             */
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
