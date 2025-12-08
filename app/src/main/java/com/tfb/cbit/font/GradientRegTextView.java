package com.tfb.cbit.font;

import android.content.Context;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;

import com.tfb.cbit.R;


public class GradientRegTextView extends AppCompatTextView {

    public GradientRegTextView(Context context) {
        super(context);
        this.setTypeface((MyCustomTypeface.getTypeFace(context, "fonts/seville_bold.ttf")));
        this.setPaintFlags(getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
    }

    public GradientRegTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTypeface((MyCustomTypeface.getTypeFace(context, "fonts/seville_bold.ttf")));
        this.setPaintFlags(getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
    }

    public GradientRegTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setTypeface((MyCustomTypeface.getTypeFace(context, "fonts/seville_bold.ttf")));
        this.setPaintFlags(getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        //Setting the gradient if layout is changed
        if (changed) {
            getPaint().setShader(new LinearGradient(0, 0, getWidth(), getHeight(),
                    ContextCompat.getColor(getContext(), R.color.start_color),
                    ContextCompat.getColor(getContext(), R.color.end_color),
                    Shader.TileMode.REPEAT));
        }
    }
}
