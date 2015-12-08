package cn.bingoogolapple.bgaannotation.demo.ui.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cn.bingoogolapple.bgaannotation.demo.R;

public class BGAIndicator extends LinearLayout implements View.OnClickListener, View.OnFocusChangeListener, ViewPager.OnPageChangeListener {
    private static final String TAG = BGAIndicator.class.getSimpleName();
    private final int BSSEEID = 0xffff00;
    private ColorStateList mTextColor;
    private int mTextSizeNormal = 12;
    private int mTextSizeSelected = 15;

    private int mTriangleColor = android.R.color.white;
    private int mTriangleHeight = 5;
    private int mTriangleMarginBottom = 2;

    private boolean mHasDivider = true;
    private int mDividerColor = android.R.color.black;
    private Drawable mDividerDrawable;
    private int mDividerWidth = 3;
    private int mDividerVerticalMargin = 10;

    private Paint mPaintFooterTriangle;
    private LayoutInflater mInflater;

    private ViewPager mViewPager;

    private List<TabInfo> mTabInfos;
    private int mTabCount = 0;
    private int mCurrentTabIndex = 0;

    private Path mPath = new Path();

    public BGAIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
        initDraw(context);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BGAIndicator);
        final int N = typedArray.getIndexCount();
        for (int i = 0; i < N; i++) {
            int attr = typedArray.getIndex(i);
            switch (attr) {
                case R.styleable.BGAIndicator_triangleColor:
                    mTriangleColor = typedArray.getColor(attr, mTriangleColor);
                    break;
                case R.styleable.BGAIndicator_triangleMarginBottom:
                    /**
                     * getDimension和getDimensionPixelOffset的功能差不多,都是获取某个dimen的值,如果是dp或sp的单位,将其乘以density,如果是px,则不乘;两个函数的区别是一个返回float,一个返回int. getDimensionPixelSize则不管写的是dp还是sp还是px,都会乘以denstiy.
                     */
                    mTriangleMarginBottom = typedArray.getDimensionPixelSize(attr, mTriangleMarginBottom);
                    break;
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    case R.styleable.BGAIndicator_triangleHeight:
                    mTriangleHeight = typedArray.getDimensionPixelSize(attr, mTriangleHeight);
                    break;
                case R.styleable.BGAIndicator_textColor:
                    mTextColor = typedArray.getColorStateList(attr);
                    break;
                case R.styleable.BGAIndicator_textSizeNormal:
                    mTextSizeNormal = typedArray.getDimensionPixelSize(attr, mTextSizeNormal);
                    break;
                case R.styleable.BGAIndicator_textSizeSelected:
                    mTextSizeSelected = typedArray.getDimensionPixelSize(attr, mTextSizeSelected);
                    break;
                case R.styleable.BGAIndicator_hasDivider:
                    mHasDivider = typedArray.getBoolean(attr,mHasDivider);
                    break;
                case R.styleable.BGAIndicator_dividerColor:
                    mDividerDrawable = typedArray.getDrawable(attr);
                    break;
                case R.styleable.BGAIndicator_dividerWidth:
                    mDividerWidth = typedArray.getDimensionPixelSize(attr, mDividerWidth);
                    break;
                case R.styleable.BGAIndicator_dividerVerticalMargin:
                    mDividerVerticalMargin = typedArray.getDimensionPixelSize(attr,mDividerVerticalMargin);
                    break;
            }
        }
        typedArray.recycle();
    }

    private void initDraw(Context context) {
        mPaintFooterTriangle = new Paint();
        mPaintFooterTriangle.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaintFooterTriangle.setColor(mTriangleColor);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    // 初始化选项卡
    public void initData(int currentTab, List<TabInfo> tabInfos, ViewPager viewPager,FragmentManager fragmentManager) {
        this.removeAllViews();
        mViewPager = viewPager;
        mTabInfos = tabInfos;
        mTabCount = tabInfos.size();
        mViewPager.setAdapter(new MyPagerAdapter(fragmentManager));
        mViewPager.setOnPageChangeListener(this);

        initTab(currentTab);
        postInvalidate();
    }

    @SuppressWarnings("deprecation")
    private void initTab(int currentTab) {
        for (int index = 0; index < mTabCount; index++) {
            View tabIndicator = mInflater.inflate(R.layout.view_indicator, this, false);
            tabIndicator.setId(BSSEEID + index);
            tabIndicator.setOnClickListener(this);

            TextView titleTv = (TextView) tabIndicator.findViewById(R.id.tv_indicator_title);
            if(mTextColor != null) {
                titleTv.setTextColor(mTextColor);
            }
            titleTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSizeNormal);
            titleTv.setText(mTabInfos.get(index).title);

            LayoutParams tabLp = new LayoutParams(0,LayoutParams.MATCH_PARENT,1);
            tabLp.gravity = Gravity.CENTER;
            tabIndicator.setLayoutParams(tabLp);
            // 防止currentTab为0时，第一个tab文字颜色没变化
            if(index == 0) {
                resetTab(tabIndicator, true);
            }
            this.addView(tabIndicator);

            if (index != mTabCount - 1 && mHasDivider) {
                LayoutParams dividerLp = new LayoutParams(mDividerWidth,LayoutParams.MATCH_PARENT);
                dividerLp.setMargins(0, mDividerVerticalMargin, 0, mDividerVerticalMargin);
                View vLine = new View(getContext());
                if(mDividerDrawable != null) {
                    vLine.setBackgroundDrawable(mDividerDrawable);
                } else {
                    vLine.setBackgroundResource(mDividerColor);
                }
                vLine.setLayoutParams(dividerLp);
                this.addView(vLine);
            }
        }
        setCurrentTab(currentTab);
    }

    private void setCurrentTab(int index) {
        if(mCurrentTabIndex != index && index > -1 && index < mTabCount) {
            View oldTab = findViewById(BSSEEID + mCurrentTabIndex);
            resetTab(oldTab, false);

            mCurrentTabIndex = index;
            View newTab = findViewById(BSSEEID + mCurrentTabIndex);
            resetTab(newTab, true);

            mViewPager.setCurrentItem(mCurrentTabIndex);
            postInvalidate();
        }
    }

    private void resetTab(View tab, boolean b) {
        TextView tv = (TextView) tab.findViewById(R.id.tv_indicator_title);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, b ? mTextSizeSelected : mTextSizeNormal);
        tab.setSelected(b);
        tab.setPressed(b);
    }


    @Override
    public void onClick(View v) {
        int currentTabIndex = v.getId() - BSSEEID;
        setCurrentTab(currentTabIndex);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (v == this && hasFocus) {
            findViewById(BSSEEID + mCurrentTabIndex).requestFocus();
            return;
        } else if (hasFocus) {
            for(int i = 0; i < mTabCount; i++) {
                if (getChildAt(i) == v) {
                    setCurrentTab(i);
                }
            }
        }
    }

    // 注意：必须要设置背景后，该方法才会被调用
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int itemWidth = getWidth();
        float indicatorScrollX = 0;

        if (mTabCount != 0) {
            itemWidth = getWidth() / mTabCount;
            indicatorScrollX = itemWidth * scrollRate;
        }

        mPath.rewind();
        float offset = 20;
        float left_x = offset + indicatorScrollX;
        float right_x = itemWidth - offset + indicatorScrollX;
        float top_y = getHeight() - mTriangleMarginBottom - mTriangleHeight;
        float bottom_y = getHeight() - mTriangleMarginBottom;

        mPath.moveTo(left_x, top_y + 1f);
        mPath.lineTo(right_x, top_y + 1f);
        mPath.lineTo(right_x, bottom_y + 1f);
        mPath.lineTo(left_x, bottom_y + 1f);
        mPath.close();

        canvas.drawPath(mPath, mPaintFooterTriangle);
    }

    /**
     * viewpager and tab have the same scroll rate
     */
    private float scrollRate;

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        scrollRate=position+positionOffset;
        postInvalidate();
    }

    @Override
    public void onPageSelected(int position) {
        setCurrentTab(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return mTabInfos.size();
        }

        @Override
        public Fragment getItem(int position) {
            return Fragment.instantiate(getContext(), mTabInfos.get(position).fragmentClass);
        }

    }

    public static class TabInfo implements Parcelable {
        public String title = null;
        public String fragmentClass = null;

        public TabInfo() {
        }

        public TabInfo(String title, String fragmentClass) {
            this.title = title;
            this.fragmentClass = fragmentClass;
        }

        public TabInfo(Parcel p) {
            title = p.readString();
            fragmentClass = p.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel p, int flags) {
            p.writeString(title);
            p.writeString(fragmentClass);
        }

        public static final Creator<TabInfo> CREATOR = new Creator<TabInfo>() {
            public TabInfo createFromParcel(Parcel p) {
                return new TabInfo(p);
            }

            public TabInfo[] newArray(int size) {
                return new TabInfo[size];
            }
        };
    }
}