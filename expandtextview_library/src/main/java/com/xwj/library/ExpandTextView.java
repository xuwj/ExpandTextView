package com.xwj.library;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * ClassName: ExpandTextView
 * Description: 仿微信可伸缩的TextView
 */
public class ExpandTextView extends LinearLayout implements View.OnClickListener {
    public static final String TAG = "ExpandTextView";

    public static final int DEFAULT_TEXT_COLOR = 0XFF000000; // 黑色
    public static final int DEFAULT_MORE_TEXT_COLOR = 0xFFFF0000; // 红色
    public static final int DEFAULT_LINE_NUM = 3;
    public static final int DEFAULT_TEXT_SIZE = 15;
    public static final int DEFAULT_MARGIN_TOP = 10;
    public static final int DEFAULT_IS_LOAD_ANIM_TIME = 100;

    private TextView mTvContent;
    private TextView mTvReadMore;

    // 内容TextView字体的颜色
    private int contentTextColor;
    // 内容TextView字体的大小
    private float contentTextSize;
    // 内容TextView默认显示最大行数
    private int contentMaxLine;
    private String contentText;
    // TextView所有的内容暂用的行数
    private int contentLine = 0;
    //  是否展开
    private boolean isExpand = false;
    //  是否开启->TextView展开动画
    private boolean isLoadAnim = false;
    // TextView展开动画执行时间
    private int isLoadAnimTime;
    private int moreTextColor;

    public ExpandTextView(Context context) {
        this(context, null);
    }

    public ExpandTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExpandTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setOrientation(VERTICAL);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ExpandTextView);
        contentTextColor = a.getColor(R.styleable.ExpandTextView_content_textcolor, DEFAULT_TEXT_COLOR);
        contentTextSize = a.getDimension(R.styleable.ExpandTextView_content_textsize, dp2px(DEFAULT_TEXT_SIZE));
        contentMaxLine = a.getInt(R.styleable.ExpandTextView_max_lines, DEFAULT_LINE_NUM);
        contentText = a.getString(R.styleable.ExpandTextView_text);
        moreTextColor = a.getColor(R.styleable.ExpandTextView_more_text_color, DEFAULT_MORE_TEXT_COLOR);
        isLoadAnim = a.getBoolean(R.styleable.ExpandTextView_is_load_anim, false);
        isLoadAnimTime = a.getInt(R.styleable.ExpandTextView_is_load_anim_time, DEFAULT_IS_LOAD_ANIM_TIME);

        a.recycle();

        initView(context);
    }

    private void initView(Context context) {

        mTvContent = new TextView(context);
        mTvContent.setText(contentText);
        mTvContent.setTextColor(contentTextColor);
        mTvContent.setTextSize(TypedValue.COMPLEX_UNIT_PX, contentTextSize);

        int textHeight = mTvContent.getLineHeight() * contentMaxLine;
        LayoutParams mParams_txt = new LayoutParams(LayoutParams.MATCH_PARENT, textHeight);
        addView(mTvContent, mParams_txt);

        mTvReadMore = new TextView(context);
        mTvReadMore.setText("全文");
        mTvReadMore.setTextSize(TypedValue.COMPLEX_UNIT_PX, dp2px(DEFAULT_TEXT_SIZE));
        mTvReadMore.setPadding(0, dp2px(5), 0, dp2px(5));

        mTvReadMore.setTextColor(moreTextColor);

        LayoutParams mParams_readmore = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        addView(mTvReadMore, mParams_readmore);

        mTvReadMore.setOnClickListener(this);

        this.post(new Runnable() {
            @Override
            public void run() {
                contentLine = mTvContent.getLineCount();
                if (contentLine <= contentMaxLine) {
                    mTvReadMore.setVisibility(View.GONE);
                    LayoutParams mParam = (LayoutParams) mTvContent.getLayoutParams();
                    mParam.height = LayoutParams.WRAP_CONTENT;
                    mTvContent.setLayoutParams(mParam);
                    ExpandTextView.this.setOnClickListener(null);
                } else {
                    //默认是非展开模式，那么设置最大行为maxLine
                    mTvContent.setMaxLines(contentMaxLine);
                    mTvReadMore.setVisibility(View.VISIBLE);
                }
            }
        });
    }


    private int dp2px(int dp) {
        return (int) (this.getResources().getDisplayMetrics().density * dp + 0.5);
    }

    @Override
    public void onClick(View v) {
        if (v == mTvReadMore) {
            flexibleHeight();
        }
    }

    private void flexibleHeight() {
        isExpand = !isExpand;
        int textHeight = 0;

        if (isExpand) {
            textHeight = contentLine * mTvContent.getLineHeight();
            mTvContent.setMaxLines(contentLine);
            mTvReadMore.setText("收起");
        } else {
            textHeight = mTvContent.getLineHeight() * contentMaxLine;

            mTvReadMore.setText("全文");
        }
        final LayoutParams mParam = (LayoutParams) mTvContent.getLayoutParams();
        mParam.height = textHeight;
        mTvContent.setLayoutParams(mParam);

        if (!isExpand && !isLoadAnim) {
            mTvContent.setMaxLines(contentMaxLine);
        }

        if (isLoadAnim) {
            // TextView的平移动画
            ValueAnimator animator_textView = ValueAnimator.ofInt(mTvContent.getHeight(), textHeight);
            animator_textView.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mParam.height = (Integer) animation.getAnimatedValue();
                    mTvContent.setLayoutParams(mParam);
                }
            });

            AnimatorSet mAnimatorSets = new AnimatorSet();
            mAnimatorSets.setDuration(isLoadAnimTime);
            mAnimatorSets.play(animator_textView);
            mAnimatorSets.start();
            mAnimatorSets.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    //动画结束之后，如果是非展开模式，则设置最大行数为maxLine
                    if (!isExpand) {
                        mTvContent.setMaxLines(contentMaxLine);
                    }
                }
            });
        }
    }
}
