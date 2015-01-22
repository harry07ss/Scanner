package com.bit.criminalintent;

import java.util.ArrayList;
import java.util.UUID;

import android.content.Context;
import android.util.Log;

public class CrimeLab {//模型层对象类，是Crime类的一个集合 单例只有一个静态对的类
	private static final String TAG="CrimeLab";
	private static final String FILENAME="Crimes.json";
	
	private ArrayList<Crime> mCrimes;//定义Crime的List容器
	private CriminalIntentJSONSerializer mSerializer;
	
	private static CrimeLab sCrimeLab; //单例的用法，静态类对象
	private Context mAppContext;//利用上下文参数可以控制activity
	
	private CrimeLab(Context appContex){//单例用法：匿名构造函数，获取上下文
		mAppContext=appContex;
		 mSerializer = new CriminalIntentJSONSerializer(mAppContext, FILENAME);
		//mCrimes=new ArrayList<Crime>();
		try{
			mCrimes=mSerializer.loadCrimes();
			
		}catch (Exception e){
			mCrimes=new ArrayList<Crime>();
			Log.e(TAG,"Error loading file:",e);
		}
		/*for(int i=0;i<100;i++){//Crime对象的初始化，可修改
			Crime c =new Crime();
			c.setTitle("Crime #"+i);
			c.setSolved(i%2==0);
			mCrimes.add(c);
		}*/
	}
	public static CrimeLab get(Context c){//get方法用于返回CrimeLab单例，调用了匿名构造函数
		if(sCrimeLab==null){
			sCrimeLab=new CrimeLab(c.getApplicationContext());
		}
		return sCrimeLab;
	}
	public ArrayList<Crime> getCrimes() {//返回整个容器
		return mCrimes;
	}
	
	public Crime getCrime(UUID id) {//利用ID搜索，返回单个Crime对象
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
