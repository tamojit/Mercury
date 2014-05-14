package com.learningpod.android.utility;

 
import com.learningpod.android.R;

import android.app.ProgressDialog;
import android.content.Context;
 

public class CustomProgressDialog extends ProgressDialog{

	public CustomProgressDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void show() {
		// TODO Auto-generated method stubc
		
		super.show();
		setContentView(R.layout.customprog);
	}

}
