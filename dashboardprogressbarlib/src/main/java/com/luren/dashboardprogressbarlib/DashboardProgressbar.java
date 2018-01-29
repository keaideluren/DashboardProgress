package com.luren.dashboardprogressbarlib;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.OvershootInterpolator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator 可爱的路人 on 2017/12/18.
 * Email:513421345@qq.com
 * 不考虑多进度的情况
 */
public class DashboardProgressbar extends View {
    //必要的工具
    private Paint mProgressPaint;
    private Paint mBgPaint;
    private Paint mSecondBgPaint;
    private Paint mDialPaint;
    private Paint mDialTextPaint;
    private Paint mTextPaint;
    private Paint mEarPaint;
    private RectF mProgressRect;
    private OnDataUpdateListener mAnimationListener;//动画回调


    //数据 ，内容等
    private float mProgress;//进度值，这个值控制指针绘制的位置
    private double mTotalData;//这个
    private double mCurrentData;//和这个来计算progress的大小
    private String endText;//终止点的文字
    private String startText;//起始点的文字
    private List<String> dialTexts;//刻度文字

    //样式，颜色，大小等
    private ArrayList<Integer> mProgressColors;//进度条的颜色
    private int mBgColor;//背景的颜色
    private Drawable mIndicatorDrawable;//指示点
    private boolean mIsShowIndicator;//是否绘制进度条指向位置的圆点
    private boolean mIsShowEarText;
    private boolean mIsShowEar;//是否绘制起止点的耳朵
    private boolean mIsShowDial;// 刻度
    private boolean mIsShowDialText;//刻度的文字
    private int mDuration;//动画时长
    private int mDialsCount;//刻度数量
    private float mStartAngel;//进度的起始角度,弧度制
    private float mEndAngel;//进度的终止角度，弧度制
    private float mEarLength;//起止点的耳朵的长度
    private float mEarWidth;//起止点的耳朵线条宽度
    private float mProgressWidth;//进度条宽度
    private float mBgWidth;//进度底色
    private float mSecondBgWidth;//第二背景宽度
    private int mSecondBgColor;//第二背景颜色
    private float mDialLength;//刻度的长度
    private float mDialSpace;//刻度与圆弧之间的间距
    private float mDialWidth;//刻度宽度
    private float mStartEndTextSize;//起止点的文字大小
    private int mStartEndTextColor;//起止点的文字颜色
    private float mDialTextSize;//刻度文字大小
    private int mDialTextColor;//刻度文字颜色
    private float mIndicatorSize;//指示 大小
    private boolean mIndicatorDraggable;
    private int mDialColor;
    private int mEarColor;
    private boolean mIsShowSecondBg;
    private boolean mIsShowBg;
    private ValueAnimator valueAnimator;
    private float radius;

    public DashboardProgressbar(Context context) {
        super(context);
        init();
    }

