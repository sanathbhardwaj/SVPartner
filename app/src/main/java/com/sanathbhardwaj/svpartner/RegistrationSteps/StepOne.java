package com.sanathbhardwaj.svpartner.RegistrationSteps;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.sanathbhardwaj.svpartner.R;

public class StepOne extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view =  inflater.inflate(R.layout.step_one_layout, container, false);
        Button buttonNext = (Button)view.findViewById(R.id.buttonNext);

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = getFragmentManager().beginTransaction().addToBackStack("sanath");;
                //ft.replace(R.id.flContainer,new StepTwo());
                ft.commit();
            }
        });
        return view;
    }
}


        // Inflate the layout for this fragment

//        Button next = (Button)view.findViewById(R.id.next);


//        next.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                FragmentTransaction ft = getFragmentManager().beginTransaction();
//                ft.replace(R.id.flContainer,new StepTwo());
//                ft.commit();
//            }
//        });

