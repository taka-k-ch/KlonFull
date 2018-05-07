package products.user.klondikeforandroid;


import java.util.ArrayList;

public class Suits implements Relocation {


    private byte suits[] = new byte[4];

    private Suits(){

    }
	
	//一手進める際は、新しく作ったInstanceに現在の状況をコピーしてから編集
    private Suits(Suits s) {
        this.suits = s.suits.clone();
    }

	//一手進める際に利用
	@Override
    public Relocation createNewInstance(){
        return new Suits(this);
    }

	//Indexを引数として該当するカード番号を返す
    public byte getCard(byte i){
        try {
            return suits[i];
        } catch (Exception e){
            return 0;
        }
    }


	//Suitsが移動元になることはないため、このオーバーライドメソッドが使われることはない
	@Override
    public ArrayList<Byte> getImmigrants(byte i){
        return null;
    }

	//Suitsが移動元になることはないため、このオーバーライドメソッドが使われることはない
	@Override
    public void departure(byte fromPoint, byte i) {
    }

	//(newされた)移動先Instanceに、移動することとなったカードを追加
	@Override
    public Relocation arrival(ArrayList<Byte> fromList, byte toPoint, byte i){
    	
    	//一手進める先のInstanceを取得
        Suits nextSuits = (Suits) createNewInstance();

        //移動先配列に移動元カードの値を格納
        nextSuits.suits[toPoint] = i;

        return nextSuits;
    }

	//移動可能かどうかを実際に判定し、移動可能であれば実行する主要メソッド
    public boolean immigration(Relocation from, byte fromPoint, byte toPoint){

    	//移動元で表になっている全カードのリストを取得(移動しないカードも含まれる)
        ArrayList<Byte> fromList = from.getImmigrants(fromPoint);

        //初期チェック（移動失敗パターン）
        if(fromList.size()==0){
            //移動元にカードがないため移動失敗
            return false;
        } else if (fromList.get(0) <= 0) {
            //移動元に表側表示カードがないため移動失敗
            return false;
        }

        //入力列の場札の最前面カードのスートを割り出す。suit_valueが0ならスペード～3ならハート
        byte suit_value = (byte) ((fromList.get(0)-1) / 13);

        //スート合わせは完了しているので、あとはカードの数字をチェックして移動可否を判断
        //場札の最前面カードの絶対値が組札の絶対値+1なら移動可能
        if (((fromList.get(0)-1) % 13) == ((suits[suit_value]-1) % 13)+1) {

            //移動先処理
            Relocation nextInstance = arrival(fromList, suit_value, fromList.get(0));
        	//一手進める
            MainSurfaceView.setSuitsList((Suits)nextInstance);

            //移動元のカードを削除
            Relocation nextFrom = from.createNewInstance();
            nextFrom.departure(fromPoint, (byte)0);


            return true;
        }

        return false;
    }

	//初期化
    public static void createInitialSuits(){
        Suits suits = new Suits();
        for(byte i=0; i<4; i++){
            suits.suits[i]=0;
        }
    	
    	//一手進める(実際のところ、これが一手目となっている)
        MainSurfaceView.setSuitsList(suits);
    }

}
