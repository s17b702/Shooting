package jp.ac.shohoku.android.shooting;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import sun.awt.geom.AreaOp;

public class Missile extends BaseObject{

    private static final float MOVE_WEIGHT = 3.0f;
    private static final float SIZE = 10f;
    private final  Paint paint = new Paint();

    public final float alignX;

    Missile(int fromX, float alignX) {
        yPosition = 0;
        xPosition = fromX;
        this.alignX = alignX;

        paint.setColor(Color.BLUE);
    }

    @Override
    public void move() {
        yPosition += 1 * MOVE_WEIGHT;
        xPosition += alignX * MOVE_WEIGHT;
    }

    @Override
    public void draw(Canvas canvas) {
        if (state != STATE_NORMAL) {
            return;
        }
        canvas.drawCircle(xPosition, yPosition, SIZE, paint);
    }

    @Override
    public boolean isHit(BaseObject object) {
        if (object.getType() == Type.Missile) {
            return false;
        }

        return  (calcDistance(this, object) < SIZE);
    }

    @Override
    public Type getType() {
        return Type.Missile;
    }
}
