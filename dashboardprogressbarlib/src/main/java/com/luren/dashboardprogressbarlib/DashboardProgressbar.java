package com.luren.dashboardprogressbarlib;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.OvershootInterpolator;
import java.util.ArrayList;
import java.util.List;

public class DashboardProgressbar extends View {
    private Paint mProgressPaint;
    private Paint mBgPaint;
    private Paint mSecondBgPaint;
    private Paint mDialPaint;
    private Paint mDialTextPaint;
    private Paint mTextPaint;
    private Paint mEarPaint;
    private RectF mProgressRect;
    private DashboardProgressbar.OnDataUpdateListener mAnimationListener;
    private float mProgress;
    private double mTotalData;
    private double mCurrentData;
    private String endText;
    private String startText;
    private List<String> dialTexts;
    private ArrayList<Integer> mProgressColors;
    private int mBgColor;
    private Drawable mIndicatorDrawable;
    private boolean mIsShowIndicator;
    private boolean mIsShowEarText;
    private boolean mIsShowEar;
    private boolean mIsShowDial;
    private boolean mIsShowDialText;
    private int mDuration;
    private int mDialsCount;
    private float mStartAngel;
    private float mEndAngel;
    private float mEarLength;
    private float mEarWidth;
    private float mProgressWidth;
    private float mBgWidth;
    private float mSecondBgWidth;
    private int mSecondBgColor;
    private float mDialLength;
    private float mDialSpace;
    private float mDialWidth;
    private float mStartEndTextSize;
    private int mStartEndTextColor;
    private float mDialTextSize;
    private int mDialTextColor;
    private float mIndicatorSize;
    private boolean mIndicatorDraggable;
    private int mDialColor;
    private int mEarColor;
    private boolean mIsShowSecondBg;
    private boolean mIsShowBg;
    private ValueAnimator valueAnimator;
    private float radius;
    private boolean isInterceptEvent = false;

    public DashboardProgressbar(Context context) {
        super(context);
        this.init();
    }