    public DashboardProgressbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public DashboardProgressbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DashboardProgressbar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        //防止内存泄漏
        if (valueAnimator != null && valueAnimator.isRunning()) {
            valueAnimator.cancel();
        }
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.DashboardProgressbar);
        mIsShowBg = ta.getBoolean(R.styleable.DashboardProgressbar_dp_isShowBg, true);
        mIsShowSecondBg = ta.getBoolean(R.styleable.DashboardProgressbar_dp_isShowSecondBg, true);
        mIsShowDial = ta.getBoolean(R.styleable.DashboardProgressbar_dp_isShowDial, true);
        mIsShowDialText = ta.getBoolean(R.styleable.DashboardProgressbar_dp_isShowDialText, true);
        mIsShowEar = ta.getBoolean(R.styleable.DashboardProgressbar_dp_isShowEar, true);
        mIsShowEarText = ta.getBoolean(R.styleable.DashboardProgressbar_dp_isShowEarText, true);
        mIsShowIndicator = ta.getBoolean(R.styleable.DashboardProgressbar_dp_isShowIndicator, true);
        mIndicatorDraggable = ta.getBoolean(R.styleable.DashboardProgressbar_dp_indicatorDraggable, false);

        mBgWidth = ta.getDimension(R.styleable.DashboardProgressbar_dp_bgWidth, 10);
        mDialWidth = ta.getDimension(R.styleable.DashboardProgressbar_dp_dialWidth, 2);
        mEarWidth = ta.getDimension(R.styleable.DashboardProgressbar_dp_earWidth, 5);
        mProgressWidth = ta.getDimension(R.styleable.DashboardProgressbar_dp_progressWidth, 10);
        mSecondBgWidth = ta.getDimension(R.styleable.DashboardProgressbar_dp_secondBgWidth, 10);
        mDialLength = ta.getDimension(R.styleable.DashboardProgressbar_dp_dialLength, 30);
        mEarLength = ta.getDimension(R.styleable.DashboardProgressbar_dp_earLength, 50);
        mDialSpace = ta.getDimension(R.styleable.DashboardProgressbar_dp_dialSpace, 20);
        mIndicatorSize = ta.getDimension(R.styleable.DashboardProgressbar_dp_indicatorSize, 20);
        mDialTextSize = ta.getDimension(R.styleable.DashboardProgressbar_dp_dialTextSize, 20);
        mStartEndTextSize = ta.getDimension(R.styleable.DashboardProgressbar_dp_earTextSize, 30);

        mDialsCount = ta.getInt(R.styleable.DashboardProgressbar_dp_dialsCount, 20);
        mDuration = ta.getInt(R.styleable.DashboardProgressbar_dp_duration, 300);

        float startAngel = ta.getFloat(R.styleable.DashboardProgressbar_dp_startAngel, 135);
        float endAngel = ta.getFloat(R.styleable.DashboardProgressbar_dp_endAngel, 405);
        setmStartAngel(startAngel);
        setmEndAngel(endAngel);

        startText = ta.getString(R.styleable.DashboardProgressbar_dp_startText);
        endText = ta.getString(R.styleable.DashboardProgressbar_dp_endText);

        mBgColor = ta.getColor(R.styleable.DashboardProgressbar_dp_bgColor, Color.GRAY);
        mDialColor = ta.getColor(R.styleable.DashboardProgressbar_dp_dialColor
                , Color.argb(255, 100, 255, 255));
        mEarColor = ta.getColor(R.styleable.DashboardProgressbar_dp_earColor
                , Color.argb(255, 255, 255, 100));
        int progressColor = ta.getColor(R.styleable.DashboardProgressbar_dp_progressColor, Color.GREEN);
        mProgressColors = new ArrayList<>();
        mProgressColors.add(progressColor);
        mDialTextColor = ta.getColor(R.styleable.DashboardProgressbar_dp_dialTextColor, Color.GREEN);
        mStartEndTextColor = ta.getColor(R.styleable.DashboardProgressbar_dp_earTextColor, Color.GREEN);
        mSecondBgColor = ta.getColor(R.styleable.DashboardProgressbar_dp_secondBgColor
                , Color.argb(255, 255, 0, 255));

        mIndicatorDrawable = ta.getDrawable(R.styleable.DashboardProgressbar_dp_indicatorDrawable);

        ta.recycle();

        mProgressRect = new RectF(0, 0, 0, 0);
        createPaint();
    }

    private void init() {
        mIsShowEar = true;
        mIsShowDial = true;
        mIsShowIndicator = true;
        mIsShowEarText = true;
        mIsShowSecondBg = true;
        mIsShowBg = true;
        mStartAngel = (float) (135f / 180 * Math.PI);
        mEndAngel = (float) (405f / 180 * Math.PI);
        mStartEndTextSize = 40;
        mStartEndTextColor = Color.GREEN;
        mBgColor = Color.GRAY;
        mSecondBgColor = Color.argb(255, 255, 0, 255);
        mDialColor = Color.argb(255, 100, 255, 255);
        mEarColor = Color.argb(255, 255, 255, 100);
        mDialTextColor = Color.GREEN;
        mDialLength = 30;
        mEarLength = 50;
        mDialSpace = 20;
        mBgWidth = 10;
        mDialWidth = 2;
        mEarWidth = 5;
        mProgressWidth = 10;
        mDialsCount = 20;
        mIndicatorSize = 20;
        mSecondBgWidth = 10;
        mProgressColors = new ArrayList<>();
        mProgressColors.add(Color.GREEN);
        mProgressColors.add(Color.BLUE);
        mProgressColors.add(Color.RED);
        mProgressColors.add(Color.YELLOW);
        mProgressColors.add(Color.CYAN);

        mIndicatorDrawable = new ShapeDrawable(new OvalShape());

        mProgressRect = new RectF(0, 0, 0, 0);
    }

    private void createPaint() {
        mTextPaint = new Paint();
        mTextPaint.setTextSize(mStartEndTextSize);
        mTextPaint.setColor(mStartEndTextColor);
        mTextPaint.setAntiAlias(true);

        mProgressPaint = new Paint();
        mProgressPaint.setStyle(Paint.Style.STROKE);
        mProgressPaint.setStrokeWidth(mProgressWidth);
        mProgressPaint.setAntiAlias(true);
        setProgressPaintColor();

        mEarPaint = new Paint();
        mEarPaint.setColor(mEarColor);
        mEarPaint.setStrokeWidth(mEarWidth);
        mEarPaint.setAntiAlias(true);

        mBgPaint = new Paint();
        mBgPaint.setStyle(Paint.Style.STROKE);
        mBgPaint.setStrokeWidth(mBgWidth);
        mBgPaint.setColor(mBgColor);
        mBgPaint.setAntiAlias(true);

        mSecondBgPaint = new Paint();
        mSecondBgPaint.setStyle(Paint.Style.STROKE);
        mSecondBgPaint.setStrokeWidth(mSecondBgWidth);
        mSecondBgPaint.setColor(mSecondBgColor);
        mSecondBgPaint.setAntiAlias(true);

        mDialPaint = new Paint();
        mDialPaint.setColor(mDialColor);
        mDialPaint.setStrokeWidth(mDialWidth);
        mDialPaint.setAntiAlias(true);
    }

    private void setProgressPaintColor() {
        if (mProgressColors != null && mProgressColors.size() > 0) {
            if (mProgressColors.size() > 1) {
                int[] colors = new int[mProgressColors.size()];
                float[] positions = new float[mProgressColors.size()];
                float step = (float) ((mEndAngel - mStartAngel) / (mProgressColors.size()) / 2 / Math.PI);
                for (int i = 0; i < mProgressColors.size(); i++) {
                    colors[i] = mProgressColors.get(i);
                    positions[i] = step * i;
                }
                SweepGradient sweepGradient = new SweepGradient(0, 0, colors, positions);
                mProgressPaint.setShader(sweepGradient);
                Matrix matrix = new Matrix();
                if (sweepGradient.getLocalMatrix(matrix)) {
                    matrix.setRotate((float) (mStartAngel * 180 / Math.PI) - 1);
                } else {
                    matrix.setRotate((float) (mStartAngel * 180 / Math.PI) - 1);
                    sweepGradient.setLocalMatrix(matrix);
                }
            } else {
                mProgressPaint.setColor(mProgressColors.get(0));
            }
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        if (widthMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(500, 500);
        } else if (heightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSize, widthSize);
        } else {
            setMeasuredDimension(widthSize, heightSize);
        }
    }

    /**
     * 1.绘制起始、终止点的耳朵
     * 2.绘制刻度及其文字
     * 3.绘制起始终止点的文字
     * 4.绘制背景,第二背景
     * 5.绘制progress
     * 6.绘制indicator
     *
     * @param canvas 画布
     */
    @Override
    protected void onDraw(Canvas canvas) {
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        float progressAngel = mStartAngel + (mEndAngel - mStartAngel) * mProgress;//弧度制的
        float startAngel360 = (float) (mStartAngel * 180 / Math.PI);
        float endAngel360 = (float) (mEndAngel * 180 / Math.PI);
        canvas.translate(width / 2, height / 2);//将画布的中间作为0，0点
        if (mIsShowDial) {
            if (mIsShowEar) {
                radius = Math.min(width, height) / 2 - Math.max(mDialLength + mDialSpace, mEarLength);
            } else {
                radius = Math.min(width, height) / 2 - mDialLength + mDialSpace;
            }
        } else {
            if (mIsShowEar) {
                radius = Math.min(width, height) / 2 - mEarLength;
            } else {
                radius = Math.min(width, height) / 2;
            }
        }
        mProgressRect.set(-radius, -radius, radius, radius);


        //        1.绘制起始、终止点的耳朵  angel弧度制 以 cos(startAngel)*radius为起始x  sin(startAngel)*radius为起始y
        //        以 cos(startAngel)*(radius+earLength)为起始x  sin(startAngel)*(radius+earLength)为起始y
        float startEarX1 = 0, startEarY1 = 0, startEarX2 = 0, startEarY2 = 0, endEarX1 = 0, endEarY1 = 0, endEarX2 = 0, endEarY2 = 0;
        if (mIsShowEar || mIsShowEarText) {
            //角度增量为  earWidth/(2PI*radius)*2PI/2 -> earWidth/radius/2,减少计算量
            float angelPP = mEarWidth / 2 / radius;
            startEarX1 = (float) Math.cos(mStartAngel + angelPP) * radius;
            startEarY1 = (float) Math.sin(mStartAngel + angelPP) * radius;
            startEarX2 = (float) Math.cos(mStartAngel + angelPP) * (radius + mEarLength);
            startEarY2 = (float) Math.sin(mStartAngel + angelPP) * (radius + mEarLength);
            endEarX1 = (float) Math.cos(mEndAngel - angelPP) * radius;
            endEarY1 = (float) Math.sin(mEndAngel - angelPP) * radius;
            endEarX2 = (float) Math.cos(mEndAngel - angelPP) * (radius + mEarLength);
            endEarY2 = (float) Math.sin(mEndAngel - angelPP) * (radius + mEarLength);
        }
        if (mIsShowEar) {
            if (mProgress <= 0) {
                mEarPaint.setColor(mEarColor);
                canvas.drawLine(startEarX1, startEarY1, startEarX2, startEarY2, mEarPaint);
                canvas.drawLine(endEarX1, endEarY1, endEarX2, endEarY2, mEarPaint);
            } else if (mProgress >= 1) {
                mEarPaint.setColor(mProgressColors.get(mProgressColors.size() - 1));
                canvas.drawLine(endEarX1, endEarY1, endEarX2, endEarY2, mEarPaint);
                mEarPaint.setColor(mProgressColors.get(0));
                canvas.drawLine(startEarX1, startEarY1, startEarX2, startEarY2, mEarPaint);
            } else {
                mEarPaint.setColor(mProgressColors.get(0));
                canvas.drawLine(startEarX1, startEarY1, startEarX2, startEarY2, mEarPaint);
                mEarPaint.setColor(mEarColor);
                canvas.drawLine(endEarX1, endEarY1, endEarX2, endEarY2, mEarPaint);
            }
        }
        //        2.绘制起始终止点的文字
        if (mIsShowEarText) {
            if (!TextUtils.isEmpty(startText)) {
                float startTextWidth = mTextPaint.measureText(startText);
                float startTextX = startEarX2 - startTextWidth;
                canvas.drawText(startText, Math.max(-width / 2, startTextX), startEarY2 + 50, mTextPaint);
            }
            if (!TextUtils.isEmpty(endText)) {
                float endTextWidth = mTextPaint.measureText(endText);
                float endTextX = width / 2 - endTextWidth;
                canvas.drawText(endText, Math.min(endTextX, endEarX2), endEarY2 + 50, mTextPaint);
            }
        }
        //        3.绘制刻度及其文字
        if (mIsShowDial && mDialsCount > 0) {
            float step = (mEndAngel - mStartAngel) / (mDialsCount + 1);
            for (int i = 0; i < mDialsCount; i++) {
                float dialX1, dialY1, dialX2, dialY2;
                float currentAngel = mStartAngel + step * (i + 1);
                dialX1 = (float) Math.cos(currentAngel) * (radius + mDialSpace);
                dialY1 = (float) Math.sin(currentAngel) * (radius + mDialSpace);
                dialX2 = (float) Math.cos(currentAngel) * (radius + mDialSpace + mDialLength);
                dialY2 = (float) Math.sin(currentAngel) * (radius + mDialSpace + mDialLength);
                if (currentAngel <= progressAngel) {
                    if (mProgressPaint.getShader() == null) {
                        mDialPaint.setColor(mProgressColors.get(0));
                        mEarPaint.setShader(null);
                    } else {
                        mDialPaint.setShader(mProgressPaint.getShader());
                    }
                    canvas.drawLine(dialX1, dialY1, dialX2, dialY2, mDialPaint);
                } else {
                    mDialPaint.setColor(mDialColor);
                    mDialPaint.setShader(null);
                    canvas.drawLine(dialX1, dialY1, dialX2, dialY2, mDialPaint);
                }
            }
        }
        //4.绘制圆环，作为背景
        if (mIsShowBg) {
            canvas.drawCircle(0, 0, radius, mBgPaint);
        }
        if (mIsShowSecondBg) {
            canvas.drawArc(mProgressRect, startAngel360, endAngel360 - startAngel360, false, mSecondBgPaint);
        }

        //        5.绘制progress
        canvas.drawArc(mProgressRect, startAngel360, (float) (progressAngel / Math.PI * 180) - startAngel360
                , false, mProgressPaint);
        //        6.绘制indicator
        if (mIsShowIndicator && mIndicatorDrawable != null) {

            int progressX, progressY;
            progressX = (int) (Math.cos(progressAngel) * radius);
            progressY = (int) (Math.sin(progressAngel) * radius);
            if (mIndicatorSize <= 0) {
                mIndicatorDrawable.setBounds(progressX - mIndicatorDrawable.getIntrinsicWidth() / 2
                        , progressY - mIndicatorDrawable.getIntrinsicHeight() / 2
                        , progressX + mIndicatorDrawable.getIntrinsicWidth() / 2
                        , progressY + mIndicatorDrawable.getIntrinsicHeight() / 2);
            } else {
                mIndicatorDrawable.setBounds((int) (progressX - mIndicatorSize / 2)
                        , (int) (progressY - mIndicatorSize / 2)
                        , (int) (progressX + mIndicatorSize / 2)
                        , (int) (progressY + mIndicatorSize / 2));
            }
            mIndicatorDrawable.draw(canvas);
        }
    }

    private boolean isInterceptEvent = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mIndicatorDraggable) {
            return super.onTouchEvent(event);
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                float progressX, progressY;
                float indicatorAngel = mStartAngel + (mEndAngel - mStartAngel) * mProgress;
                progressX = (float) Math.cos(indicatorAngel) * radius;
                progressY = (float) Math.sin(indicatorAngel) * radius;
                float downX = event.getX() - getMeasuredWidth() / 2;
                float downY = event.getY() - getMeasuredHeight() / 2;
                isInterceptEvent = Math.pow(downX - progressX, 2) + Math.pow(downY - progressY, 2)
                        < mIndicatorSize * mIndicatorSize;
                break;
            case MotionEvent.ACTION_MOVE:
                if (isInterceptEvent) {
                    float realY = event.getY() - getMeasuredHeight() / 2;
                    float realX = event.getX() - getMeasuredWidth() / 2;
                    double angle = Math.atan(realY / realX);
                    if (realX < 0) {
                        angle += Math.PI;
                    } else {
                        angle += Math.PI * 2;
                    }
                    mProgress = (float) (angle - mStartAngel) / (mEndAngel - mStartAngel);
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                if (isInterceptEvent) {
                    refreshProgress(false);
                }
                break;
        }
        return isInterceptEvent || super.onTouchEvent(event);
    }

    public void setmAnimationListener(OnDataUpdateListener mAnimationListener) {
        this.mAnimationListener = mAnimationListener;
    }

    public void setDialTexts(List<String> dialTexts) {
        this.dialTexts = dialTexts;
        invalidate();
    }

    public void setmProgressColors(ArrayList<Integer> mProgressColors) {
        this.mProgressColors = mProgressColors;
        setProgressPaintColor();
        invalidate();
    }

    public void addProgressColor(int progressColor) {
        mProgressColors.add(progressColor);
        setProgressPaintColor();
        invalidate();
    }

    public void setmBgColor(int mBgColor) {
        this.mBgColor = mBgColor;
        mBgPaint.setColor(mBgColor);
        invalidate();
    }

    public void setmIndicatorDrawable(Drawable mIndicatorDrawable) {
        this.mIndicatorDrawable = mIndicatorDrawable;
        invalidate();
    }

    public void setmIsShowIndicator(boolean mIsShowIndicator) {
        this.mIsShowIndicator = mIsShowIndicator;
        invalidate();
    }

    public void setmIsShowEarText(boolean mIsShowEarText) {
        this.mIsShowEarText = mIsShowEarText;
        invalidate();
    }

    public void setmIsShowEar(boolean mIsShowEar) {
        this.mIsShowEar = mIsShowEar;
        invalidate();
    }

    public void setmIsShowDial(boolean mIsShowDial) {
        this.mIsShowDial = mIsShowDial;
        invalidate();
    }

    public void setmIsShowDialText(boolean mIsShowDialText) {
        this.mIsShowDialText = mIsShowDialText;
        invalidate();
    }

    public void setmDuration(int mDuration) {
        this.mDuration = mDuration;
    }

    public void setmDialsCount(int mDialsCount) {
        this.mDialsCount = mDialsCount;
        invalidate();
    }

    public void setmStartAngel(float mStartAngel) {
        this.mStartAngel = (float) (mStartAngel / 180 * Math.PI);
        invalidate();
    }

    public void setmEndAngel(float mEndAngel) {
        this.mEndAngel = (float) (mEndAngel / 180 * Math.PI);
        invalidate();
    }

    public void setmEarLength(float mEarLength) {
        this.mEarLength = mEarLength;
        invalidate();
    }

    public void setmEarWidth(float mEarWidth) {
        this.mEarWidth = mEarWidth;
        mEarPaint.setStrokeWidth(mEarWidth);
        invalidate();
    }

    public void setmProgressWidth(float mProgressWidth) {
        this.mProgressWidth = mProgressWidth;
        mProgressPaint.setStrokeWidth(mProgressWidth);
        invalidate();
    }

    public void setmBgWidth(float mBgWidth) {
        this.mBgWidth = mBgWidth;
        mBgPaint.setStrokeWidth(mBgWidth);
        invalidate();
    }

    public void setmSecondBgWidth(float mSecondBgWidth) {
        this.mSecondBgWidth = mSecondBgWidth;
        mSecondBgPaint.setStrokeWidth(mSecondBgWidth);
        invalidate();
    }

    public void setmSecondBgColor(int mSecondBgColor) {
        this.mSecondBgColor = mSecondBgColor;
        mSecondBgPaint.setColor(mSecondBgColor);
        invalidate();
    }

    public void setmDialLength(float mDialLength) {
        this.mDialLength = mDialLength;
        invalidate();
    }

    public void setmDialSpace(float mDialSpace) {
        this.mDialSpace = mDialSpace;
        invalidate();
    }

    public void setmDialWidth(float mDialWidth) {
        this.mDialWidth = mDialWidth;
        mDialPaint.setStrokeWidth(mDialWidth);
        invalidate();
    }

    public void setmStartEndTextSize(float mStartEndTextSize) {
        this.mStartEndTextSize = mStartEndTextSize;
        mTextPaint.setTextSize(mStartEndTextSize);
        invalidate();
    }

    public void setmStartEndTextColor(int mStartEndTextColor) {
        this.mStartEndTextColor = mStartEndTextColor;
        mTextPaint.setColor(mStartEndTextColor);
        invalidate();
    }

    public void setmDialTextSize(float mDialTextSize) {
        this.mDialTextSize = mDialTextSize;
        invalidate();
    }

    public void setmDialTextColor(int mDialTextColor) {
        this.mDialTextColor = mDialTextColor;
        mDialPaint.setColor(mDialTextColor);
        invalidate();
    }

    public void setmIndicatorSize(float mIndicatorSize) {
        this.mIndicatorSize = mIndicatorSize;
        invalidate();
    }

    public void setmDialColor(int mDialColor) {
        this.mDialColor = mDialColor;
        if (mDialPaint != null) {
            mDialPaint.setColor(mDialColor);
            invalidate();
        }
    }

    public void setmEarColor(int mEarColor) {
        this.mEarColor = mEarColor;
        mEarPaint.setColor(mEarColor);
        invalidate();
    }

    public void setmIsShowSecondBg(boolean mIsShowSecondBg) {
        this.mIsShowSecondBg = mIsShowSecondBg;
        invalidate();
    }

    public void setmIsShowBg(boolean mIsShowBg) {
        this.mIsShowBg = mIsShowBg;
        invalidate();
    }

    public void setProgressData(double max, double current) {
        this.mTotalData = max;
        this.mCurrentData = current;
        refreshProgress(true);
    }

    public void setmCurrentData(double currentData) {
        this.mCurrentData = currentData;
        refreshProgress(true);
    }

    public void setmTotalData(double totalData) {
        this.mTotalData = totalData;
        refreshProgress(true);
    }

    private void refreshProgress(boolean restart) {
        float toProgress;
        if (mTotalData <= 0 || mCurrentData <= 0) {
            toProgress = 0f;
        } else {
            toProgress = (float) (mCurrentData / mTotalData);
            if (toProgress > 1.05) {
                toProgress = 1.05f;
            }
        }
        animateTo(toProgress, restart);
    }

    public void setEndText(String endText) {
        this.endText = endText;
        invalidate();
    }

    public void setStartText(String startText) {
        this.startText = startText;
        invalidate();
    }

    private void animateTo(final float toProgress, final boolean restart) {
        float startProgress = mProgress;
        if (getMeasuredWidth() <= 0) {
            mProgress = toProgress;
            return;
        }
        if (valueAnimator != null && valueAnimator.isRunning()) {
            valueAnimator.cancel();
        }
        if (restart) {
            valueAnimator = ValueAnimator.ofFloat(startProgress, 0, toProgress);
            valueAnimator.setInterpolator(new OvershootInterpolator(1.02f));
            valueAnimator.setDuration(mDuration);
        } else {
            valueAnimator = ValueAnimator.ofFloat(startProgress, toProgress);
            valueAnimator.setInterpolator(new BounceInterpolator());
            valueAnimator.setDuration(500);
        }
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mProgress = (float) animation.getAnimatedValue();
                postInvalidate();
                if (mAnimationListener != null) {
                    if (restart) {
                        mAnimationListener.onDataUpdate((float) animation.getAnimatedValue());
                    }
                }
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgress = toProgress;
                invalidate();
                if (mAnimationListener != null) {
                    mAnimationListener.onAnimComplete();
                }
            }
        });
        valueAnimator.start();
    }

    public interface OnDataUpdateListener {
        /**
         * 0f-1.0f
         *
         * @param progress 进度
         */
        void onDataUpdate(float progress);

        /**
         * 动画走完
         */
        void onAnimComplete();
    }
}
