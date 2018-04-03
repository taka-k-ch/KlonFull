package products.user.klondikeforandroid;

import android.content.Context;
import android.content.res.Configuration;
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
    private boolean mLooperRun;
    private static final String TAG = "MainSurfaceView";

    private long t1 = 0, t2 = 0; // スリープ用変数

    private Canvas canvas;
    private Paint paint;
    private Paint paintBG;
    private Bitmap card;

    private static ArrayList<Table> tableList;
    private static ArrayList<Deck> deckList;
    private static ArrayList<Suits> suitsList;

    private float[][] deckPosition = new float[2][2];
    private float[][] tablePosition = new float[7][2];
    private float[][] suitsPosition = new float[4][2];

    private static int[] cardSize;

    private static short undoIndex;
    private static boolean undoRun;

    static Resources resources;

    public MainSurfaceView(Context context, SurfaceView sv) {
        super(context);
        resources = context.getResources();//画像登録準備
        holder = sv.getHolder();

        // 半透明を設定
        holder.setFormat(PixelFormat.TRANSLUCENT);
        // 半透明を設定
        sv.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        // コールバック登録
        sv.getHolder().addCallback(this);
        // フォーカス可
        sv.setFocusable(true);
        // このViewをトップにする
//        setZOrderOnTop(true);

        holder.addCallback(this);
    }

    public static void initialize() {
        //初期化作業
        tableList = new ArrayList<>();
        deckList = new ArrayList<>();
        suitsList = new ArrayList<>();
        undoIndex = 0;
        Undo.initializeUndoList();  //しっかり最初にUndoクラスを初期化しないとエラー落ち確定

        //カード生成
        InitialCreation.initialCreation();

        //最初の画面状態をUndoリストの0番目に格納
        Undo.createNewInstance(undoIndex);

    }

    public void doResume() {
        //現在の画面向きにあわせてカード画像の大きさを再取得
        Card.setCardSize();
        mLooperRun = true;
        mLooper = new Thread(this);
        mLooper.start();
    }

    public void doPause() {

        mLooperRun = false;
        while (true) {
            try {
                mLooper.join();
                break;
            } catch (InterruptedException iException) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        mLooper = null;
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
//        mLooper = new Thread(this);
//        mLooper.start();
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
        Configuration config;

        // メインループ（無限ループ）
        while (mLooperRun && !undoRun) {
            t1 = System.currentTimeMillis();
            try {
                //------描画処理を開始------//
                canvas = holder.lockCanvas();

                //現在の画面向きを取得
                config = getResources().getConfiguration();
                try {
                    if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
                        //PORTRAIT用描画処理//
                        doPortraitDraw(canvas);
                    } else {
                        //LANDSCAPE用描画処理//
                        doLandscapeDraw(canvas);
                    }
                } catch (RuntimeException e) {
                    Log.e(TAG, "doDrawで例外が発生 : " + e);
                }


                if (FieldActivity.getMovingEffect()) {
                    //移動エフェクト描画//
                    doMovingEffectDraw(canvas);
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
        }   //whileここまで

    }

    private void doPortraitDraw(Canvas canvas) {
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);
        paintBG.setARGB(255, 0, 0x55, 0x25);
        canvas.drawPaint(paintBG);

        //各リストのインデックスを取得
        short undoValue[] = Undo.getUndoValue(undoIndex);
//        Log.d("undoValue", "Table:"+undoValue[0]+" / Deck:"+undoValue[1]+" / Suits:"+undoValue[2]);

        final short BOTTOM_MARGIN = (short)(FieldActivity.getHeight()/5*3+100);
        final short SIDE_MARGIN = (short)(FieldActivity.getHeight()/120);
        final float X_COEFFICIENT = (float) 7.2;

        //テーブル描写
        byte i = 6;
        byte j = 19;
        byte count = 0;
        byte backCount = 0;
        do {
            byte cardNum = tableList.get(undoValue[0]).getCard(i, j);
            card = Card.getCard(cardNum);
            if (cardNum < 0) {
                float x = FieldActivity.getWidth() * i / (float) X_COEFFICIENT+SIDE_MARGIN;
                float y = (float) (FieldActivity.getHeight() / 5*2 - (j * cardSize[1]/6.5)); //30
                canvas.drawBitmap(card, x, y, null);
                tablePosition[i][0] = x;
                tablePosition[i][1] = y;
                backCount++;
            } else if (cardNum > 0) {
                float x = FieldActivity.getWidth() * i / (float) X_COEFFICIENT+SIDE_MARGIN;
                float y = (float) (FieldActivity.getHeight() / 5*2 - (j * cardSize[1]/6.5));
                canvas.drawBitmap(card, x, y, null);
                tablePosition[i][0] = x;
                tablePosition[i][1] = y;
                count++;
            } else {
                if (j < 0) {
                    if (backCount == 0 && count == 0) {
                        float x = FieldActivity.getWidth() * i / (float) X_COEFFICIENT+SIDE_MARGIN;
                        float y = FieldActivity.getHeight() / 5*2;
                        card = Card.getCard(53);
                        canvas.drawBitmap(card, x, y, null);
                        tablePosition[i][0] = x;
                        tablePosition[i][1] = y;
                    }
                    i--;
                    j = (byte) (i + 13);
                    count = 0;
                    backCount=0;
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
            float x = FieldActivity.getWidth() * 0 / (float) X_COEFFICIENT+SIDE_MARGIN;
            card = Card.getCard(0);
            canvas.drawBitmap(card, x, BOTTOM_MARGIN, null);
            deckPosition[0][0] = x;
            deckPosition[0][1] = BOTTOM_MARGIN;
        } else {
            float x = FieldActivity.getWidth() * 0 / (float) X_COEFFICIENT+SIDE_MARGIN;
            card = Card.getCard(53);
            canvas.drawBitmap(card, x, BOTTOM_MARGIN, null);
            deckPosition[0][0] = x;
            deckPosition[0][1] = BOTTOM_MARGIN;
        }

        //デッキ開封部分
        if (deckList.get(undoValue[1]).isDeckRemain()) {
            count = 0;
            for (byte d = (byte) (deckList.get(undoValue[1]).getDeckSize() - 1); d >= 0; d--) {
                byte cardNum = deckList.get(undoValue[1]).getCard(d);
//                Log.d("cardNum","="+cardNum+" / index(d)="+d);
                if (cardNum > 0) {
//                    Log.d("表示されているcardNum","="+cardNum+" / index(d)="+d);
                    float x = FieldActivity.getWidth() * 1 / (float) X_COEFFICIENT+SIDE_MARGIN+ (count) * 40;
                    card = Card.getCard(cardNum);
                    canvas.drawBitmap(card, x, BOTTOM_MARGIN, null);
                    deckPosition[1][0] = x;
                    deckPosition[1][1] = BOTTOM_MARGIN;
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
            card = Card.getCard(53);
            byte cardNum = suitsList.get(undoValue[2]).getCard(i);
            if (cardNum != 0) {
                card = Card.getCard(cardNum);
            }
            float x = FieldActivity.getWidth() * (i+3) / (float) X_COEFFICIENT+SIDE_MARGIN;
            canvas.drawBitmap(card, x, BOTTOM_MARGIN, null);
            suitsPosition[i][0] = x;
            suitsPosition[i][1] = BOTTOM_MARGIN;
            i++;
            if (i >= 4) {
                FieldActivity.setSuitsPosition(suitsPosition);
                break;
            }
        } while (true);
        //------スート描写ここまで------//



    }

    private void doLandscapeDraw(Canvas canvas) throws RuntimeException {

        //------描画処理を開始------//
//        Canvas canvas = holder.lockCanvas();

        canvas.drawColor(0, PorterDuff.Mode.CLEAR);
        paintBG.setARGB(255, 0, 0x55, 0x25);
        canvas.drawPaint(paintBG);

        //各リストのインデックスを取得
        short undoValue[] = Undo.getUndoValue(undoIndex);
//        Log.d("undoValue", "Table:"+undoValue[0]+" / Deck:"+undoValue[1]+" / Suits:"+undoValue[2]);

        final byte UPPER_MARGIN = 40;
        final float X_COEFFICIENT = (float) 8.0;

        //テーブル描写
        byte i = 6;
        byte j = 19;
        byte count = 0;
        byte backCount = 0;
        do {
            byte cardNum = tableList.get(undoValue[0]).getCard(i, j);
            card = Card.getCard(cardNum);
            if (cardNum < 0) {
                float x = FieldActivity.getWidth() / (float) X_COEFFICIENT * (i + 1);
                float y = FieldActivity.getHeight() / 3 + (backCount * 23);
                canvas.drawBitmap(card, x, y, null);
                tablePosition[i][0] = x;
                tablePosition[i][1] = y;
                backCount++;
            } else if (cardNum > 0) {
                float x = FieldActivity.getWidth() / (float) X_COEFFICIENT * (i + 1);
                float y = FieldActivity.getHeight() / 3 + (backCount * 23)+(count * FieldActivity.getHeight()/33); //count*30
                canvas.drawBitmap(card, x, y, null);
                tablePosition[i][0] = x;
                tablePosition[i][1] = y;
                count++;
            } else {
                if (j < 0) {
                    if (backCount == 0 && count == 0) {
                        float x = FieldActivity.getWidth() / (float) X_COEFFICIENT * (i + 1);
                        float y = FieldActivity.getHeight() / 3;
                        card = Card.getCard(53);
                        canvas.drawBitmap(card, x, y, null);
                        tablePosition[i][0] = FieldActivity.getWidth() / (float) X_COEFFICIENT * (i + 1);
                        tablePosition[i][1] = FieldActivity.getHeight() / 3;
                    }
                    i--;
                    j = (byte) (i + 13);
                    count = 0;
                    backCount=0;
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
            float x = FieldActivity.getWidth() / (float) X_COEFFICIENT;
            card = Card.getCard(0);
            canvas.drawBitmap(card, x, UPPER_MARGIN, null);
            deckPosition[0][0] = x;
            deckPosition[0][1] = UPPER_MARGIN;
        } else {
            float x = FieldActivity.getWidth() / (float) X_COEFFICIENT;
            card = Card.getCard(53);
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
                    float x = FieldActivity.getWidth() / (float) X_COEFFICIENT * 2 + (count) * 40;
                    card = Card.getCard(cardNum);
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
            card = Card.getCard(53);
            byte cardNum = suitsList.get(undoValue[2]).getCard(i);
            if (cardNum != 0) {
                card = Card.getCard(cardNum);
            }
            float x = FieldActivity.getWidth() / (float) X_COEFFICIENT * (i + 4);
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
            if (cardNum > 0) {
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
        Undo.plusNowTable();
    }

    public static void setDeckList(Deck deck) {

        deckList.add(deck);
        Undo.plusNowDeck();
    }

    public static void setSuitsList(Suits suits) {

        suitsList.add(suits);
        Undo.plusNowSuits();
    }

    public static boolean doUndo() {

        short[] beforeValue = Undo.getUndoValue(undoIndex);
        Log.d("getUndoValue情報", "t=" + beforeValue[0] + " / d=" + beforeValue[1] + " / s=" + beforeValue[2]);
        if (undoIndex > 0) {
            undoRun=true;
            undoIndex--;

            Log.d("Undo実行", "現在undoIndex:" + undoIndex);
            short[] afterValue = Undo.getUndoValue(undoIndex);
            Log.d("getUndoValue情報", "t=" + afterValue[0] + " / d=" + afterValue[1] + " / s=" + afterValue[2]);
            if (beforeValue[0] != afterValue[0]) {
                Log.d(TAG, "doUndo : table消去実行");
                tableList.remove(tableList.size() - 1);
                Undo.minusNowTable();
            }
            if (beforeValue[1] != afterValue[1]) {
                Log.d(TAG, "doUndo : deck消去実行");
                deckList.remove(deckList.size() - 1);
                Undo.minusNowDeck();
            }
            if (beforeValue[2] != afterValue[2]) {
                Log.d(TAG, "doUndo : suits消去実行");
                suitsList.remove(suitsList.size() - 1);
                Undo.minusNowSuits();
            }
            Undo.deleteLastInstance();
            undoRun=false;
            return true;
        }
        return false;
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
            }
        }

    }


}