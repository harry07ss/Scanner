package com.bit.criminalintent;

import java.util.Date;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;
public class Crime {
	//模型层对象的类
    private static final String JSON_ID = "id";
    private static final String JSON_TITLE = "title";
    private static final String JSON_DATE = "date";
    private static final String JSON_SOLVED = "solved";
    private static final String JSON_PHOTO = "photo";
    private static final String DEFAULT_TITLE = "default title";
    private UUID mId;//ID好
	private String mTitle;//标题
	private Date mDate;//日期
	private boolean mSolved;//一个是否解决的选框
	private String Tag;
    private Photo mPhoto;
	public Crime(){
		mId=UUID.randomUUID();
		mDate=new Date();
		mTitle=DEFAULT_TITLE;
	}
	
    public Crime(JSONObject json) throws JSONException {//读取
        mId = UUID.fromString(json.getString(JSON_ID));
        mTitle = json.getString(JSON_TITLE);
        mSolved = json.getBoolean(JSON_SOLVED);
        mDate = new Date(json.getLong(JSON_DATE));
        if (json.has(JSON_PHOTO))
            mPhoto = new Photo(json.getJSONObject(JSON_PHOTO));
    }

    public JSONObject toJSON() throws JSONException {//保存
        JSONObject json = new JSONObject();
        json.put(JSON_ID, mId.toString());
        json.put(JSON_TITLE, mTitle);
        json.put(JSON_DATE, mDate.getTime());
        json.put(JSON_SOLVED, mSolved);
        if (mPhoto != null)
            json.put(JSON_PHOTO, mPhoto.toJSON());
        return json;
    }
	@Override
	public String toString(){
		return mTitle;
	}
	public Date getDate() {
		return mDate;
	}

	public void setDate(Date mDate) {
		this.mDate = mDate;
	}

	public boolean isSolved() {
		return mSolved;
	}

	public void setSolved(boolean mSolved) {
		this.mSolved = mSolved;
	}

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String mTilte) {
		this.mTitle = mTilte;
	}

	public UUID getId() {
		return mId;
	}
	public Photo getPhoto() {
		return mPhoto;
	}

	public void setPhoto(Photo photo) {
		mPhoto = photo;
	}
}
