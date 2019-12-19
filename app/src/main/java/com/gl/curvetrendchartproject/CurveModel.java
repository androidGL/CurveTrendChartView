package com.gl.curvetrendchartproject;

import java.util.List;

/**
 * @Author: gl
 * @CreateDate: 2019/12/18
 * @Description: 每条折线的封装类
 */
public class CurveModel {
    //坐标点列表
    private List<Value> curveLineDataList;
    //折线颜色
    private int curveColor;
    //折线描述语句
    private String curveDesc;

    public CurveModel(List<Value> curveLineDataList, int curveColor, String curveDesc) {
        this.curveLineDataList = curveLineDataList;
        this.curveColor = curveColor;
        this.curveDesc = curveDesc;
    }

    public List<Value> getCurveLineDataList() {
        return curveLineDataList;
    }

    public int getCurveColor() {
        return curveColor;
    }

    public String getCurveDesc() {
        return curveDesc;
    }
}
