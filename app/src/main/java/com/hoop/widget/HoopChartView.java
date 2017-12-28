package com.hoop.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;

/**
 * 自定义环形统计图
 *
 * @Author Yang
 */

public class HoopChartView extends View {
    //-------------数据相关-------------
    private String[] str = null;
    //分配比例大小
    private float[] strPercent = null;
    //-------------画笔相关-------------
    //圆环的画笔
    private Paint cyclePaint;
    //统计图圆的直径
    private float mChartRadius = 300;
    //标注圆环的直径
    private float mLabelRadius = 36;
    //圆的粗细
    private float mStrokeWidth = 60;
    //标注线的长度
    private float mLineStroke = 35;
    //文字大小
    private int textSize = 40;
    //圆心
    private float centerX, centerY;
    private final float marginARC = 120;
    //文字的画笔
    private Paint textPaint;
    //标注的画笔
    private Paint labelPaint;
    //-------------颜色相关-------------
    //边框颜色和标注颜色
    private int[] mColor = null;
    //文字颜色
    private final int textColor = Color.parseColor("#444444");
    //-------------View相关-------------
    //View自身的宽和高
    private int mHeight; // 高度
    private int mWidth; // 宽度
    private boolean initDrawParam = false;//是否初始化draw数据
    /***
     * set Animation to view, run for processData()
     * All codes under this ....
     */
    private float startPercent = 0; // 开始角度
    private float sweepPercent = 0; // 偏移角度

    /**
     * Constructor that is called when inflating a view from XML. This is called
     * when a view is being constructed from an XML file, supplying attributes
     *
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     */
    public HoopChartView(Context context) {
        this(context, null);
    }

