package jp.ac.shohoku.android.shooting;

import android.graphics.Canvas;

import sun.awt.geom.AreaOp;

public abstract class BaseObject {

    float xPosition;
    float yPosition;

    public abstract void draw(Canvas canvas);

    public boolean isAvailable(int width, int height) {
        if(yPosition < 0 || xPosition < 0 || yPosition > height || xPosition > width){
            return false;
        }
        return  true;
    }

    public abstract void  move();
}
