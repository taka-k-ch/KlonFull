package products.user.klondikeforandroid;


import java.util.ArrayList;

public interface Relocation {

    boolean immigration(Relocation r, byte fromPoint, byte toPoint);

    ArrayList<Byte> getImmigrants(byte i);

    void departure(byte fromPoint, byte i);

    Relocation arrival(ArrayList<Byte> fromList, byte toPoint, byte i);

    Relocation createNewInstance();



}
