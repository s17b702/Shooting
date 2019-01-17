package jp.ac.shohoku.android.shooting;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.MotionEvent;

import android.os.Handler;
import android.os.Vibrator;

import java.awt.PageAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import static javax.swing.UIManager.get;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    
    public interface EventCallback {
        void onGameOver(long score);
    }
    
    private EventCallback eventCallback;
    public void setEventCallback(EventCallback eventCallback) {
        this.eventCallback = eventCallback;
    }
    
    private  Handler handler = new Handler();

    private static final  long DRAW_INTERVAL = 1000 /60;

    private static final int MISSILE_LAUNCH_WEIGHT = 50;
    
    private static  final float SCORE_TEXT_SIZE = 60.0f; 
    
    private static final long VIBRATION_LENGTH_HIT_MISSILE = 100;
    private static final long VIBRATION_LENGTH_HIT_DROID = 1000;
    private static final int SCORE_LEVEL = 100;

    private Droid droid;
    private final List<BaseObject> missileList = new ArrayList<>();
    private  final List<BaseObject> bulletList = new ArrayList<>();

    private  final Random rand = new Random(System.currentTimeMillis());
    
    
    private long score;
    private final Paint PaintScore = new Paint();

    private  DrawThread drawThread;

    private class DrawThread extends Thread {
        private final AtomicBoolean isFinished = new AtomicBoolean();

        public void Finish() {
        }
    }

    public void finish() {
        isFinished.set(true);
    }

    @Override
    public void run() {
        SurfaceHolder holder = getHolder();
        while (!isFinished.get()) {
            if (holder.isCreating()) {
                continue;
            }

            Canvas canvas = holder.lockCanvas();
            if (canvas == null) {
                continue;
            }

            drawGame(canvas);
            holder.unlockCanvasAndPost(canvas);

            synchronized (this) {
                try {
                    wait(DRAW_INTERVAL);
                } catch (InterruptedException e) {

                }
            }
        }
    }

    public void startDrawThread() {
        stopDrawThread();

        drawThread = new DrawThread();
        drawThread.start();

    }

    public boolean stopDrawThread() {
        if (drawThread == null) {
            return false;
        }

        drawThread.Finish();
        drawThread = null;
        return true;
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        startDrawThread();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        stopDrawThread();
    }
    
    private final Vibrator vibrator;

    public GameView(Context context) {
        super(context);
        
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE); 
        
        PaintScore.setColor(Color.BLACK);
        PaintScore.setTextSize(SCORE_TEXT_SIZE);
        PaintScore.setAntiAlias(true);
        
        getHandler().addCallback(this);
    }

    private void drawGame(Canvas canvas) {
        canvas.drawColor(Color.WHITE);
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        if(droid == null) {
            Bitmap droidBitmap = BitmapFactory.decodeResource(getResources(),
                       R.drawable.doroid);
            droid = new Droid(droidBitmap, width, height);
        }

        if(rand.nextInt(MISSILE_LAUNCH_WEIGHT) == 0) {
            long count = score / SCORE_LEVEL + 1;
            for (int i = 0; i < count; i++) {
                Missile missile = launchMissile(width, height);
                missileList.add(missile);
            }
        }

        drawObjectList(canvas, missileList, width, height);

        drawObjectList(canvas, bulletList, width, height);
        
        for (int i = 0; i < missileList.size(); i++) {
            BaseObject missile = missileList.get(i);
            
            if (droid.isHit(missile)) {
                missile.hit();
                droid.hit();
                
                vibrator.vibrate(VIBRATION_LENGTH_HIT_DROID);
                
                handler.post(new Runnable () {
                    @Override
                    public void  run() {
                        eventCallback.onGameOver(score);
                    }
                });
                
                break;
            }
            
            for (int j = 0; j< bulletList.size(); j++){
                BaseObject bulletList.get(j);
                
                if (bulletList.isHit(missile)) {
                    missile.hit();
                    bulletList.hit();
                    
                    vibrator.vibrate(VIBRATION_LENGTH_HIT_MISSILE);
                    
                    score += 10;
                }
            }
        }

        droid.draw(canvas);
        
        canvas.drawText("Score:" + score, 0, SCORE_TEXT_SIZE,PaintScore);
    }

    private static void drawObjectList(
              Canvas canvas, List<BaseObject> objectList, int widht, int height){
        for(int i = 0; i < objectList.size(); i++) {
            BaseObject object = objectList.get(i);
            if(object.isAvailable(widht, height)) {
                object.move();
                object.draw(canvas);
            } else {
                objectList.remove(object);
                i--;
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                fire(event.getX(), event.getY());
                break;
        }
        return super.onTouchEvent(event);
    }

    private void  fire(float x, float y) {
        float alignX = (x - droid.rect.centerX()) / Math.abs(y - droid.rect.centerY());

        Bullet bullet = new Bullet(droid.rect, alignX);
        bulletList.add(0, bullet);
    }

    private Missile launchMissile(int width, int height) {
        int fromX = rand.nextInt(width);
        int toX = rand.nextInt(width);

        float alignX = (toX -fromX) / (float) height;
        return new Missile(fromX, alignX);
    }

}

