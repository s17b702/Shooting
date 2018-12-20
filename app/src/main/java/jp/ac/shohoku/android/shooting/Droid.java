package jp.ac.shohoku.android.shooting;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import sun.security.tools.keytool.CertAndKeyGen;

public class Droid extends BaseObject {

    private final Paint paint = new Paint();

    public final Bitmap bitmap;
    public final Rect rect;

    public Droid(Bitmap bitmap, int width, int height) {
        this.bitmap = bitmap;

        int left = (width - bitmap.getWidth()) / 2;
        int top = height -bitmap.getHeight();
        int right = left + bitmap.getWidth();
        int bottom = top + bitmap.getHeight();
        rect = new Rect(left, top, right, bottom);

        xPosition = rect.centerX();
        yPosition = rect.centerY();
    }

    @Override
    public  void draw(Canvas canvas) {
        canvas.drawBitmap(bitmap, rect.left, rect.top, paint);
    }

    @Override
    public void move(){
    }
}
