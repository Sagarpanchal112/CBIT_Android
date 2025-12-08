package com.tfb.cbit.utility;

import android.content.Context;
import com.google.android.material.textfield.TextInputLayout;
import android.util.Patterns;
import android.widget.EditText;

import com.tfb.cbit.R;


public class MyValidator {


    public static boolean isBlank(Context context, EditText editText, String msg, int snackbarPos, int minVal, int maxVal) {
        String _strEditTextVal = editText.getText().toString().trim();

       /* if (_strEditTextVal.length() == 0 && _strEditTextVal.length() > maxVal && _strEditTextVal.length() < minVal) {
            MySanckbar.showSnackBar(context,msg, AppConfig.SnackBarPosition.TOP, AppConfig.SnackBarType.LENGTH_LONG, AppConfig.SnackBarBgColor.RED, AppConfig.SnackBarTxtColor.WHITE);

            editText.requestFocus();
            return false;
        } else {
            return true;
        }*/

        if (_strEditTextVal.length() == 0) {
            /*MySanckbar.showSnackBar(context
                    , context.getString(R.string.please_enter_prefix) + " " + msg + "."
                    , snackbarPos, AppConfig.SnackBarType.LENGTH_LONG, AppConfig.SnackBarBgColor.RED
                    , AppConfig.SnackBarTxtColor.WHITE);*/
            editText.requestFocus();
            return false;
        } else if (_strEditTextVal.length() < minVal) {
           /* MySanckbar.showSnackBar(context
                    , context.getString(R.string.min_text) + " " + minVal + " " + context.getString(R.string.character_required) + " " + msg + "."
                    , snackbarPos, AppConfig.SnackBarType.LENGTH_LONG, AppConfig.SnackBarBgColor.RED
                    , AppConfig.SnackBarTxtColor.WHITE);*/
            editText.requestFocus();
            return false;
        } else if (_strEditTextVal.length() > maxVal) {
            //MySanckbar.showSnackBar(context, context.getString(R.string.max_text) + " " + maxVal + " " + context.getString(R.string.character_required) + " " + msg + ".", snackbarPos, AppConfig.SnackBarType.LENGTH_LONG, AppConfig.SnackBarBgColor.RED, AppConfig.SnackBarTxtColor.WHITE);
            editText.requestFocus();
            return false;
        } else {
            return true;
        }
    }


    public static boolean isBlank(Context context, EditText editText, String msg, int snackbarPos) {
        String _strEditTextVal = editText.getText().toString().trim();
        if (_strEditTextVal.length() == 0) {
          /*  MySanckbar.showSnackBar(context
                    , context.getString(R.string.please_enter_prefix) + " " + msg + "."
                    , snackbarPos, AppConfig.SnackBarType.LENGTH_LONG, AppConfig.SnackBarBgColor.RED
                    , AppConfig.SnackBarTxtColor.WHITE);*/
            editText.requestFocus();
            return false;
        } else {
            return true;
        }
    }


    public static boolean isNotBlank(Context context, EditText editText, String msg, int snackbarPos, int minVal, int maxVal) {

        String _strEditTextVal = editText.getText().toString().trim();
        if (_strEditTextVal.length() == 0) {
            return true;
        } else if (_strEditTextVal.length() < minVal) {
           /* MySanckbar.showSnackBar(context
                    , context.getString(R.string.min_text) + " " + minVal + " " + context.getString(R.string.character_required) + " " + msg + "."
                    , snackbarPos, AppConfig.SnackBarType.LENGTH_LONG, AppConfig.SnackBarBgColor.RED
                    , AppConfig.SnackBarTxtColor.WHITE);*/
            editText.requestFocus();
            return false;
        } else if (_strEditTextVal.length() > maxVal) {
          //  MySanckbar.showSnackBar(context, context.getString(R.string.max_text) + " " + maxVal + " " + context.getString(R.string.character_required) + " " + msg + ".", snackbarPos, AppConfig.SnackBarType.LENGTH_LONG, AppConfig.SnackBarBgColor.RED, AppConfig.SnackBarTxtColor.WHITE);
            editText.requestFocus();
            return false;
        } else {
            return true;
        }

    }

