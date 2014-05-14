package com.learningpod.androind.listeners;

import java.util.List;

import com.learningpod.android.R;
import com.learningpod.android.activities.PodQuestionActivity;

import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ChoiceSelectListner implements OnClickListener{

	private PodQuestionActivity activity;
	private boolean isButtonClicked;
	public ChoiceSelectListner(PodQuestionActivity activity, boolean isButtonClicked){
		this.activity = activity;
		this.isButtonClicked = isButtonClicked;
	}
	
	@Override
	public void onClick(View v) {
		// if the screen is in explanation mode, don't allow any action
		if(activity.isCurrentScreenForExplanation()){
			return;
		} 
		// enable the submit button
		((Button)activity.findViewById(R.id.btnsubmitnext)).setEnabled(true);
		
		if(!isButtonClicked){ // listener has been invoked by clicking on the full view
			List<View> choiceViewList = activity.getChoiceViews();
			for(int idx=0;idx<choiceViewList.size();idx++){
				
				View choiceView = choiceViewList.get(idx);
				if(v.getId()==idx){					
					choiceView.setBackgroundResource(R.drawable.choice_selected);
					activity.setCurrentSelectedChoiceIndex(idx);
					if(choiceView.getTag()==Integer.valueOf(1)){
						activity.setCurrentSelectedChoiceCorrect(true);
						// put this in the map
						//activity.getQuesToCorrectChoiceMap().put(activity.getCurrentQuestionIndex(), idx);
					}else{
						activity.setCurrentSelectedChoiceCorrect(false);
					}
				}else{
					choiceView.setBackgroundResource(R.drawable.choice_not_selected);
					if(choiceView.getTag()==Integer.valueOf(1)){
						// put this in the map
						//activity.getQuesToCorrectChoiceMap().put(activity.getCurrentQuestionIndex(), idx);
					}
				}
			}
		}
		else { // listener has been invoked by clicking on the button 
			// go through each button in the list
			
		}
	
	}

}
