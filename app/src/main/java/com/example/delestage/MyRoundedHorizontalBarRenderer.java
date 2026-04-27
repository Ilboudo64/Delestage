package com.example.delestage;

import android.graphics.Canvas;
import android.graphics.RectF;
import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.renderer.HorizontalBarChartRenderer;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.github.mikephil.charting.buffer.BarBuffer;

public class MyRoundedHorizontalBarRenderer extends HorizontalBarChartRenderer {
    private float mRadius;

    public MyRoundedHorizontalBarRenderer(BarDataProvider chart, ChartAnimator animator, ViewPortHandler viewPortHandler, float radius) {
        super(chart, animator, viewPortHandler);
        this.mRadius = radius;
    }

    @Override
    protected void drawDataSet(Canvas c, IBarDataSet dataSet, int index) {
        // --- BARRIÈRE DE SÉCURITÉ ANTI-CRASH ---
        if (mChart == null || mBarBuffers == null || index >= mBarBuffers.length) {
            return; // On arrête tout au lieu de crasher
        }

        BarBuffer buffer = mBarBuffers[index];
        if (buffer == null) return;

        buffer.setPhases(mAnimator.getPhaseX(), mAnimator.getPhaseY());
        buffer.setDataSet(index);
        buffer.setInverted(mChart.isInverted(dataSet.getAxisDependency()));
        buffer.setBarWidth(mChart.getBarData().getBarWidth());

        buffer.feed(dataSet);

        mChart.getTransformer(dataSet.getAxisDependency()).pointValuesToPixel(buffer.buffer);

        for (int j = 0; j < buffer.size(); j += 4) {
            if (!mViewPortHandler.isInBoundsTop(buffer.buffer[j + 3])) continue;

            // Dessin sécurisé de l'arrondi
            if (mRenderPaint != null) {
                RectF rect = new RectF(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2], buffer.buffer[j + 3]);
                c.drawRoundRect(rect, mRadius, mRadius, mRenderPaint);
            }
        }
    }
}