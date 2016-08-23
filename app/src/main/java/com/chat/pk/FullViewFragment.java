package com.chat.pk;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chat.pk.Util.Constants;
import com.chat.pk.View.TouchImageView;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * Created by //pintu kumar patil 9977638049 india on 30/11/15.
 */
public class FullViewFragment extends Fragment {
	Bundle bundle;
	String imgPath,fileName="";
	View view=null;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		if(view != null) {
			ViewGroup parent = (ViewGroup) view.getParent();
			if(parent != null) {
				parent.removeView(view);
			}
		}
		try {
			view = inflater.inflate(R.layout.fullview_fragment, container, false);
		}
		catch(InflateException e){
			// map is already there, just return view as it is
		}
		return view;
	
	}
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		bundle = getArguments();
		if (bundle != null) {
			imgPath = bundle.getString(Constants.CN_FILE_PATH);
			fileName= bundle.getString(Constants.CN_FILE_NAME);
		}
		TouchImageView iv = (TouchImageView) view.findViewById(R.id.tiv_image);
	        try {
				 Context context=iv.getContext();
	        	 Picasso.with(context)
				.load(new File(imgPath)).placeholder(R.drawable.avatar_group_tmp)
				.resize(300,300)
				.into(iv);
			} catch (NullPointerException e) {
				// TODO: handle exception
				e.printStackTrace();
				
			}catch (OutOfMemoryError e) {
				// TODO: handle exception
				e.printStackTrace();
			}
	}
}
