package products.user.klondikeforandroid;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class OverLaySurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable{
    private SurfaceHolder holder;
    private Thread menuThread;
    private boolean menuThreadRun;
    private static final String TAG = "OverLaySurfaceView";

    private long t1 = 0, t2 = 0; // スリープ用変数

    Canvas canvas;
    public Paint paint;
    public Paint paintBG;

    Resources resources;

    public OverLaySurfaceView(Context context, SurfaceView sv2) {
        super(context);
        resources = context.getResources();//画像登録準備
        holder = sv2.getHolder();


        // 半透明を設定
        holder.setFormat(PixelFormat.TRANSLUCENT);
        // 半透明を設定
        sv2.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        // コールバック登録
        sv2.getHolder().addCallback(this);
        // フォーカス可
        sv2.setFocusable(true);
        // このViewをトップにする
        sv2.setZOrderOnTop(true);

        holder.addCallback(this);


//        initialize();
    }

    public void menuStart() {
//        menuThreadRun = true;
//        menuThread = new Thread(this);
//        menuThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        Log.d(TAG, "surfaceChanged");
//        mLooper = new Thread(this);
//        mLooper.start();
    }

    @Override
    public void surfaceCreated(SurfaceHolder arg0) {
        Log.d(TAG, "surfaceCreated");
//        holder = arg0;
        menuThread = new Thread(this);
        menuThread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        Log.d(TAG, "surfaceDestroyed");
        menuThread = null;
    }

    @Override
    public void run() {
        Log.d(TAG, "run");

        paint = new Paint();
        paint.setAntiAlias(true);
        paintBG = new Paint();

//        while (menuThreadRun) {
            t1 = System.currentTimeMillis();
            try {
                //------描画処理を開始------//
                canvas = holder.lockCanvas();

                try {
                    //描画処理//
                    doDraw(canvas);
                } catch (RuntimeException e) {
                    Log.e(TAG, "doDrawで例外が発生 : " + e);
                }

                holder.unlockCanvasAndPost(canvas);
                //------描画処理を終了------//

            } catch (Exception e) {
                Log.e(TAG, "canvas/holderまわりで例外が発生 : " + e);
            }

            // スリープ
            t2 = System.currentTimeMillis();
            final long fTime = 48;    //デフォルト=16
            if (t2 - t1 < fTime) { // 1000 / 60 = 16.6666
                try {
                    Thread.sleep(fTime - (t2 - t1));
                } catch (InterruptedException e) {

                }
            }
//        }   //whileここまで

    }

    private void doDraw(Canvas canvas) throws RuntimeException {

//        canvas.drawColor(0, PorterDuff.Mode.CLEAR);
        paintBG.setARGB(122, 0, 0, 0);
        canvas.drawPaint(paintBG);

    }
}
