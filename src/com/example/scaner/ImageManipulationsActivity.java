package com.example.scaner;

import com.bit.criminalintent.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.MatOfPoint;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import org.opencv.utils.Converters;

import com.example.scaner.ColorBlobDetector;

//import android.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings.System;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Toast;

@SuppressLint("ClickableViewAccessibility")
public class ImageManipulationsActivity extends Activity implements
		CvCameraViewListener2, OnTouchListener// main activity
{
	/********************* 类成员变量 **************************/
	private static final String TAG = "OCVSample::Activity";

	private Sample3NativeCapturer mOpenCvCameraView;
	private List<Camera.Size> mResolutionList;

	private Mat mIntermediateMat;

	private Mat mSub;
	private MenuItem[] mResolutionMenuItems;
	private MenuItem[] mEffectMenuItems;
	private SubMenu mResolutionMenu;

	private ColorBlobDetector mDetector;

	Rect mpaper = new Rect();

	/********************* openCV回调 **************************/
	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this)
	{
		@Override
		public void onManagerConnected(int status)
		{
			switch (status)
			{
			case LoaderCallbackInterface.SUCCESS:
				{
					Log.i(TAG, "OpenCV loaded successfully");

					mOpenCvCameraView.enableView();
					mOpenCvCameraView.setOnTouchListener(ImageManipulationsActivity.this);

				}
				break;
			default:
				{
					super.onManagerConnected(status);
				}
				break;
			}
		}
	};

	/*********************
	 * onCreate 控件初始化 不能用来初始化openCV的变量类型
	 **************************/

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		Log.i(TAG, "called onCreate");
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		setContentView(R.layout.activity_image_manipulations);

		mOpenCvCameraView = (Sample3NativeCapturer) findViewById(R.id.image_manipulations_activity_surface_view);
		// mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
		//
		mOpenCvCameraView.setCvCameraViewListener(this);

	}

	/** Called when the activity is first created. */
	/********************* onPause **************************/
	@Override
	public void onPause()
	{
		super.onPause();
		if (mOpenCvCameraView != null)
			mOpenCvCameraView.disableView();
	}

	@Override
	/*********************onResume **************************/
	public void onResume()
	{
		super.onResume();

		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);

	}

	/********************* onDestroy **************************/
	public void onDestroy()
	{
		super.onDestroy();
		if (mOpenCvCameraView != null)
			mOpenCvCameraView.disableView();
	}

	/*********************
	 * onCameraViewStarted opencv的CameraView开始，只运行一次 opencv类型初始化
	 **************************/
	@Override
	public void onCameraViewStarted(int width, int height)
	{
		Log.i("Scar", "onResume in");
		mIntermediateMat = new Mat();

		Log.i("Scar", "onResume out");

	}

	/*********************
	 * onCameraViewStopped opencv的CameraView结束，只运行一次，与onCameraViewStarted对应
	 * opencv类型释放
	 **************************/
	@Override
	public void onCameraViewStopped()
	{
		if (mIntermediateMat != null)
			mIntermediateMat.release();
		if (mSub != null)
			mSub.release();
		mIntermediateMat = null;

	}

	/*********************
	 * onCameraFrame opencv的CameraView过程，每有一祯图像调用一次 图像处理得到纸张矩形
	 * 输入inputFrame，输出mpaper opencv类型释放
	 **************************/
	@Override
	public Mat onCameraFrame(CvCameraViewFrame inputFrame)
	{

		mDetector = new ColorBlobDetector();

		Mat mImag = inputFrame.rgba();
		Mat gray = new Mat();

		Imgproc.cvtColor(mImag, gray, Imgproc.COLOR_BGR2GRAY);

		// 设置提取颜色
		int cols = mImag.cols();
		int rows = mImag.rows();
		int sizeofrect = 10;

		Rect touchedRect = new Rect();

		touchedRect.x = cols / 2 - sizeofrect;
		touchedRect.y = rows / 2 - sizeofrect;

		touchedRect.width = sizeofrect * 2;
		touchedRect.height = sizeofrect * 2;
		// Core.rectangle(mImag,touchedRect.br(),touchedRect.tl(), new
		// Scalar(125, 125, 255, 255), 2);
		Mat touchedRegionRgba = mImag.submat(touchedRect);

		Mat touchedRegionHsv = new Mat();
		Imgproc.cvtColor(touchedRegionRgba, touchedRegionHsv, Imgproc.COLOR_RGB2HSV_FULL);
		Scalar mBlobColorHsv = Core.sumElems(touchedRegionHsv);
		int pointCount = touchedRect.width * touchedRect.height;

		for (int i = 0; i < mBlobColorHsv.val.length; i++)
			mBlobColorHsv.val[i] /= pointCount;
		// 提取颜色轮廓
		mDetector.setHsvColor(mBlobColorHsv);
		mDetector.setColorRadius(new Scalar(25, 50, 100, 0));
		// Log.i("Scar", mBlobColorHsv.toString());
		mDetector.process(mImag);
		List<MatOfPoint> contours = mDetector.getContours();
		MatOfPoint tmpcon = new MatOfPoint();
		// 筛选轮廓得到mpaper
		if (contours.size() == 1)
		{

			tmpcon = contours.get(0);
			// mMOP2f2.convertTo(tmpMOP2f1, CvType.CV_32S);
			if (Imgproc.contourArea(tmpcon) > 70000)
				;
			{
				mpaper = Imgproc.boundingRect(tmpcon);
				Core.rectangle(mImag, mpaper.br(), mpaper.tl(), new Scalar(0, 255, 100, 255), 10);
				
			}

		}

		// Imgproc.drawContours(mImag, contours, -1, new Scalar(0, 0, 255,
		// 255));
		mSub = gray.submat(mpaper);
		gray.release();
		
		return mImag;
	}

	/*********************
	 * onTouch 触屏事件处理 主要做了保存图像，对mpaper裁剪，处理裁剪图片，保存裁剪图片
	 **/

	@Override
	public boolean onTouch(View v, MotionEvent event)
	{
		Log.i(TAG, "onTouch event");
		// 设置保存路径
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		String currentDateandTime = sdf.format(new Date());
		String fileName = Environment.getExternalStorageDirectory().getPath() + "/pictures/" + currentDateandTime + ".jpg";
		String fileName2 = Environment.getExternalStorageDirectory().getPath() + "/pictures/" + currentDateandTime + "sub.jpg";

		if (mpaper != null)
		{

			// 处理图像
			MinMaxLocResult maxminValue = Core.minMaxLoc(mSub);

			Core.convertScaleAbs(mSub, mSub, 255.0 / (maxminValue.maxVal - maxminValue.minVal), -255.0 * maxminValue.minVal / (maxminValue.maxVal - maxminValue.minVal));

			// 保存图像
			fileName = Environment.getExternalStorageDirectory().getPath() + "/pictures/" + sdf.format(new Date()) + ".jpg";

			Highgui.imwrite(fileName2, mSub);
			Toast.makeText(this, fileName2 + " saved ", Toast.LENGTH_SHORT).show();
			// Highgui.imwrite(fileName, mImag);
		}
		return false;
	}

	/*********************
	 * onCreateOptionsMenu 创建菜单栏
	 **/
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		mResolutionMenu = menu.addSubMenu("Mode");// 你的那一栏
		mEffectMenuItems = new MenuItem[2];// 两个按钮
		mEffectMenuItems[0] = mResolutionMenu.add(1, 1, Menu.NONE, "普通模式");// 两个按键的名字
		mEffectMenuItems[1] = mResolutionMenu.add(1, 2, Menu.NONE, "语音模式");

		mResolutionMenu = menu.addSubMenu("Resolution");

		mResolutionList = mOpenCvCameraView.getResolutionList();
		mResolutionMenuItems = new MenuItem[mResolutionList.size()];

		ListIterator<Camera.Size> resolutionItr = mResolutionList.listIterator();
		int idx = 0;
		while (resolutionItr.hasNext())
		{
			Camera.Size element = resolutionItr.next();
			mResolutionMenuItems[idx] = mResolutionMenu.add(2, idx, Menu.NONE, Integer.valueOf(element.width).toString() + "x" + Integer.valueOf(element.height).toString());
			idx++;
		}

		return true;
	}

	/****** 按键处理函数 ***/
	public boolean onOptionsItemSelected(MenuItem item)
	{
		Log.i(TAG, "called onOptionsItemSelected; selected item: " + item);
		if (item.getGroupId() == 1)
		{
			int id1 = item.getItemId();
			if (id1 == 1)// 按下了普通模式
			{
				Toast.makeText(this, "普通模式", Toast.LENGTH_SHORT).show();
				// TODO 你的代码
			}
			if (id1 == 2)// 按下了语音模式
			{
				// TODO 你的代码
				Toast.makeText(this, "语音模式", Toast.LENGTH_SHORT).show();
			}

		}
		else if (item.getGroupId() == 2)
		{
			int id2 = item.getItemId();
			Camera.Size resolution = mResolutionList.get(id2);
			mOpenCvCameraView.setResolution(resolution);
			resolution = mOpenCvCameraView.getResolution();
			String caption = Integer.valueOf(resolution.width).toString() + "x" + Integer.valueOf(resolution.height).toString();
			Toast.makeText(this, caption, Toast.LENGTH_SHORT).show();
		}

		return true;
	}
}
