package products.user.klondikeforandroid;


import android.util.Log;

import java.util.ArrayList;

public class Table implements Relocation {

    @SuppressWarnings("unchecked")
    private ArrayList<Byte>[] tableArray = new ArrayList[]{
            new ArrayList<Byte>(),
            new ArrayList<Byte>(),
            new ArrayList<Byte>(),
            new ArrayList<Byte>(),
            new ArrayList<Byte>(),
            new ArrayList<Byte>(),
            new ArrayList<Byte>()
    };

    private Table(){

    }
	
	//一手進める際は、新しく作ったInstanceに現在の状況をコピーしてから編集
    private Table(Table table){
        byte i=0;
        for(ArrayList<Byte> al : table.tableArray){
            for(byte value : al){
                this.tableArray[i].add(value);
            }
            i++;
        }
    }

	//ゲームの初期化の際に利用
    public static Table createInitialTable(){
        return new Table();
    }
	
	//一手進める際に利用
	@Override
    public Relocation createNewInstance(){
        return new Table(this);
    }

	//列とIndexを引数として該当するカード番号を返す
    public byte getCard(byte i, byte j){
        try {
            return tableArray[i].get(j);
        } catch (Exception e){
            return 0;
        }
    }

	//移動元(from)から呼び出し、該当する列の表側表示になっているカードをArrayListで返す
	@Override
    public ArrayList<Byte> getImmigrants(byte fromPoint){
        ArrayList<Byte> al = new ArrayList<>();
        for(byte j=0; j<tableArray[fromPoint].size(); j++){
            if(tableArray[fromPoint].get(j) >= 0) {
                al.add(tableArray[fromPoint].get(j));
            }
        }

        return al;
    }

	//(newされた)移動元Instanceから、移動することとなったカードを消去。
	@Override
    public void departure(byte fromPoint, byte i){
        for(byte j=0; j<=i; j++){
            tableArray[fromPoint].remove(0);
        }
        if(tableArray[fromPoint].size()==0){
            Log.d("Table","departure:配列が空なのでOutOfBounds回避のため0を代入します");
            tableArray[fromPoint].add((byte)0);
        }
    	
    	//一手進める
        MainSurfaceView.setTableList(this);
    }

	//(newされた)移動先Instanceに、移動することとなったカードを追加。
	@Override
    public Relocation arrival(ArrayList<Byte> fromList, byte toPoint, byte i){
    	
    	//一手進める先のInstanceを取得
        Table nextTable = (Table) createNewInstance();

        //iの値=移動するカードの枚数。fromList=移動元で表になっている全カードリスト
    	//fromListには移動しないカードも含まれているため、iの値を使って移動元リストを最適化
    	//fromListには移動するカードのみ収録されている状態を作る
        try {
            do {
                fromList.remove(i + 1);
            } while (fromList.size()>i);
        } catch (Exception e){
        }

        //移動先配列の最前面に移動元カードの値を格納
        nextTable.tableArray[toPoint].addAll(0,fromList);

        return nextTable;
    }

