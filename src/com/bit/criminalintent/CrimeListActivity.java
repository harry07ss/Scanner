package com.bit.criminalintent;

import android.support.v4.app.Fragment;


public class CrimeListActivity extends SingleFragmentActivity {//�ڶ���activityֱ���ó�����

	@Override
	protected Fragment createFragment() {//��д���󷽷�����
		return new CrimeListFragment();//Fragment�ľ������ݼ�CrimeListFragment.java
	}

}
