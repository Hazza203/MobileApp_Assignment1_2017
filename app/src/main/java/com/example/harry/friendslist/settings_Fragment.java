package com.example.harry.friendslist;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by harry on 1/09/2017.
 */

public class settings_Fragment extends Fragment {

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstances){
        super.onViewCreated(view, savedInstances);
        getActivity().setTitle("Add Friend");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){

        return inflater.inflate(R.layout.addfriend_fragment, container, false);
    }
}
