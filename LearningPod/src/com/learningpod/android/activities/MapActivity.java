package com.learningpod.android.activities;

import java.util.HashMap;
import java.util.List;

import com.learningpod.android.BackgroundAsyncTasks;
import com.learningpod.android.BackgroundTasks;
import com.learningpod.android.BaseActivity;
import com.learningpod.android.ContentCacheStore;
import com.learningpod.android.R;
import com.learningpod.android.beans.UserProfileBean;
import com.learningpod.android.beans.pods.PodBean;
import com.learningpod.android.db.LearningpodDbHandler;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class MapActivity extends BaseActivity implements OnClickListener{
	
	
	private float lastX;
	private ViewFlipper mapFlipper;
	private List<PodBean> pods = null;
	int[] imgBtnArray={R.id.planet1,R.id.planet2,R.id.planet3,R.id.planet4,R.id.planet5,R.id.planet6,R.id.planet7,R.id.planet8,R.id.planet9,R.id.planet10,R.id.planet11,R.id.planet12,R.id.planet13,R.id.planet14,R.id.planet15};
	int[] txtPlntArry={R.id.planet1name,R.id.planet2name,R.id.planet3name,R.id.planet4name,R.id.planet5name,R.id.planet6name,R.id.planet7name,R.id.planet8name,R.id.planet9name,R.id.planet10name,R.id.planet11name,R.id.planet12name,R.id.planet13name,R.id.planet14name,R.id.planet15name,};
	int[] imgViewPlntArry={R.id.planet1ss,R.id.planet2ss,R.id.planet3ss,R.id.planet4ss,R.id.planet5ss,R.id.planet6ss,R.id.planet7ss,R.id.planet8ss,R.id.planet9ss,R.id.planet10ss,R.id.planet11ss,R.id.planet12ss,R.id.planet13ss,R.id.planet14ss,R.id.planet15ss};
	int[] pbRedArry={R.id.planet1pbred,R.id.planet2pbred,R.id.planet3pbred,R.id.planet4pbred,R.id.planet5pbred,R.id.planet6pbred,R.id.planet7pbred,R.id.planet8pbred,R.id.planet9pbred,R.id.planet10pbred,R.id.planet11pbred,R.id.planet12pbred,R.id.planet13pbred,R.id.planet14pbred,R.id.planet15pbred};
	int[] pbBlueArry={R.id.planet1pbblue,R.id.planet2pbblue,R.id.planet3pbblue,R.id.planet4pbblue,R.id.planet5pbblue,R.id.planet6pbblue,R.id.planet7pbblue,R.id.planet8pbblue,R.id.planet9pbblue,R.id.planet10pbblue,R.id.planet11pbblue,R.id.planet12pbblue,R.id.planet13pbblue,R.id.planet14pbblue,R.id.planet15pbblue};
	private Typeface headerFont;
	private boolean isSmallerScreen;
	

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		headerFont = Typeface.createFromAsset(getAssets(),
				"fonts/PaytoneOne.ttf");
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		isSmallerScreen = false;
		if(metrics.heightPixels<=800){
			isSmallerScreen = true;
		}
		// get user profile and pods from content cache
		UserProfileBean userProfileBean = ContentCacheStore.getContentCache().getLoggedInUserProfile();		
		modifyActionBar(userProfileBean.getName());
		//get list of pods. getting 
		pods  = ContentCacheStore.getContentCache().getPods();		
		setContentView(R.layout.maplayout);
		// create the view flipper
		mapFlipper = (ViewFlipper)findViewById(R.id.mapFlipper1);
		
		
		// get Inflater instance
		LayoutInflater inflater = getLayoutInflater();
		// inflate and create the Map Views
		View mapView1 = createMapView(R.layout.mapview1,0);
		View mapView2 = createMapView(R.layout.mapview2,5);
		View mapView3 = createMapView(R.layout.mapview3,10);
		
		// add listeners to next and previous buttons
		mapView1.findViewById(R.id.btnmap1next).setOnClickListener(this);
		mapView2.findViewById(R.id.btnmap2next).setOnClickListener(this);
		mapView2.findViewById(R.id.btnmap2prev).setOnClickListener(this);
		mapView3.findViewById(R.id.btnmap3prev).setOnClickListener(this);
		
		((Button)mapView1.findViewById(R.id.btnmap1next)).setTypeface(headerFont);
		((Button)mapView2.findViewById(R.id.btnmap2next)).setTypeface(headerFont);
		((Button)mapView2.findViewById(R.id.btnmap2prev)).setTypeface(headerFont);
		((Button)mapView3.findViewById(R.id.btnmap3prev)).setTypeface(headerFont);
		
		findViewById(R.id.wordlist).setOnClickListener(this);
		
		
		// add views to the flipper
		mapFlipper.addView(mapView1,0);		
		mapFlipper.addView(mapView2,1);
		mapFlipper.addView(mapView3,2);
		
		
	}
	 

	private void modifyActionBar(String strUserName){
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setCustomView(R.layout.map_screen_custom_bar);
		TextView userName = (TextView)actionBar.getCustomView().findViewById(R.id.mapscreenusername);
		userName.setText("Hi, " + strUserName);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
	}
	
	
	private View createMapView(int mapResourceId, int startIndex){	
		
		// get Inflater instance
		LayoutInflater inflater = getLayoutInflater();
		View mapView = inflater.inflate(mapResourceId, null);
		int numberOfQuestionsCompleted = 0;
		LearningpodDbHandler dbHandler = new LearningpodDbHandler(this);
		dbHandler.open();		
		
		
		// get all views		
		for(int i=startIndex;i<(startIndex+5);i++){
			PodBean pod = pods.get(i);
			numberOfQuestionsCompleted =  dbHandler.getUserProgressStatus(ContentCacheStore.getContentCache().getLoggedInUserProfile().getId(), pod.getPodId());
			ImageButton btnPlanet = (ImageButton)mapView.findViewById(imgBtnArray[i]);
			TextView txtNamePlanet = (TextView)mapView.findViewById(txtPlntArry[i]);
			txtNamePlanet.setTypeface(headerFont);
			if(isSmallerScreen){
				txtNamePlanet.setTextSize(14);
			}
			txtNamePlanet.setOnClickListener(this);
			ImageView imgSpaceshipPlanet1 = (ImageView)mapView.findViewById(imgViewPlntArry[i]);
			View progressBarRedPlanet1 = mapView.findViewById(pbRedArry[i]);
			View progressBarBluePlanet1 = mapView.findViewById(pbBlueArry[i]);
			
			// set the state 
			btnPlanet.setOnClickListener(this);
			btnPlanet.setTag(new Integer(i).toString());
			txtNamePlanet.setTag(new Integer(i).toString());
			txtNamePlanet.setText(pod.getTitle());
			if(numberOfQuestionsCompleted!=0){
				if(numberOfQuestionsCompleted==pod.getPodElements().size()){
					// change the image to the one with the flag
					imgSpaceshipPlanet1.setVisibility(View.VISIBLE);
					imgSpaceshipPlanet1.setBackgroundResource(R.drawable.spaceshipwithflag);
				}else{
					// show the spaceship with the progress bar
					imgSpaceshipPlanet1.setVisibility(View.VISIBLE);
					((LinearLayout.LayoutParams)progressBarRedPlanet1.getLayoutParams()).weight = (float)(0.2*numberOfQuestionsCompleted);
					((LinearLayout.LayoutParams)progressBarBluePlanet1.getLayoutParams()).weight = (float)(1.0-(0.2*numberOfQuestionsCompleted));
					progressBarRedPlanet1.setVisibility(View.VISIBLE);
					progressBarBluePlanet1.setVisibility(View.VISIBLE);
				}				
			}
		}
		
		dbHandler.close();
		return mapView;
	}
	
	
	// Method to handle touch event like left to right swap and right to left swap
    public boolean onTouchEvent(MotionEvent touchevent) 
    {
                 switch (touchevent.getAction())
                 {
                        // when user first touches the screen to swap
                         case MotionEvent.ACTION_DOWN: 
                         {
                             lastX = touchevent.getX();
                             break;
                        }
                         case MotionEvent.ACTION_UP: 
                         {
                             float currentX = touchevent.getX();
                             
                             // if left to right swipe on screen
                             if (lastX < currentX) 
                             {
                                  // If no more View/Child to flip
                                 if (mapFlipper.getDisplayedChild() == 0)
                                     break;
                                 
                                 // set the required Animation type to mapFlipper
                                 // The Next screen will come in form Left and current Screen will go OUT from Right 
                                 mapFlipper.setInAnimation(this, R.anim.in_from_left);
                                 mapFlipper.setOutAnimation(this, R.anim.out_to_right);
                                 // Show the previous Screen
                                 mapFlipper.showPrevious();
                             }
                             
                             // if right to left swipe on screen
                             if (lastX > currentX)
                             {
                                 if (mapFlipper.getDisplayedChild() == 2)
                                     break;
                                 // set the required Animation type to mapFlipper
                                 // The Next screen will come in form Right and current Screen will go OUT from Left 
                                 mapFlipper.setInAnimation(this, R.anim.in_from_right);
                                 mapFlipper.setOutAnimation(this, R.anim.out_to_left);
                                 // Show The next Screen
                                 mapFlipper.showNext();
                             }
                             break;
                         }
                 }
                 return false;
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		 if (v.getId()==R.id.btnmap1next || v.getId()==R.id.btnmap2next){
			 // set the required Animation type to mapFlipper
            // The Next screen will come in form Right and current Screen will go OUT from Left 
            mapFlipper.setInAnimation(this, R.anim.in_from_right);
            mapFlipper.setOutAnimation(this, R.anim.out_to_left);
            // Show The Previous Screen
            mapFlipper.showNext();
		}
		else if (v.getId()==R.id.btnmap2prev || v.getId()==R.id.btnmap3prev){
			            
            // set the required Animation type to mapFlipper
            // The Next screen will come in form Left and current Screen will go OUT from Right 
            mapFlipper.setInAnimation(this, R.anim.in_from_left);
            mapFlipper.setOutAnimation(this, R.anim.out_to_right);
            // Show the previous Screen
            mapFlipper.showPrevious();
		}
		else if (v.getId()==R.id.wordlist ){
            
	           // set the required Animation type to mapFlipper
	           // The Next screen will come in form Left and current Screen will go OUT from Right 
			Intent i = new Intent(this, WordListActivity.class);
			startActivity(i); 
			}	
		
		else if(v instanceof ImageButton){
			int selectedPlatentId = Integer.parseInt(v.getTag().toString());
			/*	if(!(selectedPlatentId==0||selectedPlatentId==1||selectedPlatentId==2||selectedPlatentId==3 || selectedPlatentId==4 || selectedPlatentId==5 || selectedPlatentId==10 )){
				return;
			}*/
			PodBean selectedPod = pods.get(selectedPlatentId);
			HashMap<String,Object> params = new HashMap<String,Object>();
			params.put("selectedPod",selectedPod);			
			new BackgroundAsyncTasks(MapActivity.this, params).execute(BackgroundTasks.LOAD_POD_QUESTIONS);
		}
		else if(v instanceof TextView){
			int selectedPlatentId = Integer.parseInt(v.getTag().toString());
			/*if(!(selectedPlatentId==0||selectedPlatentId==1||selectedPlatentId==2||selectedPlatentId==3 || selectedPlatentId==4 || selectedPlatentId==5 || selectedPlatentId==10 )){
				return;
			}*/
			PodBean selectedPod = pods.get(selectedPlatentId);
			HashMap<String,Object> params = new HashMap<String,Object>();
			params.put("selectedPod",selectedPod);			
			new BackgroundAsyncTasks(MapActivity.this, params).execute(BackgroundTasks.LOAD_POD_QUESTIONS);
		}
	}


}