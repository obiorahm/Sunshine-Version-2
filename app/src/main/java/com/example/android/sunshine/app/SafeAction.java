package com.example.android.sunshine.app;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.concurrent.Callable;

/**
 * Created by mgo983 on 8/18/17.
 */

public class SafeAction extends DialogFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.safe_action_dialog, container, false);

        TextView textView = (TextView) rootView.findViewById(R.id.safety_question);
        Bundle bundle = this.getArguments();
        String safetyQuestion = bundle.getString(GalleryActivity.EXTRA_SAFE_ACTION_MSG);
        final String safetyMenuID = bundle.getString(GalleryActivity.EXTRA_SAFE_ACTION_MENU_ITEM);
        textView.setText(safetyQuestion);


        Button btnOk = (Button) rootView.findViewById(R.id.safety_ok);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((OnokOrCancel) getActivity()).okOrCancel(true, safetyMenuID );
                dismiss();
            }
        });

        Button btnCancel = (Button) rootView.findViewById(R.id.safety_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((OnokOrCancel) getActivity()).okOrCancel(false, safetyMenuID);
                dismiss();
            }
        });

        // Inflate the layout to use as dialog or embedded fragment
        return rootView;
    }

    /** The system calls this only when creating the layout in a dialog. */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.


        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);


        return dialog;
    }

    //an interface that helps me to pass information about the button pressed
    //"ok" is passed when ok is pressed and cancel is passed when cancel is pressed.
    public interface OnokOrCancel{
        public void okOrCancel(boolean okOrCancel, String menuID);
    }
}
