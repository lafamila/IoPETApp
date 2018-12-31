package com.example.lafamila.iopet_app.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.lafamila.iopet_app.MedicineActivity;
import com.example.lafamila.iopet_app.R;
import com.example.lafamila.iopet_app.adapters.HomeItemAdapter;
import com.example.lafamila.iopet_app.adapters.MedicineItemAdapter;
import com.example.lafamila.iopet_app.util.Util;


/**
 * A simple {@link Fragment} subclass.
 */
public class MedicineFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    MedicineItemAdapter m_Adapter;
    ListView lv;
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
        lv = (ListView)rootView.findViewById(R.id.lv_medicine);
        lv.setHeaderDividersEnabled(false);
        lv.setDividerHeight(0);
        final View header = getLayoutInflater().inflate(R.layout.item_medicine, null, false) ;
        ((TextView)header.findViewById(R.id.name)).setText("");
        ((TextView)header.findViewById(R.id.morning)).setText("아침");
        ((TextView)header.findViewById(R.id.lunch)).setText("점심");
        ((TextView)header.findViewById(R.id.dinner)).setText("저녁");
        lv.addHeaderView(header);

        m_Adapter = new MedicineItemAdapter();
        lv.setAdapter(m_Adapter);

//        Button eat = (Button)rootView.findViewById(R.id.btn_eatMedicine);

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
            int type = data.getIntExtra("count", 0);

            m_Adapter.add(name, type, morning, lunch, dinner);
            lv.setAdapter(m_Adapter);
            //DB나 기타등등에 저장..

        }
    }
}
