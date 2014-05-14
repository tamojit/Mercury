package com.learningpod.android.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class LearningpodSqliteHelper extends SQLiteOpenHelper{
	
	  public static  String DATABASE_NAME = "lpProddb";
	  public static final String USER_PROGRESS_TRACKER_TABLE = "UserProgressTracker";
	  
	  private static final int DATABASE_VERSION = 1;

	  // Table creation statements
	  public static final String USER_PROGRESS_TRACKER_QUERY = "create table UserProgressTracker ( UserId Text Not Null, PodId Text Not Null, QuestionId Text, ChoiceId Text, ChoiceStatus INTEGER);";
	 
	  public LearningpodSqliteHelper(Context context) {
	    super(context, DATABASE_NAME, null, DATABASE_VERSION);
	  }

	  @Override
	  public void onCreate(SQLiteDatabase database) {
	    database.execSQL(USER_PROGRESS_TRACKER_QUERY);	   
	  }

	  @Override
	  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	    Log.w(LearningpodSqliteHelper.class.getName(),
	        "Upgrading database from version " + oldVersion + " to "
	            + newVersion + ", which will destroy all old data");
	    db.execSQL("DROP TABLE IF EXISTS AdapterUserCredentials");	    
	    onCreate(db);
	  }
	  
	 public String getDatabaseName(){
		 return DATABASE_NAME;
	 }
}
