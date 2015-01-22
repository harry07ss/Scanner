package com.bit.criminalintent;

import android.support.v4.app.Fragment;


public class CrimeListActivity extends SingleFragmentActivity {//第二个activity直接用抽象类

	@Override
	protected Fragment createFragment() {//重写抽象方法即可
		return new CrimeListFragment();//Fragment的具体内容见CrimeListFragment.java
	}

}
