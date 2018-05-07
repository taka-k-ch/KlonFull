package products.user.klondikeforandroid;


import java.util.ArrayList;

public interface Relocation {
	
	//�ړ��\���ǂ��������ۂɔ��肵�A�ړ��\�ł���Ύ��s�����v���\�b�h
    boolean immigration(Relocation r, byte fromPoint, byte toPoint);

	//�ړ���(from)����Ăяo���A�����Ŏw�肳�ꂽ�J�[�h��ArrayList�ŕԂ�
    ArrayList<Byte> getImmigrants(byte i);

	//(new���ꂽ)�ړ���Instance����A�ړ����邱�ƂƂȂ����J�[�h������
    void departure(byte fromPoint, byte i);

	//(new���ꂽ)�ړ���Instance�ɁA�ړ����邱�ƂƂȂ����J�[�h��ǉ�
    Relocation arrival(ArrayList<Byte> fromList, byte toPoint, byte i);

	//���i�߂�ۂɗ��p�B�V����Instance���擾����
    Relocation createNewInstance();



}
