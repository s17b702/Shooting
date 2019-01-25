package jp.ac.shohoku.android.shooting;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.Random;

public class Enemy {
    private Bitmap mBmp;
    private Rect mEnemyLocation;
    private int mW, mH;
    private int mX, mY; //位置
    private float mVx, mVy; //速度
    private int V;
    private double mRotation = 0;
    private double mRadian;

    /**
     * エネミーのコンストラクタ
     * @param sview リソースを読み込むため、ShootingViewを読み込む
     * @param x スポーンする位置のx座標
     * @param y スポーンする位置のy座標
     */
    public Enemy(ShootingView sview, int x, int y , int speed) {
        Resources rs = sview.getResources(); //リソースを取得
        Context context = sview.getContext(); //パッケージ名を取得するためにContextを取得
        int resId = rs.getIdentifier("enemy", "mipmap", context.getPackageName());
        mBmp  = BitmapFactory.decodeResource(rs, resId); //画像を取得
        mW = mBmp.getWidth();
        mH = mBmp.getHeight();
        setEnemyLocation(x,y, mW, mH);
        V = speed;
        mX = mEnemyLocation.left;
        mY = mEnemyLocation.top;
        mRotation = 0;
        mRadian = mRotation*Math.PI/180;
        mVx = (float)Math.cos(mRadian-Math.PI/2)*V; //画面下から上がデフォルトの進行方向なので-PI/2
        mVy = (float)Math.sin(mRadian-Math.PI/2)*V;
    }

    /**
     * エネミーの位置を設定する
     * @param left 左上の x 座標
     * @param top 左上の y 座標
     * @param right 右下の x 座標
     * @param bottom 右下の y 座標
     */
    public void setEnemyLocation(int left, int top, int right, int bottom) {
        mEnemyLocation = new Rect(left, top, right, bottom);
    }

    public Rect getEnemyLocation() {
        return mEnemyLocation;
    }

    /**
     * エネミーの画像を描画する
     * @param canvas
     */
    public void draw(Canvas canvas) {
        if (mBmp != null) {
            canvas.drawBitmap(mBmp, mX, mY, new Paint()); //描画
        }
    }

    /**
     * プレイヤーの位置に向かってエネミーが移動する
     * @param x プレイヤーの位置
     * @param y プレイヤーの位置
     */
    public void move(int x, int y) {
        mRotation = 0;
        int centerX = mEnemyLocation.left + mEnemyLocation.right / 2; //エネミーの位置
        int centerY = mEnemyLocation.top + mEnemyLocation.bottom / 2;
        Random rand = new Random();
        int num = rand.nextInt(1);
        mRadian = Math.atan2(centerY - y, centerX - x) + num; //プレイヤーの位置とエネミーの位置との角度
        mVx = (float)Math.cos(mRadian)*V;
        mVy = (float)Math.sin(mRadian)*V;
        mX -= mVx;
        mY -= mVy;
        setEnemyLocation(mX,mY, mW, mH);
    }

    public double getRadian() {
        return mRadian;
    }
}
