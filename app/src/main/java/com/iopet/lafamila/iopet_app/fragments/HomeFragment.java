package com.iopet.lafamila.iopet_app.fragments;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.lafamila.iopet_app.R;
import com.iopet.lafamila.iopet_app.adapters.HomeItemAdapter;
import com.iopet.lafamila.iopet_app.util.Util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


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


//        m_ListView.setAdapter(m_Adapter);
//        m_ListView2.setAdapter(m_Adapter2);
//        m_Adapter.add("asdf", "이미지 1", "냠냐리냠냐리 냐리냐리냠냠", R.drawable.left1);
//        m_Adapter.add("", "이미지 2", "이미지가 있는 글의 글 내용이에요오오오오옹", R.drawable.left2);
//        m_Adapter.add("", "이미지 3", "호오오오오이이이이이이이", R.drawable.left3);
//
//        m_Adapter2.add("asdf", "이미지 1", "냠냐리냠냐리 냐리냐리냠냠", R.drawable.right1);
//        m_Adapter2.add("", "이미지 2", "이미지가 있는 글의 글 내용이에요오오오오옹", R.drawable.right2);
//        m_Adapter2.add("", "이미지 3", "호오오오오이이이이이이이", R.drawable.right3);

        (new GetTask()).execute("1");
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



    private class GetTask extends AsyncTask<String, Integer, JSONArray> {

        @Override
        protected JSONArray doInBackground(String... params) {
            // TODO Auto-generated method stub

            return postData(params[0]);
        }

        protected void onPostExecute(JSONArray result) {

            for (int i = 0; i < result.length(); i++) { // Walk through the Array.
                try{
                    JSONObject obj = result.getJSONObject(i);

                    if(i%2 == 0)
                        m_Adapter.add(obj.getString("ARTICLE_URI"), obj.getString("ARTICLE_TITLE"),obj.getString("ARTICLE_TEXT"), R.drawable.left1);
                    else
                        m_Adapter2.add(obj.getString("ARTICLE_URI"), obj.getString("ARTICLE_TITLE"),obj.getString("ARTICLE_TEXT"), R.drawable.left1);
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
            m_ListView.setAdapter(m_Adapter);
            m_ListView2.setAdapter(m_Adapter2);
        }


        public JSONArray postData(String valueIWantToSend) {
            // Create a new HttpClient and Post Header
            Integer result = -1;
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(
                        Util.LOCAL_URL+"/getArticle");
            //            "http://13.125.255.139:5000/chatList");

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("hospital_id",
                        valueIWantToSend));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                result = response.getStatusLine().getStatusCode();

                if(result == 200){
                    HttpEntity entity = response.getEntity();
                    String responseString = EntityUtils.toString(entity, "UTF-8");
                    try{
                        return new JSONArray(responseString);
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }
                else{
                    return new JSONArray();

                }

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
            } catch (IOException e) {
                // TODO Auto-generated catch block
            }
            return new JSONArray();
        }
    }
}
