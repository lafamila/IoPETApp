package com.example.lafamila.iopet_app.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.lafamila.iopet_app.R;
import com.example.lafamila.iopet_app.adapters.ChatItemAdapter;
import com.example.lafamila.iopet_app.adapters.HomeItemAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class ResultFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    public ResultFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_result, container, false);

//
//        m_ListView.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView absListView, int i) {
//
//            }
//
//            @Override
//            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
//                int index = m_ListView.getFirstVisiblePosition();
//                View v = m_ListView.getChildAt(0);
//                int top = (v == null)? 0: v.getTop();
//                m_ListView2.setSelectionFromTop(index, top);
//            }
//        });


//        TextView textView = (TextView) rootView.findViewById(R.id.section_label);
//        textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
        return rootView;
    }
}
