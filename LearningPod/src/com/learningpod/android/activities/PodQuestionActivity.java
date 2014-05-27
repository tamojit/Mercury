package com.learningpod.android.activities;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.learningpod.android.BaseActivity;
import com.learningpod.android.ContentCacheStore;
import com.learningpod.android.R;
import com.learningpod.android.R.layout;
import com.learningpod.android.beans.UserProgressInfo;
import com.learningpod.android.beans.explanations.ExplanationBean;
import com.learningpod.android.beans.pods.PodBean;
import com.learningpod.android.beans.pods.PodQuestionBean;
import com.learningpod.android.beans.questions.QuestionBean;
import com.learningpod.android.beans.questions.QuestionChoiceBean;
import com.learningpod.android.db.LearningpodDbHandler;
import com.learningpod.androind.listeners.ChoiceSelectListner;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ActionBar;
import android.app.Dialog;


public class PodQuestionActivity extends BaseActivity {

	private ArrayList<QuestionBean> questions;
	private ArrayList<ArrayList<ExplanationBean>> explanations;
	private PodBean selectedPod;
	private int currentQuestionIndex = 0;
	private List<View> choiceViewList = new ArrayList<View>();
	private List<View> progressDotList = new ArrayList<View>();
	private HashMap<Integer, Integer> quesToCorrectAnswerMap = new HashMap<Integer, Integer>();
	private String[] choiceSeqArr = new String[] { "A.", "B.", "C.", "D." };
	private boolean isCurrentScreenForExplanation = false;
	private int currentSelectedChoiceIndex = -1;
	private boolean isCurrentSelectedChoiceCorrect = false;
	private boolean isThisPodComplete = false;
	// this will hold the user progress once user has completed the pod
	private List<UserProgressInfo> userProgressCompleted = null;
	private boolean isBackButtonPressed = false;
	private Typeface font = null;
	private Typeface headerFont = null;
	 
    private String email;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private Button btnBack;
	private String putinmailaddress;
	private String teacheremail;
	
