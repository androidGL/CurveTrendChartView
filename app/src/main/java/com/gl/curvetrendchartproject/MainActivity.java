package com.gl.curvetrendchartproject;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private CurveTrendChartView curveTrendChartView;
    List<Integer> xList = new ArrayList<>();
    List<Integer> yList = new ArrayList<>();
    List<Value> lowList = new ArrayList<>();
    List<Value> highList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        curveTrendChartView = findViewById(R.id.view);
        initData();

    }

    private void initData() {
        //获取纵坐标
        int base = 200;
        do {
            xList.add(base * 100);
            base -= 10;
        } while (base >= 30);

        //获取横坐标
        int base1 = 5;
        do {
            yList.add(base1 * 10);
            base1 += 1;
        } while (base1 <= 20);

        //获取高压列表
        lowList.add(new Value(50, 7000));
        lowList.add(new Value(53, 7698));
        lowList.add(new Value(69, 6354));
        lowList.add(new Value(70, 8000));
        lowList.add(new Value(90, 6345));
        lowList.add(new Value(115, 7656));
        lowList.add(new Value(130, 5666));
        lowList.add(new Value(135, 3000));
        lowList.add(new Value(169, 8900));
        lowList.add(new Value(190, 8888));
        //获取高压列表
        highList.add(new Value(50, 9000));
        highList.add(new Value(53, 9654));
        highList.add(new Value(69, 10666));
        highList.add(new Value(70, 10698));
        highList.add(new Value(90, 9888));
        highList.add(new Value(115, 9000));
        highList.add(new Value(130, 9890));
        highList.add(new Value(135, 13999));
        highList.add(new Value(169, 19000));
        highList.add(new Value(190, 10000));

        curveTrendChartView.Builder(getApplicationContext())
                //设置Y轴的数据
                .setY("mmHg", xList, 100.00)
                //设置X轴的数据
                .setX("日期", yList, 10.0)
                //添加背景说明色
                .addBKG(new BkgModel(new Value(8000, 9000), Color.parseColor("#FFF5EE"), "低压正常范围"))
                //再添加背景说明色，如果你还想添加，那就继续add
                .addBKG(new BkgModel(new Value(12000, 14000), Color.parseColor("#E0FFFF"), "高压正常范围"))
                //添加折线
                .addLine(new CurveModel(lowList, Color.parseColor("#006400"), "低压"))
                //我还想添加
                .addLine(new CurveModel(highList, Color.RED,"高压"))
                .build();
    }

    @Override
    protected void onDestroy() {
        //习惯性回收内存
        curveTrendChartView.destory();
        curveTrendChartView = null;
        xList = null;
        yList = null;
        lowList = null;
        highList = null;
        super.onDestroy();
    }
}
