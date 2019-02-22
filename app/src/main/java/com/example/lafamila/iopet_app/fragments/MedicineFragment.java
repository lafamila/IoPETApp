package com.example.lafamila.iopet_app.fragments;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


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
    int pet_id;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        pet_id = getArguments().getInt("petID");
        Log.d("lafamilafragment", pet_id+"");
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
        (new ChatTask()).execute(pet_id+"");
//        Button eat = (Button)rootView.findViewById(R.id.btn_eatMedicine);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MedicineActivity.class);
                intent.putExtra("pet_id", pet_id);
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
//            String name = data.getStringExtra("name");
//            boolean morning = data.getBooleanExtra("morning", false);
//            boolean lunch = data.getBooleanExtra("lunch", false);
//            boolean dinner = data.getBooleanExtra("dinner", false);
//            int type = data.getIntExtra("count", 0);
//            m_Adapter.add(name, type, morning, lunch, dinner);
//            lv.setAdapter(m_Adapter);
            //DB나 기타등등에 저장..

            (new ChatTask()).execute(pet_id+"");
        }
    }


    private class ChatTask extends AsyncTask<String, Integer, JSONArray> {

        @Override
        protected JSONArray doInBackground(String... params) {
            // TODO Auto-generated method stub

            return postData(params[0]);
        }

        protected void onPostExecute(JSONArray result) {

            for (int i = 0; i < result.length(); i++) { // Walk through the Array.
                try{
                    JSONObject obj = result.getJSONObject(i);
                    String strThatDay = obj.getString("MEDICINE_DATE");
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    Date d = null;
                    try {
                        d = formatter.parse(strThatDay);//catch exception
                    } catch (ParseException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                    Calendar thatDay = Calendar.getInstance();
                    thatDay.setTime(d);
                    Calendar today = Calendar.getInstance();

                    long diff = thatDay.getTimeInMillis() - today.getTimeInMillis(); //result in millis

                    int days = (int)(diff / (24 * 60 * 60 * 1000));

                    m_Adapter.add(obj.getString("MEDICINE_NAME"), days, obj.getInt("MEDICINE_MORNING")==1, obj.getInt("MEDICINE_LUNCH")==1, obj.getInt("MEDICINE_DINNER")==1);
                }catch (JSONException e){
                    e.printStackTrace();
                }
                lv.setAdapter(m_Adapter);
                // Do whatever.
            }
        }


        public JSONArray postData(String valueIWantToSend) {
            // Create a new HttpClient and Post Header
            Integer result = -1;
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(
                    Util.LOCAL_URL+"/petGetMediApp");
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
