package com.learningpod.android.activities;
//bbb
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

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
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
	

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// get user profile and pods from content cache
		UserProfileBean userProfileBean = ContentCacheStore.getContentCache().getLoggedInUserProfile();		
		//get list of pods. getting 
		pods  = ContentCacheStore.getContentCache().getPods();		
		setContentView(R.layout.home_screen);
		// create the view flipper
		mapFlipper = new ViewFlipper(this);
		LayoutParams mapParams = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		mapFlipper.setLayoutParams(mapParams);
		
		// get Inflater instance
		LayoutInflater inflater = getLayoutInflater();
		// inflate and create the Map Views
		View mapView1 = createMap1View();
		View mapView2 = createMap2View();
		View mapView3 = createMap3View();
		
		// add listeners to next and previous buttons
		mapView1.findViewById(R.id.btnmap1next).setOnClickListener(this);
		mapView2.findViewById(R.id.btnmap2next).setOnClickListener(this);
		mapView2.findViewById(R.id.btnmap2prev).setOnClickListener(this);
		mapView3.findViewById(R.id.btnmap3prev).setOnClickListener(this);
		
		// add views to the flipper
		mapFlipper.addView(mapView1,0);		
		mapFlipper.addView(mapView2,1);
		mapFlipper.addView(mapView3,2);
		setContentView(mapFlipper);
		
	}
	 
	
	
	
	private View createMap1View(){
		// get the user id
		String userId = ContentCacheStore.getContentCache().getLoggedInUserProfile().getId();
		// get Inflater instance
		LayoutInflater inflater = getLayoutInflater();
		View mapView1 = inflater.inflate(R.layout.mapview1, null);
		int numberOfQuestionsCompleted = 0;
		LearningpodDbHandler dbHandler = new LearningpodDbHandler(this);
		dbHandler.open();		
		
		// planet 1
		// get all views
		
		//Toast.makeText(getApplicationContext(), pods.size()+" checking size", Toast.LENGTH_LONG).show();
		for(int i=0;i<5;i++){
			PodBean pod1 = pods.get(0);
			numberOfQuestionsCompleted =  dbHandler.getUserProgressStatus(ContentCacheStore.getContentCache().getLoggedInUserProfile().getId(), pod1.getPodId());
			ImageButton btnPlanet1 = (ImageButton)mapView1.findViewById(imgBtnArray[i]);
			TextView txtNamePlanet1 = (TextView)mapView1.findViewById(txtPlntArry[i]);
			ImageView imgSpaceshipPlanet1 = (ImageView)mapView1.findViewById(imgViewPlntArry[i]);
			View progressBarRedPlanet1 = mapView1.findViewById(pbRedArry[i]);
			View progressBarBluePlanet1 = mapView1.findViewById(pbBlueArry[i]);
			
			// set the state 
			btnPlanet1.setOnClickListener(this);
			btnPlanet1.setTag("0");
			txtNamePlanet1.setText(pod1.getTitle());
			if(numberOfQuestionsCompleted!=0){
				if(numberOfQuestionsCompleted==pod1.getPodElements().size()){
					// change the image to the one with the flag
					imgSpaceshipPlanet1.setVisibility(View.VISIBLE);
					imgSpaceshipPlanet1.setBackgroundResource(R.drawable.spaceshipwithflag);
				}else{
					imgSpaceshipPlanet1.setVisibility(View.VISIBLE);
					((LinearLayout.LayoutParams)progressBarRedPlanet1.getLayoutParams()).weight = (float)(0.2*numberOfQuestionsCompleted);
					((LinearLayout.LayoutParams)progressBarBluePlanet1.getLayoutParams()).weight = (float)(1.0-(0.2*numberOfQuestionsCompleted));
					progressBarRedPlanet1.setVisibility(View.VISIBLE);
					progressBarBluePlanet1.setVisibility(View.VISIBLE);
				}
				
			}
		}
		
		dbHandler.close();
		return mapView1;
	}
	
	private View createMap2View(){
		LayoutInflater inflater = getLayoutInflater();
		View mapView2 = inflater.inflate(R.layout.mapview2, null);
		
		int numberOfQuestionsCompleted = 0;
		LearningpodDbHandler dbHandler = new LearningpodDbHandler(this);
		dbHandler.open();		
		
		// planet 1
		// get all views
		
		//Toast.makeText(getApplicationContext(), pods.size()+" checking size", Toast.LENGTH_LONG).show();
		for(int i=5;i<10;i++){
			PodBean pod1 = pods.get(0);
			numberOfQuestionsCompleted =  dbHandler.getUserProgressStatus(ContentCacheStore.getContentCache().getLoggedInUserProfile().getId(), pod1.getPodId());
			ImageButton btnPlanet1 = (ImageButton)mapView2.findViewById(imgBtnArray[i]);
			TextView txtNamePlanet1 = (TextView)mapView2.findViewById(txtPlntArry[i]);
			ImageView imgSpaceshipPlanet1 = (ImageView)mapView2.findViewById(imgViewPlntArry[i]);
			View progressBarRedPlanet1 = mapView2.findViewById(pbRedArry[i]);
			View progressBarBluePlanet1 = mapView2.findViewById(pbBlueArry[i]);
			
			// set the state 
			btnPlanet1.setOnClickListener(this);
			btnPlanet1.setTag("0");
			txtNamePlanet1.setText(pod1.getTitle());
			if(numberOfQuestionsCompleted!=0){
				if(numberOfQuestionsCompleted==pod1.getPodElements().size()){
					// change the image to the one with the flag
					imgSpaceshipPlanet1.setVisibility(View.VISIBLE);
					imgSpaceshipPlanet1.setBackgroundResource(R.drawable.spaceshipwithflag);
				}else{
					imgSpaceshipPlanet1.setVisibility(View.VISIBLE);
					((LinearLayout.LayoutParams)progressBarRedPlanet1.getLayoutParams()).weight = (float)(0.2*numberOfQuestionsCompleted);
					((LinearLayout.LayoutParams)progressBarBluePlanet1.getLayoutParams()).weight = (float)(1.0-(0.2*numberOfQuestionsCompleted));
					progressBarRedPlanet1.setVisibility(View.VISIBLE);
					progressBarBluePlanet1.setVisibility(View.VISIBLE);
				}
				
			}
		}
		
		dbHandler.close();
		return mapView2;
	}
	
	private View createMap3View(){
		LayoutInflater inflater = getLayoutInflater();
		View mapView3 = inflater.inflate(R.layout.mapview3, null);
		int numberOfQuestionsCompleted = 0;
		LearningpodDbHandler dbHandler = new LearningpodDbHandler(this);
		dbHandler.open();		
		
		// planet 1
		// get all views
		
		//Toast.makeText(getApplicationContext(), pods.size()+" checking size", Toast.LENGTH_LONG).show();
		for(int i=10;i<15;i++){
			PodBean pod1 = pods.get(0);
			numberOfQuestionsCompleted =  dbHandler.getUserProgressStatus(ContentCacheStore.getContentCache().getLoggedInUserProfile().getId(), pod1.getPodId());
			ImageButton btnPlanet1 = (ImageButton)mapView3.findViewById(imgBtnArray[i]);
			TextView txtNamePlanet1 = (TextView)mapView3.findViewById(txtPlntArry[i]);
			ImageView imgSpaceshipPlanet1 = (ImageView)mapView3.findViewById(imgViewPlntArry[i]);
			View progressBarRedPlanet1 = mapView3.findViewById(pbRedArry[i]);
			View progressBarBluePlanet1 = mapView3.findViewById(pbBlueArry[i]);
			
			// set the state 
			btnPlanet1.setOnClickListener(this);
			btnPlanet1.setTag("0");
			txtNamePlanet1.setText(pod1.getTitle());
			if(numberOfQuestionsCompleted!=0){
				if(numberOfQuestionsCompleted==pod1.getPodElements().size()){
					// change the image to the one with the flag
					imgSpaceshipPlanet1.setVisibility(View.VISIBLE);
					imgSpaceshipPlanet1.setBackgroundResource(R.drawable.spaceshipwithflag);
				}else{
					imgSpaceshipPlanet1.setVisibility(View.VISIBLE);
					((LinearLayout.LayoutParams)progressBarRedPlanet1.getLayoutParams()).weight = (float)(0.2*numberOfQuestionsCompleted);
					((LinearLayout.LayoutParams)progressBarBluePlanet1.getLayoutParams()).weight = (float)(1.0-(0.2*numberOfQuestionsCompleted));
					progressBarRedPlanet1.setVisibility(View.VISIBLE);
					progressBarBluePlanet1.setVisibility(View.VISIBLE);
				}
				
			}
		}
		
		dbHandler.close();
		return mapView3;
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
		if(v instanceof ImageButton){
			int selectedPlatentId = Integer.parseInt(v.getTag().toString());
			PodBean selectedPod = pods.get(selectedPlatentId);
			HashMap<String,Object> params = new HashMap<String,Object>();
			params.put("selectedPod",selectedPod);			
			new BackgroundAsyncTasks(MapActivity.this, params).execute(BackgroundTasks.LOAD_POD_QUESTIONS);
		}
		else if (v.getId()==R.id.btnmap1next || v.getId()==R.id.btnmap2next){
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
		 
		
	}


}