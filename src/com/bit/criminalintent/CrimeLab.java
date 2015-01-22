package com.bit.criminalintent;

import java.util.ArrayList;
import java.util.UUID;

import android.content.Context;
import android.util.Log;

public class CrimeLab {//ģ�Ͳ�����࣬��Crime���һ������ ����ֻ��һ����̬�Ե���
	private static final String TAG="CrimeLab";
	private static final String FILENAME="Crimes.json";
	
	private ArrayList<Crime> mCrimes;//����Crime��List����
	private CriminalIntentJSONSerializer mSerializer;
	
	private static CrimeLab sCrimeLab; //�������÷�����̬�����
	private Context mAppContext;//���������Ĳ������Կ���activity
	
	private CrimeLab(Context appContex){//�����÷����������캯������ȡ������
		mAppContext=appContex;
		 mSerializer = new CriminalIntentJSONSerializer(mAppContext, FILENAME);
		//mCrimes=new ArrayList<Crime>();
		try{
			mCrimes=mSerializer.loadCrimes();
			
		}catch (Exception e){
			mCrimes=new ArrayList<Crime>();
			Log.e(TAG,"Error loading file:",e);
		}
		/*for(int i=0;i<100;i++){//Crime����ĳ�ʼ�������޸�
			Crime c =new Crime();
			c.setTitle("Crime #"+i);
			c.setSolved(i%2==0);
			mCrimes.add(c);
		}*/
	}
	public static CrimeLab get(Context c){//get�������ڷ���CrimeLab�������������������캯��
		if(sCrimeLab==null){
			sCrimeLab=new CrimeLab(c.getApplicationContext());
		}
		return sCrimeLab;
	}
	public ArrayList<Crime> getCrimes() {//������������
		return mCrimes;
	}
	
	public Crime getCrime(UUID id) {//����ID���������ص���Crime����
		for(Crime c : mCrimes){
			if(c.getId().equals(id)){
				return c;
			}
		}
		return null;
	}
	public void addCrime(Crime c){
		mCrimes.add(c);
	}
	public void deleteCrime(Crime c){
		mCrimes.remove(c);
	}
    public boolean saveCrimes() {
        try {
            mSerializer.saveCrimes(mCrimes);
            Log.d(TAG, "crimes saved to file");
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error saving crimes: " + e);
            return false;
        }
    }
	
	
}
