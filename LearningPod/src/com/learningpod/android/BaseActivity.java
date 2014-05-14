package com.learningpod.android;

import com.learningpod.android.activities.AccountSelectorActivity;
import com.learningpod.android.activities.MapActivityBeforeLogin;

import android.accounts.Account;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

public class BaseActivity extends Activity implements
		DialogInterface.OnClickListener {

	private ProgressDialog progressDialog;
	private AlertDialog alertDialog;
	private PopupWindow loginWindow;
	private Account[] accounts = null;
	public String detail;
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// progress dialog for the application.
		progressDialog = new ProgressDialog(this);
		progressDialog.setCancelable(false);
		progressDialog.hide();

		// alert dialog for the application.
		alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", this);
		this.alertDialog.setCancelable(false);
	}

	private void initPopup(String detail) {

		loginWindow = new PopupWindow(400, 200);
		View loginWindowView = getLayoutInflater().inflate(
				R.layout.popup_menuitem, null);
		loginWindow.setContentView(loginWindowView);
		//
		TextView textinpopup = (TextView) loginWindowView
				.findViewById(R.id.text);
		textinpopup.setText(detail);

		// close the popup window
		Button Close = (Button) loginWindowView.findViewById(R.id.close);
		Close.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				{
					loginWindow.dismiss();
				}

			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is
		// presentpublic Men.
		if (!(this instanceof AccountSelectorActivity)) {
			getMenuInflater().inflate(R.menu.common_menu, menu);
		}
		
		if(this instanceof MapActivityBeforeLogin){
			menu.findItem(R.id.login).setTitle("Login");
		}
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO Auto-generated method stub
		if (item.getItemId() == R.id.help) {

			detail = "help";
			initPopup("help");
			loginWindow.showAtLocation(findViewById(android.R.id.content)
					.getRootView(), Gravity.CENTER, 0, 0);
		}

		else if (item.getItemId() == R.id.about) {
			detail = "about";
			initPopup("about");
			loginWindow.showAtLocation(findViewById(android.R.id.content)
					.getRootView(), Gravity.CENTER, 0, 0);
		} else if (item.getItemId() == R.id.terms) {
			detail = "terms";
			initPopup("terms");
			loginWindow.showAtLocation(findViewById(android.R.id.content)
					.getRootView(), Gravity.CENTER, 0, 0);
		} else if(item.getItemId()==R.id.login){
			if(this instanceof MapActivityBeforeLogin){
				// perform login
			}
			else {
				// perform logout
			}
		}
		
		return true;
	}

	public ProgressDialog getProgressDialog() {
		return this.progressDialog;
	}

	public void showAlertDialog(String alertTitle, String alertMessage) {
		if (progressDialog.isShowing()) {
			progressDialog.hide();
		}
		this.alertDialog.setTitle(alertTitle);
		this.alertDialog.setMessage(alertMessage);
		this.alertDialog.show();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub

	}

}