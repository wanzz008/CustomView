package com.wzz.customview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * 关于onMeasure方法 _wzz
 * 请参考：https://www.cnblogs.com/yishujun/p/5560838.html
 */
public class MyOnMeasureView extends View {
    private Context mContext;
    //定义一个paint
    private Paint mPaint;

    private String mText = "测试文字，自定义view";
    //绘制时控制文本绘制的范围
    private Rect mBound;

    public MyOnMeasureView(Context context) {
        this(context, null);
    }


    public MyOnMeasureView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * 二者返回结果确实不同，且 measureText() 返回结果会略微大于 getTextBounds() 所得到的宽度信息
     *
     * measureText() 会在文本的左右两侧加上一些额外的宽度 ,
     * getTextBounds() 返回的则是当前文本所需要的最小宽度，也就是整个文本外切矩形的宽度
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public MyOnMeasureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        mBound = new Rect();
        mPaint = new Paint();

        mPaint.setTextSize(50);

        // 只会获取文本的宽度
        float measureText = mPaint.measureText(mText);
        // 把文本宽高存储在Rect中
        mPaint.getTextBounds(mText, 0, mText.length(), mBound);

        int width = mBound.width();
        int height = mBound.height();

        // 打印宽度信息
        Log.d("Test",  String.format(
                "Text is '%s', measureText %f,  getTextBoundsWidth %d , getTextBoundsHeight %d" ,
                mText,
                measureText,
                width, height )
        );


    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Log.d("Test", getWidth() + " " + getHeight() ) ;

        canvas.drawColor(Color.GRAY);
        mPaint.setColor(Color.RED);
        canvas.drawText(mText, getWidth()/2 - mBound.width() / 2  ,  getHeight() /2 + mBound.height() / 2  , mPaint);

    }

    /**
     * 比onDraw先执行
     * <p>
     * 一个MeasureSpec封装了父布局传递给子布局的布局要求，每个MeasureSpec代表了一组宽度和高度的要求。
     * 一个MeasureSpec由大小和模式组成
     * 它有三种模式：UNSPECIFIED(未指定),父元素不对子元素施加任何束缚，子元素可以得到任意想要的大小;
     * EXACTLY(完全)，父元素决定子元素的确切大小，子元素将被限定在给定的边界里而忽略它本身大小；
     * AT_MOST(至多)，子元素至多达到指定大小的值。
     * <p>
     * 它常用的三个函数：
     * 1.static int getMode(int measureSpec):根据提供的测量值(格式)提取模式(上述三个模式之一)
     * 2.static int getSize(int measureSpec):根据提供的测量值(格式)提取大小值(这个大小也就是我们通常所说的大小)
     * 3.static int makeMeasureSpec(int size,int mode):根据提供的大小值和模式创建一个测量值(格式)
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        Log.e("YView", "---onMeasure.......");

        Log.e("YView", "---widthMeasureSpec = " + widthMeasureSpec + "");
        Log.e("YView", "---heightMeasureSpec = " + heightMeasureSpec + "");

        final int minimumWidth = getSuggestedMinimumWidth();
        final int minimumHeight = getSuggestedMinimumHeight();
        Log.e("YView", "---minimumWidth = " + minimumWidth + "");
        Log.e("YView", "---minimumHeight = " + minimumHeight + "");
        int width = measureWidth(minimumWidth, widthMeasureSpec);
        int height = measureHeight(minimumHeight, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    private int measureWidth(int defaultWidth, int measureSpec) {

        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        Log.e("YViewWidth", "---speSize = " + specSize + "");
        switch (specMode) {
            case MeasureSpec.AT_MOST:
                defaultWidth = (int) mPaint.measureText(mText) + getPaddingLeft() + getPaddingRight();

                Log.e("YViewWidth", "---speMode = AT_MOST");
                break;
            case MeasureSpec.EXACTLY:
                Log.e("YViewWidth", "---speMode = EXACTLY");
                defaultWidth = specSize;
                break;
            case MeasureSpec.UNSPECIFIED:
                Log.e("YViewWidth", "---speMode = UNSPECIFIED");
                defaultWidth = Math.max(defaultWidth, specSize);
        }
        return defaultWidth;
    }


    private int measureHeight(int defaultHeight, int measureSpec) {

        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        Log.e("YViewHeight", "---speSize = " + specSize + "");

        switch (specMode) {
            case MeasureSpec.AT_MOST:
                defaultHeight = (int) (-mPaint.ascent() + mPaint.descent()) + getPaddingTop() + getPaddingBottom();
                Log.e("YViewHeight", "---speMode = AT_MOST");
                break;
            case MeasureSpec.EXACTLY:
                defaultHeight = specSize;
                Log.e("YViewHeight", "---speSize = EXACTLY");
                break;
            case MeasureSpec.UNSPECIFIED:
                defaultHeight = Math.max(defaultHeight, specSize);
                Log.e("YViewHeight", "---speSize = UNSPECIFIED");
//        1.基准点是baseline
//        2.ascent：是baseline之上至字符最高处的距离
//        3.descent：是baseline之下至字符最低处的距离
//        4.leading：是上一行字符的descent到下一行的ascent之间的距离,也就是相邻行间的空白距离
//        5.top：是指的是最高字符到baseline的值,即ascent的最大值
//        6.bottom：是指最低字符到baseline的值,即descent的最大值

                break;
        }
        return defaultHeight;


    }
}