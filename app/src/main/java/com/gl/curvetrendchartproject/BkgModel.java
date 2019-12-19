package com.gl.curvetrendchartproject;

import java.util.List;

/**
 * @Author: gl
 * @CreateDate: 2019/12/18
 * @Description: 每个说明背景的封装类
 */
public class BkgModel {
    //背景的最小值和最大值
    private Value value;
    //背景颜色
    private int color;
    //背景说明文字
    private String desc;

    public BkgModel(Value value, int color, String desc) {
        this.value = value;
        this.color = color;
        this.desc = desc;
    }

    public Value getValue() {
        return value;
    }

    public int getColor() {
        return color;
    }

    public String getDesc() {
        return desc;
    }
}
