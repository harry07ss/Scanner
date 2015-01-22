package com.bit.criminalintent;

import java.util.ArrayList;


import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.view.ActionMode;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class CrimeListFragment extends ListFragment {//控制器层，处理List列表视图和Crimes容器的fragment，继承自ListFragment
	private static final String TAG = "CrimeListFragment";
	private ArrayList<Crime> mCrimes;
	private ImageView mimageview;
	@Override
	public void onCreate(Bundle savedInstanceState) {//创建列表支配器
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		getActivity().setTitle(R.string.crime_title);
		mCrimes=CrimeLab.get(getActivity()).getCrimes();//获取单例
		
		/*ArrayAdapter<Crime> adapter=new ArrayAdapter<Crime>(//列表支配器 使用默认的ArrayAdapter类创建
				getActivity(),android.R.layout.simple_list_item_1,mCrimes);*/
		CrimeAdapter adapter= new CrimeAdapter(mCrimes);//使用新定义的CrimeAdapter创建
		setListAdapter(adapter);
		
	}
	@TargetApi(11)
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v =super.onCreateView(inflater, container, savedInstanceState);
		
		ListView listView =(ListView) v.findViewById(android.R.id.list);
		 if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
	            registerForContextMenu(listView);
	        } else {
	            listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

	            listView.setMultiChoiceModeListener(new MultiChoiceModeListener(){

					@Override
					public boolean onCreateActionMode(
							android.view.ActionMode mode, Menu menu) {
		                   MenuInflater inflater = mode.getMenuInflater();
		                    inflater.inflate(R.menu.crime_list_item_contex, menu);
		                    return true;
					}

					@Override
					public boolean onPrepareActionMode(
							android.view.ActionMode mode, Menu menu) {
						// TODO Auto-generated method stub
						return false;
					}

					@Override
					public boolean onActionItemClicked(
							android.view.ActionMode mode, MenuItem item) {
						  switch (item.getItemId()) {
	                        case R.id.menu_item_delete_crime:
	                            CrimeAdapter adapter = (CrimeAdapter)getListAdapter();
	                            CrimeLab crimeLab = CrimeLab.get(getActivity());
	                            for (int i = adapter.getCount() - 1; i >= 0; i--) {
	                                if (getListView().isItemChecked(i)) {
	                                    crimeLab.deleteCrime(adapter.getItem(i));
	                                }
	                            }
	                            mode.finish(); 
	                            adapter.notifyDataSetChanged();
	                            return true;
	                        default:
	                            return false;
	                    }
					}

					@Override
					public void onDestroyActionMode(android.view.ActionMode mode) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onItemCheckedStateChanged(
							android.view.ActionMode mode, int position,
							long id, boolean checked) {
						// TODO Auto-generated method stub
						
					}

	             
	            });
	        }
		return v;
	}

	@Override
	public void onListItemClick(ListView l,View v,int position,long id){//响应点击事件
		//Crime c= (Crime)(getListAdapter()).getItem(position);
		Crime c=((CrimeAdapter)getListAdapter()).getItem(position);
		Log.d(TAG, c.getTitle()+" was clicked");
		 //
		//Intent i =new Intent(getActivity(),CrimeActivity.class);
		Intent i =new Intent(getActivity(),CrimePagerActivity.class);
		i.putExtra(CrimeFragment.EXTRA_CRIME_ID, c.getId());
		startActivity(i);
	}
	
	@Override
	public void onResume(){
		super.onResume();
		((CrimeAdapter)getListAdapter()).notifyDataSetChanged();
	}
	
	
	private class CrimeAdapter extends ArrayAdapter<Crime>{//内部类重新定义列表视图和视图与Crime对象的关系，继承自ArrayAdapter
		public CrimeAdapter(ArrayList<Crime> crimes){ //构造函数与父类相同
			super(getActivity(), 0,crimes);
		}
		@Override
		public View getView(int position,View convertView,ViewGroup parent){//重写getview方法，修改返回的 视图
			if(convertView==null){
				convertView=getActivity().getLayoutInflater()
						.inflate(R.layout.list_item_scanner, null);//利用编写的xml布局
			}
			
			Crime c=getItem(position);
			
			//如何对应布局和Crime类
			TextView titleTV=(TextView) convertView.findViewById(R.id.crime_list_item_titleTextView);
			titleTV.setText(c.getTitle());
			TextView dataTV=(TextView) convertView.findViewById(R.id.crime_list_item_dateTextView);
			dataTV.setText(c.getDate().toString());
			CheckBox solvedCB=(CheckBox)(TextView) convertView.findViewById(R.id.crime_list_item_sCheckBox);
			solvedCB.setChecked(c.isSolved());
			mimageview=(ImageView) convertView.findViewById(R.id.scanner_list_item_ImageView);
			Photo p = c.getPhoto();
	        BitmapDrawable b = null;
	        if (p != null) {
	            String path = p.getFilename();
	            b = PictureUtils.getScaledDrawable(getActivity(), path);
	        }
	        mimageview.setImageDrawable(b);
			return convertView;
		}
		
		
	}


	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.fragment_crime_list, menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		
		
		switch (item.getItemId()){
		case R.id.menu_item_new_crime:
			Crime crime=new Crime();
			CrimeLab.get(getActivity()).addCrime(crime);
			Intent i =new Intent(getActivity(),CrimePagerActivity.class);
			i.putExtra(CrimeFragment.EXTRA_CRIME_ID, crime.getId());
			startActivityForResult(i,0);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		getActivity().getMenuInflater().inflate(R.menu.crime_list_item_contex, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();
        int position = info.position;
        CrimeAdapter adapter = (CrimeAdapter)getListAdapter();
        Crime crime = adapter.getItem(position);

        switch (item.getItemId()) {
            case R.id.menu_item_delete_crime:
                CrimeLab.get(getActivity()).deleteCrime(crime);
                adapter.notifyDataSetChanged();
                return true;
        }
        return super.onContextItemSelected(item);
	}
	@Override
	public void onStop() {
		
		super.onStop();
		  PictureUtils.cleanImageView(mimageview);
	}
	
	
	


    
}
