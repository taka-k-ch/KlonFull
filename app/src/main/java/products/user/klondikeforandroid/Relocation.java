package products.user.klondikeforandroid;


import java.util.ArrayList;

public interface Relocation {
	
	//移動可能かどうかを実際に判定し、移動可能であれば実行する主要メソッド
    boolean immigration(Relocation r, byte fromPoint, byte toPoint);

	//移動元(from)から呼び出し、引数で指定されたカードをArrayListで返す
    ArrayList<Byte> getImmigrants(byte i);

	//(newされた)移動元Instanceから、移動することとなったカードを消去
    void departure(byte fromPoint, byte i);

	//(newされた)移動先Instanceに、移動することとなったカードを追加
    Relocation arrival(ArrayList<Byte> fromList, byte toPoint, byte i);

	//一手進める際に利用。新たなInstanceを取得する
    Relocation createNewInstance();



}
