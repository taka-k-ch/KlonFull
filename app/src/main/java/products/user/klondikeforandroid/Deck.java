package products.user.klondikeforandroid;


import android.util.Log;

import java.util.ArrayList;

public class Deck implements Relocation {


    private ArrayList<Byte> deck = new ArrayList<>();
    private byte marker;
    private byte nextMarker;
    private boolean refresh;


    private Deck() {

    }

    private Deck(Deck d) {
        this.deck.addAll(d.deck);
//        for(byte num : d.deck){
//            this.deck.add(num);
//        }
        this.marker = d.marker;
        this.nextMarker = d.nextMarker;
        this.refresh = d.refresh;
    }


    public static Deck createInitialDeck() {
        return new Deck();
    }

    public Relocation createNewInstance() {
        return new Deck(this);
    }

    public byte getCard(byte i) {
        try {
            return deck.get(i);
        } catch (Exception e) {
            return 0;
        }
    }

    public byte getDeckSize() {
        return (byte) deck.size();
    }

    public ArrayList<Byte> getImmigrants(byte i) {
        ArrayList<Byte> al = new ArrayList<>();
        for (byte j = 0; j < deck.size(); j++) {
            if (deck.get(j) > 0) {
                //デッキで表側表示になっている最初の一枚だけ抽出
                al.add(deck.get(j));
                break;
            }
        }

        return al;
    }

    public boolean isNextDeckRemain() {
        for (byte i = nextMarker; i < deck.size(); i++) {
            try {
                if (deck.get(i) != 0) {
                    return true;
                }
            } catch (Exception e) {

            }
        }
        return false;
    }

    public boolean isDeckRemain() {
        for (byte i = 0; i < deck.size(); i++) {
            try {
                if (deck.get(i) != 0) {
                    return true;
                }
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    //デッキが移動先になることはないため、このオーバーライドメソッドが使われることはない
    public boolean immigration(Relocation from, byte fromPoint, byte toPoint) {
        return false;
    }

    public void departure(byte fromPoint, byte i) {
        if (deck.size() == 0) {
            Log.d("Deck", "departure:DeckArrayListの要素が0です");
        } else {
            Log.d("Deck", "Marker=" + marker);
            deck.set(marker, (byte) 0);
            marker++;
        }

        MainSurfaceView.setDeckList(this);
    }

    public Relocation arrival(ArrayList<Byte> fromList, byte toPoint, byte i) {
        return null;
    }

    public boolean deckOpen() {

        Log.d("Deck", "deckOpenメソッドにin");

        if (nextMarker == 0) {
            //山札が一周するタイミングでリロードを入れる（初回にも作動してしまうのは仕様）
            //リロード時に山札配列の中にある0要素を除去
            try {
                byte i = 0;
                do {
                    if (deck.get(i) == 0) {
                        deck.remove(i);
                    } else {
                        i++;
                    }
                } while (deck.size() > i);
            } catch (Exception e) {
                Log.d("Deck", "deckOpen:リロード時に例外が発生");
            }

            if (deck.size() == 0) {
                Log.d("Deck", "山札が空です");
                return false;
            }
        }

        //山札を開封する前に一旦全部裏返す
        for (byte i = 0; i < deck.size(); i++) {
            if (deck.get(i) > 0) {
                deck.set(i, (byte) (deck.get(i) * -1));
            }
        }


        //開けるところを記録
        marker = nextMarker;

        if (!refresh) {
            //markerがついてるところから山札配列を3つ開封
            for (byte i = 0; i < 3; i++) {
                if (marker + i >= deck.size()) {
                    refresh = true;
                    break;
                } else if (deck.get(marker + i) == 0) {
                    refresh = true;
                    break;
                } else {
                    deck.set(marker + i, (byte) (deck.get(marker + i) * -1));
                    nextMarker = (byte) (marker + i + 1);
                    if (marker + i + 1 >= deck.size()) {
                        refresh = true;
                    } else if (deck.get(marker + i) == 0) {
                        refresh = true;
                    }
                }
            }
        } else {
            refresh = false;
            nextMarker = 0;
        }

        return true;
    }

    public void setInitialDeckCard(byte initialTableCard[]) {
        for (byte i = 0; i < initialTableCard.length; i++) {
            deck.add(initialTableCard[i]);
        }
        MainSurfaceView.setDeckList(this);
    }


}
