package com.iopet.lafamila.iopet_app;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

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

public class LoginActivity extends Activity {

    LinearLayout join_section, login, join;
    ImageView logo;
    boolean isJoin = true;
    float d;
    int MARGIN_DEFAULT;
    int MARGIN_JOIN;
    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;
    /***
     *  Camera Setup
     ***/
    String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

    private boolean hasPermission(){
        int res = 0;
        for(String perms : permissions){
            res = checkCallingOrSelfPermission(perms);
            if(!(res == PackageManager.PERMISSION_GRANTED)){
                return false;
            }
        }
        return true;
    }

    private void requestNecessaryPermissions(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            requestPermissions(permissions, 1111);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean isAllowed = true;
        switch(requestCode){
            case 1111:
                for(int res : grantResults){
                    isAllowed = isAllowed && (res == PackageManager.PERMISSION_GRANTED);
                }
                break;
            default:
                break;
        }
        if(!isAllowed){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if(shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)){
                    Toast.makeText(getBaseContext(), "Camera Permission denided", Toast.LENGTH_SHORT).show();
                }
                else if(shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    Toast.makeText(getBaseContext(), "External Storage Permission denided", Toast.LENGTH_SHORT).show();
                }
                else if(shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)){
                    Toast.makeText(getBaseContext(), "External Storage Permission denided", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    EditText ed_id, ed_pw, ed_confirm, ed_pet, ed_name, ed_phone, ed_hostpital;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(!hasPermission()){
            requestNecessaryPermissions();
        }


        d = getBaseContext().getResources().getDisplayMetrics().density;
        MARGIN_DEFAULT = (int)(20 * d);
        MARGIN_JOIN = (int)(45 * d);

        ed_id = (EditText)findViewById(R.id.edit_id);
        ed_pw = (EditText)findViewById(R.id.edit_pw);
        ed_confirm = (EditText)findViewById(R.id.edit_pw_confirm);
        ed_pet = (EditText)findViewById(R.id.edit_pet);
        ed_name = (EditText)findViewById(R.id.edit_name);
        ed_phone = (EditText)findViewById(R.id.edit_phone);
        ed_hostpital = (EditText)findViewById(R.id.edit_hospital);


        logo = (ImageView)findViewById(R.id.img_login);
        join_section = (LinearLayout)findViewById(R.id.linear_join_section);

        login = (LinearLayout)findViewById(R.id.linear_login);
        join = (LinearLayout)findViewById(R.id.linear_join);


        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flip();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout self = (LinearLayout) view;
                self.setEnabled(false);

                (new LoginTask()).execute();
            }
        });

    }
    @Override
    protected void onResume() {
        super.onResume();

        login.setEnabled(true);
    }
    public void flip(){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                (int)(70 * d)
        );
        if(isJoin){
            login.setVisibility(View.GONE);
            join_section.setVisibility(View.VISIBLE);
            params.setMargins(0, 0, 0, MARGIN_JOIN);
            logo.setLayoutParams(params);
        }
        else{
            if(!"".equals(ed_pw.getText().toString()) && ed_pw.getText().toString().equals(ed_confirm.getText().toString())){
                (new UploadTask()).execute();
                login.setVisibility(View.VISIBLE);
                join_section.setVisibility(View.GONE);
                params.setMargins(0, 0, 0, MARGIN_DEFAULT);
                logo.setLayoutParams(params);
            }
            else{
                Toast.makeText(getBaseContext(), "Fill Password and Confirm correctly", Toast.LENGTH_LONG).show();
                isJoin = !isJoin;
            }
        }
        isJoin = !isJoin;
    }



    class UploadTask extends AsyncTask<String, String, String> {
        HashMap<String, String> post;

        @Override
        protected void onPreExecute() {

            post = new HashMap<>();
            post.put("id", ed_id.getText().toString());
            post.put("pw", ed_pw.getText().toString());
            post.put("pet", ed_pet.getText().toString());
            post.put("name", ed_name.getText().toString());
            post.put("phone", ed_phone.getText().toString());
            post.put("hospital", ed_hostpital.getText().toString());

        }

        @Override
        protected void onPostExecute(String s) {

            if(s.equals("already")){
                Toast.makeText(getBaseContext(), "Already Exists", Toast.LENGTH_LONG).show();
            }
            else if(s.equals("pet")){
                Toast.makeText(getBaseContext(), "Incorrect Informations", Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(getBaseContext(), "Join Success", Toast.LENGTH_LONG).show();
                flip();
            }
        }

        @Override
        protected String doInBackground(String... x) {
            String data = "";
            try{

                URL url = new URL(Util.LOCAL_URL+"/petJoin");

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


    @Override
    public void onBackPressed() {

        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime) {
            super.onBackPressed();
        }
        else {
            if(!isJoin){
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        (int)(70 * d)
                );
                login.setVisibility(View.VISIBLE);
                join_section.setVisibility(View.GONE);
                params.setMargins(0, 0, 0, MARGIN_DEFAULT);
                logo.setLayoutParams(params);
                isJoin = !isJoin;
            }
            else {
                backPressedTime = tempTime;
                Toast.makeText(this, "한번 더 취소를 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    class LoginTask extends AsyncTask<String, String, String> {
        HashMap<String, String> post;

        ProgressDialog asyncDialog = new ProgressDialog(LoginActivity.this);

        @Override
        protected void onPreExecute() {

            post = new HashMap<>();
            post.put("id", ed_id.getText().toString());
            post.put("pw", ed_pw.getText().toString());
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

            asyncDialog.setMessage("로그인 중...");

            asyncDialog.show();


        }

        @Override
        protected void onPostExecute(String s) {
            asyncDialog.dismiss();
            login.setEnabled(true);
            if(s.equals("error")){
                Toast.makeText(getBaseContext(), "Incorrect ID or Password", Toast.LENGTH_LONG).show();
            }
            else if(s.equals("")){
                Toast.makeText(getBaseContext(), "Internet Connection Error", Toast.LENGTH_LONG).show();
            }
            else{
                int petID = Integer.valueOf(s);
                Intent i = new Intent(getBaseContext(), MainActivity.class);
                Log.d("lafamilalogin", petID+"");
                i.putExtra("petID", petID);
                startActivity(i);
            }
        }

        @Override
        protected String doInBackground(String... x) {
            String data = "";
            try{

                URL url = new URL(Util.LOCAL_URL+"/petLoginApp");

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