	final Context context = this;

	
	private String uid;
	private int percentage;
	private boolean isSmallerScreen;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.podquestionrelativeview);
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		isSmallerScreen = false;
		if(metrics.heightPixels<=800){
			isSmallerScreen = true;
		}
		// create the font face to be used for all texts
		font = Typeface.createFromAsset(getAssets(),
				"fonts/NotoSans-Regular.ttf");
		headerFont = Typeface.createFromAsset(getAssets(),
				"fonts/PaytoneOne.ttf");
		Bundle extras = getIntent().getExtras();
		// get list of pods
		questions = (ArrayList<QuestionBean>) extras
				.getSerializable("questions");
		// get the selected Pod
		selectedPod = (PodBean) extras.getSerializable("selectedPod");
		// get list of all explanations
		explanations = (ArrayList<ArrayList<ExplanationBean>>) extras
				.getSerializable("explanations");

		// get the custom action bar
		modifyActionBar();
		// get the current question number for this pod and user id combination
		LearningpodDbHandler dbHandler = new LearningpodDbHandler(this);
		dbHandler.open();
		List<UserProgressInfo> userProgressTemp = dbHandler
				.getUserProgressDetails(ContentCacheStore.getContentCache()
						.getLoggedInUserProfile().getId(),
						selectedPod.getPodId());
		currentQuestionIndex = userProgressTemp.size();
		dbHandler.close();

		// create the question progress bar
		createProgressBar(userProgressTemp);

		if (currentQuestionIndex == questions.size()) {
			// pod has been completed. go summary page
			isThisPodComplete = true;
			// as the pod is complete the user progress value is not going to
			// change
			userProgressCompleted = userProgressTemp;
			// go to the summary screen
			showSummaryScreen();
			return;
		}

		// show next question based on current question index
		showNextQuestion();
		// enable disable content based on screen state
		enableScreenState();

	}

	private void modifyActionBar() {
		// get the action bar
		ActionBar actionBar = getActionBar();
		// getActionBar().setTitle(goToMapView.getText().toString());
		getActionBar().setIcon(R.drawable.arrow);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setCustomView(R.layout.ques_screen_custom_bar);
		TextView goToMapButton = (TextView) actionBar.getCustomView()
				.findViewById(R.id.title);
		TextView podTitle = (TextView) actionBar.getCustomView().findViewById(
				R.id.podname);
		podTitle.setText(selectedPod.getTitle());

		podTitle.setTypeface(headerFont);
		goToMapButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(PodQuestionActivity.this,
						MapActivity.class);				
				startActivity(intent);
				PodQuestionActivity.this.finish();
			}
		});

		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM
				| ActionBar.DISPLAY_SHOW_HOME);

	}

	private void createProgressBar(List<UserProgressInfo> userProgress) {
		// create the progress dots at the top of the screen
		// show the progress status in the screen
		// get the layout container for the progress dots
		LinearLayout progressLayout = (LinearLayout) findViewById(R.id.questionprogressbarcontainer);
		// clear the progress layout
		progressLayout.removeAllViews();
		// convert the desired dp value to pixel for creating layout parameters
		int progressViewWidthInPx = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 25, getResources()
						.getDisplayMetrics());
		LinearLayout.LayoutParams progressLayoutParams = new LinearLayout.LayoutParams(
				progressViewWidthInPx, progressViewWidthInPx);
		// set the margins after necessary conversions from pixels to dp
		progressLayoutParams.setMargins((int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 10, getResources()
						.getDisplayMetrics()), (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 0, getResources()
						.getDisplayMetrics()), 0, 0);
		progressLayoutParams.gravity = Gravity.TOP;

		progressDotList.clear();
		for (int idx = 0; idx < questions.size(); idx++) {
			View progressDot = new View(this);
			progressDot.setLayoutParams(progressLayoutParams);
			progressLayout.addView(progressDot);
			// when progress bar is created for in complete pod
			if (!isThisPodComplete) {
				// show the correct and wrong answers only for the completed
				// questions
				if (idx < currentQuestionIndex) {
					if (userProgress.get(idx).isChoiceCorrect()) {
						progressDot
								.setBackgroundResource(R.drawable.dotbluecorrect);
					} else {
						progressDot
								.setBackgroundResource(R.drawable.dotbluewrong);
					}
				} else if (idx == currentQuestionIndex) {
					progressDot.setBackgroundResource(R.drawable.dotblue);
				} else {
					progressDot.setBackgroundResource(R.drawable.dotwhite);
				}
			}
			// when progress bar is created for complete pod
			else {
				if (userProgress.get(idx).isChoiceCorrect()) {
					progressDot
							.setBackgroundResource(R.drawable.dotbluecorrect);
				} else {
					progressDot.setBackgroundResource(R.drawable.dotbluewrong);
				}
			}
			progressDotList.add(progressDot);
		}
	}

	private void enableScreenState() {
		// get the views that has different states
		Button btnSubmitNext = (Button) findViewById(R.id.btnsubmitnext);
		LinearLayout explanationContainer = (LinearLayout) findViewById(R.id.explanationcontainer);
		TextView explanationContentView = (TextView) findViewById(R.id.explanationcontent);
		TextView explanationHeaderView = (TextView) findViewById(R.id.explanationheader);
		TextView questionHighlightedView = (TextView) findViewById(R.id.quesbodyhighlighted);
		TextView questionBodyView = (TextView) findViewById(R.id.quesbody);
		explanationContentView.setTypeface(font);
		explanationHeaderView.setTypeface(headerFont);
		questionHighlightedView.setTypeface(font);
		if(isSmallerScreen){
			questionHighlightedView.setTextSize(17);
			explanationContentView.setTextSize(17);
			questionBodyView.setTextSize(15);
		}
		// get the previous (Back) question button. clicking on the back button
		// will take the user to the previous question in the explanation screen
		btnBack = (Button) findViewById(R.id.btnPrevious);
		btnBack.setTypeface(headerFont);
		btnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isBackButtonPressed = true;
				currentQuestionIndex--;
				isCurrentScreenForExplanation = true;
				LearningpodDbHandler dbHandler = new LearningpodDbHandler(
						PodQuestionActivity.this);
				dbHandler.open();
				List<UserProgressInfo> userProgressTemp = dbHandler
						.getUserProgressDetails(ContentCacheStore
								.getContentCache().getLoggedInUserProfile()
								.getId(), selectedPod.getPodId());
				dbHandler.close();
				isCurrentSelectedChoiceCorrect = userProgressTemp.get(
						currentQuestionIndex).isChoiceCorrect();
				List<QuestionChoiceBean> choices = questions
						.get(currentQuestionIndex).getChoiceQuestion()
						.getChoiceInteraction();
				for (int idx = 0; idx < choices.size(); idx++) {
					if (choices
							.get(idx)
							.getChoiceId()
							.equalsIgnoreCase(
									userProgressTemp.get(currentQuestionIndex)
											.getChoiceId())) {
						currentSelectedChoiceIndex = idx;
						break;
					}
				}
				showNextQuestion();
				enableScreenState();
			}
		});

		if (isCurrentScreenForExplanation) {
			
			btnSubmitNext.setTypeface(headerFont);
			// enable the back button if this is not the first question
			if (currentQuestionIndex != 0)
				btnBack.setVisibility(View.VISIBLE);
			else
				btnBack.setVisibility(View.INVISIBLE);
			// change the background to the arrow image
			
			if (currentQuestionIndex == questions.size() - 1) {
				// we have reached the last question in the pod
				btnSubmitNext.setBackgroundResource(R.drawable.summarynext);
				btnSubmitNext.setText("SUMMARY");
				btnSubmitNext.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
				btnSubmitNext.setPadding((int) TypedValue
						.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15,
								getResources().getDisplayMetrics()), 0, 0, 0);
				
				btnSubmitNext.getLayoutParams().height = (int) TypedValue
						.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70,
								getResources().getDisplayMetrics());
				btnSubmitNext.getLayoutParams().width = (int) TypedValue
						.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 168,
								getResources().getDisplayMetrics());
				((RelativeLayout.LayoutParams) btnSubmitNext.getLayoutParams()).addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
				((RelativeLayout.LayoutParams) btnSubmitNext.getLayoutParams()).addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				((RelativeLayout.LayoutParams) btnSubmitNext.getLayoutParams()).addRule(RelativeLayout.ALIGN_PARENT_TOP,0);
				((RelativeLayout.LayoutParams) btnSubmitNext.getLayoutParams()).addRule(RelativeLayout.CENTER_HORIZONTAL,0);
			} else {
				btnSubmitNext.setBackgroundResource(R.drawable.next);
				btnSubmitNext.setText("NEXT ");
				btnSubmitNext.setGravity(Gravity.CENTER);
				btnSubmitNext.setPadding(0, 0, 0, 0);
				btnSubmitNext.getLayoutParams().height = (int) TypedValue
						.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70,
								getResources().getDisplayMetrics());
				btnSubmitNext.getLayoutParams().width = (int) TypedValue
						.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 120,
								getResources().getDisplayMetrics());
				((RelativeLayout.LayoutParams) btnSubmitNext.getLayoutParams()).addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
				((RelativeLayout.LayoutParams) btnSubmitNext.getLayoutParams()).addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				((RelativeLayout.LayoutParams) btnSubmitNext.getLayoutParams()).addRule(RelativeLayout.ALIGN_PARENT_TOP,0);
				((RelativeLayout.LayoutParams) btnSubmitNext.getLayoutParams()).addRule(RelativeLayout.CENTER_HORIZONTAL,0);
			}
			
			
			btnSubmitNext.invalidate();
			// disable the button till the animation is not over. don't disable
			// if the pod is complete and there will be no animation
			// do the animation only when pod is not complete
			if (!isThisPodComplete && !isBackButtonPressed) {

				btnSubmitNext.setEnabled(false);
				// call animation method. This will animate the movement of
				// alien
				animateAlienImageView();
			} else {
				// change the colour of highlighted question
				questionHighlightedView.setBackgroundColor(Color
						.parseColor("#8896a3"));
				((ImageView) findViewById(R.id.alienforquestion))
						.setVisibility(View.INVISIBLE);
				((ImageView) findViewById(R.id.alienforquestionlight))
						.setVisibility(View.INVISIBLE);
				((ImageView) findViewById(R.id.alienforexplanation))
						.setVisibility(View.VISIBLE);
				((ImageView) findViewById(R.id.alienforexplanationlight))
						.setVisibility(View.VISIBLE);
				explanationContainer.setVisibility(View.VISIBLE);
				explanationContainer.setBackgroundColor(Color
						.parseColor("#F4FA58"));
			}

			ArrayList<ExplanationBean> explanationsForThisQues = explanations
					.get(currentQuestionIndex);
			// get the first explanation
			ExplanationBean explanation = explanationsForThisQues.get(0);
			explanationContentView.setText(Html.fromHtml(explanation
					.getExplanation().getExplanationBody()));

			ImageView resultIcon = (ImageView) findViewById(R.id.choiceresulticon);

			// change the choice label if the selected choice is correct
			// Also show the correct/wrong icon in front of the text
			((LinearLayout.LayoutParams)((LinearLayout)findViewById(R.id.choiceLabelContainer)).getLayoutParams()).topMargin =  (int) TypedValue
					.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3,
							getResources().getDisplayMetrics());
			if (isCurrentSelectedChoiceCorrect) {
				TextView choiceLabelText = (TextView) findViewById(R.id.choicelabel);
				choiceLabelText.setText("Yay! The correct answer is "
						+ choiceSeqArr[currentSelectedChoiceIndex]);
				choiceLabelText.setTextColor(Color.parseColor("#9acc4e"));
				choiceLabelText.setTextSize(18);
				Typeface boldfont = Typeface.createFromAsset(getAssets(),
						"fonts/NotoSans-Bold.ttf");
				choiceLabelText.setTypeface(boldfont);
				// show the correct/wrong icon
				resultIcon.setVisibility(View.VISIBLE);
				resultIcon.setBackgroundResource(R.drawable.tick);
				// set the border image for the choice selected
				choiceViewList.get(currentSelectedChoiceIndex)
						.setBackgroundResource(
								R.drawable.choice_selected_correct);
				// change the progress icon for this question
				progressDotList.get(currentQuestionIndex)
						.setBackgroundResource(R.drawable.dotbluecorrect);

			} else {
				TextView choiceLabelText = (TextView) findViewById(R.id.choicelabel);
				choiceLabelText.setText("Oops! The correct answer is "
						+ choiceSeqArr[quesToCorrectAnswerMap.get(Integer
								.valueOf(currentQuestionIndex))]);
				choiceLabelText.setTextColor(Color.parseColor("#e6855b"));
				choiceLabelText.setTextSize(18);
				Typeface boldfont = Typeface.createFromAsset(getAssets(),
						"fonts/NotoSans-Bold.ttf");
				choiceLabelText.setTypeface(boldfont);
				// show the correct/wrong icon
				resultIcon.setVisibility(View.VISIBLE);
				resultIcon.setBackgroundResource(R.drawable.cross);
				// set the border image for the choice selected
				choiceViewList
						.get(currentSelectedChoiceIndex)
						.setBackgroundResource(R.drawable.choice_selected_wrong);
				// set the border image for the correct choice
				choiceViewList.get(
						quesToCorrectAnswerMap.get(Integer
								.valueOf(currentQuestionIndex)))
						.setBackgroundResource(
								R.drawable.choice_not_selected_correct);
				// change the progress icon for this question
				progressDotList.get(currentQuestionIndex)
						.setBackgroundResource(R.drawable.dotbluewrong);
			}
		} else {
			// hide the back button
			// enable the back button
			btnBack.setVisibility(View.INVISIBLE);
			// Disabled unless one option is selected and change the image
			btnSubmitNext.setEnabled(false);			
			btnSubmitNext.setText("");			
			btnSubmitNext.setBackgroundResource(R.drawable.btnsubmitgreyed);
			btnSubmitNext.getLayoutParams().height = (int) TypedValue
					.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40,
							getResources().getDisplayMetrics());
			btnSubmitNext.getLayoutParams().width = (int) TypedValue
					.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 148,
							getResources().getDisplayMetrics());

			((RelativeLayout.LayoutParams) btnSubmitNext.getLayoutParams()).addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,0);
			((RelativeLayout.LayoutParams) btnSubmitNext.getLayoutParams()).addRule(RelativeLayout.ALIGN_PARENT_RIGHT,0);
			((RelativeLayout.LayoutParams) btnSubmitNext.getLayoutParams()).addRule(RelativeLayout.ALIGN_PARENT_TOP);
			((RelativeLayout.LayoutParams) btnSubmitNext.getLayoutParams()).addRule(RelativeLayout.CENTER_HORIZONTAL);
			btnSubmitNext.invalidate();
			explanationContainer.setVisibility(View.GONE);
			// show the alien for question
			findViewById(R.id.alienforquestion).setVisibility(View.VISIBLE);
			findViewById(R.id.alienforquestionlight)
					.setVisibility(View.VISIBLE);
			// change the color of the highlighted question
			questionHighlightedView.setBackgroundColor(Color
					.parseColor("#F4FA58"));
			// set the choice label to its default state
			((LinearLayout.LayoutParams)((LinearLayout)findViewById(R.id.choiceLabelContainer)).getLayoutParams()).topMargin =  (int) TypedValue
					.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8,
							getResources().getDisplayMetrics());
			TextView choiceLabelText = (TextView) findViewById(R.id.choicelabel);
			choiceLabelText.setText("CHOOSE THE CORRECT ANSWER");
			choiceLabelText.setTextColor(Color.parseColor("#ffffff"));
			choiceLabelText.setTextSize(15);
			choiceLabelText.setTypeface(font);
			ImageView resultIcon = (ImageView) findViewById(R.id.choiceresulticon);
			resultIcon.setVisibility(View.GONE);
		}

		btnSubmitNext.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isCurrentScreenForExplanation) {
					// set the back button action false
					isBackButtonPressed = false;
					// get the number of questions completed
					// get the current question number for this pod and user id
					// combination
					LearningpodDbHandler dbHandler = new LearningpodDbHandler(
							PodQuestionActivity.this);
					dbHandler.open();
					List<UserProgressInfo> userProgressTemp = dbHandler
							.getUserProgressDetails(ContentCacheStore
									.getContentCache().getLoggedInUserProfile()
									.getId(), selectedPod.getPodId());
					int currentQuestionToBeAttempted = userProgressTemp.size();
					dbHandler.close();

					// clicking the Next button in explanation screen
					if (currentQuestionIndex == questions.size() - 1) {
						// we have reached the last question in the pod
						isThisPodComplete = true;
						showSummaryScreen();
					} else {
						// increment the question number index
						currentQuestionIndex++;
						isCurrentScreenForExplanation = false;
						// if the pod has been completed and user clicked this
						// button
						// after coming here from summary screen.
						if (isThisPodComplete) {
							isCurrentScreenForExplanation = true;
							isCurrentSelectedChoiceCorrect = userProgressCompleted
									.get(currentQuestionIndex)
									.isChoiceCorrect();
							List<QuestionChoiceBean> choices = questions
									.get(currentQuestionIndex)
									.getChoiceQuestion().getChoiceInteraction();
							for (int idx = 0; idx < choices.size(); idx++) {
								if (choices
										.get(idx)
										.getChoiceId()
										.equalsIgnoreCase(
												userProgressCompleted.get(
														currentQuestionIndex)
														.getChoiceId())) {
									currentSelectedChoiceIndex = idx;
									break;
								}
							}

						}
						// if the user clicked on back button and then clicking
						// on next button
						// and the user is still in a question which he has
						// answered
						if (currentQuestionIndex < currentQuestionToBeAttempted) {
							// keep this flag as true to avoid animation in next
							// screen
							isBackButtonPressed = true;
							isCurrentScreenForExplanation = true;
							isCurrentSelectedChoiceCorrect = userProgressTemp
									.get(currentQuestionIndex)
									.isChoiceCorrect();
							List<QuestionChoiceBean> choices = questions
									.get(currentQuestionIndex)
									.getChoiceQuestion().getChoiceInteraction();
							for (int idx = 0; idx < choices.size(); idx++) {
								if (choices
										.get(idx)
										.getChoiceId()
										.equalsIgnoreCase(
												userProgressTemp.get(
														currentQuestionIndex)
														.getChoiceId())) {
									currentSelectedChoiceIndex = idx;
									break;
								}
							}
						}
						showNextQuestion();
						enableScreenState();

					}
				} else {
					// clicking the submit button in Question screen
					// save the selected option in DB
					saveSelectedChoiceInDb();
					isCurrentScreenForExplanation = true;
					enableScreenState();

				}
			}
		});
	}

	private void saveSelectedChoiceInDb() {
		// get the question's item id from pod bean
		String questionItemId = selectedPod.getPodElements()
				.get(currentQuestionIndex).getItemId();
		// get the user id of the logged in user
		String userId = ContentCacheStore.getContentCache()
				.getLoggedInUserProfile().getId();
		// get the pod id
		String podId = selectedPod.getPodId();
		// get the selected choice id
		String choiceId = questions.get(currentQuestionIndex)
				.getChoiceQuestion().getChoiceInteraction()
				.get(currentSelectedChoiceIndex).getChoiceId();
		UserProgressInfo userProgress = new UserProgressInfo();
		userProgress.setUserId(userId);
		userProgress.setPodId(podId);
		userProgress.setQuestionId(questionItemId);
		userProgress.setChoiceId(choiceId);
		userProgress.setChoiceCorrect(isCurrentSelectedChoiceCorrect);
		// create a db handler and open the connection
		LearningpodDbHandler handler = new LearningpodDbHandler(this);
		handler.open();
		handler.saveUserProgressInfo(userProgress);
		handler.close();
	}

	private void showNextQuestion() {

		// set the progress bar dot to blue
		progressDotList.get(currentQuestionIndex).setBackgroundResource(
				R.drawable.dotblue);

		// get the next question bean
		QuestionBean nextQuestion = questions.get(currentQuestionIndex);
		// set the question body
		TextView questionBodyView = (TextView) findViewById(R.id.quesbody);
		questionBodyView.setText(Html.fromHtml(nextQuestion.getChoiceQuestion()
				.getQuestionBody().getQuestionBodyStr()));
		questionBodyView.setTypeface(font);
		// get the question highlighted part holder
		TextView questionBodyHighlightedView = (TextView) findViewById(R.id.quesbodyhighlighted);
		// set the question highlighted part
		questionBodyHighlightedView.setText(Html.fromHtml((nextQuestion
				.getChoiceQuestion().getQuestionBody()
				.getQuestionBodyHighlighted())));
		 
		questionBodyHighlightedView.getLayoutParams().height = LayoutParams.WRAP_CONTENT;

		questionBodyHighlightedView.setTypeface(font); 
		 
		// get the alien image for explanation
		findViewById(R.id.alienforexplanation).setVisibility(View.INVISIBLE);
		findViewById(R.id.alienforexplanationlight).setVisibility(
				View.INVISIBLE);
		// get Inflater instance
		LayoutInflater inflater = getLayoutInflater();
		// get choice container
		LinearLayout choiceContainer = (LinearLayout) findViewById(R.id.choicecontainer);
		// clear the previous choices
		choiceContainer.removeAllViews();
		choiceViewList.clear();
		// show choices
		List<QuestionChoiceBean> choicesForThisQuestion = nextQuestion
				.getChoiceQuestion().getChoiceInteraction();
		// create a list of all select choice buttons for changing drawable
		// later

		for (int idx = 0; idx < choicesForThisQuestion.size(); idx++) {
			// get the choice bean object
			QuestionChoiceBean choice = choicesForThisQuestion.get(idx);
			// inflate the choice view layout
			View choiceView = inflater.inflate(R.layout.choice_view, null);
			choiceView.setBackgroundColor(Color.parseColor("#0dffffff"));
			// set the button image and choice text
			
			TextView choiceBodView = ((TextView) choiceView.findViewById(R.id.choicebody));
			choiceBodView.setText(Html.fromHtml(choice.getChoiceBody()));
			choiceBodView.setTypeface(font);			
			TextView choiceSeqView = ((TextView) choiceView.findViewById(R.id.choicesequence));
			choiceSeqView.setText(choiceSeqArr[idx]);
			choiceSeqView.setTypeface(headerFont);
			if(isSmallerScreen){
				choiceBodView.setTextSize(14);
				choiceSeqView.setTextSize(20);
			}
			// set the on click listener
			choiceView.setOnClickListener(new ChoiceSelectListner(this, false));
			choiceView.setId(idx);
			// set the id as 1 if this is the correct choice
			if (choice.getCorrect().equalsIgnoreCase("true")) {
				choiceView.setTag(1);
				quesToCorrectAnswerMap.put(currentQuestionIndex, idx);
			}
			// set the id as 0 if this is the wrong choice
			else {
				choiceView.setTag(0);
			}
			// add the choice to the choice container
			choiceContainer.addView(choiceView);
			choiceViewList.add(choiceView);
		}

	}

	private void showSummaryScreen() {
		Typeface boldfont = Typeface.createFromAsset(getAssets(),
				"fonts/NotoSans-Bold.ttf");
		// update the action bar heading
		((TextView) getActionBar().getCustomView().findViewById(R.id.podname))
				.setText("Summary Of " + selectedPod.getTitle());
		// set the back button to false
		isBackButtonPressed = false;
		setContentView(R.layout.summarylayout);

		// as the user has completed this pod, store the user progress in
		// completed object
		LearningpodDbHandler dbHandler = new LearningpodDbHandler(this);
		dbHandler.open();
		userProgressCompleted = dbHandler.getUserProgressDetails(
				ContentCacheStore.getContentCache().getLoggedInUserProfile()
						.getId(), selectedPod.getPodId());
		// currentQuestionIndex = userProgress.size();
		dbHandler.close();

		int totalQuestions = userProgressCompleted.size();
		int correctAnswers = 0;
		LinearLayout summaryQuesContainer = (LinearLayout) findViewById(R.id.summaryquestioncontainer);
		// ((TextView)findViewById(R.id.summarypodname)).setText("Summary of "+
		// selectedPod.getTitle());
		((TextView)findViewById(R.id.summaryLabel)).setTypeface(font);
		LayoutInflater inflater = getLayoutInflater();		
		Button summaryBack = (Button)findViewById(R.id.btnSummaryBack);
		summaryBack.setTypeface(headerFont);
		if(isSmallerScreen){
			summaryBack.setTextSize(18);
		}
		summaryBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				currentQuestionIndex = 4;
				isCurrentSelectedChoiceCorrect = userProgressCompleted.get(
						currentQuestionIndex).isChoiceCorrect();
				List<QuestionChoiceBean> choices = questions
						.get(currentQuestionIndex).getChoiceQuestion()
						.getChoiceInteraction();
				for (int idx = 0; idx < choices.size(); idx++) {

					if (choices
							.get(idx)
							.getChoiceId()
							.equalsIgnoreCase(
									userProgressCompleted.get(
											currentQuestionIndex)
											.getChoiceId())) {
						currentSelectedChoiceIndex = idx;
						break;
					}
				}
				isCurrentScreenForExplanation = true;
				setContentView(R.layout.podquestionrelativeview);

				createProgressBar(userProgressCompleted);
				showNextQuestion();
				enableScreenState();
				
			}
		});
		
		Button summaryMap = (Button)findViewById(R.id.btnSummaryMap);
		summaryMap.setTypeface(headerFont);
		if(isSmallerScreen){
			summaryMap.setTextSize(18);
		}
		summaryMap.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(PodQuestionActivity.this,
						MapActivity.class);				
				startActivity(intent);
				PodQuestionActivity.this.finish();				
			}
		});
		 
		for (int idx = 0; idx < userProgressCompleted.size(); idx++) {
			UserProgressInfo progress = userProgressCompleted.get(idx);
			View view = inflater.inflate(R.layout.summary_question_view, null);
			TextView quesSeq = (TextView) view
					.findViewById(R.id.summaryquestionsequence);
			quesSeq.setText(Integer.valueOf(idx + 1).toString() + ". ");
			quesSeq.setTypeface(headerFont);
			ImageView quesImage = (ImageView) view
					.findViewById(R.id.summaryQuestionicon);
			if (progress.isChoiceCorrect()) {
				quesImage.setBackgroundResource(R.drawable.tick);
				correctAnswers++;
			} else {
				quesImage.setBackgroundResource(R.drawable.cross);
			}
			view.setId(idx);
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					currentQuestionIndex = v.getId();
					isCurrentSelectedChoiceCorrect = userProgressCompleted.get(
							currentQuestionIndex).isChoiceCorrect();
					List<QuestionChoiceBean> choices = questions
							.get(currentQuestionIndex).getChoiceQuestion()
							.getChoiceInteraction();
					for (int idx = 0; idx < choices.size(); idx++) {

						if (choices
								.get(idx)
								.getChoiceId()
								.equalsIgnoreCase(
										userProgressCompleted.get(
												currentQuestionIndex)
												.getChoiceId())) {
							currentSelectedChoiceIndex = idx;
							break;
						}
					}
					isCurrentScreenForExplanation = true;
					setContentView(R.layout.podquestionrelativeview);

					createProgressBar(userProgressCompleted);
					showNextQuestion();
					enableScreenState();

				}
			});
			summaryQuesContainer.addView(view);
		}

		 percentage = (int) ((correctAnswers * 100 / totalQuestions));
		((TextView) findViewById(R.id.correctpercentage)).setText(percentage
				+ "%");
		((TextView) findViewById(R.id.correctpercentage)).setTypeface(boldfont);
		((TextView) findViewById(R.id.summarycorrect)).setTypeface(boldfont);
		if(isSmallerScreen){
			((TextView) findViewById(R.id.correctpercentage)).setTextSize(58);
			((TextView) findViewById(R.id.summarycorrect)).setTextSize(24);
		}
		
		// send mail functionality
		Button sendMail = (Button) findViewById(R.id.sendMailBt);
		sendMail.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				createEmailPopUp();
			}
		});
		if (percentage == 100) {
			findViewById(R.id.star).setVisibility(View.VISIBLE);
			// show the shooting star animation
			animateShootingStar();
		}
		
	}

	private void createEmailPopUp() {

		final Dialog popupmail = new Dialog(context);
		popupmail.requestWindowFeature(Window.FEATURE_NO_TITLE);
		popupmail.setContentView(R.layout.email_popup);
		final EditText recipient = (EditText) popupmail
				.findViewById(R.id.toaddress);

		LearningpodDbHandler dbHandler = new LearningpodDbHandler(
				PodQuestionActivity.this);
		dbHandler.open();

		String userId = ContentCacheStore.getContentCache()
				.getLoggedInUserProfile().getId();

		if (!dbHandler.getTeacherEmail(userId).equals("")) {
			putinmailaddress = dbHandler.getTeacherEmail(userId);
			recipient.setText(putinmailaddress);
		}

		dbHandler.close();
		Button sendBtn = (Button) popupmail.findViewById(R.id.sendEmail);
		sendBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				
			//check internet connection	
		
	         
	     	    {   
			
			     email= recipient.getText().toString().trim();
			    //check whether a valid email is entered
			    if(recipient.getText().length()==0)
			    {
			    	recipient.setError("Please enter a valid Email");	
			    	recipient.setFocusable(true);	
			    }
			 
			    if (!email.matches(emailPattern))
		    	{
		        recipient.setError("Please enter a valid Email");	
		    	recipient.setFocusable(true);
		    	//Toast.makeText(getApplicationContext(),"invalid email address",Toast.LENGTH_LONG).show();
		    	
		    	}
			   
			    else{
			  			    			   
				LearningpodDbHandler dbHandler = new LearningpodDbHandler(
						PodQuestionActivity.this);
				dbHandler.open();
				uid = ContentCacheStore.getContentCache()
						.getLoggedInUserProfile().getId();
				teacheremail = recipient.getText().toString();
				dbHandler.storeUserTeacherMapping(uid, teacheremail);
				dbHandler.close();
				sendEmail(recipient.getText().toString());
				popupmail.dismiss();
			    }
			    }
		
		
			}
		});
		// set the focus on dummy button so that the keyboard is not open when the 
		// popup window is displayed
		Button dummyBtn = (Button)popupmail.findViewById(R.id.sendEmailDummy);
		dummyBtn.setFocusable(true);
		dummyBtn.setFocusableInTouchMode(true);
		dummyBtn.requestFocus();
		popupmail.show();
		 

	}

	
	private void sendEmail(String recipient) {

		Intent email = new Intent(Intent.ACTION_SEND);
		email.setType("text/html");
		List<android.content.pm.ResolveInfo> resInfo = getPackageManager()
				.queryIntentActivities(email, 0);

		if (!resInfo.isEmpty()) {
			for (android.content.pm.ResolveInfo info : resInfo) {
				if (info.activityInfo.packageName.toLowerCase().contains(
						"gmail")
						|| info.activityInfo.name.toLowerCase().contains(
								"gmail")) {
					email.putExtra(android.content.Intent.EXTRA_TEXT,
							Html.fromHtml(createSummaryMailBody()));
					email.putExtra(android.content.Intent.EXTRA_SUBJECT, "My Result");
					email.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{recipient});					
					email.setPackage(info.activityInfo.packageName);
					startActivity(Intent.createChooser(email, ""));
				}
			}
		}
		else{
			 email.setType("message/rfc822");
			 startActivity(Intent.createChooser(email,
			 "Choose an Email client :"));
		}
	}
	
	private String createSummaryMailBody(){
		StringBuffer summary = new StringBuffer();
		summary.append("Name : " + ContentCacheStore.getContentCache().getLoggedInUserProfile().getName());
		summary.append("\n");
		summary.append("Email Id : " + ContentCacheStore.getContentCache().getCurrentUserEmailId());
		summary.append("\n\n");
		
		
		for(int idx=0;idx<userProgressCompleted.size();idx++){
			UserProgressInfo userProgress = userProgressCompleted.get(idx);
			summary.append("Question #" + (idx+1));
			summary.append("\n");
			summary.append("\tSelected Answer : "  + getChoiceSequence(userProgress.getQuestionId(), userProgress.getChoiceId()));
			summary.append("\n");
			summary.append("\tCorrect Answer : " + getCorrectChoiceSequence(userProgress.getQuestionId()));
			summary.append("\n");
			summary.append("\tResult : " + (userProgress.isChoiceCorrect()?"Correct":"Incorrect"));
			summary.append("\n\n");
		}
		summary.append("Overall Percentage : " +  percentage + "%");
		String test = "<html>	<head>		<head>	<body>		<div id=\"container\" class=\"font\">			<div style=\"font-size:20;height:55px\">				<div style=\"padding-top: 14px;\">Summary for Jane Smith</div>			</div>			<div style=\"font-size:15;height:35px;background-color:#435869;\">				<div style=\"padding-top: 6px;\">Context Clues - Level A</div>			</div>					</div> <div><table><thead><tr><th>Col1</th><th>Col2</th><th>Col3</th></tr></thead><tbody><tr><td>val1</td><td>val2</td><td>val3</td></tr><tr><td>val1</td><td>val2</td><td>val3</td></tr></tbody></div>	</body></html>";
		
		return test;
	}
	
	private String getChoiceSequence(String questionId, String choiceId){
		 // find the question	
		for(int idx=0;idx<selectedPod.getPodElements().size();idx++){
			PodQuestionBean ques = selectedPod.getPodElements().get(idx);
			if(ques.getItemId().equalsIgnoreCase(questionId)){
				
				for(int idx1=0;idx1<questions.get(idx).getChoiceQuestion().getChoiceInteraction().size();idx1++){
					QuestionChoiceBean choice =questions.get(idx).getChoiceQuestion().getChoiceInteraction().get(idx1);
					if(choice.getChoiceId().equalsIgnoreCase(choiceId)){
						// found by choice index
						return choiceSeqArr[idx1];
					}
				}
				
			}
		}
		 
		return "";
	}
	
	private String getCorrectChoiceSequence(String questionId){
		 // find the question	
		for(int idx=0;idx<selectedPod.getPodElements().size();idx++){
			PodQuestionBean ques = selectedPod.getPodElements().get(idx);
			if(ques.getItemId().equalsIgnoreCase(questionId)){
				
				for(int idx1=0;idx1<questions.get(idx).getChoiceQuestion().getChoiceInteraction().size();idx1++){
					QuestionChoiceBean choice =questions.get(idx).getChoiceQuestion().getChoiceInteraction().get(idx1);
					if(choice.getCorrect().equalsIgnoreCase("true")){
						return choiceSeqArr[idx1];
					}
				}
			}
		}
		return "";			 
	}

	
	private void animateShootingStar(){
		
		final ImageView shootingStar1 = (ImageView) findViewById(R.id.sstar1);	
		final ImageView shootingStar2 = (ImageView) findViewById(R.id.sstar2);	
		final ImageView shootingStar3 = (ImageView) findViewById(R.id.sstar3);	
		final ImageView shootingStar4 = (ImageView) findViewById(R.id.sstar4);	
		final ImageView shootingStar5 = (ImageView) findViewById(R.id.sstar5);
		final ImageView shootingStar0 = (ImageView) findViewById(R.id.sstar0);
		final ImageView shootingStar2_5 = (ImageView) findViewById(R.id.sstar2_5);
		final ImageView shootingStar3_5 = (ImageView) findViewById(R.id.sstar3_5);		
		final ImageView shootingStar1_1 = (ImageView) findViewById(R.id.sstar1_1);
		final ImageView shootingStar2_1 = (ImageView) findViewById(R.id.sstar2_1);
		final ImageView shootingStar3_1 = (ImageView) findViewById(R.id.sstar3_1);
		
		shootingStar1.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			
			@Override
			public void onGlobalLayout() {
				// remove the call back
				shootingStar1.getViewTreeObserver().removeOnGlobalLayoutListener(this);
				
				// for star0
				int[] origLocation0 = new int[2];
				int[] destLocation0 = new int[2];
				shootingStar0.getLocationOnScreen(origLocation0);
				destLocation0[0] = origLocation0[0] - (shootingStar1.getWidth()*2);
				destLocation0[1] = origLocation0[1] + (shootingStar1.getHeight()*2);
				//shootingStar1.bringToFront();;
				TranslateAnimation animation0 = new TranslateAnimation(0,
						destLocation0[0] - origLocation0[0], 0, destLocation0[1]
								- origLocation0[1]);							
				animation0.setFillAfter(false);
				animation0.setRepeatCount(Animation.INFINITE);	
				
				// for star1
				int[] origLocation1 = new int[2];
				int[] destLocation1 = new int[2];
				shootingStar1.getLocationOnScreen(origLocation1);
				destLocation1[0] = origLocation1[0] - (shootingStar1.getWidth()*3);
				destLocation1[1] = origLocation1[1] + (shootingStar1.getHeight()*3 +50);
				//shootingStar1.bringToFront();;
				TranslateAnimation animation1 = new TranslateAnimation(0,
						destLocation1[0] - origLocation1[0], 0, destLocation1[1] 
								- origLocation1[1]);						
				animation1.setFillAfter(false);
				animation1.setRepeatCount(Animation.INFINITE);	
				
				// for star1.1
				int[] origLocation1_1 = new int[2];
				int[] destLocation1_1 = new int[2];
				shootingStar1_1.getLocationOnScreen(origLocation1_1);
				destLocation1_1[0] = origLocation1_1[0] - (shootingStar1_1.getWidth()*3);
				destLocation1_1[1] = origLocation1_1[1] + (shootingStar1_1.getHeight()*3 +50);
				//shootingStar1.bringToFront();;
				TranslateAnimation animation1_1 = new TranslateAnimation(0,
						destLocation1_1[0] - origLocation1_1[0], 0, destLocation1_1[1] 
								- origLocation1_1[1]);						
				animation1.setFillAfter(false);
				animation1.setRepeatCount(Animation.INFINITE);	
				
				
				// for star2
				int[] origLocation2 = new int[2];
				int[] destLocation2 = new int[2];
				shootingStar2.getLocationOnScreen(origLocation2);
				destLocation2[0] = origLocation2[0] - (shootingStar2.getWidth()*3);
				destLocation2[1] = origLocation2[1] + (shootingStar2.getHeight()*3+50);
				//shootingStar2.bringToFront();
				TranslateAnimation animation2 = new TranslateAnimation(0,
						destLocation2[0] - origLocation2[0], 0, destLocation2[1]
								- origLocation2[1]);		
				animation2.setFillAfter(false);
				animation2.setRepeatCount(Animation.INFINITE);
				
				// for star2.1
				int[] origLocation2_1 = new int[2];
				int[] destLocation2_1 = new int[2];
				shootingStar2_1.getLocationOnScreen(origLocation2_1);
				destLocation2_1[0] = origLocation2_1[0] - (shootingStar2_1.getWidth()*3);
				destLocation2_1[1] = origLocation2_1[1] + (shootingStar2_1.getHeight()*3+50);
				//shootingStar2.bringToFront();
				TranslateAnimation animation2_1 = new TranslateAnimation(0,
						destLocation2_1[0] - origLocation2_1[0], 0, destLocation2_1[1]
								- origLocation2_1[1]);		
				animation2_1.setFillAfter(false);
				animation2_1.setRepeatCount(Animation.INFINITE);
				
				// for star2.5
				int[] origLocation2_5 = new int[2];
				int[] destLocation2_5 = new int[2];
				shootingStar2_5.getLocationOnScreen(origLocation2_5);
				destLocation2_5[0] = origLocation2_5[0] - (shootingStar2_5.getWidth()*3);
				destLocation2_5[1] = origLocation2_5[1] + (shootingStar2_5.getHeight()*3+50);
				//shootingStar2.bringToFront();
				TranslateAnimation animation2_5 = new TranslateAnimation(0,
						destLocation2_5[0] - origLocation2_5[0], 0, destLocation2_5[1]
								- origLocation2_5[1]);	
				animation2_5.setFillAfter(false);
				animation2_5.setRepeatCount(Animation.INFINITE);
				
				
				
				// for star3
				int[] origLocation3 = new int[3];
				int[] destLocation3 = new int[3];
				shootingStar3.getLocationOnScreen(origLocation3);
				destLocation3[0] = origLocation3[0] - (shootingStar3.getWidth()*3);
				destLocation3[1] = origLocation3[1] + (shootingStar3.getHeight()*3+50);
				//shootingStar3.bringToFront();
				TranslateAnimation animation3 = new TranslateAnimation(0,
						destLocation3[0] - origLocation3[0], 0, destLocation3[1]
								- origLocation3[1]);
				animation3.setFillAfter(false);
				animation3.setRepeatCount(Animation.INFINITE);	
				
				
				// for star3.1
				int[] origLocation3_1 = new int[3];
				int[] destLocation3_1 = new int[3];
				shootingStar3_1.getLocationOnScreen(origLocation3_1);
				destLocation3_1[0] = origLocation3_1[0] - (shootingStar3_1.getWidth()*3);
				destLocation3_1[1] = origLocation3_1[1] + (shootingStar3_1.getHeight()*3+50);
				//shootingStar3.bringToFront();
				TranslateAnimation animation3_1 = new TranslateAnimation(0,
						destLocation3_1[0] - origLocation3_1[0], 0, destLocation3_1[1]
								- origLocation3_1[1]);
				animation3_1.setFillAfter(false);
				animation3_1.setRepeatCount(Animation.INFINITE);	
				
				
				// for star3.5
				int[] origLocation3_5 = new int[3];
				int[] destLocation3_5 = new int[3];
				shootingStar3_5.getLocationOnScreen(origLocation3_5);
				destLocation3_5[0] = origLocation3_5[0] - (shootingStar3_5.getWidth()*3);
				destLocation3_5[1] = origLocation3_5[1] + (shootingStar3_5.getHeight()*3+50);
				//shootingStar3.bringToFront();
				TranslateAnimation animation3_5 = new TranslateAnimation(0,
						destLocation3_5[0] - origLocation3_5[0], 0, destLocation3_5[1]
								- origLocation3_5[1]);
				animation3_5.setFillAfter(false);
				animation3_5.setRepeatCount(Animation.INFINITE);	
				
				
				
				
				// for star4
				int[] origLocation4 = new int[4];
				int[] destLocation4 = new int[4];
				shootingStar4.getLocationOnScreen(origLocation4);
				destLocation4[0] = origLocation4[0] - (shootingStar4.getWidth()*3);
				destLocation4[1] = origLocation4[1] + (shootingStar4.getHeight()*3);
				//shootingStar4.bringToFront();
				TranslateAnimation animation4 = new TranslateAnimation(0,
						destLocation4[0] - origLocation4[0], 0, destLocation4[1]
								- origLocation4[1]);
				animation4.setFillAfter(false);
				animation4.setRepeatCount(Animation.INFINITE);	
				
				// for star5
				int[] origLocation5 = new int[2];
				int[] destLocation5 = new int[2];
				shootingStar5.getLocationOnScreen(origLocation5);
				destLocation5[0] = origLocation5[0] - (shootingStar5.getWidth()*2);
				destLocation5[1] = origLocation5[1] + (shootingStar5.getHeight()*2);
				//shootingStar5.bringToFront();
				TranslateAnimation animation5 = new TranslateAnimation(0,
						destLocation5[0] - origLocation5[0], 0, destLocation5[1]
								- origLocation5[1]);
				animation5.setFillAfter(false);
				animation5.setRepeatCount(Animation.INFINITE);
				
				// set the offset to create the lag between stars
				/*animation0.setStartOffset(0);
				animation1.setStartOffset(300);
				animation2.setStartOffset(200);
				animation2_5.setStartOffset(0);
				animation3.setStartOffset(100);
				animation3_5.setStartOffset(0);
				animation4.setStartOffset(0);
				animation5.setStartOffset(100);*/
				
				// create a fade out animation. 10 mili seconds is added as calibration value to keep
				// both animations at sync
				Animation fadeOut0 = new AlphaAnimation(1, 0);
				fadeOut0.setFillAfter(false);
				fadeOut0.setRepeatCount(Animation.INFINITE);
				
				Animation fadeOut1 = new AlphaAnimation(1, 0);
				fadeOut1.setFillAfter(false);
				fadeOut1.setRepeatCount(Animation.INFINITE);
				
				Animation fadeOut1_1 = new AlphaAnimation(1, 0);
				fadeOut1_1.setFillAfter(false);
				fadeOut1_1.setRepeatCount(Animation.INFINITE);
				
				Animation fadeOut2 = new AlphaAnimation(1, 0);
				fadeOut2.setFillAfter(false);
				fadeOut2.setRepeatCount(Animation.INFINITE);
				

				Animation fadeOut2_1 = new AlphaAnimation(1, 0);
				fadeOut2_1.setFillAfter(false);
				fadeOut2_1.setRepeatCount(Animation.INFINITE);
				
				
				Animation fadeOut2_5 = new AlphaAnimation(1, 0);
				fadeOut2_5.setFillAfter(false);
				fadeOut2_5.setRepeatCount(Animation.INFINITE);
				
				Animation fadeOut3 = new AlphaAnimation(1, 0);
				fadeOut3.setFillAfter(false);
				fadeOut3.setRepeatCount(Animation.INFINITE);
				
				Animation fadeOut3_1 = new AlphaAnimation(1, 0);
				fadeOut3_1.setFillAfter(false);
				fadeOut3_1.setRepeatCount(Animation.INFINITE);
				
				Animation fadeOut3_5 = new AlphaAnimation(1, 0);
				fadeOut3_5.setFillAfter(false);
				fadeOut3_5.setRepeatCount(Animation.INFINITE);
				
				
				Animation fadeOut4 = new AlphaAnimation(1, 0);
				fadeOut4.setFillAfter(false);
				fadeOut4.setRepeatCount(Animation.INFINITE);
				
				Animation fadeOut5 = new AlphaAnimation(1, 0);
				fadeOut5.setFillAfter(false);
				fadeOut5.setRepeatCount(Animation.INFINITE);
				
				
				// create Animation set for each shooting star
				
				final AnimationSet set0 = new AnimationSet(false);				
				set0.addAnimation(fadeOut0);
				set0.addAnimation(animation0);
				set0.setDuration(2000);
				
				final AnimationSet set1 = new AnimationSet(false);
				set1.addAnimation(fadeOut1);
				set1.addAnimation(animation1);
				set1.setDuration(2000);
				
				final AnimationSet set1_1 = new AnimationSet(false);
				set1_1.addAnimation(fadeOut1_1);
				set1_1.addAnimation(animation1_1);
				set1_1.setDuration(2000);
				
				
				final AnimationSet set2 = new AnimationSet(false);
				set2.addAnimation(fadeOut2);
				set2.addAnimation(animation2);
				set2.setDuration(2000);
				

				final AnimationSet set2_1 = new AnimationSet(false);
				set2_1.addAnimation(fadeOut2_1);
				set2_1.addAnimation(animation2_1);
				set2_1.setDuration(2000);
				
				final AnimationSet set2_5 = new AnimationSet(false);
				set2_5.addAnimation(fadeOut2_5);
				set2_5.addAnimation(animation2_5);
				set2_5.setDuration(2000);
				
				
				final AnimationSet set3 = new AnimationSet(false);
				set3.addAnimation(fadeOut3);
				set3.addAnimation(animation3);
				set3.setDuration(2000);		
				
				final AnimationSet set3_1 = new AnimationSet(false);
				set3_1.addAnimation(fadeOut3_1);
				set3_1.addAnimation(animation3_1);
				set3_1.setDuration(2000);
				
				final AnimationSet set3_5 = new AnimationSet(false);
				set3_5.addAnimation(fadeOut3_5);
				set3_5.addAnimation(animation3_5);
				set3_5.setDuration(2000);
				
				final AnimationSet set4 = new AnimationSet(false);
				set4.addAnimation(fadeOut4);
				set4.addAnimation(animation4);
				set4.setDuration(2000);
				
				final AnimationSet set5 = new AnimationSet(false);
				set5.addAnimation(fadeOut5);
				set5.addAnimation(animation5);
				set5.setDuration(2000);
				
				// set the offset to create the lag between stars
				/*set0.setStartOffset(0);
				set1.setStartOffset(1000);
				set2.setStartOffset(200);
				set2_5.setStartOffset(800);
				set3.setStartOffset(100);
				set3_5.setStartOffset(1000);
				set4.setStartOffset(0);
				set5.setStartOffset(800);*/
				
				
				
				// start the animation set for each shooting star
				
				
				new Handler().postDelayed(new Runnable(){
					@Override
					public void run() {
						shootingStar0.startAnimation(set0);
						
					}					
				}, 0);
				
				new Handler().postDelayed(new Runnable(){
					@Override
					public void run() {
						shootingStar1.startAnimation(set1);
						
					}					
				}, 1200);
				
				new Handler().postDelayed(new Runnable(){
					@Override
					public void run() {
						shootingStar1_1.startAnimation(set1_1);
						
					}					
				}, 1600);
				
				new Handler().postDelayed(new Runnable(){
					@Override
					public void run() {
						shootingStar2.startAnimation(set2);
						
					}					
				}, 100);
				
				new Handler().postDelayed(new Runnable(){
					@Override
					public void run() {
						shootingStar2_1.startAnimation(set2_1);
						
					}					
				}, 1900);
				
				new Handler().postDelayed(new Runnable(){
					@Override
					public void run() {
						shootingStar2_5.startAnimation(set2_5);
						
					}					
				}, 800);
				
				new Handler().postDelayed(new Runnable(){
					@Override
					public void run() {
						shootingStar3.startAnimation(set3);
						
					}					
				}, 0);
				
				new Handler().postDelayed(new Runnable(){
					@Override
					public void run() {
						shootingStar3_1.startAnimation(set3_1);
						
					}					
				}, 1700);
				
				new Handler().postDelayed(new Runnable(){
					@Override
					public void run() {
						shootingStar3_5.startAnimation(set3_5);
						
					}					
				}, 1100);
				
				new Handler().postDelayed(new Runnable(){
					@Override
					public void run() {
						shootingStar4.startAnimation(set4);
						
					}					
				}, 200);
				
				new Handler().postDelayed(new Runnable(){
					@Override
					public void run() {
						shootingStar5.startAnimation(set5);
						
					}					
				}, 800);
				
			}
		});
		
		
	}
	private void animateAlienImageView() {
		final ImageView alienForQues = (ImageView) findViewById(R.id.alienforquestion);
		final ImageView alienForQuesLight = (ImageView) findViewById(R.id.alienforquestionlight);
		final ImageView alienForExp = (ImageView) findViewById(R.id.alienforexplanation);
		final ImageView alienForExpLight = (ImageView) findViewById(R.id.alienforexplanationlight);
		int[] origLocation = new int[2];
		int[] destLocation = new int[2];
		
		alienForQues.getLocationOnScreen(origLocation);
		alienForQues.bringToFront();
		alienForQues.requestLayout();
		alienForQues.invalidate();
		alienForExp.getLocationOnScreen(destLocation);
		TranslateAnimation translateAnimation = new TranslateAnimation(0,
				destLocation[0] - origLocation[0], 0, destLocation[1]
						- origLocation[1]);
		translateAnimation.setDuration(2000);
		translateAnimation.setFillAfter(true);
		// create rotation animation
		Animation rotateAnimation = new RotateAnimation(0f,60f,Animation.RELATIVE_TO_SELF,alienForExp.getPivotX(),Animation.RELATIVE_TO_SELF,alienForExp.getPivotY());
		rotateAnimation.setDuration(2000);
		rotateAnimation.setFillAfter(false);
		rotateAnimation.setStartOffset(2000);
		rotateAnimation.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				
				
			}
		});
		translateAnimation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationEnd(Animation animation) {
				btnBack.setEnabled(true);
				alienForQues.clearAnimation();
				alienForQues.setVisibility(View.INVISIBLE);
				alienForQuesLight.setVisibility(View.INVISIBLE);
				alienForExp.setVisibility(View.VISIBLE);
				alienForExpLight.setVisibility(View.VISIBLE);
				// show the explanation container and the alien image
				LinearLayout explanationContainer = (LinearLayout) findViewById(R.id.explanationcontainer);

				// make the explanation container visible
				explanationContainer.setBackgroundColor(Color
						.parseColor("#F4FA58"));

				// enable the next/summary button
				((Button) findViewById(R.id.btnsubmitnext)).setEnabled(true);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				// switch off the alien space ship light
				btnBack.setEnabled(false);
				alienForQuesLight.setVisibility(View.INVISIBLE);
				// change the colour of highlighted question
				TextView questionHighlightedView = (TextView) findViewById(R.id.quesbodyhighlighted);
				questionHighlightedView.setBackgroundColor(Color
						.parseColor("#8896a3"));
				((LinearLayout) findViewById(R.id.explanationcontainer))
						.setVisibility(View.VISIBLE);
				((LinearLayout) findViewById(R.id.explanationcontainer))
						.setBackgroundColor(Color.parseColor("#8896a3"));
			}

		});
		
		// create animation set for storing both animations
		
		AnimationSet set = new AnimationSet(false);
		set.addAnimation(translateAnimation);
		//set.addAnimation(rotateAnimation);
		alienForQues.startAnimation(set);

	}

	public void setCurrentSelectedChoiceCorrect(
			boolean currentSelectedChoiceCorrect) {
		this.isCurrentSelectedChoiceCorrect = currentSelectedChoiceCorrect;
	}

	public boolean isCurrentSelectedChoiceCorrect() {
		return isCurrentSelectedChoiceCorrect;
	}

	public int getCurrentSelectedChoiceIndex() {
		return currentSelectedChoiceIndex;
	}

	public void setCurrentSelectedChoiceIndex(int currentSelectedChoiceIndex) {
		this.currentSelectedChoiceIndex = currentSelectedChoiceIndex;
	}

	public boolean isCurrentScreenForExplanation() {
		return isCurrentScreenForExplanation;
	}

	public List<View> getChoiceViews() {
		return choiceViewList;
	}

	public HashMap<Integer, Integer> getQuesToCorrectChoiceMap() {
		return quesToCorrectAnswerMap;
	}

	public int getCurrentQuestionIndex() {
		return currentQuestionIndex;
	}
	
	
	
	
}
