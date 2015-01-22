package com.bit.criminalintent;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
/**
 * ��Fragment��Activity�����࣬��������ԭ����Activity
 * @author zhili
 *
 */
public abstract class SingleFragmentActivity extends FragmentActivity {
    protected abstract Fragment createFragment();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        
        FragmentManager fm =getSupportFragmentManager(); //��ù�����
        Fragment fragment=fm.findFragmentById(R.id.fragmentContainer);//�󶨵���Ӧ��������
        
        if(fragment==null){//һ��fragment����
        	fragment=createFragment();
        	fm.beginTransaction().add(R.id.fragmentContainer, fragment).commit();
        }
    }


}
