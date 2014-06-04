package com.learningpod.android.activities;

import static com.learningpod.android.utility.AppConstants.FIRST_COLUMN;
import static com.learningpod.android.utility.AppConstants.SECOND_COLUMN;
import static com.learningpod.android.utility.AppConstants.THIRD_COLUMN;

import java.util.ArrayList;
import java.util.HashMap;

import com.learningpod.android.R;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ListviewAdapter extends BaseAdapter {
	public ArrayList<HashMap<String, String>> list;
	Activity activity;
	private Typeface Font = null;
	private Typeface meaningfont = null;

	public ListviewAdapter(Activity activity,
			ArrayList<HashMap<String, String>> list) {
		super();
		this.activity = activity;
		this.list = list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	private class ViewHolder {
		LinearLayout layout;
		TextView txtFirst;
		TextView txtSecond;
		TextView txtThird;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		// TODO Auto-generated method stub
		ViewHolder holder;
		LayoutInflater inflater = activity.getLayoutInflater();

		if (convertView == null) {

			convertView = inflater.inflate(R.layout.listview_row, null);
			holder = new ViewHolder();
			holder.txtFirst = (TextView) convertView
					.findViewById(R.id.FirstText);
			holder.txtSecond = (TextView) convertView
					.findViewById(R.id.SecondText);
			holder.txtThird = (TextView) convertView
					.findViewById(R.id.ThirdText);
			holder.layout = (LinearLayout) convertView
					.findViewById(R.id.relativeLayout1);

			convertView.setTag(holder);
		
		} else {
			holder = (ViewHolder) convertView.getTag();
			
		}

		Font = Typeface.createFromAsset(this.activity.getAssets(),
				"fonts/NotoSans-Bold.ttf");
		meaningfont = Typeface.createFromAsset(this.activity.getAssets(),
				"fonts/NotoSans-Regular.ttf");

		HashMap<String, String> map = list.get(position);

		holder.txtFirst.setTypeface(Font);
		holder.txtThird.setTypeface(meaningfont);
		holder.txtFirst.setText(map.get(FIRST_COLUMN));
		holder.txtSecond.setText(map.get(SECOND_COLUMN));
		holder.txtThird.setText(map.get(THIRD_COLUMN));

		if (position % 2 != 0) {
			// holder.layout.setBackgroundColor(Color.GRAY);

			convertView.setBackgroundResource(R.drawable.wordlistbg1);
		}

		return convertView;
	}

}
