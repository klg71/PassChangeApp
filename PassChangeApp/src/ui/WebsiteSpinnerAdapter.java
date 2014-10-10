package ui;

import java.util.Calendar;
import java.util.List;

import core.Website;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WebsiteSpinnerAdapter extends ArrayAdapter<Website> {


	private List<Website> websites;

	public WebsiteSpinnerAdapter(Context context, int resource,
			List<Website> objects) {
		super(context, resource, objects);
		this.websites=objects;
	}


	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		LinearLayout layout=new LinearLayout(parent.getContext());
		layout.setOrientation(LinearLayout.HORIZONTAL);
		TextView view = new TextView(parent.getContext());
		view.setText(websites.get(position).getName());
		
		ImageView imageView=new ImageView(parent.getContext());
		imageView.setImageResource(websites.get(position).getImageSource());
		imageView.setLayoutParams(new LayoutParams(128, 128));
		layout.addView(imageView);
		layout.addView(view);
		return layout;
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout layout=new LinearLayout(parent.getContext());
		layout.setOrientation(LinearLayout.HORIZONTAL);
		TextView view = new TextView(parent.getContext());
		view.setText(websites.get(position).getName());
		
		ImageView imageView=new ImageView(parent.getContext());
		imageView.setImageResource(websites.get(position).getImageSource());
		imageView.setLayoutParams(new LayoutParams(128, 128));
		layout.addView(imageView);
		layout.addView(view);
		return layout;
	}

	


}
