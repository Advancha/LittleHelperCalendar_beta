package com.example.myapplication;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;

/**
 * Created by Ира on 19.05.2017.
 */

public class AlertDialogFragment_Disppatch extends DialogFragment implements DialogInterface.OnClickListener {
    final String LOG_TAG = "myLogs";
    private int selected_id=0;
    private OnFragmentInteractionListener mListener;

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (getActivity() instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) getActivity();
        } else {
            throw new RuntimeException(getActivity().toString()
                    + " must implement OnFragmentInteractionListener");
        }
        //AlertDialog.Builder adb = new AlertDialog.Builder(getActivity())
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.dispatch_dialog_title)
                .setNegativeButton(R.string.dispatch_dialog_negative_button, this)
                .setPositiveButton(R.string.dispatch_dialog_positive_button, this)
                .setMessage(R.string.dispatch_dialog_message).setCancelable(true);

        return adb.create();

    }
    @Override
    public void onClick(DialogInterface dialog, int which) {
        int i = 0;
        switch (which) {
            case Dialog.BUTTON_POSITIVE:
                sendNotification();
                break;
            case Dialog.BUTTON_NEGATIVE:
              //  i = R.string.dispatch_dialog_negative_button;
                break;
            case Dialog.BUTTON_NEUTRAL:
                break;
            default:
                break;
        }
    }

    public void sendNotification() {
        final DBHelper dbHelper = new DBHelper(getActivity());
        final Cursor cursor = dbHelper.getCursorForVisitList(mListener.onFragmentDataRequest());
        cursor.moveToFirst();
        for (int i=0; i<cursor.getCount(); i++){
            String message = "Hi! You got a message from android!";
            String cellPhone=cursor.getString(cursor.getColumnIndexOrThrow(DBContract.TabClients.COLUMN_NAME_PHONE));
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(cellPhone, null, message, null, null);
            cursor.moveToNext();
        }

    }
}
