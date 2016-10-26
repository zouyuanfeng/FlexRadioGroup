package com.itzyf.flexradiogroup;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, Animation.AnimationListener {
    private boolean mProtectFromCheckedChange = false;

    private DrawerLayout mDrawerLayout;

    private FlexRadioGroup fblFilterPrice, fblFilterType, fblFilterStructure;
    private TextView text;

    private String filterPrices[] = {"0-15万", "15万-25万", "25万-35万", "35万-不限"};
    private String filterTypes[] = {"紧凑型", "经济型", "商务型", "豪华型", "跑车", "SUV", "微面"};
    private String filterStructures[] = {"两厢", "三厢", "掀背", "旅行版", "敞篷车"};

    private Animation animCollapse, animExpand;

    private SparseBooleanArray isCollapses; //是否收缩

    private Drawable dropUp, dropDown;

    private int currentAnimId;//当前正在执行动画的ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initToolbar("筛选菜单");
        mDrawerLayout = (DrawerLayout) findViewById(R.id.id_drawer_layout);
        fblFilterPrice = (FlexRadioGroup) findViewById(R.id.fbl_filter_price);
        fblFilterType = (FlexRadioGroup) findViewById(R.id.fbl_filter_type);
        fblFilterStructure = (FlexRadioGroup) findViewById(R.id.fbl_filter_structure);
        text = (TextView) findViewById(R.id.text);
        findViewById(R.id.btn_clear).setOnClickListener(this);
        findViewById(R.id.btn_submit).setOnClickListener(this);

        isCollapses = new SparseBooleanArray();

        createRadioButton(filterPrices, fblFilterPrice);
        createRadioButton(filterTypes, fblFilterType);
        createRadioButton(filterStructures, fblFilterStructure);

        findViewById(R.id.tv_price).setOnClickListener(this);
        findViewById(R.id.tv_type).setOnClickListener(this);
        findViewById(R.id.tv_structure).setOnClickListener(this);

        animExpand = AnimationUtils.loadAnimation(this, R.anim.expand);
        animExpand.setAnimationListener(this);
        animExpand.setFillAfter(true);

        animCollapse = AnimationUtils.loadAnimation(this, R.anim.collapse);
        animCollapse.setAnimationListener(this);
        animCollapse.setFillAfter(true);

    }

    public void initToolbar(@NonNull String title) {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar == null)
            throw new IllegalArgumentException("未引入toolbar_head布局文件");
        mToolbar.setTitle(title);
        setSupportActionBar(mToolbar);
    }

    private void createRadioButton(String[] filters, final FlexRadioGroup group) {
        /**
         *  64dp菜单的边距{@link DrawerLayout#MIN_DRAWER_MARGIN}+10dp*2为菜单内部的padding=84dp
         */
        float margin = DensityUtils.dp2px(this, 85);
        float width = DensityUtils.getWidth(this);
        for (String filter : filters) {
            RadioButton rb = (RadioButton) getLayoutInflater().inflate(R.layout.item_label, null);
            rb.setText(filter);
            FlexboxLayout.LayoutParams lp = new FlexboxLayout.LayoutParams((int) (width - margin) / 3, ViewGroup.LayoutParams.WRAP_CONTENT);
            rb.setLayoutParams(lp);
            group.addView(rb);

            /**
             * 下面两个监听器用于点击两次可以清除当前RadioButton的选中
             * 点击RadioButton后，{@link FlexRadioGroup#OnCheckedChangeListener}先回调，然后再回调{@link View#OnClickListener}
             * 如果当前的RadioButton已经被选中时，不会回调OnCheckedChangeListener方法，故判断没有回调该方法且当前RadioButton确实被选中时清除掉选中
             */
            group.setOnCheckedChangeListener(new FlexRadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(@IdRes int checkedId) {
                    mProtectFromCheckedChange = true;
                }
            });
            rb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!mProtectFromCheckedChange && ((RadioButton) v).isChecked()) {
                        group.clearCheck();
                    } else mProtectFromCheckedChange = false;
                }
            });
        }
        isCollapses.put(group.getId(), false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_clear:
                fblFilterPrice.clearCheck();
                fblFilterType.clearCheck();
                fblFilterStructure.clearCheck();
                break;
            case R.id.btn_submit:
                mDrawerLayout.closeDrawers();
                StringBuilder sb = new StringBuilder();
                RadioButton rbPrice = (RadioButton) findViewById(fblFilterPrice.getCheckedRadioButtonId());
                if (rbPrice != null)
                    sb.append(rbPrice.getText().toString()).append("\n");
                RadioButton rbType = (RadioButton) findViewById(fblFilterType.getCheckedRadioButtonId());
                if (rbType != null)
                    sb.append(rbType.getText().toString()).append("\n");
                RadioButton rbStructure = (RadioButton) findViewById(fblFilterStructure.getCheckedRadioButtonId());
                if (rbStructure != null)
                    sb.append(rbStructure.getText().toString()).append("\n");
                text.setText(sb.toString());
                break;
            case R.id.tv_type:
                startAnim(fblFilterType, (TextView) v);
                break;
            case R.id.tv_price:
                startAnim(fblFilterPrice, (TextView) v);
                break;
            case R.id.tv_structure:
                startAnim(fblFilterStructure, (TextView) v);
                break;
        }
    }


    /**
     * 设置箭头
     */
    private void setArrow(TextView view, boolean isCollapse) {
        if (!isCollapse) {
            if (dropUp == null) {
                dropUp = getResources().getDrawable(R.drawable.ic_arrow_drop_up_black_24dp);
                dropUp.setBounds(0, 0, dropUp.getMinimumWidth(), dropUp.getMinimumHeight());
            }
            view.setCompoundDrawables(null, null, dropUp, null);
        } else {
            if (dropDown == null) {
                dropDown = getResources().getDrawable(R.drawable.ic_arrow_drop_down_black_24dp);
                dropDown.setBounds(0, 0, dropDown.getMinimumWidth(), dropDown.getMinimumHeight());
            }
            view.setCompoundDrawables(null, null, dropDown, null);
        }


    }

    /**
     * 重新设置isCollapse值，保存当前动画状态
     * 启动动画
     *
     * @param group
     */
    private void startAnim(FlexRadioGroup group, TextView view) {
        currentAnimId = group.getId();
        boolean isCollapse = !isCollapses.get(group.getId());
        isCollapses.put(group.getId(), isCollapse);
        if (isCollapse) {
            group.startAnimation(animCollapse);
        } else {
            group.startAnimation(animExpand);
        }
        setArrow(view, isCollapse);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.END))
            mDrawerLayout.closeDrawers();
        else
            super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_filter:
                mDrawerLayout.openDrawer(GravityCompat.END);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onAnimationStart(Animation animation) {
        if (!isCollapses.get(currentAnimId)) {
            findViewById(currentAnimId).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if (isCollapses.get(currentAnimId)) {
            findViewById(currentAnimId).setVisibility(View.GONE);
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
