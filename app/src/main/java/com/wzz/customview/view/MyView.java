package com.wzz.customview.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.wzz.customview.R;


public class MyView extends View {
    public MyView(Context context) {
        this(context, null);
    }

    public MyView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    private static final String TAG = "MyView";

    private int height;
    private int width;
    private String name;
    private int sex;
    private boolean student;

    public MyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        for (int i = 0; i < attrs.getAttributeCount() ; i++) {

            Log.i(TAG, "=============" + attrs.getAttributeName(i) + "  = " + attrs.getAttributeValue(i) );
        }

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.burce);

        height = array.getInt(R.styleable.burce_mHeight, 0);
        width = array.getInt(R.styleable.burce_mWidth, 0);
        name = array.getString(R.styleable.burce_mName);
        sex = array.getInt(R.styleable.burce_sex, 0);
        student = array.getBoolean(R.styleable.burce_student, true);

        mTextColor = array.getColor(R.styleable.burce_mColor, Color.BLUE);
        float size = array.getDimension(R.styleable.burce_mSize, 15);


        array.recycle(); // 必须写 回收typedArray

        Log.i(TAG, "height: " + height);
        Log.i(TAG, "width: " + width);
        Log.i(TAG, "name: " + name);
        Log.i(TAG, "sex: " + sex);
        Log.i(TAG, "student: " + student);
        Log.i(TAG, "color: " + mTextColor);
        Log.i(TAG, "size: " + size);

//        this.setBackgroundColor(color);


        //新建画笔对象
        mPaint = new Paint();
        //设置画笔
        mPaint.setTextSize(mTextSize);
        mBound = new Rect();
        //设置画笔绘制文字及相关区域
        mPaint.getTextBounds(mText, 0, mText.length(), mBound);
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mText = "点击了......";
                //刷新画布
                postInvalidate();
            }
        });

    }

    //文本
    private String mText = "haha";
    //文本的颜色
    private int mTextColor;
    //文本的大小
    private int mTextSize = 50 ;
    //文本的背景
    private int mTextBg = Color.RED ;
    //绘制时控制文本绘制的范围
    private Rect mBound;
    //绘制文本画笔
    private Paint mPaint;

    @Override
    protected void onDraw(Canvas canvas) {
        //设置画布颜色即文字背景色
        mPaint.setColor(mTextBg);
        //绘制背景,全屏
        canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), mPaint);
        //设置文字颜色
        mPaint.setColor(mTextColor);
        //绘制文字
        canvas.drawText(mText, getWidth() / 2 - mBound.width() / 2, getHeight() / 2 + mBound.height() / 2, mPaint);
    }
}