    public static boolean isValidEmail(Context context, String msg, EditText editText) {
        if (!Patterns.EMAIL_ADDRESS.matcher(editText.getText().toString()).matches()) {
            // MySanckbar.showSnackBar(context, msg, AppConfig.SnackBarPosition.TOP, AppConfig.SnackBarType.LENGTH_LONG, AppConfig.SnackBarBgColor.RED, AppConfig.SnackBarTxtColor.WHITE);
            editText.setError(msg);
            editText.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    public static boolean isValidEmailETAndTextInputErr(Context context, String msg, EditText editText, TextInputLayout textInputLayout) {
        if (!(editText.getText().toString().trim().toLowerCase()
                .matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"))) {
            // MySanckbar.showSnackBar(context, msg, AppConfig.SnackBarPosition.TOP, AppConfig.SnackBarType.LENGTH_LONG, AppConfig.SnackBarBgColor.RED, AppConfig.SnackBarTxtColor.WHITE);
            textInputLayout.setError(msg);
            editText.requestFocus();
            return false;
        } else {
            textInputLayout.setErrorEnabled(false);
            return true;
        }
    }


    public static boolean isPasswordSameETError(Context context, EditText edtPassword1, EditText edtPassword2, String msg) {
        String strPassword1 = edtPassword1.getText().toString().trim();
        String strPassword2 = edtPassword2.getText().toString().trim();
        if (!strPassword1.equals(strPassword2)) {
            edtPassword2.setError(msg);
            edtPassword2.requestFocus();
            return false;
        } else {
            return true;
        }
    }


    public static boolean isBlankETError(Context context, EditText editText, String msg, int minVal, int maxVal) {
        String strEditTextVal = editText.getText().toString().trim();
        if (strEditTextVal.length() == 0) {
            editText.setError(msg);
            editText.requestFocus();
            return false;
        } else if (strEditTextVal.length() < minVal) {
            editText.setError(context.getString(R.string.min_text) + " " + minVal + " " + context.getString(R.string.character_requiredeterr));
            editText.requestFocus();
            return false;
        } else if (strEditTextVal.length() > maxVal) {
            editText.setError(context.getString(R.string.max_text) + " " + maxVal + " " + context.getString(R.string.character_requiredeterr));
            editText.requestFocus();
            return false;
        } else {
            editText.setError(null);
            return true;
        }
    }


    public static boolean isBlankETAndTextInputError(Context context, EditText editText, TextInputLayout textInputLayout, String msg, int minVal, int maxVal) {
        String strEditTextVal = editText.getText().toString().trim();
        if (strEditTextVal.length() == 0) {
            textInputLayout.setError(msg);
            editText.requestFocus();
            return false;
        } else if (strEditTextVal.length() < minVal) {
            textInputLayout.setError(context.getString(R.string.min_text) + " " + minVal + " " + context.getString(R.string.character_requiredeterr));
            editText.requestFocus();
            return false;
        } else if (strEditTextVal.length() > maxVal) {
            textInputLayout.setError(context.getString(R.string.max_text) + " " + maxVal + " " + context.getString(R.string.character_requiredeterr));
            editText.requestFocus();
            return false;
        } else {
            textInputLayout.setErrorEnabled(false);
            return true;
        }
    }

    public static boolean isPasswordSameETAndTextInputError(Context context, EditText edtPassword1, EditText edtPassword2, TextInputLayout textInputLayout2, String msg) {
        String strPassword1 = edtPassword1.getText().toString().trim();
        String strPassword2 = edtPassword2.getText().toString().trim();
        if (!strPassword1.equals(strPassword2)) {
            textInputLayout2.setError(msg);
            edtPassword2.requestFocus();
            return false;
        } else {
            textInputLayout2.setErrorEnabled(false);
            return true;
        }
    }

    public static boolean isValidSpinner(Context context, int position, String msg) {
        if (position <= 0) {
            /*MySanckbar.showSnackBar(context, context.getString(R.string.please_select_prefix) + " " + msg + ".", AppConfig.SnackBarPosition.TOP,
                    AppConfig.SnackBarType.LENGTH_LONG, AppConfig.SnackBarBgColor.RED,
                    AppConfig.SnackBarTxtColor.WHITE);*/
            return false;
        } else

        {
            return true;
        }

    }


    /*public static boolean isEmptyReview(Context context, CustomRatingBar customRatingBar) {

        if (customRatingBar.getScore() == 0) {
            MySanckbar.showSnackBar(context, context.getString(R.string.please_select_review), AppConfig.SnackBarPosition.TOP,
                    AppConfig.SnackBarType.LENGTH_LONG, AppConfig.SnackBarBgColor.RED,
                    AppConfig.SnackBarTxtColor.WHITE);
            return false;
        } else {
            return true;
        }
    }*/


}
