package jp.ac.shohoku.android.shooting;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {
    public static int width = 0;
    public static int height = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        // TODO Auto-generated method stub
        super.onWindowFocusChanged(hasFocus);

        // View のサイズを取得
        ShootingView sview = (ShootingView) findViewById(R.id.ShootingView);
        width = sview.getWidth();
        height = sview.getHeight();
    }

    /**
     * ShootingView の幅を取得
     * @return
     */
    public static int getViewWidth(){
        return width;
    }

    /**
     * ShootingView の高さを取得
     * @return
     */
    public static int getViewHeight() {
        return height;
    }
}