    /**
     * Constructor that is called when inflating a view from XML. This is called
     * when a view is being constructed from an XML file, supplying attributes
     *
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     * @param attrs   The attributes of the XML tag that is inflating the view.
     */
    public HoopChartView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Constructor that is called when inflating a view from XML. This is called
     * when a view is being constructed from an XML file, supplying attributes
     *
     * @param context      The Context the view is running in, through which it can
     *                     access the current theme, resources, etc.
     * @param attrs        The attributes of the XML tag that is inflating the view.
     * @param defStyleAttr An attribute in the current theme that contains a
     *                     reference to a style resource that supplies default values for
     *                     the view. Can be 0 to not look for defaults.
     */
    public HoopChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //初始化画笔
        initPaint();
    }

    /**
     * This is called during layout when the size of this view has changed. If
     * you were just added to the view hierarchy, you're called with the old
     * values of 0.
     *
     * @param w    Current width of this view.
     * @param h    Current height of this view.
     * @param oldw Old width of this view.
     * @param oldh Old height of this view.
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        //边框画笔
        cyclePaint = new Paint();
        cyclePaint.setAntiAlias(true);
        cyclePaint.setStyle(Paint.Style.STROKE);
        cyclePaint.setStrokeWidth(mStrokeWidth);
        //标注画笔
        labelPaint = new Paint();
        labelPaint.setAntiAlias(true);
        labelPaint.setStyle(Paint.Style.FILL);
        labelPaint.setStrokeWidth(5);
        //文字画笔
        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(textColor);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setStrokeWidth(1);
        textPaint.setTextSize(textSize);
    }

    /**
     * 画统计圆环
     */
    private void drawCycle(Canvas canvas, int index) {
        // 绘制统计图
        cyclePaint.setColor(mColor[index]);
        startPercent = sweepPercent + startPercent;
        //这里采用比例占100的百分比乘于360的来计算出占用的角度
        sweepPercent = strPercent[index] * 360;
        canvas.drawArc(new RectF(0, 0, mChartRadius, mChartRadius), startPercent, (float) Math.ceil(sweepPercent) + 1, false, cyclePaint);
        float consF = Math.abs((float) Math.cos(Math.toRadians((2 * startPercent + sweepPercent) / 2)));
        float sinF = Math.abs((float) Math.sin(Math.toRadians((2 * startPercent + sweepPercent) / 2)));
        // Log.e("DRAW", i + "->开始：" + startPercent + " 偏移" + sweepPercent + " 计算：" + (2 * startPercent + sweepPercent) / 2);
        // Log.e("DRAW", i + "cos:" + consF + " sin:" + sinF);
        float angle = (2 * startPercent + sweepPercent) / 2;
        int[] xys = checkXY(angle);
        float flagX = xys[0] * consF * (mChartRadius + marginARC) / 2 + centerX;
        float flagY = xys[1] * sinF * (mChartRadius + marginARC) / 2 + centerY;
        // flagY = quadrant == 3 || quadrant == 4 ? flagY + 10 : flagY;
        // 画圆环牵引线
        labelPaint.setColor(mColor[index]);
        float[] whs = drawLabel(canvas, flagX, flagY, xys, angle);
        // 画文字
        float textW = textPaint.measureText(str[index]);
        flagX = xys[0] == -1 ? flagX - textW - whs[0] : flagX + whs[0];
        flagY = flagY + mLabelRadius / 2 + whs[1] - 3;
        drawText(canvas, str[index], flagX, flagY);
    }

    /**
     * 判断方向 根据角度
     *
     * @param angle
     * @return
     */
    private int[] checkXY(float angle) {
        if (angle <= 90) {
            return new int[]{1, 1};
        } else if (angle <= 180) {
            return new int[]{-1, 1};
        } else if (angle <= 270) {
            return new int[]{-1, -1};
        } else if (angle <= 360) {
            return new int[]{1, -1};
        } else {
            return new int[]{1, 1};
        }
    }

    /**
     * 画圆环牵引线
     * 返回 整个标注的width，height
     *
     * @param canvas
     * @param flagX
     * @param flagY
     * @param xys
     * @return
     */
    private float[] drawLabel(Canvas canvas, float flagX, float flagY, int[] xys, float angle) {
        float width = mLabelRadius, height = 0;
        // 半径
        float halfRadius = mLabelRadius / 2;
        // 外环圆
        labelPaint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(flagX, flagY, halfRadius, labelPaint);
        // 内环圆
        labelPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(flagX, flagY, halfRadius - 8, labelPaint);
        // 划线
        float marginExcursion = 5;// 间距偏移
        float initX = flagX + (halfRadius + marginExcursion) * xys[0];
        float transX = initX + mLineStroke * xys[0];
        // 由于变态的设计，有变态的拐角，所以有折线；且是由不同位置而做的触边处理
        float drawExcursion = 30;// 划线偏移
        // 触边判断及相关逻辑处理
        if (angle > 45 && angle < 135) {
            flagY += marginExcursion;
            // 线一处理
            canvas.drawLines(new float[]{initX, flagY, transX, flagY + drawExcursion}, labelPaint);
            // 线二处理
            canvas.drawLines(new float[]{transX, flagY + drawExcursion, transX + mLineStroke * xys[0], flagY + drawExcursion}, labelPaint);
            width += 2 * mLineStroke;
            height += drawExcursion + marginExcursion;
        } else if (angle > 225 && angle < 315) {
            flagY -= marginExcursion;
            // 线一处理
            canvas.drawLines(new float[]{initX, flagY, transX, flagY - drawExcursion}, labelPaint);
            // 线二处理
            canvas.drawLines(new float[]{transX, flagY - drawExcursion, transX + mLineStroke * xys[0], flagY - drawExcursion}, labelPaint);
            width += 2 * mLineStroke;
            height -= drawExcursion;
            height -= marginExcursion;
        } else {
            canvas.drawLines(new float[]{initX, flagY, transX, flagY}, labelPaint);
            width += mLineStroke;
        }
        return new float[]{width, height};
    }


    /**
     * 绘制文字
     *
     * @param canvas 画布
     * @param string 绘制的内容
     * @param flagX  起始点X坐标
     * @param flagY  起始点Y坐标
     */
    private void drawText(Canvas canvas, String string, float flagX, float flagY) {
        canvas.drawText(string, flagX, flagY, textPaint);
    }

    /**
     * 装配数据
     *
     * @param datas
     */
    private void processSetData(@NonNull List<HoopChart> datas) {
        if (datas != null && datas.size() > 0) {
            // 对所有数据进行清空处理，并对数据进行赋值
            int len = datas.size();
            str = new String[len];
            strPercent = new float[len];
            mColor = new int[len];
            // 赋值数据
            for (int i = 0; i < datas.size(); i++) {
                HoopChart investChart = datas.get(i);
                if (investChart != null) {
                    str[i] = investChart.text;
                    strPercent[i] = investChart.percent;
                    mColor[i] = investChart.color;
                }
            }
        }
    }

    /**
     * 自定义图标绘制工作
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 设置参数
        if (!initDrawParam) {
            initDrawParam = true;
            //设置直径/圆心
            mChartRadius = mWidth > 0 ? mWidth / 3 : mWidth;
            centerX = centerY = mChartRadius / 2;
        }
        //初始化画布位置
        canvas.translate(mWidth / 2 - mChartRadius / 2, mHeight / 2 - mChartRadius / 2);
        // 绘制虚圆
        cyclePaint.setColor(Color.parseColor("#E3F0F6"));
        canvas.drawCircle(centerX, centerY, mChartRadius / 2, cyclePaint);
        // 执行绘制
        if (strPercent != null && strPercent.length > 0) {
            startPercent = 0;
            sweepPercent = 0;
            for (int index = 0; index < strPercent.length; index++) {
                drawCycle(canvas, index);
            }
        }
    }

    /**
     * Draw a series of datas
     * It's more datas to chart, You must set the arrays to 6.
     * hecking is performed, so the caller must ensure
     *
     * @param datas
     */
    public void refresh(@NonNull List<HoopChart> datas) {
        processSetData(datas);
        // 执行操作，刷新UI
        invalidate();
    }

    /**
     * 投资数据
     */
    public static class HoopChart {
        public String text; // 文字
        public float percent; // 百分比小数
        public int color; // 颜色

        public HoopChart(String text, float percent, int color) {
            this.text = text;
            this.percent = percent;
            this.color = color;
        }
    }
}
