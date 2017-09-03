package com.example.harry.friendslist;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
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

public class ListFriends extends Fragment implements
        AdapterView.OnItemClickListener{

    private static final String TAG = addfriend_Fragment.class.getSimpleName();
    private Model model = Model.getInstance();
    private List<Friend> friends = new LinkedList<>();
    private ListView friendsList;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstances){
        super.onViewCreated(view, savedInstances);
        getActivity().setTitle("Friends List");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.friendsList, container, false);
        friends = model.getCurrentUser().getFriendsList();
        friendsList = view.findViewById(R.id.list);
        LinkedList<String> names = new LinkedList<>();

        for(int i=0;friends.size()> i; i++){
            names.add( friends.get(i).getName());
        }

        ArrayAdapter<String> listViewAdapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                names
        );
        friendsList.setAdapter(listViewAdapter);
        friendsList.setOnItemClickListener(this);
        friendsList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                model.getCurrentUser().removeAFriend(friends.get(i).getId());

                Toast.makeText(getActivity(), "You just removed: " + friends.get(position).getName(),
                        Toast.LENGTH_LONG).show();
                friends.remove(i);
            }
        });

        return view;
    }


    @Override
    public boolean onItemLongClick
            (AdapterView<?> adapterView, View view, final int position, long l) {

    }
}
