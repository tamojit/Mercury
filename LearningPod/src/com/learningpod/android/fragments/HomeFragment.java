package com.learningpod.android.fragments;

import com.learningpod.android.R;

import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class HomeFragment extends Fragment {
	private String username;
	public HomeFragment(){
		
	}
	
	
	
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		username = getArguments().getString("username");
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        TextView txtUserName = (TextView)rootView.findViewById(R.id.username);
		txtUserName.setText("Hello, " + username + "!");
		
		TextView txtlabel1 = (TextView)rootView.findViewById(R.id.homelabel1);
		TextView txtlabel2 = (TextView)rootView.findViewById(R.id.homelabel2);
		Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/RussoOne.ttf");
		txtlabel1.setTypeface(font);
		txtlabel2.setTypeface(font);
        return rootView;
    }
}
