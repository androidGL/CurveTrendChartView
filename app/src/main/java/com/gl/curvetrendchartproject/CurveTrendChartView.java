package com.gl.curvetrendchartproject;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: gl
 * @CreateDate: 2019/12/18
 * @Description: 一个通用的趋势图
 */
public class CurveTrendChartView  extends View {
    /**
     * 先想想思路~
     * 一个曲线图有以下6个要素
     * 1，需要有横轴集合 List<T> xDataList
     * 2，需要有Y轴集合
     * 3，需要有横轴单位
     * 4，需要有Y轴单位
     * 5，需要有数据点集合，可能是多条折线，以及折现颜色，折线含义
     * 6，需要有正常范围、异常范围，以及范围背景色，范围含义
     *
     * 需要的画笔有
     * 1，虚线画笔
     * 2，文字（横坐标文字，纵坐标文字，描述含义的文字）
     * 3，折线的画笔
     *
     *
     * 需要的类：
     * 1，表示X数据和Y数据的
     * 2，背景
     * 3，每个折线
     */
    private Context mContext;
    //总宽高
    private int mWidth;
    private int mHeight;
    //Y轴宽高
    private int mYWidth = 100, mYHeight;
    //X轴宽高
    private int mXWidth, mXHeight = 50;

    //虚线的画笔
    private Paint mDottedLinePaint;
    private Path mDottedLinePath;
    private int mDottedLineColor = Color.BLUE;
    //曲线的画笔
    private Paint mCurvePaint;
    //曲线圆点的画笔
    private Paint mPointPaint;
    private int mPointColor = Color.BLACK;
    private int pointSize = 8;

    //文字的画笔
    private Paint mTextPaint;
    private int mTextColor = Color.BLACK;
    private int mTextSize = 30;
    //X轴文字大小,颜色
    private int mXTextSize, mXTextColor;
    //Y轴文字大小,颜色
    private int mYTextSize, mYTextColor;

    // Y轴的数据源
    private List<Integer> mYDataList;
    private double minData, maxData;
    // Y轴数据的单位
    private String mYDataUnit = "mmHg";
    private int mYUnitHeight = 30;
    //y轴每个item的高度
    private float mYItemHeight;

    //X轴的数据源
    private List<Integer> mXDataList;
    //X轴数据的单位
    private String mXDataUnit = "日期";
    private float mXUnitHeight = 70;
    //X轴每个item的宽度
    private float mXItemWidth;

    //曲线的数据源
    private List<CurveModel> mLineList;
    private List<BkgModel> mBkgList;

    //正常范围/异常范围背景
    private Paint bkgPaint;

    //顶部描述文字
    //描述框的高度
    private int descHeight = 100;
    //X轴和Y轴保留的小数点
    private double mXNum,mYNum;

    public CurveTrendChartView(Context context) {
        this(context, null);
    }

