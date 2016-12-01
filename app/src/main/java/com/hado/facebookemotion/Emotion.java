package com.hado.facebookemotion;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Hado on 27-Nov-16.
 */

public class Emotion {
    private Context context;

    public static final int MINIMAL_SIZE = Util.dpToPx(28);

    public static final int NORMAL_SIZE = Util.dpToPx(40);

    public static final int CHOOSE_SIZE = Util.dpToPx(100);

    public static final int DISTANCE = Util.dpToPx(15);

    public static final int MAX_WIDTH_TITLE = Util.dpToPx(70);

    public int currentSize = NORMAL_SIZE;

    public int beginSize;

    public int endSize;

    public float currentX;

    public float currentY;

    public float beginY;

    public float endY;

    public Bitmap imageOrigin;

    public Bitmap imageTitle;

    public Paint emotionPaint;

    public Paint titlePaint;

    private float ratioWH;


    public Emotion(Context context, String title, int imageResource) {
        this.context = context;

        imageOrigin = BitmapFactory.decodeResource(context.getResources(), imageResource);

        emotionPaint = new Paint(Paint.FILTER_BITMAP_FLAG);
        emotionPaint.setAntiAlias(true);

        titlePaint = new Paint(Paint.FILTER_BITMAP_FLAG);
        titlePaint.setAntiAlias(true);

        generateTitleView(title);
    }

    private void generateTitleView(String title) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View titleView = inflater.inflate(R.layout.title_view, null);
        ((TextView) titleView).setText(title);

        int w = (int) context.getResources().getDimension(R.dimen.width_title);
        int h = (int) context.getResources().getDimension(R.dimen.height_title);
        ratioWH = (w * 1.0f) / (h * 1.0f);
        imageTitle = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(imageTitle);
        titleView.layout(0, 0, w, h);
        ((TextView) titleView).getPaint().setAntiAlias(true);
        titleView.draw(c);
    }

    public void setAlphaTitle(int alpha) {
        titlePaint.setAlpha(alpha);
    }

    public void drawEmotion(Canvas canvas) {
        canvas.drawBitmap(imageOrigin, null, new RectF(currentX, currentY, currentX + currentSize, currentY + currentSize), emotionPaint);
        drawTitle(canvas);
    }

    public void drawTitle(Canvas canvas) {
        int width = (currentSize - NORMAL_SIZE) * 7 / 6;
        int height = (int) (width / ratioWH);

        setAlphaTitle(Math.min(CommonDimen.MAX_ALPHA * width / MAX_WIDTH_TITLE, CommonDimen.MAX_ALPHA));

        if (width <= 0 || height <= 0) return;

        float x = currentX + (currentSize - width) / 2;
        float y = currentY - DISTANCE - height;

        canvas.drawBitmap(imageTitle, null, new RectF(x, y, x + width, y + height), titlePaint);
    }
}
