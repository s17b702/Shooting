package jp.ac.shohoku.android.shooting;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author s17b702
 */
public class ShootingView extends SurfaceView implements Runnable, Callback {

    Player mPlayer;
    Enemy mEnemy;
    Bullet mBullet;
    Bitmap mBackGround;
    private final List<Enemy> EnemyList = new ArrayList<>();
    private final List<Bullet> BulletList = new ArrayList<>();

    public static final int OPENING = 0;  //オープニング画面
    public static final int GAMEPLAY = 1; //ゲーム画面
    public static final int RESULT = 2;   //リザルト画面
    public static final int TYPE_PLAYER = 0;
    public static final int TYPE_ENEMY = 1;

    private MediaPlayer mBGM1,mBGM2,mBGM3;
    private SoundPool mSoundPool;
    public int Tap_SE,P_Bullet,P_Destroy,E_Bullet,E_Destroy; //効果音用の ID

    public static final int SCREEN_EDGE = 0;  //画面
    public static int NEXUS7_WIDTH = 0;
    public static int NEXUS7_HEIGHT = 0;
    private SurfaceHolder mHolder;
    private int mGameState; //ゲームの状態を表す変数
    private int mScore = 0; //スコア
    private int mHighScore = 0; //ハイスコア
    private final Random rand = new Random(System.currentTimeMillis());

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
        SoundInit();
        StateChange(OPENING); //最初は OPENING 表示画面
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
        StateBGMPlay();
    }

    /*
     * @see android.view.SurfaceHolder.Callback#surfaceDestroyed(android.view.SurfaceHolder)
     */
    public void surfaceDestroyed(SurfaceHolder holder) {
        BGMStop();
        //BGMRelease();
        //mSoundPool.release();
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
                        SEPlay(Tap_SE);
                        mPlayer = new Player(this);
                        for(int i = 0; i < EnemyList.size(); i++) { //エネミーのリセット
                            Enemy object = EnemyList.get(i);
                            EnemyList.remove(object);
                            i--;
                        }
                        for(int i = 0; i < BulletList.size(); i++) { //弾のリセット
                            Bullet object = BulletList.get(i);
                            BulletList.remove(object);
                            i--;
                        }
                        StateChange(GAMEPLAY);
                        break;
                    case GAMEPLAY:
                        mPlayer.move(x,y);
                        break;
                    case RESULT:
                        SEPlay(Tap_SE);
                        StateChange(OPENING);
                        break;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                switch (mGameState) { //ゲームの状態によって処理を振り分ける
                    case OPENING:
                        break;
                    case GAMEPLAY:
                        mPlayer.move(x,y);
                        break;
                    case RESULT:
                        break;
                }
                break;
            case MotionEvent.ACTION_UP:
                switch (mGameState) { //ゲームの状態によって処理を振り分ける
                    case OPENING:
                        break;
                    case GAMEPLAY:
                        SEPlay(P_Bullet);
                        mBullet = new Bullet(mPlayer.getPlayerLocation(),mPlayer.getRadian(),40,TYPE_PLAYER);
                        BulletList.add(mBullet);
                        break;
                    case RESULT:
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
        Paint paint = new Paint();
        NEXUS7_WIDTH = MainActivity.getViewWidth();
        NEXUS7_HEIGHT = MainActivity.getViewHeight();
        paint.setTextSize(60);
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawRect(0, 0, SCREEN_EDGE, NEXUS7_HEIGHT, paint);
        canvas.drawRect(NEXUS7_WIDTH - SCREEN_EDGE, 0, NEXUS7_WIDTH, NEXUS7_HEIGHT, paint);
        Resources rs = this.getContext().getResources(); //リソースを取得
        Bitmap bmp;
        switch (mGameState) { //ゲームの状態によって処理を振り分ける
            case OPENING:
                bmp = BitmapFactory.decodeResource(rs, R.mipmap.title);
                mBackGround = Bitmap.createScaledBitmap(bmp, NEXUS7_WIDTH + 1, NEXUS7_HEIGHT + 1, false);
                canvas.drawBitmap(mBackGround,0, 0, paint);
                int x = NEXUS7_WIDTH/2 - 210;
                int y = NEXUS7_HEIGHT/2 + 190;
                canvas.drawText("TOUCH  SCREEN", x, y, paint);
                canvas.drawText("HIGH SCORE:"+ mHighScore, x, y + 120, paint);
                break;
            case GAMEPLAY:
                bmp = BitmapFactory.decodeResource(rs, R.mipmap.background);
                mBackGround = Bitmap.createScaledBitmap(bmp, NEXUS7_WIDTH + 1, NEXUS7_HEIGHT + 1, false);
                canvas.drawBitmap(mBackGround,0, 0, paint);
                mPlayer.draw(canvas);
                Rect PLoc = mPlayer.getPlayerLocation(); //プレイヤーの位置計算
                int centerX = PLoc.left + PLoc.right / 2;
                int centerY = PLoc.top + PLoc.bottom / 2;
                drawEnemy(canvas,EnemyList,centerX,centerY);
                drawBullet(canvas,BulletList,NEXUS7_WIDTH,NEXUS7_HEIGHT);
                canvas.drawText("SCORE:"+ mScore, SCREEN_EDGE + 10, 50, paint);
                break;
            case RESULT:
                bmp = BitmapFactory.decodeResource(rs, R.mipmap.gameover);
                mBackGround = Bitmap.createScaledBitmap(bmp, NEXUS7_WIDTH + 1, NEXUS7_HEIGHT + 1, false);
                canvas.drawBitmap(mBackGround,0, 0, paint);
                canvas.drawText("SCORE:"+ mScore, SCREEN_EDGE + 10, 50, paint);
                break;
        }
        mHolder.unlockCanvasAndPost(canvas); // サーフェースのロックを外す
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
                if(ScoreRand(mScore/100 + 9500) == 0 && EnemyList.size() <= 10) { //エネミーのスポーン
                    long count = mScore / 10000 + 1;
                    if(count + EnemyList.size() > 10){ count = count - (10 - EnemyList.size()); } //エネミーは10体まで
                    if(count >= 2 && rand.nextInt(2) == 0) { count = 1;}
                    for (int i = 0; i < count; i++) {
                        int Speed = 3;
                        if(ScoreRand(mScore * 100) == 0){
                            Speed = 8;
                        } else if(ScoreRand(mScore * 10) <= 2){
                            Speed = 7;
                        } else if(ScoreRand(mScore * 10) <= 3){
                            Speed = 6;
                        } else if(ScoreRand(mScore * 10) <= 4){
                            Speed = 5;
                        } else if(ScoreRand(mScore * 10) <= 5){
                            Speed = 4;
                        }
                        mEnemy = new Enemy(this,rand.nextInt(NEXUS7_WIDTH),rand.nextInt(NEXUS7_HEIGHT),Speed);
                        EnemyList.add(mEnemy);
                    }
                }
                Rect PRect = mPlayer.getPlayerLocation();
                for(int i = 0; i < EnemyList.size(); i++) { //エネミーの処理
                    Enemy Enemy = EnemyList.get(i);
                    Rect ERect = Enemy.getEnemyLocation();
                    if(calcDistance(PRect,ERect) < 2f){ //プレイヤーがエネミーに当たった時
                        SEPlay(P_Destroy);
                        StateChange(RESULT);
                    }
                    if(ScoreRand(mScore * 10) <= 20) { //エネミーの弾発射
                        if(ScoreRand(mScore * 2) <= mScore/50000) {
                            if(mScore >= 10000 && rand.nextInt(mScore / 10000) == 0) {
                                continue;
                            }
                            SEPlay(E_Bullet);
                            mBullet = new Bullet(Enemy.getEnemyLocation(), Enemy.getRadian(), 15, TYPE_ENEMY);
                            BulletList.add(mBullet);
                        }
                    }
                }
                for(int i = 0; i < BulletList.size(); i++) { //弾の処理
                    Bullet Bullet = BulletList.get(i);
                    int posX = Bullet.getPositionX();
                    int posY = Bullet.getPositionY();
                    Rect BRect = new Rect(posX + 40, posY + 40, 30,30);
                    if(calcDistance(PRect,BRect) < 50f && Bullet.getType() == TYPE_ENEMY){ //プレイヤーが弾に当たった時
                        SEPlay(P_Destroy);
                        StateChange(RESULT);
                    }
                    for(int j = 0; j < EnemyList.size(); j++) {
                        Enemy Enemy = EnemyList.get(j);
                        Rect ERect = Enemy.getEnemyLocation();
                        if(calcDistance(ERect,BRect) < 50f && Bullet.getType() == TYPE_PLAYER){ //エネミーが弾に当たった時
                            SEPlay(E_Destroy);
                            EnemyList.remove(Enemy);
                            ScorePlus(200);
                            j--;
                            BulletList.remove(Bullet);
                            i--;
                            break;
                        }
                    }
                }
                break;
            case RESULT:
                break;
        }
        // end of switch
        draw();
    } //end of if

    /**
     * 2つのオブジェクトの距離を計算
     * @param obj1 オブジェクト1
     * @param obj2 オブジェクト2
     * @return オブジェクト同士の距離
     */
    public double calcDistance(Rect obj1, Rect obj2) {
        float distX = obj1.centerX() - obj2.centerX();
        float distY = obj1.centerY() - obj2.centerY();
        return Math.sqrt(Math.pow(distX, 2) + Math.pow(distY, 2));
    }

    private static void drawBullet(Canvas canvas, List<Bullet> objectList, int widht, int height){
        for(int i = 0; i < objectList.size(); i++) {
            Bullet object = objectList.get(i);
            if(object.isAvailable(widht, height)) {
                object.move();
                object.draw(canvas);
            } else {
                objectList.remove(object);
                i--;
            }
        }
    }

    private static void drawEnemy(Canvas canvas, List<Enemy> objectList, int x, int y){
        for(int i = 0; i < objectList.size(); i++) {
            Enemy object = objectList.get(i);
            object.move(x,y);
            object.draw(canvas);
        }
    }

    public void ScorePlus(int point) {
        this.mScore += point;
    }

    private void StateChange(int state){
        mGameState = state;
        if(state == RESULT && mScore > mHighScore){
            mHighScore = mScore;
        }
        StateBGMPlay();
    }

    private int ScoreRand(int score){
        int random = 1000 - score / 10;
        if (random <= 30){ random = 30; }
        return rand.nextInt(random);
    }

    private void SoundInit(){
        Context context = getContext();
        mBGM1 = mBGM1.create(context, R.raw.title);
        mBGM1.setLooping(true);
        mBGM1.setVolume(1.0f, 1.0f);
        mBGM2 = mBGM1.create(context, R.raw.gameplay);
        mBGM2.setLooping(true);
        mBGM2.setVolume(1.0f, 1.0f);
        mBGM3 = mBGM1.create(context, R.raw.gameover);
        mBGM3.setLooping(false);
        mBGM3.setVolume(1.0f, 1.0f);
        mSoundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
        Tap_SE = mSoundPool.load(context, R.raw.tap_se, 1);
        P_Bullet = mSoundPool.load(context, R.raw.player_bullet, 1);
        P_Destroy = mSoundPool.load(context, R.raw.player_destroy, 1);
        E_Bullet = mSoundPool.load(context, R.raw.enemy_bullet, 1);
        E_Destroy = mSoundPool.load(context, R.raw.enemy_destroy, 1);
    }

    /**
     * BGMを再生する
     */
    private void BGMPlay(MediaPlayer mediaPlayer) {
        BGMStop();
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(0);
            mediaPlayer.start();
        }
    }

    private void StateBGMPlay(){
        if(mGameState == OPENING){
            BGMPlay(mBGM1);
        } else if(mGameState == GAMEPLAY){
            BGMPlay(mBGM2);
        } else{
            BGMPlay(mBGM3);
        }
    }

    /**
     * BGMを停止する
     */
    private void BGMStop() {
        if(mBGM1.isPlaying()) {
            mBGM1.stop();
            mBGM1.prepareAsync();
        }
        if(mBGM2.isPlaying()) {
            mBGM2.stop();
            mBGM2.prepareAsync();
        }
        if(mBGM3.isPlaying()) {
            mBGM3.stop();
            mBGM3.prepareAsync();
        }
    }

    private void BGMRelease(){
        mBGM1.release();
        mBGM2.release();
        mBGM3.release();
    }

    /**
     * 効果音を再生する
     */
    private void SEPlay(int se) {
        mSoundPool.play(se, 1.0f, 1.0f, 1, 0, 1);
    }
}