package products.user.klondikeforandroid;


import java.util.LinkedList;
import java.util.Random;

public class InitialCreation {

    private static byte initialTable[];
    private static byte initialDeck[];
    private static Random rnd;
    private static LinkedList<Byte>stockCard;

    public static void initialCreation() {
        initialTable = new byte[28];
        initialDeck = new byte[24];
        rnd = new Random();
        stockCard = new LinkedList<>();
        for(byte i = 0; i < 52; i++){
            stockCard.add(i);
        }
        tableCreation();
        deckCreation();
        suitsCreation();
    }

    public static void tableCreation() {

        //乱数でカードを生成しテーブルに格納
        for(byte i = 0; i < 28; i++){
            byte n;
            n = (byte) (rnd.nextInt(stockCard.size()));
            initialTable[i] = (byte) ((stockCard.get(n)+1)*-1);
            stockCard.remove(n);
        }

        Table table = Table.createInitialTable();
        table.setInitialTableCard(initialTable);

    }

    public static void deckCreation() {


        //乱数でカードを生成し山札として格納
        for(byte i = 0; i < 24; i++){
            byte n;
            n = (byte) (rnd.nextInt(stockCard.size()));
            initialDeck[i] = (byte) ((stockCard.get(n)+1)*-1);
            stockCard.remove(n);
        }


        Deck deck = Deck.createInitialDeck();
        deck.setInitialDeckCard(initialDeck);

    }

    public static void suitsCreation() {
        Suits.createInitialSuits();
    }
}
