package com.bit.criminalintent;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
/**
 * 单Fragment的Activity抽象类，用来代替原来的Activity
 * @author zhili
 *
 */
public abstract class SingleFragmentActivity extends FragmentActivity {
    protected abstract Fragment createFragment();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        
        FragmentManager fm =getSupportFragmentManager(); //获得管理器
        Fragment fragment=fm.findFragmentById(R.id.fragmentContainer);//绑定到对应的容器中
        
        if(fragment==null){//一个fragment事务
        	fragment=createFragment();
        	fm.beginTransaction().add(R.id.fragmentContainer, fragment).commit();
        }
    }


}
