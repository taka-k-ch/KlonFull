package products.user.klondikeforandroid;


import java.util.Random;

public class InitialCreation {

    private static boolean cardUsage[] = new boolean[52];
    private static byte initialTable[] = new byte[28];
    private static byte initialDeck[] = new byte[24];
    private static Random rnd = new Random();


    public static void initialCreation(){

        tableCreation();
        deckCreation();
        suitsCreation();
    }

    public static void tableCreation() {

        //乱数でカードを生成しテーブルに格納
        for(byte i=0; i<28; i++) {
            byte n;
            do {
                //カードが特定される
                n = (byte)(rnd.nextInt(52));	//全札種類数
                //カードかぶり回避
                if (cardUsage[n] == false) {
                    initialTable[i] = (byte) ((n+1)*-1);
                    cardUsage[n] = true;
                }
            } while (initialTable[i] != (byte)((n+1)*-1));
        }

        Table table = Table.createInitialTable();
        table.setInitialTableCard(initialTable);

    }

    public static void deckCreation() {


        //乱数でカードを生成し山札として格納
        for(byte i=0; i<24; i++) {	//山札枚数(24)=全枚数52-テーブル展開枚数28
            byte n;
            do {
                //カードが特定される
                n = (byte)(rnd.nextInt(52));	//
                //カードかぶり回避
                if (cardUsage[n] == false) {
                    initialDeck[i] = (byte) ((n+1)*-1);
                    cardUsage[n] = true;
                }
            } while (initialDeck[i] != (byte) ((n+1)*-1));
        }

        Deck deck = Deck.createInitialDeck();
        deck.setInitialDeckCard(initialDeck);

    }

    public static void suitsCreation(){
        Suits.createInitialSuits();
    }
}
