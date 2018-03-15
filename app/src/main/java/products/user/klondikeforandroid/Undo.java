package products.user.klondikeforandroid;


import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class Undo {

    private static ArrayList<Undo> undoList = new ArrayList<>();

    private static short nowTable = -1; //InitialCreateで+1され
    private static short nowDeck = -1;  //開幕時には0となる
    private static short nowSuits = -1;

    private short table;
    private short deck;
    private short suits;

    private Undo() {
        this.table = nowTable;
        this.deck = nowDeck;
        this.suits = nowSuits;
        undoList.add(this);
        Log.d("Undo","インスタンス格納情報 : t="+table+" / d="+deck+" / s="+suits);
    }

    public static Undo createNewInstance(int index) {
        try {
            while (undoList.size() > index) {
                undoList.remove(index);
            }
        } catch (Exception e) {

        }

        return new Undo();
    }

    public static short[] getUndoValue(int index) {
        if(index>=undoList.size()) {
            Log.d("Undo","Undo.getUndoValue:超簡易スレッドセーフ");
            try {
//                while (index < undoList.size()) {
                    Thread.sleep(100);
//                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

//        Log.d("getUndoValue情報","t="+undoList.get(index).table+" / d="+undoList.get(index).deck+" / s="+undoList.get(index).suits);

        return new short[]{
                undoList.get(index).table,
                undoList.get(index).deck,
                undoList.get(index).suits
        };
    }

    public static short getUndoSize(){
        return (short) undoList.size();
    }

    public static void setNowTable() {
        nowTable++;
    }

    public static void setNowDeck() {
        nowDeck++;
    }

    public static void setNowSuits() {
        nowSuits++;
    }


}
