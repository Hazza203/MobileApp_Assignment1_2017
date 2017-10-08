package com.example.harry.friendslist;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TimePicker;

import com.example.harry.friendslist.model.CurrentUser;
import com.example.harry.friendslist.model.Friend;
import com.example.harry.friendslist.model.Meeting;
import com.example.harry.friendslist.model.Model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by harry on 1/09/2017.
 */

public class ViewMeetings_Fragment extends Fragment implements View.OnClickListener{

    Model model;
    CurrentUser user;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstances){
        super.onViewCreated(view, savedInstances);
        getActivity().setTitle("View Meetings");
        Button addMeetingButton = (Button)view.findViewById(R.id.addMeetingButton);
        addMeetingButton.setOnClickListener(this);
    }

    //Create the listview
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.viewmeetings_fragment, container, false);
        final ListView meetingLV = view.findViewById(R.id.meetingLV);



        model = Model.getInstance();
        user = model.getCurrentUser();
        //Get the model and current user
        try {

            Date time = DateFormat.getTimeInstance(DateFormat.MEDIUM).parse("12:00:00 PM");
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date dob = dateFormat.parse("01/01/1970");

        } catch (ParseException e) {
            e.printStackTrace();
        }

        final List<Meeting> meetings = user.getMeetingList();

        final ArrayList<String> meetingItems = new ArrayList<>();

        for(int i = 0; i < meetings.size(); i++){
            Meeting meeting = meetings.get(i);
            meetingItems.add(meeting.getTitle());
        }

        //Load the meeting titles into the listview
        final ArrayAdapter<String> listViewAdapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                meetingItems
        );

        meetingLV.setAdapter(listViewAdapter);

        //Wait for listview item clicked, display alert box with meeting info which is editable
        meetingLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                final int pos = position;
                final List<Meeting> meetings = user.getMeetingList();
                final Meeting meeting = meetings.get(position);

                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());

                LinearLayout layout = new LinearLayout(getContext());
                layout.setOrientation(LinearLayout.VERTICAL);

                alert.setTitle("Edit Meeting Details");

                final EditText title = new EditText(getContext());
                title.setText(meeting.getTitle());
                layout.addView(title);

                final EditText startTime = new EditText(getContext());
                startTime.setText(meeting.getStartTime());
                startTime.setOnClickListener(new View.OnClickListener() {

                    //Click on start time edit text to bring up a time picker
                    @Override
                    public void onClick(View v) {
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
                startTime.setKeyListener(null);
                layout.addView(startTime);

                final EditText endTime = new EditText(getContext());
                endTime.setText(meeting.getEndTime());
                endTime.setOnClickListener(new View.OnClickListener() {

                    //Click on end time edit text to bring up a time picker
                    @Override
                    public void onClick(View v) {
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

                //On alert dialog OK, bring up second alert dialog to list
                //Friends on a multichoice selector
                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        final String mName = title.getText().toString();
                        final String sTime = startTime.getText().toString();
                        final String eTime = endTime.getText().toString();
                        if(mName.equals("")|| sTime.equals("") || eTime.equals("")){
                            return;
                        }

                        final List<Friend> friendList = user.getFriendsList();
                        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());

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
                        //Update meeting
                        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                List<Friend> newFriends = new LinkedList();
                                for(int i = 0; i < selectedItems.size(); i++){
                                    newFriends.add(friendList.get(selectedItems.get(i)));
                                }
                                user.removeMeeting(meeting.getID());
                                user.newMeeting(mName, sTime, eTime, newFriends, meeting.getLocation());
                                meetingItems.remove(pos);
                                List<Meeting> meetings = user.getMeetingList();
                                Meeting meeting = meetings.get(pos);
                                meetingItems.add(meeting.getTitle());

                                listViewAdapter.notifyDataSetChanged();
                            }
                        });

                        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // Canceled.
                            }
                        });
                        alert.show();
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });

                alert.show();
            }
        });

        //On long click listener which brings up alert dialog box to delete item

        meetingLV.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {
                AlertDialog.Builder adb=new AlertDialog.Builder(getContext());
                adb.setTitle("Delete?");
                adb.setMessage("Are you sure you want to delete ID: " + (position +1));
                final int positionToRemove = position;
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Meeting mToRemove = meetings.get(position);
                        meetings.remove(position);
                        user.removeMeeting(mToRemove.getID());
                        meetingItems.remove(position);
                        listViewAdapter.notifyDataSetChanged();
                    }});
                adb.show();
                return true;
            }
        });

        return view;
    }

    @Override
    public void onClick(View v){
        Bundle bundle = new Bundle();
        bundle.putBoolean("tag", true);
        Fragment fragment = new addMeeting_Fragment();
        fragment.setArguments(bundle);
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.add(fragment, "addMeetingFrag");
        ft.commit();
    }

    @Override
    public void onResume(){
        super.onResume();
    }
}
