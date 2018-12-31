package com.example.lafamila.iopet_app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.graphics.Point;
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
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;

import com.example.lafamila.iopet_app.fragments.ChatFragment;
import com.example.lafamila.iopet_app.fragments.HomeFragment;
import com.example.lafamila.iopet_app.fragments.MedicineFragment;
import com.example.lafamila.iopet_app.fragments.ResultFragment;
import com.example.lafamila.iopet_app.fragments.UnalysisFragment;
import com.example.lafamila.iopet_app.util.Util;

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

    /***
     *  Chatting Setup
     ***/


    int room_id;


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
        room_id = intent.getIntExtra("room_id", -1);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(2);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));


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
            switch(position){
                case 0:
                    return new ChatFragment();
                case 1:
                    return new MedicineFragment();
                case 2:
                    return new HomeFragment();
                case 3:
                    return new ResultFragment();
                case 4:
                    return new UnalysisFragment();
                default:
                    return new HomeFragment();

            }
        }

        @Override
        public int getCount() {

            return 5;
        }
    }
}
