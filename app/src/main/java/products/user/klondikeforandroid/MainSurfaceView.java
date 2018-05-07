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

    private static ArrayList<Table> tableList;  //Table(場札)インスタンス格納用
    private static ArrayList<Deck> deckList;    //Deck(山札)インスタンス格納用
    private static ArrayList<Suits> suitsList;  //Suits(組札)インスタンス格納用

    //現在の手数を表す。1手進む毎に場の状況を↑のListに保存していく。
    //Undoするたびにこれを-1することで、1手前の状況をListから呼び出す。
    private static short undoIndex;
    private static boolean undoRun;


    //場札・山札・組札の表示座標(左上角部分)を格納
    //float[i][j]のうち、i=列数、j=0ならx座標/j=1ならy座標を示す
    private float[][] tablePosition = new float[7][2];  //場札(7列)の座標を格納
    private float[][] deckPosition = new float[2][2];   //山札(2列:未開封部分/開封部分)の座標を格納
    private float[][] suitsPosition = new float[4][2];  //組札(4列)の座標を格納

    private static int[] cardSize;  //カード自体の画像サイズを格納

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

                //画面の向きによってカード画像サイズや配置が変わるため分岐
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
                    //カードをドラッグしてる間、半透明のカードを表示させる
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

    //PORTRAIT用描画処理//
    private void doPortraitDraw(Canvas canvas) {
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);

        //緑の背景を描写
        paintBG.setARGB(255, 0, 0x55, 0x25);
        canvas.drawPaint(paintBG);


        //各リストのインデックスを取得(現在の場の状況を取得)
        short undoValue[] = Undo.getUndoValue(undoIndex);
