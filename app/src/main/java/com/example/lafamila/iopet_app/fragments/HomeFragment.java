package com.example.lafamila.iopet_app.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.lafamila.iopet_app.R;
import com.example.lafamila.iopet_app.adapters.ChatItemAdapter;
import com.example.lafamila.iopet_app.adapters.HomeItemAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    public HomeFragment() {
    }
    ListView m_ListView;
    HomeItemAdapter m_Adapter;


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
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        m_Adapter = new HomeItemAdapter();
        m_ListView = (ListView) rootView.findViewById(R.id.lv_home);
        m_Adapter.add("asdf", "이미지 있는 글", "이미지가 있는 글의 글 내용이에요오오오오옹");
        m_Adapter.add("", "이미지 없는 글", "이미지가 없는 글의 글 내용이에요오오오오옹");
        m_ListView.setAdapter(m_Adapter);

//        TextView textView = (TextView) rootView.findViewById(R.id.section_label);
//        textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
        return rootView;
    }
}
