package com.example.lafamila.iopet_app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lafamila.iopet_app.fragments.ChatFragment;
import com.example.lafamila.iopet_app.fragments.HomeFragment;
import com.example.lafamila.iopet_app.fragments.MedicineFragment;
import com.example.lafamila.iopet_app.fragments.ResultFragment;
import com.example.lafamila.iopet_app.fragments.UnalysisFragment;
import com.example.lafamila.iopet_app.util.Util;

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

import static com.example.lafamila.iopet_app.util.Util.LOCAL_URL;

public class MainActivity extends AppCompatActivity {


    /***
     *  Camera Setup
     ***/
    String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

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
            }
        }
    }

    int pet_id;
    int room_id;
    ImageView menu;
    /***
     *  Tab Layout Setup
     ***/

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar toolbar = findViewById(R.id.main_toolbar);
        menu = toolbar.findViewById(R.id.toolbar_menu);
        if(!hasPermission()){
            requestNecessaryPermissions();
        }

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        double ratio = height * 1.0 / width;
        double r = 0.0;

        int w = width, h = height;
        Camera camera = Camera.open();
        for(Camera.Size str: camera.getParameters().getSupportedPreviewSizes()){
            double temp = (str.width * 1.0 / str.height);
            if(temp > r && temp != ratio){
                r = temp;
                w = str.width;
                h = str.height;
            }
        }
        Util.RATIO = r;
        Util.HEIGHT = h;
        Util.WIDTH = w;

        Intent intent = getIntent();
        pet_id = intent.getIntExtra("petID", 0);
        Log.d("lafamilamainA", pet_id+"");

        (new LoginTask()).execute();




    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }




    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Bundle bundle = new Bundle();
            bundle.putInt("petID", pet_id);
            bundle.putInt("room_id", room_id);
            switch(position){
                case 0:
                    ChatFragment chatFragment = new ChatFragment();
                    chatFragment.setArguments(bundle);
                    return chatFragment;
                case 1:
                    MedicineFragment medicineFragment = new MedicineFragment();
                    medicineFragment.setArguments(bundle);
                    return medicineFragment;
                case 2:
                    HomeFragment homeFragment = new HomeFragment();
                    homeFragment.setArguments(bundle);
                    return homeFragment;
                case 3:
                    ResultFragment resultFragment = new ResultFragment();
                    resultFragment.setArguments(bundle);
                    return resultFragment;
                case 4:
                    UnalysisFragment unalysisFragment = new UnalysisFragment();
                    unalysisFragment.setArguments(bundle);
                    return unalysisFragment;
                default:
                    HomeFragment defaultFragment = new HomeFragment();
                    defaultFragment.setArguments(bundle);
                    return defaultFragment;

            }
        }

        @Override
        public int getCount() {

            return 5;
        }
    }


    class LoginTask extends AsyncTask<String, String, String> {
        HashMap<String, String> post;


        @Override
        protected void onPreExecute() {

            post = new HashMap<>();
            post.put("pet_id", pet_id+"");

        }

        @Override
        protected void onPostExecute(String s) {

            room_id = Integer.valueOf(s);
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            if(room_id == 0){
                menu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getBaseContext(), "asdf", Toast.LENGTH_LONG).show();
                    }
                });
            }

            mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

            mViewPager = (ViewPager) findViewById(R.id.container);
            mViewPager.setAdapter(mSectionsPagerAdapter);
            mViewPager.setCurrentItem(2);
            TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

            mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
            tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        }

        @Override
        protected String doInBackground(String... x) {
            String data = "";
            try{

                URL url = new URL(LOCAL_URL+"/petLogin");

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
