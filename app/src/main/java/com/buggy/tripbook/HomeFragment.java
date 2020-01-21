
package com.buggy.tripbook;


import android.os.Bundle;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    ArrayList<TripItemModel> tripList = new ArrayList<>();
    static public RecyclerViewAdapter adapter;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root =inflater.inflate(R.layout.fragment_home, container, false);
        ((MainActivity)getActivity()).setActionBarTitle("Home");

        FloatingActionButton fab = root.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.hide(getActivity().getSupportFragmentManager().findFragmentById(R.id.flMain));
                ft.add(R.id.flMain,new AddFragment());
                ft.addToBackStack(null);
                ft.commit();
            }
        });


            //MainActivity.createPlaceholderTrips();
            RecyclerView rv = (RecyclerView) root.findViewById(R.id.recyclerView);
            adapter = new RecyclerViewAdapter(MainActivity.mtripList, getContext());
            rv.setLayoutManager(new LinearLayoutManager(getActivity()));
            rv.setAdapter(adapter);



        // Inflate the layout for this fragment
        return root;
    }

}
