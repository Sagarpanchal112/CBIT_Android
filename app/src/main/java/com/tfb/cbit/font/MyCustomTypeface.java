package com.tfb.cbit.font;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;

import java.util.Hashtable;

public class MyCustomTypeface {
	 private static final Hashtable<String, Typeface> cache = new Hashtable<String, Typeface>();
	
	    public static Typeface getTypeFace(Context context, String assetPath) {
	        synchronized (cache) {
	            if (!cache.containsKey(assetPath)) {
	                try {
	                    Typeface typeFace = Typeface.createFromAsset(
	                            context.getAssets(), assetPath);
	                    cache.put(assetPath, typeFace);
						Log.e("TypeFaces", "Typeface loaded.");
	                } catch (Exception e) {
					//	new CallRequests(context).setExceptionLog(e.getMessage(), e.getStackTrace().toString());

						Log.e("TypeFaces", "Typeface not loaded.");
	                    return null;
	                }
	            }
	            return cache.get(assetPath);
	        }
	    }
}

