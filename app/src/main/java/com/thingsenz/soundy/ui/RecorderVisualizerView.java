package com.thingsenz.soundy.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.thingsenz.soundy.R;

import java.util.ArrayList;
import java.util.List;

public class RecorderVisualizerView extends View {

    private static final int LINE_WIDTH=5;
    private static final int LINE_SCALE=10;
    private List<Float> amplitudes;
    private int width,height;
    private Paint linePaint;

    public RecorderVisualizerView(Context context, AttributeSet attrs) {
        super(context,attrs);
        linePaint=new Paint();
        linePaint.setColor(ContextCompat.getColor(context, R.color.primary));
        linePaint.setStrokeWidth(LINE_WIDTH);


    }

    @Override
    protected void onSizeChanged(int w,int h,int oldw,int oldh) {
        width=w;height=h;amplitudes=new ArrayList<Float>(width/LINE_WIDTH);
    }

    public void clear() {
        amplitudes.clear();
    }


    public void addAmpplitude(float amp) {
        amplitudes.add(amp);
        if (amplitudes.size()*LINE_WIDTH>=width) {
            amplitudes.remove(0);
        }
    }


    @Override
    public void onDraw(Canvas canvas) {

        int middle=height/2;
        float curX=0;

        for (float power: amplitudes) {
            float scaledHeight=power/LINE_SCALE;
            curX+=LINE_WIDTH;
            canvas.drawLine(curX,middle+scaledHeight/2,curX,middle-scaledHeight/2,linePaint);
        }

    }

}
