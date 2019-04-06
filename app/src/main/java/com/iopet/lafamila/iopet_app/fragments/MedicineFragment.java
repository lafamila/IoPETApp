package com.iopet.lafamila.iopet_app.fragments;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.budiyev.android.circularprogressbar.CircularProgressBar;
import com.iopet.lafamila.iopet_app.MedicineActivity;
import com.example.lafamila.iopet_app.R;
import com.iopet.lafamila.iopet_app.adapters.MedicineItemAdapter;
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
    ImageView pill;
    TextView percent;
    CircularProgressBar progressBar;
    int whole;
    int[] len = new int[]{0, 0, 0};
    int total = 0;
    int t = 0;

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

        pill = (ImageView)rootView.findViewById(R.id.pill);
        percent = (TextView)rootView.findViewById(R.id.tv_medicineName);
        progressBar = (CircularProgressBar)rootView.findViewById(R.id.progress_bar);

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

            int total = 0;
            m_Adapter = new MedicineItemAdapter();

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
                    if(days > 0) {
                        Log.d("lafamiladays", days+"");
                        m_Adapter.add(obj.getString("MEDICINE_NAME"), days, obj.getInt("MEDICINE_MORNING") == 1, obj.getInt("MEDICINE_LUNCH") == 1, obj.getInt("MEDICINE_DINNER") == 1);
                        String start = obj.getString("MEDICINE_UPLOAD");
                        Date s = null;
                        try {
                            s = formatter.parse(start);//catch exception
                        } catch (ParseException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        Calendar startDay = Calendar.getInstance();
                        startDay.setTime(s);
                        diff = thatDay.getTimeInMillis() - startDay.getTimeInMillis();
                        days = (int)(diff / (24 * 60 * 60 * 1000));
                        total += days * (obj.getInt("MEDICINE_MORNING") + obj.getInt("MEDICINE_LUNCH") + obj.getInt("MEDICINE_DINNER"));
                        len[0] += obj.getInt("MEDICINE_MORNING");
                        len[1] += obj.getInt("MEDICINE_LUNCH");
                        len[2] += obj.getInt("MEDICINE_DINNER");
                        Log.d("lafamiladays", total+"");

                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
            lv.setAdapter(m_Adapter);
            // Do whatever.
            //adapter에 추가된게 있으면 + 시간대가 맞으면 + 한번더 Async -> 아직 추가안된거면 버튼활성화 + 클릭가능, 최종 percent + progressbar 조정
            if(total > 0){
                whole = total;
                (new CheckTask()).execute(pet_id+"", total+"");
            }
            Log.d("lafamilatag", total+"");
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

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
    }

    private class UploadTask extends AsyncTask<String, Integer, JSONArray>{
        @Override
        protected JSONArray doInBackground(String... params) {
            // TODO Auto-generated method stub
            return postData(params[0]);
        }

        protected void onPostExecute(JSONArray result) {

        }


        public JSONArray postData(String valueIWantToSend) {
            // Create a new HttpClient and Post Header
            Integer result = -1;
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(
                    Util.LOCAL_URL+"/petSetMediCheckApp");
            //            "http://13.125.255.139:5000/chatList");

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("pet_id",
                        valueIWantToSend));
                nameValuePairs.add(new BasicNameValuePair("num", len[t]+""));
                nameValuePairs.add(new BasicNameValuePair("time", t+""));

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

    private class CheckTask extends AsyncTask<String, Integer, JSONArray> {
        int before;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            total = 0;
        }

        @Override
        protected JSONArray doInBackground(String... params) {
            // TODO Auto-generated method stub
            before = Integer.parseInt(params[1]);
            return postData(params[0]);
        }

        protected void onPostExecute(JSONArray result) {

            Calendar cal = Calendar.getInstance();
            int h = cal.get(Calendar.HOUR_OF_DAY);
            if(h > 7 && h<=10){
                t = 0;
            }
            else if(h > 11 && h<=14){
                t = 1;
            }
//            else if(h > 16 && h<=19){
            else if(h > 21 && h<=22){
                t = 2;
            }
            else{
                t = -1;
            }
            boolean isAct = true;
            if(t == -1){
                isAct = false;
            }
            for (int i = 0; i < result.length(); i++) { // Walk through the Array.
                try{
                    JSONObject obj = result.getJSONObject(i);

                    String strThatDay = obj.getString("CHECK_DATE");
                    Log.d("lafamilal", strThatDay);
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

                    if((thatDay.get(Calendar.YEAR) == today.get(Calendar.YEAR)) && (thatDay.get(Calendar.MONTH) == today.get(Calendar.MONTH)) &&(thatDay.get(Calendar.DATE) == today.get(Calendar.DATE))){
                        int time = obj.getInt("CHECK_TIME");
                        if(time == t){
                            isAct = false;
                        }
                    }
                    total += obj.getInt("CHECK_NUM");
                    Log.d("lafamilal", total + "");



                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
            // Do whatever.
            //adapter에 추가된게 있으면 + 시간대가 맞으면 + 한번더 Async -> 아직 추가안된거면 버튼활성화 + 클릭가능, 최종 percent + progressbar 조정
            if(isAct){
                pill.setImageResource(R.drawable.pill2);
                pill.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //클릭하면 Check에 추가되면서 비활성화, percent 증가
                        pill.setImageResource(R.drawable.pill1);
                        pill.setOnClickListener(null);
                        percent.setText(((total+len[t])*100.0/before)+"%");
                        progressBar.setProgress((float)((total+len[t])*100.0/before));
                        (new UploadTask()).execute(pet_id+"");
                    }
                });
            }
            percent.setText((total*100.0/before)+"%");
            progressBar.setProgress((float)(total*100.0/before));
            Log.d("lafamilatag", total+"");
        }


        public JSONArray postData(String valueIWantToSend) {
            // Create a new HttpClient and Post Header
            Integer result = -1;
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(
                    Util.LOCAL_URL+"/petGetMediCheckApp");
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
