package com.tfb.cbit.utility;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.tfb.cbit.R;

import java.lang.ref.WeakReference;


public class CustomDialog {

   // public android.app.ProgressDialog progressDialog;
    public AlertDialog progressDialog;
    public WeakReference<Activity> weakActivity;
    public AlertDialog oneButtonDialog, twoButtonDialog;
    private AlertDialog alertListDialog;
    private Dialog dialogonebutton, dialogtwobutton,dialogprogress;

  public void displayProgress(Context context,String msg) {

        weakActivity = new WeakReference<Activity>((Activity) context);

        if (weakActivity.get() != null && !weakActivity.get().isFinishing()) {
            if (progressDialog == null) {
                //progressDialog = new android.app.ProgressDialog(context);
                //progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                AlertDialog.Builder progressBuilder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.AppTheme));
                LayoutInflater inflater = (LayoutInflater)context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
                View dialogView = inflater.inflate(R.layout.loading_dialog, null);
                TextView textView = dialogView.findViewById(R.id.tvMsg);
                textView.setText(msg);
                progressBuilder.setView(dialogView);
               // progressBuilder.setCancelable(false);
                progressDialog = progressBuilder.create();
               // progressDialog.show();

            } else {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();

                TextView textView = progressDialog.findViewById(R.id.tvMsg);
                assert textView != null;
                textView.setText(msg);
            }
            //progressDialog.setMessage("Loading");
            if (!((Activity) context).isFinishing()) {
                progressDialog.show();
            }
            progressDialog.setCancelable(false);
        }
    }


    public void dismissProgress(Context context) {
        weakActivity = new WeakReference<Activity>((Activity) context);

        if (weakActivity.get() != null && !weakActivity.get().isFinishing()) {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }


    public void dismissonedialog() {
        if (dialogonebutton != null && dialogonebutton.isShowing()) {
            dialogonebutton.dismiss();
        }
    }

    public  void dismissdialogtwobutton(){
        if (dialogtwobutton != null && dialogtwobutton.isShowing()) {
            dialogtwobutton.dismiss();
        }
    }

    public void showDialogTwoButton(Context context, String title, String message,
                                    String btnPositiveLabel, String btnNegativeLabel,
                                    DialogInterface.OnClickListener positiveButtonClickListener, DialogInterface.OnClickListener negativeButtonClickListener) {


        weakActivity = new WeakReference<Activity>((Activity) context);

        if (weakActivity.get() != null && !weakActivity.get().isFinishing()) {
            if (twoButtonDialog != null)
                twoButtonDialog.dismiss();

            AlertDialog.Builder twoButtonDialogBuilder = new AlertDialog.Builder(context);
            twoButtonDialogBuilder.setTitle(title);

            twoButtonDialogBuilder.setMessage(message);
            twoButtonDialogBuilder.setCancelable(false);


            if (positiveButtonClickListener == null) {
                twoButtonDialogBuilder.setPositiveButton(btnPositiveLabel,
                        new DialogInterface.OnClickListener()

                        {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        }

                );
            } else {
                twoButtonDialogBuilder.setPositiveButton(btnPositiveLabel, positiveButtonClickListener);
            }

            if (negativeButtonClickListener == null) {
                twoButtonDialogBuilder.setNegativeButton(btnNegativeLabel,
                        new DialogInterface.OnClickListener()

                        {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        }

                );
            } else {
                twoButtonDialogBuilder.setNegativeButton(btnNegativeLabel, negativeButtonClickListener);
            }


            twoButtonDialog = twoButtonDialogBuilder.create();
            twoButtonDialog.show();
        }
    }


    public void showDialogOneButton(Context context, String title, String message,
                                    String btnLabel, DialogInterface.OnClickListener buttonClickListener) {


        weakActivity = new WeakReference<>((Activity) context);

        if (weakActivity.get() != null && !weakActivity.get().isFinishing()) {
            if (twoButtonDialog != null)
                twoButtonDialog.dismiss();

            AlertDialog.Builder oneButtonDialogBuilder = new AlertDialog.Builder(context);
            oneButtonDialogBuilder.setTitle(title);

            oneButtonDialogBuilder.setMessage(message);
            oneButtonDialogBuilder.setCancelable(false);


            if (buttonClickListener == null) {
                oneButtonDialogBuilder.setNegativeButton(btnLabel,
                        new DialogInterface.OnClickListener()

                        {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        }

                );
            } else {
                oneButtonDialogBuilder.setNegativeButton(btnLabel, buttonClickListener);
            }

            oneButtonDialog = oneButtonDialogBuilder.create();
            oneButtonDialog.show();
        }
    }

    public void showDialogOneButtonUpdate(Context context, String title, String message,
                                          String btnLabel, DialogInterface.OnClickListener buttonClickListener) {
        AlertDialog.Builder oneButtonDialogBuilder = new AlertDialog.Builder(context);
        oneButtonDialogBuilder.setTitle(title);
        oneButtonDialogBuilder.setMessage(message);
        oneButtonDialogBuilder.setCancelable(false);
        if (buttonClickListener == null) {
            oneButtonDialogBuilder.setNegativeButton(btnLabel,
                    (dialog, id) -> dialog.cancel()
            );
        } else {
            oneButtonDialogBuilder.setNegativeButton(btnLabel, buttonClickListener);
        }
        oneButtonDialog = oneButtonDialogBuilder.create();
        oneButtonDialog.show();
    }
}
