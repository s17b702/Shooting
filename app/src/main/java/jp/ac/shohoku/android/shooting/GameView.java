package jp.ac.shohoku.android.shooting;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.SurfaceHolder;
import android.view.SurfaceView;r

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    private static final  long DRAW_INTERVAL = 1000 /60;

    private  static final int MISSILE_LAUNCH_WEIGHT = 50;

    private Droid droid;
    private final List<BaseObject> missileList = new ArrayList<>();

    private  final Random rand = new Random(System.currentTimeMillis());

    private  DrawThread drawThread;

    private class DrawThread extends Thread {
        private final AtomicBoolean isFinished = new AtomicBoolean();
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
                    wait(DRAM_INTERVAL);
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
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        stopDrawThread();
    }

    public GameView(Context context) {
        super(context);
        getHandler().addCallback(this);
    }

    private void drawGame(Canvas canvas) {
        canvas.drawColor(Color.WHITE);
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        if(droid == null) {
            Bitmap droidBitmap = BitmapFactory.decodeResource(getResources(),
                       R.drawable.droid);
            droid = new Droid(droidBitmap, width, height);
        }

        if(rand.nextInt(MISSILE_LAUNCH_WEIGHT) == 0) {
            Missile missile = launchMissile(width, height);
            missileList.add(missile);
        }

        droid.draw(canvas);
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

    private Missile launchMissile(int width, int height) {
        int fromX = rand.nextInt(width);
        int toX = rand.nextInt(width);

        float alignX = (toX -fromX) / (float) height;
        return new Missile(fromX, alignX);
    }

}
