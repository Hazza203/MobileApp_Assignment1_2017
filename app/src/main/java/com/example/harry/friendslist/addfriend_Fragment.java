package com.example.harry.friendslist;

import android.content.ContentResolver;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.widget.AdapterView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.provider.ContactsContract;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.harry.friendslist.model.Friend;

import java.util.ArrayList;
import java.util.LinkedList;


/**
 * Created by harry on 1/09/2017.
 */

public class addfriend_Fragment extends Fragment implements
        AdapterView.OnItemClickListener {

    private String LOG_TAG = this.getClass().getName();

    private final static int[] TO_IDS = {
            android.R.id.text1
    };
    private static final String TAG = addfriend_Fragment.class.getSimpleName();

    private ListView mContactsList;


    private LinkedList<Friend> friends = new LinkedList<>();


    public addfriend_Fragment(){}

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstances){
        Log.i(LOG_TAG, "Add friend fragment");
        super.onViewCreated(view, savedInstances);
        getActivity().setTitle("Add Friend");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState){


        View view = inflater.inflate(R.layout.addfriend_fragment, container, false);
        mContactsList = view.findViewById(R.id.list);

        friends = retrieveContactName();
        LinkedList<String> names = new LinkedList<>();

        for(int i=0;friends.size()> i; i++){
            names.add( friends.get(i).getName());
        }

        ArrayAdapter<String> listViewAdapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                names
        );
        mContactsList.setAdapter(listViewAdapter);
        mContactsList.setOnItemClickListener(this);
        return view;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }
    @Override
    public void onItemClick(
            AdapterView<?> parent, View item, int position, long rowID) {

        Log.d(TAG, "Name you clicked on:  "+friends.get(position).getName());

}

    private LinkedList<Friend> retrieveContactName() {

        LinkedList<Friend> contacts = new LinkedList<Friend>();
        ContentResolver cr = getActivity().getContentResolver();
        // querying contact data store
        String name;
        String email = "fake@fake.fake";
        Cursor cursor = getActivity().getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                Cursor cur1 = cr.query(
                        ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                        new String[]{id}, null);
                name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (cur1.moveToNext()) {
                    email = cur1.getString(cur1.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                }
                contacts.add(new Friend(name,email));
            }
        }
        cursor.close();

        return contacts;
    }
}