    public DashboardProgressbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init(context, attrs);
    }

    public DashboardProgressbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init(context, attrs);
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(this.valueAnimator != null && this.valueAnimator.isRunning()) {
            this.valueAnimator.cancel();
        }

    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.DashboardProgressbar);
        this.mIsShowBg = ta.getBoolean(R.styleable.DashboardProgressbar_dp_isShowBg, true);
        this.mIsShowSecondBg = ta.getBoolean(R.styleable.DashboardProgressbar_dp_isShowSecondBg, true);
        this.mIsShowDial = ta.getBoolean(R.styleable.DashboardProgressbar_dp_isShowDial, true);
        this.mIsShowDialText = ta.getBoolean(R.styleable.DashboardProgressbar_dp_isShowDialText, true);
        this.mIsShowEar = ta.getBoolean(R.styleable.DashboardProgressbar_dp_isShowEar, true);
        this.mIsShowEarText = ta.getBoolean(R.styleable.DashboardProgressbar_dp_isShowEarText, true);
        this.mIsShowIndicator = ta.getBoolean(R.styleable.DashboardProgressbar_dp_isShowIndicator, true);
        this.mIndicatorDraggable = ta.getBoolean(R.styleable.DashboardProgressbar_dp_indicatorDraggable, false);
        this.mBgWidth = ta.getDimension(R.styleable.DashboardProgressbar_dp_bgWidth, 10.0F);
        this.mDialWidth = ta.getDimension(R.styleable.DashboardProgressbar_dp_dialWidth, 2.0F);
        this.mEarWidth = ta.getDimension(R.styleable.DashboardProgressbar_dp_earWidth, 5.0F);
        this.mProgressWidth = ta.getDimension(R.styleable.DashboardProgressbar_dp_progressWidth, 10.0F);
        this.mSecondBgWidth = ta.getDimension(R.styleable.DashboardProgressbar_dp_secondBgWidth, 10.0F);
        this.mDialLength = ta.getDimension(R.styleable.DashboardProgressbar_dp_dialLength, 30.0F);
        this.mEarLength = ta.getDimension(R.styleable.DashboardProgressbar_dp_earLength, 50.0F);
        this.mDialSpace = ta.getDimension(R.styleable.DashboardProgressbar_dp_dialSpace, 20.0F);
        this.mIndicatorSize = ta.getDimension(R.styleable.DashboardProgressbar_dp_indicatorSize, 20.0F);
        this.mDialTextSize = ta.getDimension(R.styleable.DashboardProgressbar_dp_dialTextSize, 20.0F);
        this.mStartEndTextSize = ta.getDimension(R.styleable.DashboardProgressbar_dp_earTextSize, 30.0F);
        this.mDialsCount = ta.getInt(R.styleable.DashboardProgressbar_dp_dialsCount, 20);
        this.mDuration = ta.getInt(R.styleable.DashboardProgressbar_dp_duration, 300);
        float startAngel = ta.getFloat(R.styleable.DashboardProgressbar_dp_startAngel, 135.0F);
        float endAngel = ta.getFloat(R.styleable.DashboardProgressbar_dp_endAngel, 405.0F);
        this.setmStartAngel(startAngel);
        this.setmEndAngel(endAngel);
        this.startText = ta.getString(R.styleable.DashboardProgressbar_dp_startText);
        this.endText = ta.getString(R.styleable.DashboardProgressbar_dp_endText);
        this.mBgColor = ta.getColor(R.styleable.DashboardProgressbar_dp_bgColor, -7829368);
        this.mDialColor = ta.getColor(R.styleable.DashboardProgressbar_dp_dialColor, Color.argb(255, 100, 255, 255));
        this.mEarColor = ta.getColor(R.styleable.DashboardProgressbar_dp_earColor, Color.argb(255, 255, 255, 100));
        int progressColor = ta.getColor(R.styleable.DashboardProgressbar_dp_progressColor, -16711936);
        this.mProgressColors = new ArrayList();
        this.mProgressColors.add(Integer.valueOf(progressColor));
        this.mDialTextColor = ta.getColor(R.styleable.DashboardProgressbar_dp_dialTextColor, -16711936);
        this.mStartEndTextColor = ta.getColor(R.styleable.DashboardProgressbar_dp_earTextColor, -16711936);
        this.mSecondBgColor = ta.getColor(R.styleable.DashboardProgressbar_dp_secondBgColor, Color.argb(255, 255, 0, 255));
        this.mIndicatorDrawable = ta.getDrawable(R.styleable.DashboardProgressbar_dp_indicatorDrawable);
        ta.recycle();
        this.mProgressRect = new RectF(0.0F, 0.0F, 0.0F, 0.0F);
        this.createPaint();
    }

    private void init() {
        this.mIsShowEar = true;
        this.mIsShowDial = true;
        this.mIsShowIndicator = true;
        this.mIsShowEarText = true;
        this.mIsShowSecondBg = true;
        this.mIsShowBg = true;
        this.mStartAngel = 2.3561945F;
        this.mEndAngel = 7.0685835F;
        this.mStartEndTextSize = 40.0F;
        this.mStartEndTextColor = -16711936;
        this.mBgColor = -7829368;
        this.mSecondBgColor = Color.argb(255, 255, 0, 255);
        this.mDialColor = Color.argb(255, 100, 255, 255);
        this.mEarColor = Color.argb(255, 255, 255, 100);
        this.mDialTextColor = -16711936;
        this.mDialLength = 30.0F;
        this.mEarLength = 50.0F;
        this.mDialSpace = 20.0F;
        this.mBgWidth = 10.0F;
        this.mDialWidth = 2.0F;
        this.mEarWidth = 5.0F;
        this.mProgressWidth = 10.0F;
        this.mDialsCount = 20;
        this.mIndicatorSize = 20.0F;
        this.mSecondBgWidth = 10.0F;
        this.mProgressColors = new ArrayList();
        this.mProgressColors.add(Integer.valueOf(-16711936));
        this.mProgressColors.add(Integer.valueOf(-16776961));
        this.mProgressColors.add(Integer.valueOf(-65536));
        this.mProgressColors.add(Integer.valueOf(-256));
        this.mProgressColors.add(Integer.valueOf(-16711681));
        this.mIndicatorDrawable = new ShapeDrawable(new OvalShape());
        this.mProgressRect = new RectF(0.0F, 0.0F, 0.0F, 0.0F);
    }

    private void createPaint() {
        this.mTextPaint = new Paint();
        this.mTextPaint.setTextSize(this.mStartEndTextSize);
        this.mTextPaint.setColor(this.mStartEndTextColor);
        this.mTextPaint.setAntiAlias(true);
        this.mProgressPaint = new Paint();
        this.mProgressPaint.setStyle(Style.STROKE);
        this.mProgressPaint.setStrokeWidth(this.mProgressWidth);
        this.mProgressPaint.setAntiAlias(true);
        this.setProgressPaintColor();
        this.mEarPaint = new Paint();
        this.mEarPaint.setColor(this.mEarColor);
        this.mEarPaint.setStrokeWidth(this.mEarWidth);
        this.mEarPaint.setAntiAlias(true);
        this.mBgPaint = new Paint();
        this.mBgPaint.setStyle(Style.STROKE);
        this.mBgPaint.setStrokeWidth(this.mBgWidth);
        this.mBgPaint.setColor(this.mBgColor);
        this.mBgPaint.setAntiAlias(true);
        this.mSecondBgPaint = new Paint();
        this.mSecondBgPaint.setStyle(Style.STROKE);
        this.mSecondBgPaint.setStrokeWidth(this.mSecondBgWidth);
        this.mSecondBgPaint.setColor(this.mSecondBgColor);
        this.mSecondBgPaint.setAntiAlias(true);
        this.mDialPaint = new Paint();
        this.mDialPaint.setColor(this.mDialColor);
        this.mDialPaint.setStrokeWidth(this.mDialWidth);
        this.mDialPaint.setAntiAlias(true);
    }

    private void setProgressPaintColor() {
        if(this.mProgressColors != null && this.mProgressColors.size() > 0) {
            if(this.mProgressColors.size() > 1) {
                int[] colors = new int[this.mProgressColors.size()];
                float[] positions = new float[this.mProgressColors.size()];
                float step = (float)((double)((this.mEndAngel - this.mStartAngel) / (float)this.mProgressColors.size() / 2.0F) / 3.141592653589793D);

                for(int i = 0; i < this.mProgressColors.size(); ++i) {
                    colors[i] = ((Integer)this.mProgressColors.get(i)).intValue();
                    positions[i] = step * (float)i;
                }

                SweepGradient sweepGradient = new SweepGradient(0.0F, 0.0F, colors, positions);
                this.mProgressPaint.setShader(sweepGradient);
                Matrix matrix = new Matrix();
                if(sweepGradient.getLocalMatrix(matrix)) {
                    matrix.setRotate((float)((double)(this.mStartAngel * 180.0F) / 3.141592653589793D) - 1.0F);
                } else {
                    matrix.setRotate((float)((double)(this.mStartAngel * 180.0F) / 3.141592653589793D) - 1.0F);
                    sweepGradient.setLocalMatrix(matrix);
                }
            } else {
                this.mProgressPaint.setColor(((Integer)this.mProgressColors.get(0)).intValue());
            }
        }

    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        if(widthMode == -2147483648) {
            this.setMeasuredDimension(500, 500);
        } else if(heightMode == -2147483648) {
            this.setMeasuredDimension(widthSize, widthSize);
        } else {
            this.setMeasuredDimension(widthSize, heightSize);
        }

    }

    protected void onDraw(Canvas canvas) {
        int width = this.getMeasuredWidth();
        int height = this.getMeasuredHeight();
        float progressAngel = this.mStartAngel + (this.mEndAngel - this.mStartAngel) * this.mProgress;
        float startAngel360 = (float)((double)(this.mStartAngel * 180.0F) / 3.141592653589793D);
        float endAngel360 = (float)((double)(this.mEndAngel * 180.0F) / 3.141592653589793D);
        canvas.translate((float)(width / 2), (float)(height / 2));
        if(this.mIsShowDial) {
            if(this.mIsShowEar) {
                this.radius = (float)(Math.min(width, height) / 2) - Math.max(this.mDialLength + this.mDialSpace, this.mEarLength);
            } else {
                this.radius = (float)(Math.min(width, height) / 2) - this.mDialLength + this.mDialSpace;
            }
        } else if(this.mIsShowEar) {
            this.radius = (float)(Math.min(width, height) / 2) - this.mEarLength;
        } else {
            this.radius = (float)(Math.min(width, height) / 2);
        }

        this.mProgressRect.set(-this.radius, -this.radius, this.radius, this.radius);
        float startEarX1 = 0.0F;
        float startEarY1 = 0.0F;
        float startEarX2 = 0.0F;
        float startEarY2 = 0.0F;
        float endEarX1 = 0.0F;
        float endEarY1 = 0.0F;
        float endEarX2 = 0.0F;
        float endEarY2 = 0.0F;
        float step;
        if(this.mIsShowEar || this.mIsShowEarText) {
            step = this.mEarWidth / 2.0F / this.radius;
            startEarX1 = (float)Math.cos((double)(this.mStartAngel + step)) * this.radius;
            startEarY1 = (float)Math.sin((double)(this.mStartAngel + step)) * this.radius;
            startEarX2 = (float)Math.cos((double)(this.mStartAngel + step)) * (this.radius + this.mEarLength);
            startEarY2 = (float)Math.sin((double)(this.mStartAngel + step)) * (this.radius + this.mEarLength);
            endEarX1 = (float)Math.cos((double)(this.mEndAngel - step)) * this.radius;
            endEarY1 = (float)Math.sin((double)(this.mEndAngel - step)) * this.radius;
            endEarX2 = (float)Math.cos((double)(this.mEndAngel - step)) * (this.radius + this.mEarLength);
            endEarY2 = (float)Math.sin((double)(this.mEndAngel - step)) * (this.radius + this.mEarLength);
        }

        if(this.mIsShowEar) {
            if(this.mProgress <= 0.0F) {
                this.mEarPaint.setColor(this.mEarColor);
                canvas.drawLine(startEarX1, startEarY1, startEarX2, startEarY2, this.mEarPaint);
                canvas.drawLine(endEarX1, endEarY1, endEarX2, endEarY2, this.mEarPaint);
            } else if(this.mProgress >= 1.0F) {
                this.mEarPaint.setColor(((Integer)this.mProgressColors.get(this.mProgressColors.size() - 1)).intValue());
                canvas.drawLine(endEarX1, endEarY1, endEarX2, endEarY2, this.mEarPaint);
                this.mEarPaint.setColor(((Integer)this.mProgressColors.get(0)).intValue());
                canvas.drawLine(startEarX1, startEarY1, startEarX2, startEarY2, this.mEarPaint);
            } else {
                this.mEarPaint.setColor(((Integer)this.mProgressColors.get(0)).intValue());
                canvas.drawLine(startEarX1, startEarY1, startEarX2, startEarY2, this.mEarPaint);
                this.mEarPaint.setColor(this.mEarColor);
                canvas.drawLine(endEarX1, endEarY1, endEarX2, endEarY2, this.mEarPaint);
            }
        }

        if(this.mIsShowEarText) {
            float endTextX;
            if(!TextUtils.isEmpty(this.startText)) {
                step = this.mTextPaint.measureText(this.startText);
                endTextX = startEarX2 - step;
                canvas.drawText(this.startText, Math.max((float)(-width / 2), endTextX), startEarY2 + 50.0F, this.mTextPaint);
            }

            if(!TextUtils.isEmpty(this.endText)) {
                step = this.mTextPaint.measureText(this.endText);
                endTextX = (float)(width / 2) - step;
                canvas.drawText(this.endText, Math.min(endTextX, endEarX2), endEarY2 + 50.0F, this.mTextPaint);
            }
        }

        int progressY;
        if(this.mIsShowDial && this.mDialsCount > 0) {
            step = (this.mEndAngel - this.mStartAngel) / (float)(this.mDialsCount + 1);

            for(progressY = 0; progressY < this.mDialsCount; ++progressY) {
                float currentAngel = this.mStartAngel + step * (float)(progressY + 1);
                float dialX1 = (float)Math.cos((double)currentAngel) * (this.radius + this.mDialSpace);
                float dialY1 = (float)Math.sin((double)currentAngel) * (this.radius + this.mDialSpace);
                float dialX2 = (float)Math.cos((double)currentAngel) * (this.radius + this.mDialSpace + this.mDialLength);
                float dialY2 = (float)Math.sin((double)currentAngel) * (this.radius + this.mDialSpace + this.mDialLength);
                if(currentAngel <= progressAngel) {
                    if(this.mProgressPaint.getShader() == null) {
                        this.mDialPaint.setColor(((Integer)this.mProgressColors.get(0)).intValue());
                        this.mEarPaint.setShader((Shader)null);
                    } else {
                        this.mDialPaint.setShader(this.mProgressPaint.getShader());
                    }

                    canvas.drawLine(dialX1, dialY1, dialX2, dialY2, this.mDialPaint);
                } else {
                    this.mDialPaint.setColor(this.mDialColor);
                    this.mDialPaint.setShader((Shader)null);
                    canvas.drawLine(dialX1, dialY1, dialX2, dialY2, this.mDialPaint);
                }
            }
        }

        if(this.mIsShowBg) {
            canvas.drawCircle(0.0F, 0.0F, this.radius, this.mBgPaint);
        }

        if(this.mIsShowSecondBg) {
            canvas.drawArc(this.mProgressRect, startAngel360, endAngel360 - startAngel360, false, this.mSecondBgPaint);
        }

        canvas.drawArc(this.mProgressRect, startAngel360, (float)((double)progressAngel / 3.141592653589793D * 180.0D) - startAngel360, false, this.mProgressPaint);
        if(this.mIsShowIndicator && this.mIndicatorDrawable != null) {
            int progressX = (int)(Math.cos((double)progressAngel) * (double)this.radius);
            progressY = (int)(Math.sin((double)progressAngel) * (double)this.radius);
            if(this.mIndicatorSize <= 0.0F) {
                this.mIndicatorDrawable.setBounds(progressX - this.mIndicatorDrawable.getIntrinsicWidth() / 2, progressY - this.mIndicatorDrawable.getIntrinsicHeight() / 2, progressX + this.mIndicatorDrawable.getIntrinsicWidth() / 2, progressY + this.mIndicatorDrawable.getIntrinsicHeight() / 2);
            } else {
                this.mIndicatorDrawable.setBounds((int)((float)progressX - this.mIndicatorSize / 2.0F), (int)((float)progressY - this.mIndicatorSize / 2.0F), (int)((float)progressX + this.mIndicatorSize / 2.0F), (int)((float)progressY + this.mIndicatorSize / 2.0F));
            }

            this.mIndicatorDrawable.draw(canvas);
        }

    }

    @SuppressLint({"ClickableViewAccessibility"})
    public boolean onTouchEvent(MotionEvent event) {
        if(!this.mIndicatorDraggable) {
            return super.onTouchEvent(event);
        } else {
            switch(event.getAction()) {
                case 0:
                    float indicatorAngel = this.mStartAngel + (this.mEndAngel - this.mStartAngel) * this.mProgress;
                    float progressX = (float)Math.cos((double)indicatorAngel) * this.radius;
                    float progressY = (float)Math.sin((double)indicatorAngel) * this.radius;
                    float downX = event.getX() - (float)(this.getMeasuredWidth() / 2);
                    float downY = event.getY() - (float)(this.getMeasuredHeight() / 2);
                    this.isInterceptEvent = Math.pow((double)(downX - progressX), 2.0D) + Math.pow((double)(downY - progressY), 2.0D) < (double)(this.mIndicatorSize * this.mIndicatorSize);
                    break;
                case 1:
                    if(this.isInterceptEvent) {
                        this.refreshProgress(false);
                    }
                    break;
                case 2:
                    if(this.isInterceptEvent) {
                        float realY = event.getY() - (float)(this.getMeasuredHeight() / 2);
                        float realX = event.getX() - (float)(this.getMeasuredWidth() / 2);
                        double angle = Math.atan((double)(realY / realX));
                        if(realX < 0.0F) {
                            angle += 3.141592653589793D;
                        } else {
                            angle += 6.283185307179586D;
                        }

                        this.mProgress = (float)(angle - (double)this.mStartAngel) / (this.mEndAngel - this.mStartAngel);
                        this.invalidate();
                    }
            }

            return this.isInterceptEvent || super.onTouchEvent(event);
        }
    }

    public void setmAnimationListener(DashboardProgressbar.OnDataUpdateListener mAnimationListener) {
        this.mAnimationListener = mAnimationListener;
    }

    public void setDialTexts(List<String> dialTexts) {
        this.dialTexts = dialTexts;
        this.invalidate();
    }

    public void setmProgressColors(ArrayList<Integer> mProgressColors) {
        this.mProgressColors = mProgressColors;
        this.setProgressPaintColor();
        this.invalidate();
    }

    public void addProgressColor(int progressColor) {
        this.mProgressColors.add(Integer.valueOf(progressColor));
        this.setProgressPaintColor();
        this.invalidate();
    }

    public void setmBgColor(int mBgColor) {
        this.mBgColor = mBgColor;
        this.mBgPaint.setColor(mBgColor);
        this.invalidate();
    }

    public void setmIndicatorDrawable(Drawable mIndicatorDrawable) {
        this.mIndicatorDrawable = mIndicatorDrawable;
        this.invalidate();
    }

    public void setmIsShowIndicator(boolean mIsShowIndicator) {
        this.mIsShowIndicator = mIsShowIndicator;
        this.invalidate();
    }

    public void setmIsShowEarText(boolean mIsShowEarText) {
        this.mIsShowEarText = mIsShowEarText;
        this.invalidate();
    }

    public void setmIsShowEar(boolean mIsShowEar) {
        this.mIsShowEar = mIsShowEar;
        this.invalidate();
    }

    public void setmIsShowDial(boolean mIsShowDial) {
        this.mIsShowDial = mIsShowDial;
        this.invalidate();
    }

    public void setmIsShowDialText(boolean mIsShowDialText) {
        this.mIsShowDialText = mIsShowDialText;
        this.invalidate();
    }

    public void setmDuration(int mDuration) {
        this.mDuration = mDuration;
    }

    public void setmDialsCount(int mDialsCount) {
        this.mDialsCount = mDialsCount;
        this.invalidate();
    }

    public void setmStartAngel(float mStartAngel) {
        this.mStartAngel = (float)((double)(mStartAngel / 180.0F) * 3.141592653589793D);
        this.invalidate();
    }

    public void setmEndAngel(float mEndAngel) {
        this.mEndAngel = (float)((double)(mEndAngel / 180.0F) * 3.141592653589793D);
        this.invalidate();
    }

    public void setmEarLength(float mEarLength) {
        this.mEarLength = mEarLength;
        this.invalidate();
    }

    public void setmEarWidth(float mEarWidth) {
        this.mEarWidth = mEarWidth;
        this.mEarPaint.setStrokeWidth(mEarWidth);
        this.invalidate();
    }

    public void setmProgressWidth(float mProgressWidth) {
        this.mProgressWidth = mProgressWidth;
        this.mProgressPaint.setStrokeWidth(mProgressWidth);
        this.invalidate();
    }

    public void setmBgWidth(float mBgWidth) {
        this.mBgWidth = mBgWidth;
        this.mBgPaint.setStrokeWidth(mBgWidth);
        this.invalidate();
    }

    public void setmSecondBgWidth(float mSecondBgWidth) {
        this.mSecondBgWidth = mSecondBgWidth;
        this.mSecondBgPaint.setStrokeWidth(mSecondBgWidth);
        this.invalidate();
    }

    public void setmSecondBgColor(int mSecondBgColor) {
        this.mSecondBgColor = mSecondBgColor;
        this.mSecondBgPaint.setColor(mSecondBgColor);
        this.invalidate();
    }

    public void setmDialLength(float mDialLength) {
        this.mDialLength = mDialLength;
        this.invalidate();
    }

    public void setmDialSpace(float mDialSpace) {
        this.mDialSpace = mDialSpace;
        this.invalidate();
    }

    public void setmDialWidth(float mDialWidth) {
        this.mDialWidth = mDialWidth;
        this.mDialPaint.setStrokeWidth(mDialWidth);
        this.invalidate();
    }

    public void setmStartEndTextSize(float mStartEndTextSize) {
        this.mStartEndTextSize = mStartEndTextSize;
        this.mTextPaint.setTextSize(mStartEndTextSize);
        this.invalidate();
    }

    public void setmStartEndTextColor(int mStartEndTextColor) {
        this.mStartEndTextColor = mStartEndTextColor;
        this.mTextPaint.setColor(mStartEndTextColor);
        this.invalidate();
    }

    public void setmDialTextSize(float mDialTextSize) {
        this.mDialTextSize = mDialTextSize;
        this.invalidate();
    }

    public void setmDialTextColor(int mDialTextColor) {
        this.mDialTextColor = mDialTextColor;
        this.mDialPaint.setColor(mDialTextColor);
        this.invalidate();
    }

    public void setmIndicatorSize(float mIndicatorSize) {
        this.mIndicatorSize = mIndicatorSize;
        this.invalidate();
    }

    public void setmDialColor(int mDialColor) {
        this.mDialColor = mDialColor;
        if(this.mDialPaint != null) {
            this.mDialPaint.setColor(mDialColor);
            this.invalidate();
        }

    }

    public void setmEarColor(int mEarColor) {
        this.mEarColor = mEarColor;
        this.mEarPaint.setColor(mEarColor);
        this.invalidate();
    }

    public void setmIsShowSecondBg(boolean mIsShowSecondBg) {
        this.mIsShowSecondBg = mIsShowSecondBg;
        this.invalidate();
    }

    public void setmIsShowBg(boolean mIsShowBg) {
        this.mIsShowBg = mIsShowBg;
        this.invalidate();
    }

    public void setProgressData(double max, double current) {
        this.mTotalData = max;
        this.mCurrentData = current;
        this.refreshProgress(true);
    }

    public void setmCurrentData(double currentData) {
        this.mCurrentData = currentData;
        this.refreshProgress(true);
    }

    public void setmTotalData(double totalData) {
        this.mTotalData = totalData;
        this.refreshProgress(true);
    }

    private void refreshProgress(boolean restart) {
        float toProgress;
        if(this.mTotalData > 0.0D && this.mCurrentData > 0.0D) {
            toProgress = (float)(this.mCurrentData / this.mTotalData);
            if((double)toProgress > 1.05D) {
                toProgress = 1.05F;
            }
        } else {
            toProgress = 0.0F;
        }

        this.animateTo(toProgress, restart);
    }

    public void setEndText(String endText) {
        this.endText = endText;
        this.invalidate();
    }

    public void setStartText(String startText) {
        this.startText = startText;
        this.invalidate();
    }

    private void animateTo(final float toProgress, final boolean restart) {
        float startProgress = this.mProgress;
        if(this.getMeasuredWidth() <= 0) {
            this.mProgress = toProgress;
        } else {
            if(this.valueAnimator != null && this.valueAnimator.isRunning()) {
                this.valueAnimator.cancel();
            }

            if(restart) {
                this.valueAnimator = ValueAnimator.ofFloat(new float[]{startProgress, 0.0F, toProgress});
                this.valueAnimator.setInterpolator(new OvershootInterpolator(1.02F));
                this.valueAnimator.setDuration((long)this.mDuration);
            } else {
                this.valueAnimator = ValueAnimator.ofFloat(new float[]{startProgress, toProgress});
                this.valueAnimator.setInterpolator(new BounceInterpolator());
                this.valueAnimator.setDuration(500L);
            }

            this.valueAnimator.addUpdateListener(new AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    DashboardProgressbar.this.mProgress = ((Float)animation.getAnimatedValue()).floatValue();
                    DashboardProgressbar.this.postInvalidate();
                    if(DashboardProgressbar.this.mAnimationListener != null && restart) {
                        DashboardProgressbar.this.mAnimationListener.onDataUpdate(((Float)animation.getAnimatedValue()).floatValue());
                    }

                }
            });
            this.valueAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    DashboardProgressbar.this.mProgress = toProgress;
                    DashboardProgressbar.this.invalidate();
                    if(DashboardProgressbar.this.mAnimationListener != null) {
                        DashboardProgressbar.this.mAnimationListener.onAnimComplete();
                    }

                }
            });
            this.valueAnimator.start();
        }
    }

    public interface OnDataUpdateListener {
        void onDataUpdate(float var1);

        void onAnimComplete();
    }
}
