package com.iopet.lafamila.iopet_app.fragments;


import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.lafamila.iopet_app.R;
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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;


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
    int pet_id;
    LinearLayout all;
    TextView[] value;
    TextView[] level;
    ProgressBar[] bar;
    int[] c, d, a;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        pet_id = getArguments().getInt("petID");

        value = new TextView[10];
        level = new TextView[10];
        bar = new ProgressBar[3];
        d = new int[]{R.id.d1, R.id.d2, R.id.d3, R.id.d4, R.id.d5, R.id.d6, R.id.d7, R.id.d8, R.id.d9, R.id.d10};
        c = new int[]{R.id.c1, R.id.c2, R.id.c3, R.id.c4, R.id.c5, R.id.c6, R.id.c7, R.id.c8, R.id.c9, R.id.c10};
        a = new int[]{R.id.progress_bar1, R.id.progress_bar2, R.id.progress_bar3};
        View rootView = inflater.inflate(R.layout.fragment_result, container, false);
        all = rootView.findViewById(R.id.result_all);
        for(int i=0;i<10;i++){
            value[i] = rootView.findViewById(d[i]);
            level[i] = rootView.findViewById(c[i]);
        }
        for(int i=0;i<3;i++){
            bar[i] = rootView.findViewById(a[i]);
        }
        (new ChatTask()).execute(pet_id+"");
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

    private class ChatTask extends AsyncTask<String, Integer, JSONArray> {

        @Override
        protected JSONArray doInBackground(String... params) {
            // TODO Auto-generated method stub

            return postData(params[0]);
        }

        protected void onPostExecute(JSONArray result) {
            if(result.length() == 0){
                all.setVisibility(View.INVISIBLE);
            }
            else{
                all.setVisibility(View.VISIBLE);
                for(int i=0;i<result.length();i++){
                    try{
                        JSONObject obj = result.getJSONObject(i);
                        for(int j=1;j<=10;j++) {

                            value[j-1].setText(obj.getInt("status" + j) + "");
                            level[j-1].setText("정상");
                            value[j-1].setTextColor(Color.parseColor("#000000"));
                            level[j-1].setTextColor(Color.parseColor("#000000"));
                        }
                        bar[0].setProgress(0);
                        bar[1].setProgress(0);
                        bar[2].setProgress(0);
                    }catch(JSONException e){
                        e.printStackTrace();
                    }
                }
            }
//            for (int i = 0; i < result.length(); i++) { // Walk through the Array.
//                try{
//                    JSONObject obj = result.getJSONObject(i);
//                    String strThatDay = obj.getString("MEDICINE_DATE");
//                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
//                    Date d = null;
//                    try {
//                        d = formatter.parse(strThatDay);//catch exception
//                    } catch (ParseException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//
//
//                    Calendar thatDay = Calendar.getInstance();
//                    thatDay.setTime(d);
//                    Calendar today = Calendar.getInstance();
//
//                    long diff = thatDay.getTimeInMillis() - today.getTimeInMillis(); //result in millis
//
//                    int days = (int)(diff / (24 * 60 * 60 * 1000));
//
//                    m_Adapter.add(obj.getString("MEDICINE_NAME"), days, obj.getInt("MEDICINE_MORNING")==1, obj.getInt("MEDICINE_LUNCH")==1, obj.getInt("MEDICINE_DINNER")==1);
//                }catch (JSONException e){
//                    e.printStackTrace();
//                }
//                lv.setAdapter(m_Adapter);
//                // Do whatever.
//            }
        }


        public JSONArray postData(String valueIWantToSend) {
            // Create a new HttpClient and Post Header
            Integer result = -1;
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(
                    Util.LOCAL_URL+"/petGetResult");
            //            "http://13.125.255.139:5000/chatList");

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("pet_id",
                        valueIWantToSend));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                result = response.getStatusLine().getStatusCode();
                if(result == 200){
                    HttpEntity entity = response.getEntity();
                    String responseString = EntityUtils.toString(entity, "UTF-8");
                    Log.d("lafamilia", responseString);
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
