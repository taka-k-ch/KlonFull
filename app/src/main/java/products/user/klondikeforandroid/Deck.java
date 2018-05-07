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

	//一手進める際は、新しく作ったInstanceに現在の状況をコピーしてから編集
    private Deck(Deck d) {
        this.deck.addAll(d.deck);
//        for(byte num : d.deck){
//            this.deck.add(num);
//        }
        this.marker = d.marker;
        this.nextMarker = d.nextMarker;
        this.refresh = d.refresh;
    }


	//ゲームの初期化の際に利用
    public static Deck createInitialDeck() {
        return new Deck();
    }

	//一手進める際に利用
	@Override
    public Relocation createNewInstance() {
        return new Deck(this);
    }

	//Indexを引数として該当するカード番号を返す
    public byte getCard(byte i) {
        try {
            return deck.get(i);
        } catch (Exception e) {
            return 0;
        }
    }

	//山札の残数を取得
    public byte getDeckSize() {
        return (byte) deck.size();
    }

	//移動元(from)から呼び出し、Deckの最上位表側表示カードをArrayListで返す
	@Override
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
	@Override
    public boolean immigration(Relocation from, byte fromPoint, byte toPoint) {
        return false;
    }

	//newされた移動元Instanceから、移動することとなったカードを消去。
	@Override
    public void departure(byte fromPoint, byte i) {
        if (deck.size() == 0) {
            Log.d("Deck", "departure:DeckArrayListの要素が0です");
        } else {
            Log.d("Deck", "Marker=" + marker);
        	//現在山札において最上位となっているIndex(marker)のカードを0にする
        	//(0のカードは、山札が一周するタイミング(リロード時)にリストから排除される)
            deck.set(marker, (byte) 0);
        	//最上位カードの位置を1つ後ろにずらす
            marker++;
        }

    	//一手進める
        MainSurfaceView.setDeckList(this);
    }

	//デッキが移動先になることはないため、このオーバーライドメソッドが使われることはない
	@Override
    public Relocation arrival(ArrayList<Byte> fromList, byte toPoint, byte i) {
        return null;
    }

	//Deck開封メソッド
    public boolean deckOpen() {
        Log.d("Deck", "deckOpenメソッドにin");

        if (nextMarker == 0) {
            //山札が一周するタイミングでリロードを入れる（初回にも作動してしまうのは仕様）
            //リロード時にDeckリストの中にある0要素を除去
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
                Log.e("Deck", "deckOpen:リロード時に例外が発生"+e);
            }

        	//山札が空の場合にこのメソッドの出番はないので、開封失敗としてreturn false
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

        //山札にめくるところが残されていない場合、refresh=trueとなる。
        //refresh=trueの場合、Deckが一周したことを示すため、次回山札開封時には山札画像のみ表示される
        if (!refresh) {
        	
            //markerがついてるところから山札Listを1枚づつ開封
            for (byte i = 0; i < 3; i++) {
            	
                if (marker + i >= deck.size()) {
	            	//markerが山札の総数より向こう側に行ってしまった場合=リロードへ
                	refresh = true;
                    break;
                } else if (deck.get(marker + i) == 0) {
                	//次に表になるカードが0の場合=リロードへ(実際にはここは踏まないかも)
                    refresh = true;
                    break;
                } else {
                	//if節にひっかからなければ、markerのところを1枚開封
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
        	//山札画像の表示が完了。次からまた通常通り山札の周回が再開
            refresh = false;
            nextMarker = 0;
        }

        return true;
    }

	//初期化の際に利用。渡されたカード情報(配列)を初期状態として登録
    public void setInitialDeckCard(byte initialTableCard[]) {
        for (byte i = 0; i < initialTableCard.length; i++) {
            deck.add(initialTableCard[i]);
        }
    	
    	//一手進める(実際のところ、これが一手目となっている)
        MainSurfaceView.setDeckList(this);
    }


}