//        Log.d("undoValue", "Table:"+undoValue[0]+" / Deck:"+undoValue[1]+" / Suits:"+undoValue[2]);


        //どんな画面サイズの端末上で実行してもレイアウトが崩れないようにするため、
        //取得した画面サイズから相対位置を指定してカードの配置をしていく。
        //いろいろ試したところ、この計算式(係数)を使うとカードが綺麗に配置される模様
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
                //裏側表示カードがテーブル上に存在
                float x = FieldActivity.getWidth() * i / (float) X_COEFFICIENT+SIDE_MARGIN;
                float y = (float) (FieldActivity.getHeight() / 5*2 - (j * cardSize[1]/6.5)); //30
                canvas.drawBitmap(card, x, y, null);
                tablePosition[i][0] = x;
                tablePosition[i][1] = y;
                backCount++;
            } else if (cardNum > 0) {
                //表側表示カードがテーブル上に存在
                float x = FieldActivity.getWidth() * i / (float) X_COEFFICIENT+SIDE_MARGIN;
                float y = (float) (FieldActivity.getHeight() / 5*2 - (j * cardSize[1]/6.5));
                canvas.drawBitmap(card, x, y, null);
                tablePosition[i][0] = x;
                tablePosition[i][1] = y;
                count++;
            } else {
                if (j < 0) {
                    //カードが1枚も存在しない場合(白い枠線だけ表示させる)
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
                    //場札の座標を格納した配列を、ユーザーの入力を管制しているクラスに渡す。
                    //この配列の座標を参照して、ユーザーの操作を解釈することになる。
                    FieldActivity.setTablePosition(tablePosition);
                    break;
                }
            }
            j--;
        } while (true);
        //------テーブル描写ここまで------//


        //------デッキ描写ここから------//
        if (deckList.get(undoValue[1]).isNextDeckRemain()) {
            //山札にめくるカードが残されている場合
            float x = FieldActivity.getWidth() * 0 / (float) X_COEFFICIENT+SIDE_MARGIN;
            card = Card.getCard(0);
            canvas.drawBitmap(card, x, BOTTOM_MARGIN, null);
            deckPosition[0][0] = x;
            deckPosition[0][1] = BOTTOM_MARGIN;
        } else {
            //山札にめくるカードが残されていない場合(白い枠線だけ表示)
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
                    //このターンのdeckListの中で、表側表示になっているカードをサーチして表示する。
//                    Log.d("表示されているcardNum","="+cardNum+" / index(d)="+d);
                    float x = FieldActivity.getWidth() * 1 / (float) X_COEFFICIENT+SIDE_MARGIN+ (count) * 40;
                    card = Card.getCard(cardNum);
                    canvas.drawBitmap(card, x, BOTTOM_MARGIN, null);
                    deckPosition[1][0] = x;
                    deckPosition[1][1] = BOTTOM_MARGIN;
                    count++;
                }
                if (d == 0) {
                    //場札の座標を格納した配列を、ユーザーの入力を管制しているクラスに渡す。
                    //この配列の座標を参照して、ユーザーの操作を解釈することになる。
                    FieldActivity.setDeckPosition(deckPosition);
                }
            }

        }
        //------デッキ描写ここまで------//


        //------スート描写ここから------//
        i = 0;
        do {
            //Bitmap変数cardにとりあえず白枠の画像をセットしておく
            card = Card.getCard(53);
            byte cardNum = suitsList.get(undoValue[2]).getCard(i);
            if (cardNum != 0) {
                //各組札に何かカードが格納されていれば、そのカードの画像をセット
                card = Card.getCard(cardNum);
            }
            float x = FieldActivity.getWidth() * (i+3) / (float) X_COEFFICIENT+SIDE_MARGIN;

            //↑でセットされた画像を描写
            canvas.drawBitmap(card, x, BOTTOM_MARGIN, null);
            suitsPosition[i][0] = x;
            suitsPosition[i][1] = BOTTOM_MARGIN;
            i++;
            if (i >= 4) {
                //場札の座標を格納した配列を、ユーザーの入力を管制しているクラスに渡す。
                //この配列の座標を参照して、ユーザーの操作を解釈することになる。
                FieldActivity.setSuitsPosition(suitsPosition);
                break;
            }
        } while (true);
        //------スート描写ここまで------//



    }

    //LANDSCAPE用描画処理//
    private void doLandscapeDraw(Canvas canvas) throws RuntimeException {

        canvas.drawColor(0, PorterDuff.Mode.CLEAR);

        //緑の背景を描写
        paintBG.setARGB(255, 0, 0x55, 0x25);
        canvas.drawPaint(paintBG);

        //各リストのインデックスを取得(現在の場の状況を取得)
        short undoValue[] = Undo.getUndoValue(undoIndex);
//        Log.d("undoValue", "Table:"+undoValue[0]+" / Deck:"+undoValue[1]+" / Suits:"+undoValue[2]);


        //どんな画面サイズの端末上で実行してもレイアウトが崩れないようにするため、
        //取得した画面サイズから相対位置を指定してカードの配置をしていく。
        //いろいろ試したところ、この計算式(係数)を使うとカードが綺麗に配置される模様
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
                //裏側表示カードがテーブル上に存在
                float x = FieldActivity.getWidth() / (float) X_COEFFICIENT * (i + 1);
                float y = FieldActivity.getHeight() / 3 + (backCount * 23);
                canvas.drawBitmap(card, x, y, null);
                tablePosition[i][0] = x;
                tablePosition[i][1] = y;
                backCount++;
            } else if (cardNum > 0) {
                //表側表示カードがテーブル上に存在
                float x = FieldActivity.getWidth() / (float) X_COEFFICIENT * (i + 1);
                float y = FieldActivity.getHeight() / 3 + (backCount * 23)+(count * FieldActivity.getHeight()/33); //count*30
                canvas.drawBitmap(card, x, y, null);
                tablePosition[i][0] = x;
                tablePosition[i][1] = y;
                count++;
            } else {
                if (j < 0) {
                    //カードが1枚も存在しない場合(白い枠線だけ表示させる)
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
                    //場札の座標を格納した配列を、ユーザーの入力を管制しているクラスに渡す。
                    //この配列の座標を参照して、ユーザーの操作を解釈することになる。
                    FieldActivity.setTablePosition(tablePosition);
                    break;
                }
            }
            j--;
        } while (true);
        //------テーブル描写ここまで------//


        //------デッキ描写ここから------//

        if (deckList.get(undoValue[1]).isNextDeckRemain()) {
            //山札にめくるカードが残されている場合
            float x = FieldActivity.getWidth() / (float) X_COEFFICIENT;
            card = Card.getCard(0);
            canvas.drawBitmap(card, x, UPPER_MARGIN, null);
            deckPosition[0][0] = x;
            deckPosition[0][1] = UPPER_MARGIN;
        } else {
            //山札にめくるカードが残されていない場合(白い枠線だけ表示)
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
                    //このターンのdeckListの中で、表側表示になっているカードをサーチして表示する。
//                    Log.d("表示されているcardNum","="+cardNum+" / index(d)="+d);
                    float x = FieldActivity.getWidth() / (float) X_COEFFICIENT * 2 + (count) * 40;
                    card = Card.getCard(cardNum);
                    canvas.drawBitmap(card, x, UPPER_MARGIN, null);
                    deckPosition[1][0] = x;
                    deckPosition[1][1] = UPPER_MARGIN;
                    count++;
                }
                if (d == 0) {
                    //場札の座標を格納した配列を、ユーザーの入力を管制しているクラスに渡す。
                    //この配列の座標を参照して、ユーザーの操作を解釈することになる。
                    FieldActivity.setDeckPosition(deckPosition);
                }
            }

        }
        //------デッキ描写ここまで------//


        //------スート描写ここから------//
        i = 0;
        do {
            //Bitmap変数cardにとりあえず白枠の画像をセットしておく
            card = Card.getCard(53);
            byte cardNum = suitsList.get(undoValue[2]).getCard(i);
            if (cardNum != 0) {
                //各組札に何かカードが格納されていれば、そのカードの画像をセット
                card = Card.getCard(cardNum);
            }
            float x = FieldActivity.getWidth() / (float) X_COEFFICIENT * (i + 4);

            //↑でセットされた画像を描写
            canvas.drawBitmap(card, x, UPPER_MARGIN, null);
            suitsPosition[i][0] = x;
            suitsPosition[i][1] = UPPER_MARGIN;
            i++;
            if (i >= 4) {
                //場札の座標を格納した配列を、ユーザーの入力を管制しているクラスに渡す。
                //この配列の座標を参照して、ユーザーの操作を解釈することになる。
                FieldActivity.setSuitsPosition(suitsPosition);
                break;
            }
        } while (true);
        //------スート描写ここまで------//

    }

    //カードのドラッグ中に表示させる半透明カード(残像エフェクト)の描写
    public void doMovingEffectDraw(Canvas canvas) {

        //透過度
        paint.setAlpha(100);

        //タップされた位置を取得
        float[] currentPosition = FieldActivity.getCurrentPosition();

        //タップされた位置(移動元)がTable/Deck/Suitsのどれなのかを取得
        //(なお、Suitsを起点にカードを移動することはできないので、Type='s'だった場合は何もしない)
        char fromType = FieldActivity.getTouchFromType();

        //移動元の列数を取得
        byte fromPoint = FieldActivity.getTouchFromPoint();

        //各リストのインデックスを取得(現在の場の状況を取得)
        short[] undoValue = Undo.getUndoValue(undoIndex);

        //半透明カード(残像エフェクト)の表示位置を補正
        float x = currentPosition[0] - cardSize[0] / 2;
        float y = currentPosition[1] - cardSize[1] / 2;

        if (fromType == 't') {
            //テーブルを起点に移動している場合
            byte cardNum = tableList.get(undoValue[0]).getCard(fromPoint, (byte) 0);
            if (cardNum > 0) {
                Bitmap card = Card.getCard(cardNum);
                canvas.drawBitmap(card, x, y, paint);
            }

        } else if (fromType == 'd' && fromPoint == 1) {
            //デッキを起点に移動している場合
            try {
                ArrayList<Byte> dl = deckList.get(undoValue[1]).getImmigrants((byte) 0);
                Bitmap card = Card.getCard(dl.get(0));
                canvas.drawBitmap(card, x, y, paint);
            } catch (Exception e) {
                Log.e(TAG,"doMovingEffectDrawにて例外発生");
            }
        }

    }

    public static void setTableList(Table table) {
        //現在のTable状況をリストに追加し一手進める(Undoに備える)
        tableList.add(table);
        Undo.plusNowTable();
    }

    public static void setDeckList(Deck deck) {
        //現在のDeck状況をリストに追加し一手進める(Undoに備える)
        deckList.add(deck);
        Undo.plusNowDeck();
    }

    public static void setSuitsList(Suits suits) {
        //現在のSuits状況をリストに追加し一手進める(Undoに備える)
        suitsList.add(suits);
        Undo.plusNowSuits();
    }

    //Undo実行メソッド
    public static boolean doUndo() {

        //各リストのインデックスを取得(現在の場の状況を取得)
        short[] beforeValue = Undo.getUndoValue(undoIndex);
        Log.d("getUndoValue情報", "t=" + beforeValue[0] + " / d=" + beforeValue[1] + " / s=" + beforeValue[2]);

        //初手以外(一手戻せる状況)だったらUndo実行
        if (undoIndex > 0) {
            undoRun=true;

            //undoIndexを-1し、一手前の状況(インスタンス)を呼び出す。
            undoIndex--;
            Log.d("Undo実行", "現在undoIndex:" + undoIndex);
            short[] afterValue = Undo.getUndoValue(undoIndex);
            Log.d("getUndoValue情報", "t=" + afterValue[0] + " / d=" + afterValue[1] + " / s=" + afterValue[2]);

            //一手前の状況と現在の状況に差異が生じているリストの最後尾を消去
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

            //各リストのindexを管理しているUndoリスト自体の最後尾も消去しUndo完了
            Undo.deleteLastInstance();
            undoRun=false;
            return true;
        }
        return false;
    }

	//Redo未実装
    public static void doRedo() {
/*
        undoIndex++;
        if(undoIndex>=Undo.getUndoSize()){
            undoIndex = (short) (Undo.getUndoSize()-1);
        }
        Log.d("Redo実行","現在undoIndex:"+undoIndex);
*/

    }

	
	/*
	*以下doReleseActionメソッドにて、入力された操作を実行に移していく。
	*その際、Tableの操作はTableクラス、Deckの操作はDeckクラス、Suitsの操作はSuitsクラスでそれぞれ管制されているため、
	*入力された操作を解釈して、適切なクラスの実行メソッドを呼び出さなければならない。
	*そこで、上記3クラスに共通のInterface(Relocationインターフェース)を実装しておき、ポリモーフィズムで対処する。
	*具体的には、Relocation変数[to]と[from]を用意し、移動先のインスタンスを[to]に、移動元のインスタンスを[from]に格納。
	*そしてRelocationインターフェースの抽象メソッド[immigration]をto.immigration(from,*,*)で呼び出せば、
	*カードの全移動パターンを実現できるようになっている。
	*/
    public static void doReleaseAction(char fromType, byte fromPoint,
                                       char toType, byte toPoint) {

        //毎回Relocation変数を初期化しておく
        Relocation to = null, from = null;


        Log.d("各ListSize", "t:" + tableList.size()
                + " / d:" + deckList.size()
                + " / s:" + suitsList.size()
                + " / u:" + Undo.getUndoSize());

        //何もないところが移動元となっている場合(空き地をタップしている場合)
        if (fromPoint < 0) {
            Log.d("fromPointが-1以下", "何も実行せず");
            return;
        }
                                       	
        //各リストのインデックスを取得(現在の場の状況を取得)
        short undoValue[] = Undo.getUndoValue(undoIndex);

        Log.d("値:", "fT" + fromType + "fP" + fromPoint + "tT" + toType + "tP" + toPoint);

        if (fromType == 't') {
            //Tableが移動元の場合
        	
        	//fromに現在のテーブル状況を示すインスタンスを渡す。
            from = tableList.get(undoValue[0]);
            Log.d("InputThread", "起点:table");


            if (fromPoint == toPoint) {
                //移動先と移動元が同じテーブルだった場合=開封
                Log.d("InputThread", "table開封を試行");
                if (tableList.get(undoValue[0]).tableOpen(fromPoint)) {
                    //該当テーブルの一番上のカードが裏側表示のカードだった場合は開封成功
                    Log.d("開封", "成功");
                	
                	//一手進める
                    undoIndex++;
                    Undo.createNewInstance(undoIndex);
                    return;
                } else {
                    //テーブルオープンに失敗した場合、テーブル→スートへの格納にチャレンジ
                	//toに現在のスート状況を示すインスタンスを渡す
                    to = suitsList.get(undoValue[2]);
                }


            } else {
                //移動先と移動元が違なる地点である場合=カードの移動が発生
            	
                if (toType == 't') {
                    //テーブル→テーブル
                    to = tableList.get(undoValue[0]);

                } else if (toType == 's') {
                    //テーブル→スート
                    to = suitsList.get(undoValue[2]);
                }
            }

        } else if (fromType == 'd') {
            //Deckが起点

            if (!deckList.get(undoValue[1]).isDeckRemain()) {
                //Deckの残り枚数=0の場合は何もできないのでreturn
                return;
            }

        	
            if (fromPoint == 0 && toPoint == 0) {
                //Deckがタップされた場合=パイルを開封
            	//このパターンの時は、toとfromを使わずに処理を完了させる。
            	
            	//現在の山札の状況がコピーされている、新たな山札インスタンスを発行
                Deck nextInstance = (Deck) deckList.get(undoValue[1]).createNewInstance();
                Log.d("InputThread", "デッキ開封");
            	
            	//新たな山札インスタンスを使って、山札を開封しにいく
            	//成功すれば、boolean型の戻り値trueが返ってくる
                if (nextInstance.deckOpen()) {
                    Log.d("InputThread", "成功");
                	
                    //成功した場合、成功後のDeckの状況をリストに格納
                	MainSurfaceView.setDeckList(nextInstance);
                	
					//一手進める
                	undoIndex++;
                    Undo.createNewInstance(undoIndex);
                }
            	//前述したとおり、このパターンではカードの移動が発生しないため、ここでreturn
                return;

            } else if (fromPoint == 1) {
                //開封済デッキが移動元
            	
            	//fromに現在の山札の状況を示すインスタンスを格納
                from = deckList.get(undoValue[1]);

                if (toType == 't') {
                    //デッキ→テーブル
                	
                	//toに現在のテーブルの状況を示すインスタンスを格納
                    to = tableList.get(undoValue[0]);
                	
                } else if (toType == 's') {
                    //デッキ→スート
                	
                	//toに現在のスートの状況を示すインスタンスを格納
                    to = suitsList.get(undoValue[2]);
                	
                } else if (toType == 'd' && toPoint == 1) {
                    //開封済みデッキがタップされているケース
                    Log.d("InputThread", "開封済みdeck→suitsへ直接格納");

                    //デッキ→スートへの直接格納にチャレンジ
                	//toに現在のスートの状況を示すインスタンスを格納
                    to = suitsList.get(undoValue[2]);
                }
            }
        }

		//toまたはfromがnullである場合は何もしない
        if (to == null || from == null) {
            Log.d(TAG, "doReleaseAction : [to] or [from]がnullのため動作回避");
        } else {
            //toとfromには、ここまで通ってきたif条件節により、
            //移動先と元のインスタンスが渡されている。
            //また、Table,Deck,Suitsの各クラスにはインターフェースが実装されており、
            //以下のto.immigrationを実行するだけで、全パターンの移動が実行されることとなる。
            //なお、移動が成功した場合にのみimmigrationメソッドからtrueが戻ってくる。
            if (to.immigration(from, fromPoint, toPoint)) {
                //移動成功
                Log.d("InputThread", "immigration成功");
            	
                //一手進める
                undoIndex++;
                Undo.createNewInstance(undoIndex);
            }
        }

    }


}