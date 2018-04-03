package products.user.klondikeforandroid;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class FieldActivity extends AppCompatActivity {

    private static FieldActivity fieldActivity;
    private SurfaceView surfaceView;
    private MainSurfaceView mainSurfaceView;
    private static boolean touch = false;
    private static float tx;
    private static float ty;
    private static int widthReal, heightReal;
    private static int cardWidth, cardHeight;
    private static float[][] tablePosition = new float[7][2];
    private static float[][] deckPosition = new float[2][2];
    private static float[][] suitsPosition = new float[4][2];
    private static char touchFromType;
    private static byte touchFromPoint;
    private static boolean movingEffect;

    private static boolean rotationPortrait;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_field);
        Log.d("FieldActivity","onCreate");

        //初回起動時のみ初期化メソッドを呼ぶ
        if(fieldActivity==null) {
            MainSurfaceView.initialize();
        }

        //画面の向きを決定
        fieldActivity = this;
        decideRotation();


//        // タイトルバーを消す
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        // ステータスバーを消す
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //画面サイズ取得
        WindowManager wm = (WindowManager)getSystemService(WINDOW_SERVICE);
        Display disp = wm.getDefaultDisplay();
        Point realSize = new Point();
        disp.getRealSize(realSize);
        widthReal = realSize.x;
        heightReal = realSize.y;

        surfaceView = findViewById(R.id.surfaceViewMain);
        mainSurfaceView = new MainSurfaceView(this, surfaceView);
    }

    @Override
    protected void onStart(){
        super.onStart();


    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.d("FieldActivity","onResume");

        //ナビゲーションボタンを消す
        FieldActivity.clearNavigationButton();
        mainSurfaceView.doResume();
    }

    @Override
    protected void onPause(){
        super.onPause();
        Log.d("FieldActivity","onPause");
        mainSurfaceView.doPause();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.d("FieldActivity","onDestroy");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO 自動生成されたメソッド・スタブ

        //カード画像の大きさを取得
        int[] size = Card.getCardSize();
        cardWidth = size[0];
        cardHeight = size[1];

        tx = event.getX();
        ty = event.getY();

            // 触る
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                movingEffect = false;
                touchMatching(tx, ty);
            }
            // 触ったままスライド
            else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                //移動中のカードの残像を表示
                movingEffect = true;
                moveMatching(tx, ty);
            }
            // 離す
            else if (event.getAction() == MotionEvent.ACTION_UP) {
                movingEffect = false;
                releaseMatching(tx, ty);
            }

        // 再描画の指示
//        invalidate();
//        surfaceView.invalidate();

//        if (HolderCallBack.roopEnd) {
//            Intent intent = new Intent(this, MainActivity.class);
//            startActivity(intent);
//            finish();
//        }

