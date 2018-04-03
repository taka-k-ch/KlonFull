package products.user.klondikeforandroid;


import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class Undo {

    private static ArrayList<Undo> undoList;

    private static short nowTable; //InitialCreateで+1され
    private static short nowDeck;  //開幕時には0となる
    private static short nowSuits;

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
/*
        if(index>=undoList.size()) {
            Log.d("Undo","Undo.getUndoValue:超簡易スレッドセーフ");
            try {
                    Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
*/

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

    public static void deleteLastInstance(){
        undoList.remove(undoList.size()-1);
    }

    public static void plusNowTable() {
        nowTable++;
    }

    public static void plusNowDeck() {
        nowDeck++;
    }

    public static void plusNowSuits() {
        nowSuits++;
    }

    public static void minusNowTable() {
        nowTable--;
    }

    public static void minusNowDeck() {
        nowDeck--;
    }

    public static void minusNowSuits() {
        nowSuits--;
    }

    public static void initializeUndoList(){
        undoList = new ArrayList<>();
        nowTable = -1;
        nowDeck = -1;
        nowSuits = -1;
    }

}
