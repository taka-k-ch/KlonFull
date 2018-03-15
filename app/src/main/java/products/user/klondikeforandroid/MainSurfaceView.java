package products.user.klondikeforandroid;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

public class MainSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private SurfaceHolder holder;
    private Thread mLooper;
    private static final String TAG = "MainSurfaceView";

    public static final byte UPPER_MARGIN = 100;
    private long t1 = 0, t2 = 0; // スリープ用変数

    public Paint paint;
    public Paint paintBG;

    private static ArrayList<Table> tableList = new ArrayList<>();
    private static ArrayList<Deck> deckList = new ArrayList<>();
    private static ArrayList<Suits> suitsList = new ArrayList<>();

    private float[][] deckPosition = new float[2][2];
    private float[][] tablePosition = new float[7][2];
    private float[][] suitsPosition = new float[4][2];

    private static int[] cardSize;

    private static short undoIndex;
    private static boolean greenLight = true;


    static Resources resources;

    public MainSurfaceView(Context context, SurfaceView sv) {
        super(context);
        resources = context.getResources();//画像登録準備
        holder = sv.getHolder();

        // 半透明を設定
        holder.setFormat(PixelFormat.TRANSLUCENT);
        // 半透明を設定
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
        // コールバック登録
        getHolder().addCallback(this);
        // フォーカス可
        setFocusable(true);
        // このViewをトップにする
//        setZOrderOnTop(true);

        holder.addCallback(this);

        initialize();
    }

    private void initialize() {
        InitialCreation.initialCreation();
        Undo.createNewInstance(undoIndex);
    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        Log.d(TAG, "surfaceChanged");

    }

    @Override
    public void surfaceCreated(SurfaceHolder arg0) {
        Log.d(TAG, "surfaceCreated");
//        holder = arg0;
        mLooper = new Thread(this);
        mLooper.start();


    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        Log.d(TAG, "surfaceDestroyed");
        mLooper = null;
    }

    @Override
    public void run() {
        Log.d(TAG, "run");
        paint = new Paint();
        paint.setAntiAlias(true);
        paintBG = new Paint();

        cardSize = Card.getCardSize();


        // メインループ（無限ループ）
        while (true) {
            t1 = System.currentTimeMillis();

            //------描画処理を開始------//
            Canvas canvas = holder.lockCanvas();

//                if(greenLight) {
            //描画処理//
            doDraw(canvas);
//                }


            if (FieldActivity.getMovingEffect()) {
                //移動エフェクト描画//
                doMovingEffectDraw(canvas);
            }

            holder.unlockCanvasAndPost(canvas);

            //------描画処理を終了------//

            // スリープ
            t2 = System.currentTimeMillis();
            final long fTime=48;    //デフォルト=16
            if (t2 - t1 < fTime) { // 1000 / 60 = 16.6666
                try {
                    Thread.sleep(fTime - (t2 - t1));
                } catch (InterruptedException e) {

                }
            }
        }   //whileここまで

    }

    private void doDraw(Canvas canvas) {

        //------描画処理を開始------//
//        Canvas canvas = holder.lockCanvas();

        canvas.drawColor(0, PorterDuff.Mode.CLEAR);

        paintBG.setARGB(255, 0, 0x55, 0x25);
        canvas.drawPaint(paintBG);

        //各リストのインデックスを取得
        short undoValue[] = Undo.getUndoValue(undoIndex);
//        Log.d("undoValue", "Table:"+undoValue[0]+" / Deck:"+undoValue[1]+" / Suits:"+undoValue[2]);

        //テーブル描写
        byte i = 6;
        byte j = 19;
        byte count = 0;
        do {
            byte cardNum = tableList.get(undoValue[0]).getCard(i, j);

            if (cardNum != 0) {
                count++;
                float x = FieldActivity.getWidth() / (float) 8.5 * (i + 1) + 30;
                float y = FieldActivity.getHeight() / 3 + 30 + (count * 25);
                Bitmap card = Card.getCard(cardNum);
                canvas.drawBitmap(card, x, y, null);
                tablePosition[i][0] = x;
                tablePosition[i][1] = y;
            } else {
                if (j < 0) {
                    if (count == 0) {
                        tablePosition[i][0] = FieldActivity.getWidth() / (float) 8.5 * (i + 1) + 30;
                        tablePosition[i][1] = FieldActivity.getHeight() / 3 + 30;
                    }
                    i--;
                    j = (byte) (i + 13);
                    count = 0;
                }
                if (i < 0) {
                    FieldActivity.setTablePosition(tablePosition);
                    break;
                }
            }
            j--;
        } while (true);
        //------テーブル描写ここまで------//


        //------デッキ描写ここから------//

        if (deckList.get(undoValue[1]).isNextDeckRemain()) {
            float x = FieldActivity.getWidth() / (float) 8.5 + 30;
            Bitmap card = Card.getCard(0);
            canvas.drawBitmap(card, x, UPPER_MARGIN, null);
            deckPosition[0][0] = x;
            deckPosition[0][1] = UPPER_MARGIN;
        }

        //デッキ開封部分
        if (deckList.get(undoValue[1]).isDeckRemain()) {
            count = 0;
            for (byte d = (byte) (deckList.get(undoValue[1]).getDeckSize() - 1); d >= 0; d--) {
                byte cardNum = deckList.get(undoValue[1]).getCard(d);
//                Log.d("cardNum","="+cardNum+" / index(d)="+d);
                if (cardNum > 0) {
//                    Log.d("表示されているcardNum","="+cardNum+" / index(d)="+d);
                    float x = FieldActivity.getWidth() / (float) 8.5 * 2 + 30 + (count) * 40;
                    Bitmap card = Card.getCard(cardNum);
                    canvas.drawBitmap(card, x, UPPER_MARGIN, null);
                    deckPosition[1][0] = x;
                    deckPosition[1][1] = UPPER_MARGIN;
                    count++;
                }
                if (d == 0) {
                    FieldActivity.setDeckPosition(deckPosition);
                }
            }

        }
        //------デッキ描写ここまで------//


        //------スート描写ここから------//
        i = 0;
        do {
            Bitmap card = Card.getCard(53);
            byte cardNum = suitsList.get(undoValue[2]).getCard(i);
            if (cardNum != 0) {
                card = Card.getCard(cardNum);
            }
            float x = FieldActivity.getWidth() / (float) 8.5 * (i + 4) + 30;
            canvas.drawBitmap(card, x, UPPER_MARGIN, null);
            suitsPosition[i][0] = x;
            suitsPosition[i][1] = UPPER_MARGIN;
            i++;
            if (i >= 4) {
                FieldActivity.setSuitsPosition(suitsPosition);
                break;
            }
        } while (true);
        //------スート描写ここまで------//


//        holder.unlockCanvasAndPost(canvas);
        //------描画処理を終了------//


        greenLight = false;

    }

    public void doMovingEffectDraw(Canvas canvas) {
        //------描画処理を開始------//
//        Canvas canvas = holder.lockCanvas();

//        canvas.drawColor(0, PorterDuff.Mode.CLEAR);

        paint.setAlpha(100);

        float[] currentPosition = FieldActivity.getCurrentPosition();
        char fromType = FieldActivity.getTouchFromType();
        byte fromPoint = FieldActivity.getTouchFromPoint();

        short[] undoValue = Undo.getUndoValue(undoIndex);

        float x = currentPosition[0] - cardSize[0] / 2;
        float y = currentPosition[1] - cardSize[1] / 2;

        if (fromType == 't') {
            //テーブルから移動中
            byte cardNum = tableList.get(undoValue[0]).getCard(fromPoint, (byte) 0);
            if(cardNum>0) {
                Bitmap card = Card.getCard(cardNum);
                canvas.drawBitmap(card, x, y, paint);
            }

        } else if (fromType == 'd' && fromPoint == 1) {
            //デッキから移動中
            try {
                ArrayList<Byte> dl = deckList.get(undoValue[1]).getImmigrants((byte) 0);
                Bitmap card = Card.getCard(dl.get(0));
                canvas.drawBitmap(card, x, y, paint);
            } catch (Exception e) {

            }
        }

//        holder.unlockCanvasAndPost(canvas);
//        //------描画処理を終了------//
    }

    public static void setTableList(Table table) {

        tableList.add(table);
        Undo.setNowTable();
    }

    public static void setDeckList(Deck deck) {

        deckList.add(deck);
        Undo.setNowDeck();
    }

    public static void setSuitsList(Suits suits) {

        suitsList.add(suits);
        Undo.setNowSuits();
    }

    public static void doUndo() {
//        short[] doUndoValue = Undo.getUndoValue(undoIndex);
//        Log.d("getUndoValue情報","t="+doUndoValue[0]+" / d="+doUndoValue[1]+" / s="+doUndoValue[2]);
//        undoIndex--;
//        if(undoIndex<0){
//            undoIndex = 0;
//        }
//        Log.d("Undo実行","現在undoIndex:"+undoIndex);
//        doUndoValue = Undo.getUndoValue(undoIndex);
//        Log.d("getUndoValue情報","t="+doUndoValue[0]+" / d="+doUndoValue[1]+" / s="+doUndoValue[2]);
//        while(doUndoValue[0]<tableList.size()){
//            Log.d("table消去実行","");
//            tableList.remove(doUndoValue[0]);
//        }
//        while(doUndoValue[1]<deckList.size()){
//            Log.d("deck消去実行","");
//            deckList.remove(doUndoValue[1]);
//        }
//        while(doUndoValue[2]<suitsList.size()){
//            Log.d("suits消去実行","");
//            suitsList.remove(doUndoValue[2]);
//        }

    }

    public static void doRedo() {
/*
        undoIndex++;
        if(undoIndex>=Undo.getUndoSize()){
            undoIndex = (short) (Undo.getUndoSize()-1);
        }
        Log.d("Redo実行","現在undoIndex:"+undoIndex);
*/

    }

    public static void doReleaseAction(char fromType, byte fromPoint,
                                       char toType, byte toPoint) {

        Relocation to = null, from = null;


        Log.d("各ListSize", "t:" + tableList.size()
                + " / d:" + deckList.size()
                + " / s:" + suitsList.size()
                + " / u:" + Undo.getUndoSize());

        if (fromPoint < 0) {
            Log.d("fromPointが-1以下", "何も実行せず");
            return;
        }

        short undoValue[] = Undo.getUndoValue(undoIndex);

        Log.d("値:", "fT" + fromType + "fP" + fromPoint + "tT" + toType + "tP" + toPoint);

        if (fromType == 't') {
            //テーブルをタッチ
            from = tableList.get(undoValue[0]);
            Log.d("InputThread", "起点:table");


            if (fromPoint == toPoint) {
                //移動先と元が同じテーブルだった場合=開封
                Log.d("InputThread", "table開封を試行");
                if (tableList.get(undoValue[0]).tableOpen(fromPoint)) {
                    Log.d("開封", "成功");
                    undoIndex++;
                    Undo.createNewInstance(undoIndex);
                    greenLight = true;
                    return;
                } else {
                    //テーブルオープンに失敗した場合、スートへの格納にチャレンジ
                    to = suitsList.get(undoValue[2]);
                }


            } else {

                if (toType == 't') {
                    //テーブル→テーブル
                    to = tableList.get(undoValue[0]);

                } else if (toType == 's') {
                    //テーブル→スート
                    to = suitsList.get(undoValue[2]);
                }
            }

        } else if (fromType == 'd') {
            if (!deckList.get(undoValue[1]).isDeckRemain()) {
                return;
            }

            if (fromPoint == 0 && toPoint == 0) {
                //デッキパイルを開封
                //デッキに残りがあるかチェック
                Deck nextInstance = (Deck) deckList.get(undoValue[1]).createNewInstance();
                Log.d("InputThread", "デッキ開封");
                if (nextInstance.deckOpen()) {
                    Log.d("InputThread", "成功");
                    MainSurfaceView.setDeckList(nextInstance);
                    undoIndex++;
                    Undo.createNewInstance(undoIndex);
                    greenLight = true;
                }

                return;

            } else if (fromPoint == 1) {
                //開封済デッキをタッチ
                from = deckList.get(undoValue[1]);

                if (toType == 't') {
                    //デッキ→テーブル
                    to = tableList.get(undoValue[0]);
                } else if (toType == 's') {
                    //デッキ→スート
                    to = suitsList.get(undoValue[2]);
                } else if (toType == 'd' && toPoint == 1) {
                    Log.d("InputThread", "開封済みdeck→suitsへ直接格納");
                    //開封済みデッキをタップ
                    //スートへの格納にチャレンジ
                    to = suitsList.get(undoValue[2]);
                }
            }
        }

        if (to == null || from == null) {
            Log.d("nurupo!", "ぬるぽ！");
        } else {

            if (to.immigration(from, fromPoint, toPoint)) {
                //移動実行
                Log.d("InputThread", "immigration成功");
                undoIndex++;
                Undo.createNewInstance(undoIndex);
                greenLight = true;
            }
        }

    }


}