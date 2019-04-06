package com.iopet.lafamila.iopet_app.fragments;


import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.AutoFocusCallback;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.lafamila.iopet_app.R;
import com.iopet.lafamila.iopet_app.util.Util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class UnalysisFragment extends Fragment {

    Camera camera;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    ImageButton shot;
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
    int pet_id;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        pet_id = getArguments().getInt("petID");
        View rootView = inflater.inflate(R.layout.fragment_unalysis, container, false);
        shot = (ImageButton) rootView.findViewById(R.id.btn_shot);

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

        RelativeLayout background = (RelativeLayout)rootView.findViewById(R.id.relative_surface);
        background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shot.setEnabled(false);
                camera.autoFocus(myAutoFocusCallback);
            }
        });

//        TextView textView = (TextView) rootView.findViewById(R.id.section_label);
//        textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
        return rootView;
    }

    AutoFocusCallback myAutoFocusCallback = new AutoFocusCallback(){
        @Override
        public void onAutoFocus(boolean b, Camera camera) {
            shot.setEnabled(true);
        }
    };

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
//                Toast.makeText(getContext(), "Image saved : " + uriTarget.toString(), Toast.LENGTH_LONG).show();
                Toast.makeText(getContext(), "Image saved : " + getRealPathFromURI(uriTarget), Toast.LENGTH_LONG).show();

                (new UploadFileAsync()).execute(getRealPathFromURI(uriTarget));
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



    private class UploadFileAsync extends AsyncTask<String, Void, String> {
        String sourceFileUri;
        @Override
        protected String doInBackground(String... params) {

            try {
                sourceFileUri = params[0];

                HttpURLConnection conn = null;
                DataOutputStream dos = null;
                String lineEnd = "\r\n";
                String twoHyphens = "--";
                String boundary = "*****";
                int bytesRead, bytesAvailable, bufferSize;
                byte[] buffer;
                int maxBufferSize = 1 * 1024 * 1024;
                File sourceFile = new File(sourceFileUri);

                if (sourceFile.isFile()) {

                    try {
                        String upLoadServerUri = Util.LOCAL_URL+"/petType?pet="+pet_id;

                        FileInputStream fileInputStream = new FileInputStream(
                                sourceFile);
                        URL url = new URL(upLoadServerUri);

                        conn = (HttpURLConnection) url.openConnection();
                        conn.setDoInput(true); // Allow Inputs
                        conn.setDoOutput(true); // Allow Outputs
                        conn.setUseCaches(false); // Don't use a Cached Copy
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Connection", "Keep-Alive");
                        conn.setRequestProperty("ENCTYPE",
                                "multipart/form-data");
                        conn.setRequestProperty("Content-Type",
                                "multipart/form-data;boundary=" + boundary);
//                        conn.setRequestProperty("lafamila", sourceFileUri);

                        dos = new DataOutputStream(conn.getOutputStream());

                        dos.writeBytes(twoHyphens + boundary + lineEnd);
                        dos.writeBytes("Content-Disposition: form-data; name=\"lafamila\"" + lineEnd);
                        dos.writeBytes("Content-Type: image/pjpeg " + lineEnd);

                        dos.writeBytes(lineEnd);



                        bytesAvailable = fileInputStream.available();

                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        buffer = new byte[bufferSize];

                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                        while (bytesRead > 0) {

                            dos.write(buffer, 0, bufferSize);
                            bytesAvailable = fileInputStream.available();
                            bufferSize = Math
                                    .min(bytesAvailable, maxBufferSize);
                            bytesRead = fileInputStream.read(buffer, 0,
                                    bufferSize);

                        }

                        dos.writeBytes(lineEnd);
                        dos.writeBytes(twoHyphens + boundary + twoHyphens
                                + lineEnd);
                        dos.flush();
                        dos.close();


//                        OutputStream os = conn.getOutputStream();
//                        BufferedWriter writer = new BufferedWriter(
//                                new OutputStreamWriter(os, "UTF-8"));
//                        writer.write(getPostDataString(post));
//                        writer.flush();
//                        writer.close();
//                        os.close();

                        // Responses from the server (code and message)
                        int serverResponseCode = conn.getResponseCode();
                        String serverResponseMessage = conn
                                .getResponseMessage();

                        fileInputStream.close();
                        return readStream(conn.getInputStream());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                else{
                    Log.d("lafamilia", "empty");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("lafamilia", sourceFileUri);
            Log.d("lafamilia", result);
            if(result.length() > 0){
                Toast.makeText(getContext(), "Done!", Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(getContext(), "Internal Server Error, Try Again!", Toast.LENGTH_LONG).show();
            }

        }

        HashMap<String, String> post;


        @Override
        protected void onPreExecute() {

            post = new HashMap<>();
            post.put("pet", pet_id+"");
        }


        @Override
        protected void onProgressUpdate(Void... values) {
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

    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getActivity().getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        Log.d("lafailia", result);
        return result;
    }
}

