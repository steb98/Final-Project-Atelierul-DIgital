package com.buggy.tripbook;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class EditTripFragment extends Fragment {
    public EditTripFragment() {
        //must be empty
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MainActivity activity = (MainActivity)getActivity();
        activity.setActionBarTitle("Edit Trip");
        return inflater.inflate(R.layout.fragment_edit_trip, container, false);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //completare fielduri

        int pos = RecyclerViewAdapter.pos;
        TripItemModel tripElement = MainActivity.mtripList.get(pos);

        EditText etTripTitle = (EditText) getView().findViewById(R.id.editTripName);
        etTripTitle.setText(tripElement.getTripTitle());

        EditText etTripDestination = (EditText) getView().findViewById(R.id.editTripDestination);
        etTripDestination.setText(tripElement.getTripDestination());

        SeekBar sbTripPrice = (SeekBar) getView().findViewById(R.id.editTripPrice);
        final TextView tvTripPrice = (TextView) getView().findViewById(R.id.editTripPriceSliderText);
        sbTripPrice.setProgress(tripElement.getTripPrice()/10);
        tvTripPrice.setText(tripElement.getTripPrice()+" $");
        sbTripPrice.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                tvTripPrice.setText(seekBar.getProgress()*10+" $");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        RadioGroup rg = (RadioGroup) getView().findViewById(R.id.editTripType);
        String tripType = tripElement.getTripType().toString();
        if(tripType.equals("City Break")){
            rg.check(R.id.cityBreak);
        }
        if(tripType.equals("Sea Side")){
            rg.check(R.id.seaSide);
        }
        if(tripType.equals("Mountains")){
            rg.check(R.id.mountains);
        }

        TextView tvTripStartDate = (TextView)getView().findViewById(R.id.editTripStartDate);
        tvTripStartDate.setText(tripElement.getTripStartDay()+"/"+
                tripElement.getTripStartMonth()+"/"+
                tripElement.getTripStartYear());

        TextView tvTripEndDate = (TextView)getView().findViewById(R.id.editTripEndDate);
        tvTripEndDate.setText(tripElement.getTripEndDay()+"/"+
                tripElement.getTripEndMonth()+"/"+
                tripElement.getTripEndYear());

        RatingBar rbTripRating = (RatingBar) getView().findViewById(R.id.editTripRating);
        rbTripRating.setRating(tripElement.getTripRating());
    }
}
