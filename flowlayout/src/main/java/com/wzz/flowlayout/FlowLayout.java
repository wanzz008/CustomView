package com.wzz.flowlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class FlowLayout extends ViewGroup {
    public FlowLayout(Context context) {
        this(context , null );
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs , 0 );
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        lineViews = new ArrayList<>();
        views = new ArrayList<>();
        heights = new ArrayList<>();

    }

    private List<View> lineViews;//每一行的子View
    private List<List<View>> views;//所有的行 一行一行的存储
    private List<Integer> heights;//每一行的高度

    //  MeasureSpec是封装父布局对子布局的布局要求的类。为了减少内存分配，MeasueSpecs用整数表示。这个类提供打包和解包<size,mode>元组为整型
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        //1. 测量自身
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 2. 为每个子View计算测量的限制信息 Mode / Size
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int flowWidth = 0 ; // 最终的flow的宽度
        int flowHeight = 0 ;

        // 每一行的宽度和高度
        //记录当前行的宽度和高度
        int lineWidth = 0;// 宽度是当前行子view的宽度之和
        int lineHeight = 0;// 高度是当前行所有子View中高度的最大值

        int childCount = getChildCount();

        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            //测量子View 获取到当前子View的测量的宽度/高度
            measureChild(child,widthMeasureSpec,heightMeasureSpec);
            //获取到当前子View的测量的宽度/高度
            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();

            if ( lineWidth + childWidth > widthSize ){ // 需要换行

                views.add( lineViews );
                lineViews = new ArrayList<>();  // 新创建一行

                flowWidth = Math.max( flowWidth , lineWidth );
                flowHeight = flowHeight + lineHeight ;

                heights.add( lineHeight );

                lineWidth = 0 ;
                lineHeight = 0 ;

            }
            lineWidth += childWidth ;
            lineHeight = Math.max( lineHeight , childHeight ) ;
            lineViews.add( child );

        }

        // 确定布局的自身尺寸大小 ---> FlowLayout最终宽高
        setMeasuredDimension( widthMode == MeasureSpec.EXACTLY ? widthSize : flowWidth ,
                heightMode == MeasureSpec.EXACTLY ? heightSize : flowHeight );
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int lineCount = views.size();

        int currX = 0;
        int currY = 0;

        for (int i = 0; i < lineCount; i++) {

            List<View> lineViews = this.views.get(i); // 取出一行中所有的view
            int lineHeight = heights.get(i); // 这一行的高度
            //遍历当前行的子View
            for (int j = 0; j < lineViews.size(); j++) {  //布局当前行的每一个view

                View child = lineViews.get(j);

                int left = currX ;
                int top = currY ;
                int right = left + child.getMeasuredWidth() ;
                int bottom = top + child.getMeasuredHeight() ;

                child.layout( left , top , right , bottom );
                // 确定下一个view的left
                currX += child.getMeasuredWidth() ;

            }

            currY += lineHeight ;
            currX = 0 ;

        }

    }
}
