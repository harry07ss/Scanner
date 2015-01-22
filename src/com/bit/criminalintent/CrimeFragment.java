package com.bit.criminalintent;

import java.util.Date;
import java.util.UUID;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
/**
 *控制器部分完成界面到数据的过程 
 *CrimeFragment 完成“创建Crime”界面 和创建对应Crime对象
 * @author zhili
 * @version 1.0.0
 */
public class CrimeFragment extends Fragment {
	public static final String EXTRA_CRIME_ID="com.bit.android.crime_id";
	private static final String DIALOG_DATE="date";
	private static final int REQUEST_DATE=0;
	private static final int REQUEST_PHOTO=1;
	private static final String TAG = "CrimeFragment";
	protected static final String DIALOG_IMAGE = "image";
	private Crime mCrime;
	private EditText mTitleField;
	private Button mDateButton;
	private CheckBox mSolvedCheckBox;
	private ImageButton mPhotoButton;
	private ImageView mPhotoView;
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		//UUID crimeId=(UUID)getActivity().getIntent().getSerializableExtra(EXTRA_CRIME_ID);
		UUID crimeId=(UUID)getArguments().getSerializable(EXTRA_CRIME_ID);
		mCrime=CrimeLab.get(getActivity()).getCrime(crimeId);
		setHasOptionsMenu(true);
	}
    public void updateDate() {
        mDateButton.setText(mCrime.getDate().toString());
    }
/**
 * 关联视图
 */

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle savedInstanceState) {
		View v=inflater.inflate(R.layout.fragment_crime,parent,false);
		
		if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB){
			if(NavUtils.getParentActivityName(getActivity())!=null){
				getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
			}
		}
		mTitleField=(EditText)v.findViewById(R.id.crime_title);
		mTitleField.setText(mCrime.getTitle());
		mTitleField.addTextChangedListener(new TextWatcher(){//监听的设置
			public void onTextChanged(
					CharSequence c,int start,int before,int count){
				mCrime.setTitle(c.toString());
			}
			
			public void beforeTextChanged(
					CharSequence c,int start,int count,int before){
				//space
			}
			
			public void afterTextChanged(Editable c){
				//space
			}
		});
		
		mDateButton=(Button)v.findViewById(R.id.crime_date);
		updateDate();
		//mDateButton.setEnabled(false);
		mDateButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				FragmentManager fm =getActivity().getSupportFragmentManager();
				//DatePickerFragment dialog =new DatePickerFragment();
				DatePickerFragment dialog=DatePickerFragment.newInstance(mCrime.getDate());
				dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
				dialog.show(fm, DIALOG_DATE);
				
			}
		});
		
		
		mSolvedCheckBox=(CheckBox)v.findViewById(R.id.crime_solved);
		mSolvedCheckBox.setChecked(mCrime.isSolved());
		mSolvedCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			public void  onCheckedChanged(CompoundButton buttonView,boolean isChecked){
				mCrime.setSolved(isChecked);
				}
		});
		
		mPhotoButton=(ImageButton)v.findViewById(R.id.crime_imageButton);
		mPhotoButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i =new Intent(getActivity(),CrimeCameraActivity.class);
				startActivityForResult(i, REQUEST_PHOTO);
				
			}
		});
		mPhotoView=(ImageView) v.findViewById(R.id.crime_imageView);
		mPhotoView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
                Photo p = mCrime.getPhoto();
                if (p == null) 
                    return;

                FragmentManager fm = getActivity()
                    .getSupportFragmentManager();
                String path = getActivity()
                    .getFileStreamPath(p.getFilename()).getAbsolutePath();
                ImageFragment.createInstance(path)
                    .show(fm, DIALOG_IMAGE);		
			}
		});
		
		PackageManager pm =getActivity().getPackageManager();
		boolean hasACamera=pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)||(Camera.getNumberOfCameras()>0);
		if(!hasACamera){
			mPhotoButton.setEnabled(false);
		}
		return v;
	}
	
    private void showPhoto() {
        // (re)set the image button's image based on our photo
        Photo p = mCrime.getPhoto();
        BitmapDrawable b = null;
        if (p != null) {
            String path = getActivity()
                .getFileStreamPath(p.getFilename()).getAbsolutePath();
            b = PictureUtils.getScaledDrawable(getActivity(), path);
        }
        mPhotoView.setImageDrawable(b);
    }
    
    @Override
    public void onStart() {
        super.onStart();
        showPhoto();
    }
    
    @Override
    public void onStop() {
        super.onStop();
        PictureUtils.cleanImageView(mPhotoView);
    }
	/**
	 * 用于替代Fragment的构造方法，根据CrimeLab内的Id号创建对应的Fragment
	 * @param crimeId 
	 * @return CrimeFragment
	 * 在对应的activity里调用
	 */
	public static CrimeFragment newInstance(UUID crimeId){
		Bundle args =new Bundle();
		args.putSerializable(EXTRA_CRIME_ID, crimeId);
		
		CrimeFragment fragment=new CrimeFragment();
		fragment.setArguments(args);
		
		return fragment;
	}
	
	@Override
	 public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;
        if (requestCode == REQUEST_DATE) {
            Date date = (Date)data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            updateDate();
        }
        if(requestCode==REQUEST_PHOTO){
        	String filename=data.getStringExtra(CrimeCameraFragment.EXTRA_PHOY_FILENAME);
        	if(filename!=null){
        		Log.i(TAG,"filename:"+filename);
        		
        		Photo p=new Photo(filename);
        		mCrime.setPhoto(p);
        		
        	}
        }
    }
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()){
		case android.R.id.home:
			if(NavUtils.getParentActivityName(getActivity())!=null){
				NavUtils.navigateUpFromSameTask(getActivity());
			}
			return true;
		default:
		return super.onOptionsItemSelected(item);
		}
	}
	@Override
	public void onPause() {
		super.onPause();
		CrimeLab.get(getActivity()).saveCrimes();
	}
	
	

}
