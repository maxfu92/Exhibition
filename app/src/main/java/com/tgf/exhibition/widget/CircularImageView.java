package com.tgf.exhibition.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;

/**
 * Created by jeff on 2016/5/22.
 */
public class CircularImageView extends MaskedImageView {
    public CircularImageView(Context paramContext) {
        super(paramContext);
    }

    public CircularImageView(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
    }

    public CircularImageView(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
    }

    public Bitmap createMask() {
        Bitmap localBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas localCanvas = new Canvas(localBitmap);
        Paint localPaint = new Paint(1);
        localPaint.setColor(Color.BLACK);
        RectF localRectF = new RectF(0.0F, 0.0F, getWidth(), getHeight());
        localCanvas.drawOval(localRectF, localPaint);
        return localBitmap;
    }
}
