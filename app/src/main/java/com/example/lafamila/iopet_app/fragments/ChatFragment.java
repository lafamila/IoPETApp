package com.example.lafamila.iopet_app.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.lafamila.iopet_app.ChatActivity;
import com.example.lafamila.iopet_app.R;
import com.example.lafamila.iopet_app.util.Util;


public class ChatFragment extends Fragment {


    public ChatFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);
        Button emergency = (Button) rootView.findViewById(R.id.btn_emergency);
        emergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ChatActivity.class);
                intent.putExtra("room_id", Util.TEMP_ROOM_ID);
                startActivity(intent);
            }
        });
        return rootView;
    }
}
