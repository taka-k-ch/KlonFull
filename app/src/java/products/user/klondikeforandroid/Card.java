package products.user.klondikeforandroid;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Card {

    private static int originalWidth;
    private static int originalHeight;

    private static int width;
    private static int height;

    private static Resources resources;
    private static Bitmap[] cardBit = new Bitmap[54];


    public static Bitmap getCard(int i) {
        if (i < 0) {
            i = 0;
        }
        return cardBit[i];
    }

    public static int[] getCardSize() {
        return new int[]{width, height};
    }

    public static void setCardSize() {
        if (FieldActivity.getRotation()) {
            //Portrait時のCardSize
//            width = (int) (FieldActivity.getWidth() / 6.4);
//            height = (int) (width * 1.5);

            //縦置きポートレート用カードサイズ
//            height = (int)(FieldActivity.getHeight() / 8);
//            width = (int) (height/3*2);

            //横置きポートレート用カードサイズ
            width = (int) (FieldActivity.getWidth() / 7.7);
            height = (int) (width*1.5);
        } else {
            //Landscape時のCardSize
            width = (int) (FieldActivity.getWidth() / 10);
            height = (int) (width * 1.5);
//            height = (int)(FieldActivity.getHeight() / );
//            width = (int) (height/3*2);
        }

        resources = MainSurfaceView.resources;
        Bitmap img;

        originalWidth = BitmapFactory.decodeResource(resources, R.drawable.z02).getWidth();
        originalHeight = BitmapFactory.decodeResource(resources, R.drawable.z02).getHeight();


        //------画像設定------//

        //裏表紙
        img = BitmapFactory.decodeResource(resources, R.drawable.z02);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[0] = Bitmap.createBitmap(img);

        //スペード
        img = BitmapFactory.decodeResource(resources, R.drawable.s01);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[1] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.s02);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[2] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.s03);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[3] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.s04);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[4] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.s05);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[5] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.s06);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[6] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.s07);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[7] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.s08);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[8] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.s09);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[9] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.s10);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[10] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.s11);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[11] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.s12);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[12] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.s13);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[13] = Bitmap.createBitmap(img);

        //クローバー
        img = BitmapFactory.decodeResource(resources, R.drawable.c01);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[14] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.c02);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[15] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.c03);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[16] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.c04);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[17] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.c05);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[18] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.c06);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[19] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.c07);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[20] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.c08);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[21] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.c09);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[22] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.c10);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[23] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.c11);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[24] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.c12);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[25] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.c13);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[26] = Bitmap.createBitmap(img);

        //ダイヤ
        img = BitmapFactory.decodeResource(resources, R.drawable.d01);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[27] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.d02);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[28] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.d03);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[29] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.d04);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[30] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.d05);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[31] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.d06);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[32] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.d07);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[33] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.d08);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[34] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.d09);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[35] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.d10);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[36] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.d11);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[37] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.d12);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[38] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.d13);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[39] = Bitmap.createBitmap(img);

        //ハート
        img = BitmapFactory.decodeResource(resources, R.drawable.h01);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[40] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.h02);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[41] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.h03);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[42] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.h04);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[43] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.h05);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[44] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.h06);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[45] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.h07);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[46] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.h08);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[47] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.h09);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[48] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.h10);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[49] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.h11);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[50] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.h12);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[51] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.h13);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[52] = Bitmap.createBitmap(img);

        //枠線画像
        img = BitmapFactory.decodeResource(resources, R.drawable.waku);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[53] = Bitmap.createBitmap(img);

    }
/*
    static {
        resources = MainSurfaceView.resources;
        Bitmap img;

        originalWidth = BitmapFactory.decodeResource(resources, R.drawable.z02).getWidth();
        originalHeight = BitmapFactory.decodeResource(resources, R.drawable.z02).getHeight();


        //------画像設定------//

        //裏表紙
        img = BitmapFactory.decodeResource(resources, R.drawable.z02);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[0] = Bitmap.createBitmap(img);

        //スペード
        img = BitmapFactory.decodeResource(resources, R.drawable.s01);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[1] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.s02);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[2] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.s03);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[3] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.s04);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[4] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.s05);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[5] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.s06);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[6] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.s07);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[7] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.s08);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[8] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.s09);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[9] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.s10);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[10] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.s11);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[11] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.s12);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[12] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.s13);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[13] = Bitmap.createBitmap(img);

        //クローバー
        img = BitmapFactory.decodeResource(resources, R.drawable.c01);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[14] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.c02);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[15] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.c03);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[16] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.c04);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[17] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.c05);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[18] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.c06);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[19] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.c07);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[20] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.c08);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[21] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.c09);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[22] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.c10);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[23] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.c11);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[24] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.c12);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[25] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.c13);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[26] = Bitmap.createBitmap(img);

        //ダイヤ
        img = BitmapFactory.decodeResource(resources, R.drawable.d01);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[27] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.d02);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[28] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.d03);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[29] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.d04);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[30] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.d05);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[31] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.d06);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[32] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.d07);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[33] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.d08);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[34] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.d09);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[35] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.d10);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[36] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.d11);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[37] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.d12);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[38] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.d13);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[39] = Bitmap.createBitmap(img);

        //ハート
        img = BitmapFactory.decodeResource(resources, R.drawable.h01);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[40] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.h02);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[41] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.h03);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[42] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.h04);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[43] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.h05);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[44] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.h06);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[45] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.h07);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[46] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.h08);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[47] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.h09);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[48] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.h10);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[49] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.h11);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[50] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.h12);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[51] = Bitmap.createBitmap(img);

        img = BitmapFactory.decodeResource(resources, R.drawable.h13);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[52] = Bitmap.createBitmap(img);

        //枠線画像
        img = BitmapFactory.decodeResource(resources, R.drawable.waku);
        img = Bitmap.createScaledBitmap(img, width, height, false);
        cardBit[53] = Bitmap.createBitmap(img);
    }
    */
}
