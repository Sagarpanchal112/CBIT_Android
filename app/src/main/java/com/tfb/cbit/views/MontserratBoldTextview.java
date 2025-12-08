package com.tfb.cbit.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

public class MontserratBoldTextview extends AppCompatTextView {

/*
 * Caches typefaces based on their file path and name, so that they don't have to be created every time when they are referenced.
 */
private static Typeface mTypeface;

public MontserratBoldTextview(final Context context) {
    this(context, null);
}

public MontserratBoldTextview(final Context context, final AttributeSet attrs) {
    this(context, attrs, 0);
}

public MontserratBoldTextview(final Context context, final AttributeSet attrs, final int defStyle) {
    super(context, attrs, defStyle);

     if (mTypeface == null) {
         mTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/Montserrat-Bold.otf");
     }
     setTypeface(mTypeface);
}

}