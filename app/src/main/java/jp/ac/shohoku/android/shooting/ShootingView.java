package jp.ac.shohoku.android.shooting;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author s17b702
 */
public class ShootingView extends SurfaceView implements Runnable, Callback {

    Player mPlayer;

    public static final int OPENING = 0;  //オープニング画面
    public static final int GAMEPLAY = 1; //ゲーム画面
    public static final int RESULT = 2;   //リザルト画面

    public static final int SCREEN_EDGE = 0;  //画面
    public static int NEXUS7_WIDTH = 0;
    public static int NEXUS7_HEIGHT = 0;
    private SurfaceHolder mHolder;
    private int mGameState; //ゲームの状態を表す変数
    private int mScore = 0; //スコア

    /**
     * コンストラクタ<br />
     * 引数は ContextとAttributeSet
     * @param context
     * @param attrs
     */
    public ShootingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * 初期化用のメソッド<br />
     * 各種変数の初期化やコールバックの割り当てなどを行う
     */
    private void init() {
        mHolder = getHolder(); // SurfaceHolder を取得する．
        mHolder.addCallback(this);
        setFocusable(true); // フォーカスをあてることを可能にするメソッド
        requestFocus(); // フォーカスを要求して実行を可能にする
        mGameState = OPENING; //最初は OPENING 表示画面
    }

    /**
     * 定期的に実行するスレッドを生成し，定期的に実行の設定を行う<br />
     * このメソッドはサーフェスが生成されたタイミングで実行される．
     */
    private void start() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        //scheduledAtFixedRate の第 1 引数：実行可能なクラス．第 4 引数：ミリ秒に設定している
        //第 2 引数は実行を開始する時刻，第 3 引数は実行する間隔：
        executor.scheduleAtFixedRate(this, 30, 30, TimeUnit.MILLISECONDS);
    }

    /*
     * @see
     * android.view.SurfaceHolder.Callback#surfaceChanged(android.view.SurfaceHolder, int, int, int)
     */
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    /*
     * サーフェスが生成されたとき，とりあえず画面に表示し，その後定期実行するスレッドをスタート
     *
     * @see
     * android.view.SurfaceHolder.Callback#surfaceCreated(android.view.SurfaceHolder)
     */
    public void surfaceCreated(SurfaceHolder holder) {
        draw();
        start();
    }

    /*
     * @see android.view.SurfaceHolder.Callback#surfaceDestroyed(android.view.SurfaceHolder)
     */
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    /**
     * イベント処理するためのメソッド
     * @return
     */
    public boolean onTouchEvent(MotionEvent event) {
        NEXUS7_WIDTH = MainActivity.getViewWidth();
        NEXUS7_HEIGHT = MainActivity.getViewHeight();
        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (action) { //イベントの種類によって処理を振り分ける
            case MotionEvent.ACTION_DOWN: //画面上で押下されたとき
                switch (mGameState) { //ゲームの状態によって処理を振り分ける
                    case OPENING:
                        if(NEXUS7_WIDTH/2-150 < x && x < NEXUS7_WIDTH/2+150
                                && NEXUS7_HEIGHT/2-90 < y && y < NEXUS7_HEIGHT/2-40){ //ボタンの内部
                            mGameState = GAMEPLAY;
                        }
                        break;
                    case GAMEPLAY:
                        mGameState = RESULT;
                        break;
                    case RESULT:
                        mGameState = OPENING;
                        break;
                }
                break;
        }
        return true;
    }

    /**
     * 描画用のメソッド<br />
     * 画面への描画処理はすべてこの中に書く
     */
    private void draw() {
        Canvas canvas = mHolder.lockCanvas(); // サーフェースをロック
        canvas.drawColor(Color.WHITE); // キャンバスを白に塗る
        //String msg = null;
        Paint paint = new Paint();
        paint.setTextSize(60);
        NEXUS7_WIDTH = MainActivity.getViewWidth();
        NEXUS7_HEIGHT = MainActivity.getViewHeight();

        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawRect(0, 0, SCREEN_EDGE, NEXUS7_HEIGHT, paint);
        canvas.drawRect(NEXUS7_WIDTH - SCREEN_EDGE, 0, NEXUS7_WIDTH, NEXUS7_HEIGHT, paint);

        switch (mGameState) { //ゲームの状態によって処理を振り分ける
            case OPENING:
                //オープニング画面の表示
                writeStartButton(canvas, paint); //スタートボタンの描画
                break;
            case GAMEPLAY:
                mPlayer = new Player(this);
                mPlayer.draw(canvas);
                canvas.drawText("SCORE:"+ getScore(), SCREEN_EDGE + 10, 50, paint);
                break;
            case RESULT:
                canvas.drawText("SCORE:"+ getScore(), SCREEN_EDGE + 10, 50, paint);
                break;
        }
        mHolder.unlockCanvasAndPost(canvas); // サーフェースのロックを外す
    }

    /**
     * スタートボタンの表示
     * @param canvas
     * @param paint
     */
    private void writeStartButton(Canvas canvas, Paint paint) {
        int left = NEXUS7_WIDTH/2 - 150;
        int top = NEXUS7_HEIGHT/2 - 90;
        int right = left + 250;
        int bottom = top + 65;
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(left, top, right, bottom, paint);
        canvas.drawText("START", left + 30, bottom - 10, paint);
    }

    /*
     * 実行可能メソッド．このクラスの中では定期実行される
     *
     * @see java.lang.Runnable#run()
     */
    public void run() {

        switch (mGameState) {
            case OPENING:
                mScore = 0;
                break;
            case GAMEPLAY:
                ScorePlus(1);
                break;
            case RESULT:
                break;
        }
        // end of switch
        draw();
    } //end of if

    public int getScore() {
        return mScore;
    }

    public void ScorePlus(int point) {
        this.mScore += point;
    }
}