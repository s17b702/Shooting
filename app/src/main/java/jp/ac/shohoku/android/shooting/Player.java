package jp.ac.shohoku.android.shooting;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.widget.ImageView;

public class Player {
    private Bitmap mBmp;
    private Rect mPlayerLocation;
    private int mW, mH;
    private int mX, mY; //位置
    private float mVx, mVy; //速度
    private final int V=10;
    private double mRotation = 0;

    /**
     * カードのコンストラクタ
     * @param sview リソースを読み込むため、ShootingViewを読み込む
     */
    public Player(ShootingView sview) {
        Resources rs = sview.getResources(); //リソースを取得
        Context context = sview.getContext(); //パッケージ名を取得するためにContextを取得
        int resId = rs.getIdentifier("player1", "mipmap", context.getPackageName());
        mBmp  = BitmapFactory.decodeResource(rs, resId); //画像を取得
        mW = mBmp.getWidth();
        mH = mBmp.getHeight();
        int wCenter = ShootingView.NEXUS7_WIDTH / 2;
        int hCenter = ShootingView.NEXUS7_HEIGHT / 2;
        setPlayerLocation(wCenter - mW/2,hCenter - mH/2, mW, mH); //画面中央に配置
        mX = mPlayerLocation.left;
        mY = mPlayerLocation.top;
        mRotation = 0;
        double radian = mRotation*Math.PI/180;
        mVx = (float)Math.cos(radian-Math.PI/2)*V; //画面下から上がデフォルトの進行方向なので-PI/2
        mVy = (float)Math.sin(radian-Math.PI/2)*V;
    }

    /**
     * プレイヤーの位置を設定する
     * @param left 左上の x 座標
     * @param top 左上の y 座標
     * @param right 右下の x 座標
     * @param bottom 右下の y 座標
     */
    public void setPlayerLocation(int left, int top, int right, int bottom) {
        mPlayerLocation = new Rect(left, top, right, bottom);
    }

    /**
     * プレイヤーの画像を描画する
     * @param canvas
     */
    public void draw(Canvas canvas) {
        if (mBmp != null) {
            canvas.drawBitmap(mBmp, mX, mY, new Paint()); //プレイヤーの描画
        }
    }

    /**
     * タップした位置に向かってプレイヤーが移動する
     * @param x タップされたx座標
     * @param y タップされたy座標
     */
    public void move(int x, int y) {
        mRotation = 0;
        int centerX = mPlayerLocation.left + mPlayerLocation.right / 2; //プレイヤーの位置
        int centerY = mPlayerLocation.top + mPlayerLocation.bottom / 2;
        double radian = Math.atan2(centerY - y, centerX - x); //タップした位置とプレイヤーの位置との角度
        mVx = (float)Math.cos(radian)*V;
        mVy = (float)Math.sin(radian)*V;
        mX -= mVx;
        mY -= mVy;
        setPlayerLocation(mX,mY, mW, mH);
    }
}
