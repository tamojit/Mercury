package com.learningpod.android.activities;

import static com.learningpod.android.activities.Constant.FIRST_COLUMN;
import static com.learningpod.android.activities.Constant.SECOND_COLUMN;
import static com.learningpod.android.activities.Constant.THIRD_COLUMN;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.NodeList;

import com.learningpod.android.BaseActivity;
import com.learningpod.android.R;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Paint;
import android.os.Bundle;
import android.provider.DocumentsContract.Document;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

public class MultiColumnActivity extends BaseActivity 
{
	private ArrayList<HashMap<String,String>> list;
	LinearLayout wordListContentView;
	ListviewAdapter adapter;
	//int noun[]={R.array.nounwordlist1,R.array.nounwordlist2};
	int i=0;
	int verb=0;
	int vocabulary=0;
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        modifyActionBar();
        wordListContentView=(LinearLayout)findViewById(R.id.listContentView);
        createWordList();
    }    
   // String []description1={"Tier1","Tier2","Tier3"};
    String []ListHeading = {"TIER II NOUNS","TIER II VERBS","TIER III VOCABULARY"};
    
	private void modifyActionBar() {
		// get the action bar
		ActionBar actionBar = getActionBar();
		// getActionBar().setTitle(goToMapView.getText().toString());
		getActionBar().setIcon(R.drawable.arrow);
		actionBar.setCustomView(R.layout.custm);
		TextView goToMapButton = (TextView) actionBar.getCustomView()
				.findViewById(R.id.title);
		goToMapButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
		TextView podTitle = (TextView) actionBar.getCustomView().findViewById(
				R.id.podname);
		podTitle.setText("WORDLIST");
		podTitle.setPadding(100, 5, 0, 0);
	
		podTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);

		//	podTitle.setTypeface(headerFont);
		goToMapButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MultiColumnActivity.this,MapActivityBeforeLogin.class);
				//intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				//MultiColumnActivity.this.finish();
			}
		});

		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM
				| ActionBar.DISPLAY_SHOW_HOME);

	}
   
    private void createWordList() {
    	for(int i=0;i<3;i++)
    	{
		LayoutInflater inflater=getLayoutInflater();
		View wordListView=inflater.inflate(R.layout.word_list, null);
		ListView wordList=(ListView)wordListView.findViewById(R.id.wordList);
		
		TextView ListHeadings=(TextView)wordListView.findViewById(R.id.wordListText);
		ListHeadings.setText(ListHeading[i]);
		
		populateList(i);
		adapter =new ListviewAdapter(MultiColumnActivity.this, list);
		wordList.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, list.size()*62));
		wordList.setAdapter(adapter);
		wordListContentView.addView(wordListView);
    	}
	}

	private void populateList(int k) {
    	
		switch(k){
		
		case 0:
	
         	list = new ArrayList<HashMap<String,String>>();
    		Resources res = getResources();
			String []nounitems = res.getStringArray(R.array.nounwordlist);
    		int j= nounitems.length;   
    	    while(i<j){
		    HashMap<String,String> temp = new HashMap<String,String>();
			temp.put(FIRST_COLUMN,nounitems[i]);
			temp.put(SECOND_COLUMN,nounitems[i+1]);
			temp.put(THIRD_COLUMN, nounitems[i+2]);
			i=i+3;
	      	list.add(temp);
    		}
    	          break;
    	
    	case 1:
    	
    		list = new ArrayList<HashMap<String,String>>();
    		Resources res1 = getResources();
			String []verbitems = res1.getStringArray(R.array.verbwordlist);
    		int z= verbitems.length;   
    	    while(verb<z){
		    HashMap<String,String> temp = new HashMap<String,String>();
			temp.put(FIRST_COLUMN,verbitems[verb]);
			temp.put(SECOND_COLUMN,verbitems[verb+1]);
			temp.put(THIRD_COLUMN, verbitems[verb+2]);
			verb=verb+3;
	      	list.add(temp);
    		}
    	          break;
		
    	
    	
    	case 2:
        	
    		list = new ArrayList<HashMap<String,String>>();
    		Resources res2 = getResources();
			String []vocabularyitems = res2.getStringArray(R.array.vocabularywordlist);
    		int h= vocabularyitems.length;   
    	    while(vocabulary<h){
		    HashMap<String,String> temp = new HashMap<String,String>();
			temp.put(FIRST_COLUMN,vocabularyitems[vocabulary]);
			temp.put(SECOND_COLUMN,vocabularyitems[vocabulary+1]);
			temp.put(THIRD_COLUMN, vocabularyitems[vocabulary+2]);
			vocabulary=vocabulary+3;
	      	list.add(temp);
    		}
    	          break;
    	
		}
	}
}