package com.example.androidlabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class DetailsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_details, container, false);
        TextView mTextMessage = (TextView) view.findViewById(R.id.text_message_here);
        mTextMessage.setText("Message: " + getArguments().getString(ChatRoomActivity.MESSAGE_CONTENT));
        TextView mTextId = (TextView) view.findViewById(R.id.text_id);
        mTextId.setText("ID= " + getArguments().getString(ChatRoomActivity.DATABASE_ID));
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox_isSend);
        checkBox.setChecked(getArguments().getBoolean(ChatRoomActivity.MESSAGE_IS_SEND));

        view.findViewById(R.id.btn_hide).setOnClickListener(v -> {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .remove(this)
                    .addToBackStack(null)
                    .commit();
        });
        return view;
    }
}