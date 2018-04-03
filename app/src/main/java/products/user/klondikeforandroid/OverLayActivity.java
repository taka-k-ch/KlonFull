package products.user.klondikeforandroid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class OverLayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_over_lay);

        View decor = this.getWindow().getDecorView();
        decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    public void onRetryButton(View view){
        while(MainSurfaceView.doUndo());
        finish();
    }

    public void onNewGameButton(View view){
        MainSurfaceView.initialize();
        finish();
    }

    public void onPortraitButton(View view) {
        FieldActivity.setRotation(true);
        finish();
        FieldActivity.decideRotation();
    }
    public void onLandScapeButton(View view) {
        FieldActivity.setRotation(false);
        finish();
        FieldActivity.decideRotation();
    }

    public void onCancelButton(View view) {
        finish();
    }
}
