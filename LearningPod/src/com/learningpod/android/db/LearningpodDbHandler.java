package com.learningpod.android.db;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.learningpod.android.beans.UserProgressInfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class LearningpodDbHandler {

	private LearningpodSqliteHelper dbHelper;
	private SQLiteDatabase database;
	private Context context;
	
	public LearningpodDbHandler(Context context){
		dbHelper = new LearningpodSqliteHelper(context);
		this.context = context;
	}
	
	public static boolean doesDatabaseExist(Context context) {
	    File dbFile=context.getDatabasePath(LearningpodSqliteHelper.DATABASE_NAME);
	    return dbFile.exists();
	}
	
	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}
	
	public void deleteAllDataFromDB(){
		database.delete(LearningpodSqliteHelper.USER_PROGRESS_TRACKER_TABLE, null, null);
	}
	public void close() {
		dbHelper.close();
	}
	
	public void saveUserProgressInfo(UserProgressInfo progressInfo){		
		
		ContentValues values = new ContentValues();
		values.put("UserId", progressInfo.getUserId());
		values.put("PodId", progressInfo.getPodId());
		values.put("QuestionId",progressInfo.getQuestionId());
		values.put("ChoiceId",progressInfo.getChoiceId());
		if(progressInfo.isChoiceCorrect()){
			values.put("ChoiceStatus",1);
		}
		else{
			values.put("ChoiceStatus",0);
		}
		database.insert(dbHelper.USER_PROGRESS_TRACKER_TABLE, null, values);
	}
	
	public int getUserProgressStatus(String userId, String podId){
		Cursor cursor = database.rawQuery("select count(*) from UserProgressTracker where UserId ='" +userId + "' and PodId = '"  +podId + "'" , null);
		cursor.moveToFirst();		
		return cursor.getInt(0);
	}
	
	public List<UserProgressInfo> getUserProgressDetails(String userId, String podId){
		Cursor cursor = database.rawQuery("select * from UserProgressTracker where UserId ='" +userId + "' and PodId = '"  +podId + "'" , null);
		List<UserProgressInfo> userProgressList = new ArrayList<UserProgressInfo>();
		if(cursor.moveToFirst()){
			while(true){
				UserProgressInfo progress = new UserProgressInfo();
				progress.setPodId(podId);
				progress.setUserId(userId);
				progress.setQuestionId(cursor.getString(2));
				progress.setChoiceId(cursor.getString(3));
				progress.setChoiceCorrect(cursor.getInt(4)==1 ? true:false);
				userProgressList.add(progress);
				if(!cursor.moveToNext()){break;}
			}
		}
		return userProgressList;
	}
	
}
