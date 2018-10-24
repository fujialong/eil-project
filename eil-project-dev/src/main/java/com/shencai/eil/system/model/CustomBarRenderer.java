package com.shencai.eil.system.model;

import org.jfree.chart.renderer.category.BarRenderer;

import java.awt.*;

/**
 * Created by fanhj on 2018/10/23.
 */
public class CustomBarRenderer extends BarRenderer {
    private Paint[] colors;
    //初始化柱子颜色
    private String[] colorValues = { "#5B9BD5", "#ED7D31"};

    public CustomBarRenderer() {
        colors = new Paint[colorValues.length];
        for (int i = 0; i < colorValues.length; i++) {
            colors[i] = Color.decode(colorValues[i]);
        }
    }

    public Paint getItemPaint(int i, int j) {
        return colors[i % colors.length];
    }
}