    public CurveTrendChartView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public CurveTrendChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        //获取属性文件
        TypedArray array = mContext.obtainStyledAttributes(attrs, R.styleable.CurveTrendChartView);
        mXTextSize = array.getDimensionPixelSize(R.styleable.CurveTrendChartView_xTextSize,mTextSize);
        mYTextSize = array.getDimensionPixelSize(R.styleable.CurveTrendChartView_yTextSize,mTextSize);
        mXTextColor = array.getColor(R.styleable.CurveTrendChartView_xTextColor,mTextColor);
        mYTextColor = array.getColor(R.styleable.CurveTrendChartView_yTextColor,mTextColor);
        //TypedArray使用完一定要回收，否则会造成内存泄漏
        array.recycle();
    }

    /**
     * 初始化画笔和路径
     */
    private void initPaint() {
        mDottedLinePaint = new Paint();
        mDottedLinePaint.setAntiAlias(true);//抗锯齿效果
        mDottedLinePaint.setStyle(Paint.Style.STROKE);
        mDottedLinePaint.setColor(mDottedLineColor);
        mDottedLinePaint.setStrokeWidth(2);
        mDottedLinePath = new Path();

        mCurvePaint = new Paint();
        mCurvePaint.setAntiAlias(true);
        mCurvePaint.setStyle(Paint.Style.STROKE);

        mPointPaint = new Paint();
        mPointPaint.setAntiAlias(true);
        mPointPaint.setStyle(Paint.Style.FILL);
        mPointPaint.setColor(mPointColor);
        mPointPaint.setStrokeWidth(pointSize);
        mPointPaint.setTextAlign(Paint.Align.CENTER);

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        bkgPaint = new Paint();
        bkgPaint.setAntiAlias(true);
        bkgPaint.setStyle(Paint.Style.FILL);

    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mXWidth = mWidth - mYWidth;
        mXItemWidth = (mWidth - mYWidth - mXUnitHeight) / (float) mXDataList.size();
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制背景和说明文字的方法，在该方法初始化了高度的一些变量，所以首先执行该方法
        drawDesc(canvas);
        //绘制Y坐标
        drawYLineText(canvas);
        //绘制X坐标
        drawXLineText(canvas);
        //绘制背景虚线
        drawDottedLine(canvas);
        //绘制每条折线
        drawCurceLine(canvas);
    }
    /**
     * 绘制描述语句部分
     * @param canvas
     * 初始化了全局变量@{mYHeight}
     */
    private void drawDesc(Canvas canvas) {
        if(null != mBkgList && mBkgList.size()>0){
            //设置对齐方式为左对齐
            mTextPaint.setTextAlign(Paint.Align.LEFT);
            int bkgHeight = 40;
            mTextPaint.setTextSize(mTextSize);
            mTextPaint.setColor(mTextColor);

            //绘制背景说明颜色和说明文字
            //定义框的左边距，定义每个说明背景颜色块宽度
            int left = mWidth / 2 + 20, width = 80;
            //说明框的高度
            descHeight =  10+bkgHeight/2;
            for(BkgModel item : mBkgList){
                canvas.save();
                bkgPaint.setStrokeWidth(bkgHeight);
                bkgPaint.setColor(item.getColor());
                //画背景框
                canvas.drawLine(left, descHeight+10 , left + width,descHeight+10 , bkgPaint);
                //画描述语句
                canvas.drawText(item.getDesc(), left + width + 20, descHeight+10+mTextSize/2, mTextPaint);
                canvas.restore();
                descHeight = descHeight+10+bkgHeight;
            }

            //绘制折线说明颜色和说明文字
            left = left + width + 230;
            int height =10+ bkgHeight/2;
            for(CurveModel item:mLineList){
                canvas.save();
                mCurvePaint.setColor(item.getCurveColor());
                //画折线颜色
                canvas.drawLine(left, height+10, left + width, height+10, mCurvePaint);
                //画描述语句
                canvas.drawText(item.getCurveDesc(), left + width + 20, height+10+mTextSize/2, mTextPaint);
                canvas.restore();
                height = height+10+bkgHeight;

            }

            //画描述语句的外框
            canvas.drawRect(new Rect(mWidth / 2, 10, mWidth - 10, descHeight + 10 - bkgHeight/2), mDottedLinePaint);

            //初始化y轴高度和每行的高度
//            descHeight -= 10;
            mYHeight = mHeight - mXHeight -mYUnitHeight- descHeight;
            //注意，这儿最好转为float，不然会导致高度计算不太准，我找位置不准的原因找了很久很久，终于找到了
            mYItemHeight = mYHeight / (float) mYDataList.size();

            //在趋势表中绘制背景色
            for(BkgModel item : mBkgList){
                canvas.save();
                //获取最低值和最高值的下标
                int bottom = mYDataList.indexOf(item.getValue().getX()),high = mYDataList.indexOf(item.getValue().getY());
                //计算背景的高度
                bkgPaint.setStrokeWidth((bottom - high) * mYItemHeight);
                bkgPaint.setColor(item.getColor());
                //计算背景的起始位置
                float diastolicStart = (float) ((high +(bottom - high)/2.0 +0.5 ) * mYItemHeight + mYUnitHeight + descHeight);
                canvas.drawLine(mYWidth, diastolicStart, mWidth, diastolicStart, bkgPaint);
                canvas.restore();

            }

        }
    }

    /**
     * 对应Y轴的虚线
     * @param canvas
     */
    private void drawDottedLine(Canvas canvas) {
        int count = mYDataList.size();
        if (count > 0) {
            canvas.save();
            //虚线效果：先画5的实线，再画5的空白，开始绘制的偏移值为0
            mDottedLinePaint.setPathEffect(new DashPathEffect(new float[]{5, 5}, 0));
            mDottedLinePaint.setStrokeWidth(1f);
            float startY;
            for (int i = 0; i < count; i++) {
                //因为要绘制在中间，所以得加上mYItemHeight / 2，再加上mYUnitHeight + descHeight
                startY = i * mYItemHeight + mYItemHeight / 2 + mYUnitHeight + descHeight;
                mDottedLinePath.reset();
                mDottedLinePath.moveTo(mYWidth, startY);
                mDottedLinePath.lineTo(mWidth, startY);
                canvas.drawPath(mDottedLinePath, mDottedLinePaint);
            }
            canvas.restore();
        }
    }

    /**
     * Y轴的文字
     * @param canvas
     */
    private void drawYLineText(Canvas canvas) {
        int count = mYDataList.size();
        if (count > 0) {
            canvas.save();
            mTextPaint.setTextSize(mYTextSize);
            mTextPaint.setColor(mYTextColor);
            float startY;
            float baseline;
            Paint.FontMetricsInt metrics = mTextPaint.getFontMetricsInt();
            mTextPaint.setTextAlign(Paint.Align.CENTER);
            for (int i = 0; i < count; i++) {
                startY = (i + 1) * mYItemHeight;
                baseline = (startY * 2 - mYItemHeight - metrics.bottom - metrics.top) / 2 + mYUnitHeight + descHeight;
                canvas.drawText(String.valueOf(mYDataList.get(i)/mYNum), mYWidth / 2, baseline, mTextPaint);
            }
            canvas.drawText(mYDataUnit, mYWidth / 2, descHeight, mTextPaint);
            canvas.restore();
        }
    }


    /**
     * X轴的文字
     * @param canvas
     */
    private void drawXLineText(Canvas canvas) {
        int count = mXDataList.size();
        if (count > 0) {
            canvas.save();
            mTextPaint.setTextAlign(Paint.Align.CENTER);
            mTextPaint.setTextSize(mXTextSize);
            mTextPaint.setColor(mXTextColor);
            float startX;
            for (int i = 0; i < count; i++) {
                startX = mYWidth + i * mXItemWidth + mXItemWidth / 2;
                canvas.drawText(String.valueOf((mXDataList.get(i)/mXNum)), startX, mHeight-mXHeight/2, mTextPaint);
            }
            canvas.restore();
            mTextPaint.setTextSize(mTextSize);
            mTextPaint.setColor(mTextColor);
            canvas.drawText(mXDataUnit, mWidth - mXItemWidth / 2, mHeight-mXHeight/2, mTextPaint);
        }
    }

    /**
     * 绘制曲线
     * @param canvas
     */
    private void drawCurceLine(Canvas canvas) {
        for(CurveModel item:mLineList) {
            canvas.save();
            mCurvePaint.setColor(item.getCurveColor());
            List<Value> curveLineDataList = item.getCurveLineDataList();
            int count = curveLineDataList.size();
            if (count > 0) {
                mDottedLinePath.reset();
                float stopX, stopY;
                float baseHeight = mYItemHeight / 2 + mYUnitHeight + descHeight;
                //因为虚线是在中间绘制的，所以得减去最上虚线的上半部分和最下虚线的下半部分
                float totalHeight = mYHeight  - mYItemHeight;
                float totalWidth = mWidth - mYWidth - mXUnitHeight - mXItemWidth;
                mDottedLinePath.moveTo(mXItemWidth / 2 + mYWidth, (float) (totalHeight * ((curveLineDataList.get(0).getY() - maxData) / (minData - maxData))) + baseHeight);
                canvas.drawPoint(mXItemWidth / 2 + mYWidth, (float) (totalHeight * ((curveLineDataList.get(0).getY() - maxData) / (minData - maxData))) + baseHeight, mPointPaint);
                for (int i = 1; i < count; i++) {
                    stopX = (float) (totalWidth * (curveLineDataList.get(i).getX() - mXDataList.get(0)) / (mXDataList.get(mXDataList.size() - 1) - mXDataList.get(0)) + mYWidth + mXItemWidth / 2);
                    //根据比例求得点的坐标
                    stopY = (float) (totalHeight * ((curveLineDataList.get(i).getY() - maxData) / (minData - maxData)) + baseHeight);
                    mDottedLinePath.lineTo(stopX, stopY);
                    canvas.drawPoint(stopX, stopY, mPointPaint);

                }
                canvas.drawPath(mDottedLinePath, mCurvePaint);
            }
            canvas.restore();
        }
    }

    /**
     * 初始化全局变量
     * @param context 上下文
     * @param xUnit x轴的单位
     * @param xDataList X轴数据源
     * @param xNum x轴保留的小数点位数
     * @param yUnit y轴的单位
     * @param yDataList y轴的数据源
     * @param yNum y轴保留的小数点位数
     * @param lineList 曲线列表
     * @param bkgItemList 说明背景列表
     * @return
     */
    private CurveTrendChartView init(Context context, String xUnit, List<Integer> xDataList, double xNum, String yUnit, List<Integer> yDataList, double yNum, List<CurveModel> lineList, List<BkgModel> bkgItemList){
        this.mContext = context;
        this.mXDataUnit = xUnit;
        this.mXDataList = xDataList;
        this.mXNum = xNum;
        this.mYDataUnit = yUnit;
        this.mYDataList = yDataList;
        this.mYNum = yNum;
        this.mLineList = lineList;
        this.mBkgList = bkgItemList;
        this.minData = mYDataList.get(mYDataList.size() - 1);
        this.maxData = mYDataList.get(0);
        initPaint();
        invalidate();
        return this;
    }

    public Builder Builder(Context context){
        return new Builder(context);
    }

    //利用建造者模式构造一个数据表
    public class Builder{
        private Context mContext;
        //Y轴数据
        private List<Integer> mYDataList;
        //Y轴单位
        private String mYUnit;
        //X轴数据
        private List<Integer> mXDataList;
        //X轴单位
        private String mXUnit;
        private double mXNum,mYNum;
        //每条数据源集合
        private List<CurveModel> mLineList = new ArrayList<>();
        //每个背景集合
        private List<BkgModel> mBkgItemList = new ArrayList<>();

        private Builder(Context context){
            this.mContext = context;
        }
        public Builder setX(String xUnit,List<Integer> xDataList,double xNum){
            this.mXUnit = xUnit;
            this.mXDataList = xDataList;
            this.mXNum = xNum;
            return this;
        }
        public Builder setY(String yUnit,List<Integer> yDataList,double yNum){
            this.mYUnit = yUnit;
            this.mYDataList = yDataList;
            this.mYNum = yNum;
            return this;
        }
        public Builder addLine(CurveModel item){
            this.mLineList.add(item);
            return this;
        }
        public Builder addBKG(BkgModel item){
            mBkgItemList.add(item);
            return this;
        }
        public CurveTrendChartView build(){
            return init(mContext,mXUnit,mXDataList,mXNum,mYUnit,mYDataList,mYNum,mLineList,mBkgItemList);
        }
    }

    public void destory(){
        mBkgList = null;
        mLineList = null;
        mXDataList = null;
        mYDataList = null;
        mContext = null;
    }
}
