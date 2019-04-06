package com.iopet.lafamila.iopet_app.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.iopet.lafamila.iopet_app.ChatActivity;
import com.example.lafamila.iopet_app.R;


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

    int roomID;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        roomID = getArguments().getInt("room_id");

        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);

        Button chat = (Button) rootView.findViewById(R.id.btn_chat);
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(roomID != 0){
                    Intent intent = new Intent(getContext(), ChatActivity.class);
                    intent.putExtra("room_id", roomID);
                    intent.putExtra("type", 0);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(getContext(), "고객 전용입니다.", Toast.LENGTH_LONG).show();
                }
            }
        });

        Button emergency = (Button) rootView.findViewById(R.id.btn_emergency);
        emergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(roomID != 0){
                    Intent intent = new Intent(getContext(), ChatActivity.class);
                    intent.putExtra("room_id", roomID);
                    intent.putExtra("type", 1);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(getContext(), "고객 전용입니다.", Toast.LENGTH_LONG).show();
                }
            }
        });
        return rootView;
    }





}
