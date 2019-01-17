package jp.ac.shohoku.android.shooting;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class Bullet extends BaseObject{

    private static final float MOVE_WEIGHT = 3.0f;

    private final Paint paint = new Paint();

    private static  final float SIZE = 15f;

    public final float alignX;

    Bullet(Rect rect, float alignXValue) {
        xPosition = rect.centerX();
        yPosition = rect.centerY();
        alignX = alignXValue;

        paint.setColor(Color.RED);
    }

    @Override
    public void move() {
        yPosition -= 1 *MOVE_WEIGHT;
        xPosition += alignX * MOVE_WEIGHT;
    }

    @Override
    public boolean isHit(BaseObject object) {
        return false;
    }

    @Override
    public void draw(Canvas canvas) {
        if (state != STATE_NORMAL) {
            return;
        }
        canvas.drawCircle(xPosition, yPosition, SIZE, paint);
    }



    @Override
    public Type getType() {
        return Type.Bullet;
    }
}
