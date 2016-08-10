package com.tgf.exhibition.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by jeff on 2016/5/22.
 */
public abstract class MaskedImageView extends ImageView {
    private static final Xfermode MASK_XFERMODE;
    private Bitmap mMask;
    private Paint mPaint;

    static {
        MASK_XFERMODE = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
    }

    public MaskedImageView(Context paramContext) {
        super(paramContext);
    }

    public MaskedImageView(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
    }

    public MaskedImageView(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
    }

    public abstract Bitmap createMask();

    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();
        if (drawable == null)
            return;
        try {
            if (mPaint == null) {
                mPaint = new Paint();
                mPaint.setFilterBitmap(false);
                mPaint.setXfermode(MASK_XFERMODE);
            }
            int i = canvas.saveLayer(0.0F, 0.0F, getWidth(), getHeight(), null, Canvas.CLIP_SAVE_FLAG);
            drawable.setBounds(0, 0, getWidth(), getHeight());
            drawable.draw(canvas);
            if ((mMask == null) || (mMask.isRecycled())) {
                mMask = createMask();
            }
            canvas.drawBitmap(mMask, 0.0F, 0.0F, mPaint);
            //canvas.restoreToCount(i);
            canvas.restore();
        } catch (Exception localException) {
        }
    }
}
