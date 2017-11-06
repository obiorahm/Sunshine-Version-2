package com.example.android.sunshine.app;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

/**
 * Created by mgo983 on 11/6/17.
 */

public class TextDialog extends DialogFragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.text_dialog, container, false);

        Button okButton = (Button)  rootView.findViewById(R.id.text_dialog_ok);

        Button cancelButton = (Button) rootView.findViewById(R.id.text_dialog_cancel);

        final EditText textViewCategory = (EditText) rootView.findViewById(R.id.text_dialog_category);

        final DatabaseReference mFirebaseReference = FirebaseDatabase.getInstance().getReference("word_categories").child("");


        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String category = textViewCategory.getText().toString();

                WordCategories wordCategories = new WordCategories(category);

                mFirebaseReference.child(category).setValue(wordCategories);

            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
}
