package com.iopet.lafamila.iopet_app;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.example.lafamila.iopet_app.R;
import com.iopet.lafamila.iopet_app.util.Util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import static com.iopet.lafamila.iopet_app.util.Util.LOCAL_URL;

public class MedicineActivity extends AppCompatActivity {
    Button save;
    EditText name, count;
    CheckBox check1, check2, check3;
    int pet_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine);

        pet_id = getIntent().getIntExtra("pet_id", 0);
        Log.d("lafamilaactivity", pet_id+"");

        save = (Button)findViewById(R.id.btn_saveMedicine);
        name = (EditText)findViewById(R.id.edit_medicineName);
        check1 = (CheckBox)findViewById(R.id.check_1);
        check2 = (CheckBox)findViewById(R.id.check_2);
        check3 = (CheckBox)findViewById(R.id.check_3);
        count = (EditText)findViewById(R.id.edit_count);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                (new MedicineTask()).execute();
            }
        });
    }



    class MedicineTask extends AsyncTask<String, String, String> {
        HashMap<String, String> post;


        @Override
        protected void onPreExecute() {

            post = new HashMap<>();

            post.put("pet_id", pet_id+"");
            post.put("medicine_name", name.getText().toString());
            post.put("medicine_morning", check1.isChecked()?"1":"0");
            post.put("medicine_lunch", check2.isChecked()?"1":"0");
            post.put("medicine_dinner",check3.isChecked()?"1":"0");
            post.put("medicine_date", count.getText().toString());

        }

        @Override
        protected void onPostExecute(String s) {

            Intent intent = new Intent();
            intent.putExtra("name", name.getText().toString());
            intent.putExtra("morning", check1.isChecked());
            intent.putExtra("lunch", check2.isChecked());
            intent.putExtra("dinner", check3.isChecked());
            intent.putExtra("count", Integer.valueOf(count.getText().toString()));
            setResult(Util.ADD_MEDICINE, intent);
            finish();
        }

        @Override
        protected String doInBackground(String... x) {
            String data = "";
            try{

                URL url = new URL(LOCAL_URL+"/petMedicineApp");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(1500000);
                conn.setConnectTimeout(1500000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);


                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(post));

                writer.flush();
                writer.close();
                os.close();



                InputStream is = conn.getInputStream();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is, "UTF-8"));
                data = reader.readLine();
                reader.close();
                is.close();
            }catch (Exception e){
                e.printStackTrace();
            }
            return data;
        }
    }



    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

}
