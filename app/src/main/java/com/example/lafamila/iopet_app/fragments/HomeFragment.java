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
public class HomeFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    public HomeFragment() {
    }
    ListView m_ListView, m_ListView2;
    HomeItemAdapter m_Adapter, m_Adapter2;


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
        m_ListView = (ListView) rootView.findViewById(R.id.lv_home1);
        m_Adapter2 = new HomeItemAdapter();
        m_ListView2 = (ListView) rootView.findViewById(R.id.lv_home2);


        m_Adapter.add("asdf", "이미지 1", "냠냐리냠냐리 냐리냐리냠냠", R.drawable.left1);
        m_Adapter.add("", "이미지 2", "이미지가 있는 글의 글 내용이에요오오오오옹", R.drawable.left2);
        m_Adapter.add("", "이미지 3", "호오오오오이이이이이이이", R.drawable.left3);

        m_Adapter2.add("asdf", "이미지 1", "냠냐리냠냐리 냐리냐리냠냠", R.drawable.right1);
        m_Adapter2.add("", "이미지 2", "이미지가 있는 글의 글 내용이에요오오오오옹", R.drawable.right2);
        m_Adapter2.add("", "이미지 3", "호오오오오이이이이이이이", R.drawable.right3);

        m_ListView.setAdapter(m_Adapter);
        m_ListView2.setAdapter(m_Adapter2);
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
