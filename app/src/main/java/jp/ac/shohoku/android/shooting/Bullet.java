package jp.ac.shohoku.android.shooting;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class Bullet{
    private final Paint paint = new Paint();
    private static final float SIZE = 15f;
    public double mRadian;
    private int V;
    private int xPosition, yPosition;
    private float mVx, mVy; //速度
    int Type; // 0 = プレイヤー, 1 = エネミー
    int color;

    /**
     * 弾のコンストラクタ
     * @param rect 発射元のオブジェクト
     * @param radian 発射角度
     * @param type 0 = プレイヤー, 1 = エネミー
     */
    public Bullet(Rect rect, double radian, int speed, int type) {
        xPosition = rect.left + rect.right / 2;
        yPosition = rect.top + rect.bottom / 2;
        mRadian = radian;
        V = speed;
        Type = type;
        if(Type == 0){
            color = Color.YELLOW;
        } else{
            color = Color.RED;
        }
        paint.setColor(color);
    }

    public void draw(Canvas canvas) {
        canvas.drawCircle(xPosition, yPosition, SIZE, paint);
    }

    public void move() {
        mVx = (float)Math.cos(mRadian)*V;
        mVy = (float)Math.sin(mRadian)*V;
        xPosition -= mVx;
        yPosition -= mVy;
    }

    public boolean isAvailable(int width, int height) {
        if (yPosition < 0 || xPosition < 0 || yPosition > height || xPosition > width) {
            return false;
        }
        return true;
    }

    public int getPositionX() {
        return xPosition;
    }

    public int getPositionY() {
        return yPosition;
    }

    public int getType() {
        return Type;
    }

}
