package jp.ac.shohoku.android.shooting;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class Player {
    private Bitmap mBmp = null;
    private Rect mPlayerLocation;
    private int mW, mH;

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
     * カードの画像を描画する
     * @param canvas
     */
    public void draw(Canvas canvas) {
        float left = mPlayerLocation.left;
        float top = mPlayerLocation.top;
        if (mBmp != null) {
            canvas.drawBitmap(mBmp, left, top, new Paint()); //プレイヤーの描画
        }
    }

    /**
     * 移動
     */
    public void move() {


    }
}
