package com.wzz.flowlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FlowLayout4 extends ViewGroup {
    public FlowLayout4(Context context) {
        this(context, null);
    }

    public FlowLayout4(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowLayout4(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    List<View> lineViews ;
    /**
     * 记录每一行中的view集合
     */
    Map<Integer, List<View>> map ;
    /**
     * 记录每一行中的最大高度集合
     */
    Map<Integer, Integer> heights ;


    //  MeasureSpec是封装父布局对子布局的布局要求的类。为了减少内存分配，MeasueSpecs用整数表示。这个类提供打包和解包<size,mode>元组为整型
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        /**
         * 为了防止多次测量 要把初始化放在onMeasure里
         */
        init();

        //1. 测量自身
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 2. 为每个子View计算测量的限制信息 Mode / Size
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int flowWidth = 0; // 最终的flow的宽度
        int flowHeight = 0; // 最终的flow的高度

        // 每一行的宽度和高度
        //记录当前行的宽度和高度
        int lineWidth = 0;// 宽度是当前行子view的宽度之和
        int lineHeight = 0;// 高度是当前行所有子View中高度的最大值

        int childCount = getChildCount();

        int line = 0 ;

        for (int i = 0; i < childCount; i++) {

            View child = getChildAt(i);
            // 测量子view的宽高
            measureChild(child, widthMeasureSpec, heightMeasureSpec); // 或者在for循环外直接使用measureChildren(); 即可一次测量所有子view的宽高

            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();

            if (  lineWidth + childWidth > widthSize) {  // 代表新一行

                // 新的一行需要重新创建一个list对象
                lineViews = new ArrayList<>();

                line ++ ;
                // 重置每一行的宽高 初始为0
                lineWidth = 0;
                lineHeight = 0;

            }


            lineWidth += childWidth;
            lineHeight = Math.max(lineHeight, childHeight);

            lineViews.add(child);

            // 将此行的子view的属性 添加至map
            map.put( line , lineViews ) ;
            heights.put( line , lineHeight );

            // 布局的宽度为 所有行中的宽度最大的一行
            flowWidth = Math.max( flowWidth, lineWidth );

        }

        /**
         * 遍历所有子view后， 计算此布局具体的高度：为每一行的最大高度相加
         */
        Set<Integer> keySet = heights.keySet();
        for (Integer key : keySet) {
            Integer height = heights.get(key);
            flowHeight += height ;
        }


        // 确定布局的自身尺寸大小 ---> FlowLayout最终宽高
        setMeasuredDimension(widthMode == MeasureSpec.EXACTLY ? widthSize : flowWidth,
                heightMode == MeasureSpec.EXACTLY ? heightSize : flowHeight);
    }

    /**
     * 为了防止多次测量 初始化要在onMeasure()中去做
     */
    private void init() {
        lineViews = new ArrayList<>();
        /**
         * 记录每一行中的view集合
         */
        map = new HashMap<>();
        /**
         * 记录每一行中的最大高度集合
         */
        heights = new HashMap<>();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int currX = 0;
        int currY = 0;

        Set<Integer> keySet = map.keySet(); // 行数
        for (Integer key : keySet) { // 第一行 第二行
            List<View> views = map.get(key);
            Integer lineHeight = heights.get(key);

            // 遍历每一行的子view
            for (View child : views) {

                int left = currX;
                int top = currY;
                int right = left + child.getMeasuredWidth();
                int bottom = top + child.getMeasuredHeight();

                child.layout(left, top, right, bottom);

                currX += child.getMeasuredWidth();
            }

            /**
             * 遍历完一行后 x从0开始 height为
             */
            currX = 0 ;
            currY += lineHeight ;


        }

    }
}
