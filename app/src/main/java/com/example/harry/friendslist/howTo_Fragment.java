package com.example.harry.friendslist;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by harry on 1/09/2017.
 */

public class howTo_Fragment extends Fragment {

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstances){
        super.onViewCreated(view, savedInstances);
        getActivity().setTitle("How To");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.howto_fragment, container, false);
        TextView aboutTextView = view.findViewById(R.id.about_text_view);

        String aboutText = (getString(R.string.how_to));
        aboutTextView.setText(aboutText);
        return view;
    }
}
