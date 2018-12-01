package com.example.lafamila.iopet_app.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.lafamila.iopet_app.MedicineActivity;
import com.example.lafamila.iopet_app.R;
import com.example.lafamila.iopet_app.util.Util;


/**
 * A simple {@link Fragment} subclass.
 */
public class MedicineFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    public MedicineFragment() {
    }


//    public static HomeFragment newInstance(int sectionNumber) {
//        HomeFragment fragment = new HomeFragment();
//        Bundle args = new Bundle();
//        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_medicine, container, false);
        Button add = (Button)rootView.findViewById(R.id.btn_addMedicine);
        Button eat = (Button)rootView.findViewById(R.id.btn_eatMedicine);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MedicineActivity.class);
                startActivityForResult(intent, Util.ADD_MEDICINE);
            }
        });

        //저장된 정보가 있으면, eat 을 시간과 그 여부에 따라 활성화
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Util.ADD_MEDICINE && resultCode == Util.ADD_MEDICINE){
            String name = data.getStringExtra("name");
            boolean morning = data.getBooleanExtra("morning", false);
            boolean lunch = data.getBooleanExtra("lunch", false);
            boolean dinner = data.getBooleanExtra("dinner", false);

            //DB나 기타등등에 저장..

        }
    }
}