//        Log.d("TouchEvent", "X:" + tx + ",Y:" + ty);

        return true;

    }

    private void touchMatching(float tx, float ty){
        Log.d("InputThread","タッチ検出");
//        Log.d("InputThread", "table[0]の位置 x:"+tablePosition[0][0]+"y:"+tablePosition[0][1]);

        for(byte i=0; i<tablePosition.length; i++){
            if (tablePosition[i] != null) {
                if (tx >= tablePosition[i][0] && tx <= tablePosition[i][0] + cardWidth) {
                    if (ty >= tablePosition[i][1] && ty <= tablePosition[i][1] + cardHeight) {
                        //どのテーブルをタッチしたか判明
                        Log.d("InputThread","タッチ検出(table["+i+"])");
                        touchFromType = 't';
                        touchFromPoint = i;
                        return;
                    }
                }
            }
        }

        for(byte i=0; i<deckPosition.length; i++){
            if (deckPosition[i] != null) {
                if (tx >= deckPosition[i][0] && tx <= deckPosition[i][0] + cardWidth) {
                    if (ty >= deckPosition[i][1] && ty <= deckPosition[i][1] + cardHeight) {
                        //デッキのどこをタッチしたか判明
                        //i=0なら未開封の山札、i=1なら開封済のカード部分
                        Log.d("InputThread","タッチ検出(deck"+i+")");
                        touchFromType = 'd';
                        touchFromPoint = i;
                        return;
                    }
                }
            }
        }

        touchFromType = ' ';
        touchFromPoint = -1;

    }

    private void moveMatching(float tx, float ty){

        for(byte i=0; i<tablePosition.length; i++){
            if (tablePosition[i] != null) {
                if (tx >= tablePosition[i][0] && tx <= tablePosition[i][0] + cardWidth) {
                    if (ty >= tablePosition[i][1] && ty <= tablePosition[i][1] + cardHeight) {
                        //どのテーブルに移動中か判明
                        Log.d("InputThread","移動中(table["+i+"]上空");
                        return;
                    }
                }
            }
        }

        for(byte i=0; i<suitsPosition.length; i++){
            if (suitsPosition[i] != null) {
                if (tx >= suitsPosition[i][0] && tx <= suitsPosition[i][0] + cardWidth) {
                    if (ty >= suitsPosition[i][1] && ty <= suitsPosition[i][1] + cardHeight) {
                        //どのスートに移動中か判明
                        Log.d("InputThread","移動中(suits["+i+"]上空)");
                        return;
                    }
                }
            }
        }

    }

    private void releaseMatching(float tx, float ty){

        for(byte i=0; i<tablePosition.length; i++){
            if (tablePosition[i] != null) {
                if (tx >= tablePosition[i][0] && tx <= tablePosition[i][0] + cardWidth) {
                    if (ty >= tablePosition[i][1] && ty <= tablePosition[i][1] + cardHeight) {
                        //どのテーブルに投下したか判明
                        Log.d("InputThread","リリース検知(table["+i+"])");

                        MainSurfaceView.doReleaseAction(
                                touchFromType, touchFromPoint, 't', i);
                        return;
                    }
                }
            }
        }

        for(byte i=0; i<suitsPosition.length; i++){
            if (suitsPosition[i] != null) {
                if (tx >= suitsPosition[i][0] && tx <= suitsPosition[i][0] + cardWidth) {
                    if (ty >= suitsPosition[i][1] && ty <= suitsPosition[i][1] + cardHeight) {
                        //どのスートに投下したか判明
                        Log.d("InputThread","リリース検知(suits["+i+"])");
                        MainSurfaceView.doReleaseAction(
                                touchFromType, touchFromPoint, 's', i);
                        return;
                    }
                }
            }
        }

        for(byte i=0; i<deckPosition.length; i++){
            if (deckPosition[i] != null) {
                if (tx >= deckPosition[i][0] && tx <= deckPosition[i][0] + cardWidth) {
                    if (ty >= deckPosition[i][1] && ty <= deckPosition[i][1] + cardHeight) {
                        //デッキのどこをタッチしたか判明
                        if(touchFromType == 'd' && touchFromPoint == 0){
                            //デッキパイルをタップしている
                            Log.d("InputThread","deckパイルタップ:パイル開封へ");
                            MainSurfaceView.doReleaseAction(
                                    touchFromType, touchFromPoint, 'd', (byte)0);
                        } else if(touchFromType == 'd' && touchFromPoint ==1){
                            Log.d("InputThread","deck開封部タップ:suits収納へ");
                            MainSurfaceView.doReleaseAction(
                                    touchFromType, touchFromPoint, 'd', (byte)1);
                        }

                        return;
                    }
                }
            }
        }

    }


    public void onUndoButton(View v){
        Log.d("UndoButton","が押されました");
        //Undo実施
        MainSurfaceView.doUndo();
    }
/*
    public void onRedoButton(View v){
        Log.d("RedoButton","が押されました");
        MainSurfaceView.doRedo();
    }
*/

    public void onMenuButton(View v){
        Log.d("MenuButton","が押されました");
        //menu画面を開く
        Intent intent = new Intent(getApplication(), OverLayActivity.class);
        startActivity(intent);
    }

    public static void clearNavigationButton() {
        //ナビゲーションボタンを消す
        View decor = fieldActivity.getWindow().getDecorView();
        decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    public static void decideRotation(){
        //画面の向きをスイッチング
        Configuration config = fieldActivity.getResources().getConfiguration();
        if (config.orientation == Configuration.ORIENTATION_PORTRAIT && !rotationPortrait) {
            fieldActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else if (config.orientation == Configuration.ORIENTATION_LANDSCAPE && rotationPortrait) {
            fieldActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    public static boolean getRotation(){
        //現在の画面向き(true=PORTRAIT)
        if(fieldActivity.getResources().getConfiguration().orientation==Configuration.ORIENTATION_PORTRAIT){
            return true;
        } else {
            return false;
        }
    }

    public static void setRotation(boolean rotation){
        rotationPortrait = rotation;
    }

    public static float getWidth(){
        return widthReal;
    }

    public static float getHeight(){
        return heightReal;
    }

    public static float[] getCurrentPosition(){
        return new float[]{tx,ty};
    }

    public static char getTouchFromType(){
        return touchFromType;
    }

    public static byte getTouchFromPoint(){
        return touchFromPoint;
    }

    public static boolean getMovingEffect(){
        return movingEffect;
    }

    public static void setTablePosition(float[][] tp){
        tablePosition = tp;
    }

    public static void setDeckPosition(float[][] dp){
        deckPosition = dp;
    }

    public static void setSuitsPosition(float[][] sp){
        suitsPosition = sp;
    }



}
