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

    private Table(Table table){
        byte i=0;
        for(ArrayList<Byte> al : table.tableArray){
            for(byte value : al){
                this.tableArray[i].add(value);
            }
            i++;
        }

    }

    public static Table createInitialTable(){
        return new Table();
    }

    public Relocation createNewInstance(){
        return new Table(this);
    }

    public byte getCard(byte i, byte j){
        try {
            return tableArray[i].get(j);
        } catch (Exception e){
            return 0;
        }
    }

    public ArrayList<Byte> getImmigrants(byte fromPoint){
        ArrayList<Byte> al = new ArrayList<>();
        for(byte j=0; j<tableArray[fromPoint].size(); j++){
            if(tableArray[fromPoint].get(j) >= 0) {
                al.add(tableArray[fromPoint].get(j));
            }
        }


        return al;
    }

    public void departure(byte fromPoint, byte i){
        for(byte j=0; j<=i; j++){
            tableArray[fromPoint].remove(0);
        }
        if(tableArray[fromPoint].size()==0){
            Log.d("Table","departure:配列が空なのでOutOfBounds回避のため0を代入します");
            tableArray[fromPoint].add((byte)0);
        }
        MainSurfaceView.setTableList(this);
    }

    public Relocation arrival(ArrayList<Byte> fromList, byte toPoint, byte i){
        Table nextTable = (Table) createNewInstance();

        //iの値を使って何枚のカードを移動するのか把握し、移動元リストを最適化
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

    public boolean immigration(Relocation from, byte fromPoint, byte toPoint){

        ArrayList<Byte> fromList = from.getImmigrants(fromPoint);

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

    public boolean tableOpen(byte fromPoint){

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
        MainSurfaceView.setTableList(nextTable);


        return true;
    }

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

        MainSurfaceView.setTableList(this);
    }




}
