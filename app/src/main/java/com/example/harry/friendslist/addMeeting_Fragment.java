package com.example.harry.friendslist;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.harry.friendslist.model.CurrentUser;
import com.example.harry.friendslist.model.Friend;
import com.example.harry.friendslist.model.Model;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class addMeeting_Fragment extends Fragment {

    private static final String TAG = addfriend_Fragment.class.getSimpleName();
    private Model model = Model.getInstance();
    private ListView friendsList;
    private CurrentUser user;
    private Double lat;
    private Double lng;
    private Boolean fromFrag = false;
    private LatLng latLng;


    public void onViewCreated(View view, @Nullable Bundle savedInstances){
        super.onViewCreated(view, savedInstances);
        getActivity().setTitle("Friends List");
        user = model.getCurrentUser();
        makeMeeting();

    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.addmeeting_fragment, container, false);
        if(getArguments().size() > 1){
            lat = getArguments().getDouble("lat");
            lng = getArguments().getDouble("lng");
            latLng = new LatLng(lat, lng);
        }else if (getArguments().size() == 1){
            fromFrag = getArguments().getBoolean("tag");
        }
        return view;
    }

    private void makeMeeting (){
        boolean addMore = true;
        final int startHour =24, startMin = 24;

        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        //First dialog box with Meeting title, start and end time

        alert.setTitle("Create new meeting");

        final EditText title = new EditText(getContext());
        title.setHint("Title");
        layout.addView(title);

        final EditText startTime = new EditText(getContext());
        startTime.setOnClickListener(new View.OnClickListener() {

            //When starttime edit text is clicked a clock widget appears to select the time
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        startTime.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });
        startTime.setHint("Start Time");
        startTime.setKeyListener(null);
        layout.addView(startTime);

        final EditText endTime = new EditText(getContext());
        endTime.setOnClickListener(new View.OnClickListener() {

            //When endTime edit text is clicked a clock widget appears to select the time
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                final int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String sTime = startTime.getText().toString();
                        String time[] = sTime.split(":");

                        int hour = Integer.parseInt(time[0]);
                        int min = Integer.parseInt(time[1]);

                        if(hour == selectedHour){
                            if(selectedMinute > min){
                                endTime.setText( selectedHour + ":" + selectedMinute);
                            }
                        }else if(selectedHour > hour){
                            endTime.setText( selectedHour + ":" + selectedMinute);
                        }
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });
        endTime.setHint("End Time");
        endTime.setKeyListener(null);
        layout.addView(endTime);

        alert.setView(layout);

        //On alertdialog box 1 OK, create a 2nd dialog box to get the selected friends
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                final String mName = title.getText().toString();
                final String sTime = startTime.getText().toString();
                final String eTime = endTime.getText().toString();
                if(mName.equals("")|| sTime.equals("") || eTime.equals("")){
                    return;
                }
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());

                //Loads all friends into a multichoice selector

                final List<Friend> friendList = user.getFriendsList();
                final ArrayList<Integer> selectedItems = new ArrayList();
                final CharSequence[] friends = new CharSequence[friendList.size()];
                for(int i = 0; i < friendList.size(); i++){
                    Friend friend = friendList.get(i);
                    friends[i] = friend.getName();
                }
                alert.setTitle("Select Friends");
                alert.setMultiChoiceItems(friends, null,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            // indexSelected contains the index of item (of which checkbox checked)
                            @Override

                            public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                                if (isChecked) {
                                    // If the user checked the item, add it to the selected items
                                    selectedItems.add(indexSelected);
                                } else if (selectedItems.contains(indexSelected)) {
                                    // Else, if the item is already in the array, remove it
                                    selectedItems.remove(Integer.valueOf(indexSelected));
                                }
                            }
                        });
                //Finally create meeting
                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        List<Friend> meetingFriends = new LinkedList();
                        for(int i = 0; i < selectedItems.size(); i++){
                            meetingFriends.add(friendList.get(selectedItems.get(i)));
                        }
                        user.newMeeting(mName, sTime, eTime, meetingFriends, latLng);
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {}});
                alert.show();
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {}});

        alert.show();

    }
}
