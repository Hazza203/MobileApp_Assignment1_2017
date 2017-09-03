package com.example.harry.friendslist;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.harry.friendslist.model.Friend;
import com.example.harry.friendslist.model.Model;

import java.util.LinkedList;
import java.util.List;

public class listFriends extends Fragment implements
        AdapterView.OnItemClickListener{

    private static final String TAG = addfriend_Fragment.class.getSimpleName();
    private Model model = Model.getInstance();
    private ListView friendsList;


    public void onViewCreated(View view, @Nullable Bundle savedInstances){
        super.onViewCreated(view, savedInstances);
        getActivity().setTitle("Friends List");
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.friendslist, container, false);
        final List<Friend> friends = model.getCurrentUser().getFriendsList();
        final LinkedList<String> names = new LinkedList<>();
        friendsList = view.findViewById(R.id.friendslist);
        Log.i(TAG, Integer.toString(friends.size()));

        for(int i=0; i < friends.size(); i++){
            names.add(friends.get(i).getName());
        }

        final ArrayAdapter<String> listViewAdapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                names
        );
        friendsList.setAdapter(listViewAdapter);
        friendsList.setOnItemClickListener(this);
        friendsList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getActivity(), "You just removed: " + friends.get(i).getName(),
                        Toast.LENGTH_LONG).show();
                String id = friends.get(i).getId();
                friends.remove(i);
                model.getCurrentUser().removeAFriend(id);
                names.remove(i);
                listViewAdapter.notifyDataSetChanged();
                return true;
            }
        });

        return view;
    }
    public void onItemClick(
            AdapterView<?> parent, View item, int position, long rowID) {

    }
}