	//移動可能かどうかを実際に判定し、移動可能であれば実行する主要メソッド
	@Override
    public boolean immigration(Relocation from, byte fromPoint, byte toPoint){

    	//移動元で表になっている全カードのリストを取得(移動しないカードも含まれる)
        ArrayList<Byte> fromList = from.getImmigrants(fromPoint);

    	//移動しないパターンのいくつかを最初にはじく
        if(fromList == null){
            return false;
        } else if(fromList.size()==0){
            return false;
        } else if(fromList.get(0) == 0){
            return false;
        } else if(tableArray[toPoint].get(0) % 13 == 1) {
            //移動先の表側表示カードの先頭が1の場合。1の上には何も乗せられないのでfalse
            return false;
        }

        //移動先の最前面カードの絶対値を割り出す。
        byte card_num = (byte) (((tableArray[toPoint].get(0)-1) % 13)+1);
    	

        if (tableArray[toPoint].get(0) >= 27) {	//移動先の一番上が赤の場合
        	
            //入力により指定された移動元の列の中から、移動可能なカードをサーチ
            for(byte i=0; i<fromList.size(); i++) {
                //例えば、移動先カードの値が28(ハート2)ならcard_num=2
                //移動元の列から黒色の1のカードを探す。
                if (fromList.get(i) == card_num-1 || fromList.get(i) == card_num+13-1) {
                    //移動可能カードを発見の場合

                    //テーブル→テーブル移動のケース
                    if(this==from){
                        Log.d("テーブル→テーブル", "newするインスタンスは1枚");
                        //移動先処理
                        Relocation nextInstance = arrival(fromList, toPoint, i);
                        nextInstance.departure(fromPoint,i);
                    } else {
                        //移動先処理
                        Relocation nextInstance = arrival(fromList, toPoint, i);
                    	//一手進める
                        MainSurfaceView.setTableList((Table)nextInstance);

                        //移動元のカードを削除
                        Relocation nextFrom = from.createNewInstance();
                        nextFrom.departure(fromPoint, i);
                    }
                    return true;
                }
            }

            return false;

        } else if (tableArray[toPoint].get(0) >= 1) {	//移動先の一番上が黒の場合
        	
            //入力により指定された移動元の列の中から、移動可能なカードをサーチ
            for(byte i=0; i<fromList.size(); i++) {
                //例えば、移動先カードの値が15(スペード2)ならcard_num=2
                //移動元の列から赤色の1のカードを探す。
                if (fromList.get(i) == card_num+26-1 || fromList.get(i) == card_num+39-1) {
                    //移動可能カードを発見の場合

                    //テーブル→テーブル移動のケース
                    if(this==from){
                        Log.d("テーブル→テーブル", "newするインスタンスは1枚");
                        //移動先処理
                        Relocation nextInstance = arrival(fromList, toPoint, i);
                        nextInstance.departure(fromPoint,i);
                    } else {
                        //移動先処理
                        Relocation nextInstance = arrival(fromList, toPoint, i);
                    	//一手進める
                        MainSurfaceView.setTableList((Table)nextInstance);

                        //移動元のカードを削除
                        Relocation nextFrom = from.createNewInstance();
                        nextFrom.departure(fromPoint, i);
                    }

                    return true;
                }
            }
            return false;

         } else if (tableArray[toPoint].get(0) == 0) {
            //移動先が空き地or裏側表示だった場合。13とその上物の札は全て移動可能


            //入力により指定された移動元の列の中から、移動可能なカードをサーチ
            for(byte i=0; i<fromList.size(); i++) {
                if (fromList.get(i) % 13 == 0) {

                    //テーブル→テーブル移動のケース
                    if(this==from){
                        //移動先処理
                        Relocation nextInstance = arrival(fromList, toPoint, i);
                        nextInstance.departure(fromPoint,i);
                    } else {
                        //移動先処理
                        Relocation nextInstance = arrival(fromList, toPoint, i);
                    	//一手進める
                        MainSurfaceView.setTableList((Table)nextInstance);

                        //移動元のカードを削除
                        Relocation nextFrom = from.createNewInstance();
                        nextFrom.departure(fromPoint, i);
                    }

                    return true;
                }
            }
        } else {
            return false;
        }
        return false;
    }

	//裏側表示になっているTableのカードをめくるだけのメソッド
    public boolean tableOpen(byte fromPoint){

    	//一手進める先のInstanceを取得
        Table nextTable = (Table) createNewInstance();

        if (tableArray[fromPoint].size() == 0) {
            //開くカードがないためtable開封できず
            return false;
        } else if (tableArray[fromPoint].get(0) >= 1) {
            //表側表示カードが残っているのでtable開封できない
            return false;
        }

        //最前面の裏側表示カードをリバース
        nextTable.tableArray[fromPoint].set(0,(byte)(tableArray[fromPoint].get(0) * -1));
    	
    	//一手進める
        MainSurfaceView.setTableList(nextTable);


        return true;
    }

	//初期化の際に利用。渡されたカード情報(配列)を初期状態として登録
    public void setInitialTableCard(byte initialTableCard[]){
        byte k=0;
        for(byte i=0; i<7; i++){
            for(byte j=0; j<=i; j++){
                tableArray[i].add(initialTableCard[k]);
                k++;
            }
        }

        //テーブルの最上位カードを開封
        for(ArrayList<Byte> al : tableArray){
            byte surfaceNum = al.get(0);
            surfaceNum *= -1;
            al.set(0,surfaceNum);
        }

    	//一手進める(実際のところ、これが一手目となっている)
        MainSurfaceView.setTableList(this);
    }




}
