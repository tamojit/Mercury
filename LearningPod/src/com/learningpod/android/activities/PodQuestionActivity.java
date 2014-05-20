package com.learningpod.android.activities;

import java.io.IOException;
import java.io.InputStream;
import java.text.ChoiceFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import android.content.res.AssetManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ActionBar;
import android.app.Dialog;
import android.app.Activity;

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
	 

	private String putinmailaddress;
	private String teacheremail;
	private String userId;
	final Context context = this;
	private String mail;
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
		if(metrics.heightPixels<1200){
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
		actionBar.setCustomView(R.layout.custm);
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
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
		Button btnBack = (Button) findViewById(R.id.btnPrevious);
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
			btnSubmitNext.setBackgroundResource(R.drawable.next);
			if (currentQuestionIndex == questions.size() - 1) {
				// we have reached the last question in the pod
				btnSubmitNext.setText("SUMMARY");
				btnSubmitNext.getLayoutParams().height = (int) TypedValue
						.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80,
								getResources().getDisplayMetrics());
				btnSubmitNext.getLayoutParams().width = (int) TypedValue
						.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 180,
								getResources().getDisplayMetrics());
				((LinearLayout.LayoutParams) btnSubmitNext.getLayoutParams()).leftMargin = (int) TypedValue
						.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 120,
								getResources().getDisplayMetrics());
			} else {
				btnSubmitNext.setText("NEXT ");
				btnSubmitNext.getLayoutParams().height = (int) TypedValue
						.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80,
								getResources().getDisplayMetrics());
				btnSubmitNext.getLayoutParams().width = (int) TypedValue
						.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 120,
								getResources().getDisplayMetrics());
				((LinearLayout.LayoutParams) btnSubmitNext.getLayoutParams()).leftMargin = (int) TypedValue
						.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 180,
								getResources().getDisplayMetrics());
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
			// Disabled unless one option is selected
			btnSubmitNext.setEnabled(false);
			btnSubmitNext.setText("");
			btnSubmitNext.setBackgroundResource(R.drawable.btnsubmit);
			btnSubmitNext.getLayoutParams().height = (int) TypedValue
					.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40,
							getResources().getDisplayMetrics());
			btnSubmitNext.getLayoutParams().width = (int) TypedValue
					.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 148,
							getResources().getDisplayMetrics());
			((LinearLayout.LayoutParams) btnSubmitNext.getLayoutParams()).leftMargin = 0;
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
		if (percentage == 100) {
			findViewById(R.id.star).setVisibility(View.VISIBLE);
		}
		// send mail functionality
		Button sendMail = (Button) findViewById(R.id.sendMailBt);

		sendMail.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				createEmailPopUp();
			}
		});

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

		if (dbHandler.getTeacherEmail(userId) != "") {
			putinmailaddress = dbHandler.getTeacherEmail(userId);
			recipient.setText(putinmailaddress);
		}

		dbHandler.close();
		Button sendBtn = (Button) popupmail.findViewById(R.id.sendEmail);
		sendBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				// after sending the email, clear the fields
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
							createSummaryMailBody());
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
		return summary.toString();
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
		TranslateAnimation animation = new TranslateAnimation(0,
				destLocation[0] - origLocation[0], 0, destLocation[1]
						- origLocation[1]);
		animation.setDuration(2000);
		animation.setFillAfter(false);
		// animation.setZAdjustment(Animation.ZORDER_TOP);
		animation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
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
		// alienForQues.bringToFront();
		alienForQues.startAnimation(animation);

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
