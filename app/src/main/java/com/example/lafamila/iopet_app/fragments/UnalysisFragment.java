package com.example.lafamila.iopet_app.fragments;


import android.content.ContentValues;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.PictureCallback;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.lafamila.iopet_app.R;
import com.example.lafamila.iopet_app.util.Util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;


/**
 * A simple {@link Fragment} subclass.
 */
public class UnalysisFragment extends Fragment {

    Camera camera;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    boolean previewing = false;
    private static final String ARG_SECTION_NUMBER = "section_number";

    public UnalysisFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_unalysis, container, false);
        Button shot = (Button) rootView.findViewById(R.id.btn_shot);

        getActivity().getWindow().setFormat(PixelFormat.UNKNOWN);
        surfaceView = (SurfaceView)rootView.findViewById(R.id.surfaceview);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                camera = Camera.open();
                camera.setDisplayOrientation(90);

                Display display = getActivity().getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int width = size.x;

                android.widget.RelativeLayout.LayoutParams params = new android.widget.RelativeLayout.LayoutParams(width, (int)(Util.RATIO * width));
                surfaceView.setLayoutParams(params);
                camera.stopPreview();
                Camera.Parameters parameters = camera.getParameters();
                parameters.setPreviewSize(Util.WIDTH, Util.HEIGHT);
                camera.setParameters(parameters);
                camera.startPreview();
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
                if(previewing){
                    camera.stopPreview();
                    previewing = false;
                }
                if(camera != null){
                    try{
                        camera.setPreviewDisplay(surfaceHolder);
                        camera.startPreview();
                        previewing = true;
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                if(camera != null){
                    camera.stopPreview();
                    camera.release();
                    camera = null;
                    previewing = false;

                }

            }
        });
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        if(!previewing){
            camera = Camera.open();
            if(camera != null){
                try{
                    camera.setPreviewDisplay(surfaceHolder);
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }

        shot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(camera != null && previewing){

                    camera.takePicture(myShutterCallback, myPictureCallback_RAW, myPictureCallback_JPG);



                }
            }
        });

//        TextView textView = (TextView) rootView.findViewById(R.id.section_label);
//        textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
        return rootView;
    }

    ShutterCallback myShutterCallback = new ShutterCallback(){
        @Override
        public void onShutter() {

        }
    };

    PictureCallback myPictureCallback_RAW = new PictureCallback() {
        @Override
        public void onPictureTaken(byte[] bytes, Camera camera) {

        }
    };

    PictureCallback myPictureCallback_JPG = new PictureCallback() {
        @Override
        public void onPictureTaken(byte[] bytes, Camera cam) {

            Uri uriTarget = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
            OutputStream imageFileOS;
            try{
                imageFileOS = getActivity().getContentResolver().openOutputStream(uriTarget);
                imageFileOS.write(bytes);
                imageFileOS.flush();
                imageFileOS.close();
                Toast.makeText(getContext(), "Image saved : " + uriTarget.toString(), Toast.LENGTH_LONG).show();
                camera.stopPreview();
                camera.release();
                camera = null;
                previewing = false;

            }catch (FileNotFoundException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    };
}
