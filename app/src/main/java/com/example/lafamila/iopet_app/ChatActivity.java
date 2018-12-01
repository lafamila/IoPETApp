package com.example.lafamila.iopet_app;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;


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

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


import com.example.lafamila.iopet_app.adapters.ChatItemAdapter;
import com.example.lafamila.iopet_app.util.Util;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.github.nkzawa.emitter.Emitter;

public class ChatActivity extends AppCompatActivity {
    ListView m_ListView;
    ChatItemAdapter m_Adapter;
    private Socket mSocket;
    EditText edit;
    int room_id;
    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        JSONObject arg;
        String sender = "";
        String msg = "";
        boolean isImage = false;
        @Override
        public void call(Object... args) {
            arg = (JSONObject) args[0];
            try{
                sender = arg.getString("sender");
                msg = arg.getString("message");
                isImage = !arg.getString("type").equals("text");
                Log.d("messageRecieved", msg);
                Log.d("messageSender", sender);

            } catch (JSONException e) {
                return;
            }
            if(sender.equals("hospt")){
                Log.d("messageRecieved", msg);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        m_Adapter.add(msg, 0, isImage);
                        m_ListView.setAdapter(m_Adapter);
                        // add the message to view
                    }
                });

            }

        }
    };
//    private Emitter.Listener onMessageReceived = new Emitter.Listener() {
//        @Override
//        public void call(Object... args) {
//            JSONObject receivedData = (JSONObject) args[0];
//            try{
//                Log.d("message",receivedData.getString("message"));
//                ((CustomAdapter)m_ListView.getAdapter()).add(receivedData.getString("message"), 0);
//                m_ListView.clearFocus();
//                edit.requestFocus();
//                m_ListView.requestFocus();
//            }catch (JSONException e){
//                e.printStackTrace();
//            }
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();
        room_id = intent.getIntExtra("room_id", -1);


        try{
            mSocket = IO.socket(Util.LOCAL_URL);
            JSONObject object = new JSONObject();
            try{
                object.put("room_id", room_id);
                object.put("sender", "pet");
            }catch (JSONException e){
                e.printStackTrace();
            }

            mSocket.connect();
            mSocket.emit("join", object);
            mSocket.on("received", onNewMessage);
            //m_Adapter.add("New User", 2);
        }catch (URISyntaxException e){
            e.printStackTrace();
        }



        (new ChatTask()).execute(""+room_id);

        edit = (EditText)findViewById(R.id.editText1);
        m_Adapter = new ChatItemAdapter();
        m_ListView = (ListView) findViewById(R.id.listView1);

        m_ListView.setAdapter(m_Adapter);



        Button upload = (Button)findViewById(R.id.button2);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 1);
                //http://programmerguru.com/android-tutorial/how-to-upload-image-to-php-server/
            }
        });
        Button send = (Button)findViewById(R.id.button1);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txt = edit.getText().toString();
                edit.setText("");

                m_Adapter.add(txt, 1, false);
                //이미지를 업로드하는경우
                //m_Adapter.add(업로드된 파일 경로, 1, true);
                m_ListView.setAdapter(m_Adapter);
                edit.requestFocus();
                m_ListView.requestFocus();
                JSONObject object = new JSONObject();
                try{
                    object.put("message", txt);
                    object.put("type", "text");
                    object.put("room_id", room_id);
                    object.put("sender", "pet");
                }catch (JSONException e){
                    e.printStackTrace();
                }
                mSocket.emit("message", object);
            }
        });





    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        JSONObject object = new JSONObject();
        try{
            object.put("room_id", room_id);
        }catch (JSONException e){
            e.printStackTrace();
        }
        mSocket.emit("leave", object);
        mSocket.disconnect();
        mSocket.off("message", onNewMessage);
    }



    private class ChatTask extends AsyncTask<String, Integer, JSONArray> {

        @Override
        protected JSONArray doInBackground(String... params) {
            // TODO Auto-generated method stub

            return postData(params[0]);
        }

        protected void onPostExecute(JSONArray result) {
            Log.d("status", result.toString());
            for (int i = 0; i < result.length(); i++) { // Walk through the Array.
                try{
                    JSONObject obj = result.getJSONObject(i);

                    m_Adapter.add(obj.getString("CHAT_MESSAGE"), obj.getInt("CHAT_SEND"), obj.getInt("CHAT_TYPE")==1);
                }catch (JSONException e){
                    e.printStackTrace();
                }
                m_ListView.setAdapter(m_Adapter);
                // Do whatever.
            }
        }


        public JSONArray postData(String valueIWantToSend) {
            // Create a new HttpClient and Post Header
            Integer result = -1;
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(
                    Util.LOCAL_URL+"/chatList");
            //            "http://13.125.255.139:5000/chatList");

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("room_id",
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1)
            if (resultCode == Activity.RESULT_OK) {
                Uri selectedImage = data.getData();

                String filePath = getPath(selectedImage);
                String file_extn = filePath.substring(filePath.lastIndexOf(".") + 1);

                if (file_extn.equals("img") || file_extn.equals("jpg") || file_extn.equals("jpeg") || file_extn.equals("gif") || file_extn.equals("png")) {
                    //Upload image
                    (new UploadFileAsync()).execute(filePath);
                    Log.d("lafamilalafamila", filePath);
                    Log.d("lafamilalafamila", file_extn);
                } else {
                    //NOT IN REQUIRED FORMAT
                }
            }
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        String imagePath = cursor.getString(column_index);

        return imagePath;
    }


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
                        String upLoadServerUri = Util.LOCAL_URL+"/uploadImage";

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
                        conn.setRequestProperty("lafamila", sourceFileUri);

                        dos = new DataOutputStream(conn.getOutputStream());

                        dos.writeBytes(twoHyphens + boundary + lineEnd);
                        dos.writeBytes("Content-Disposition: form-data; name=\"lafamila\";filename=\""
                                + sourceFileUri + "\"" + lineEnd);

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

                        // Responses from the server (code and message)
                        int serverResponseCode = conn.getResponseCode();
                        String serverResponseMessage = conn
                                .getResponseMessage();

                        fileInputStream.close();
                        dos.flush();
                        dos.close();
                        return readStream(conn.getInputStream());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("lafamilafamila", result);

            m_Adapter.add(result, 1, true);
            //이미지를 업로드하는경우
            //m_Adapter.add(업로드된 파일 경로, 1, true);
            m_ListView.setAdapter(m_Adapter);

            //Emit
            JSONObject object = new JSONObject();
            try{
                object.put("message", result);
                object.put("type", "image");
                object.put("room_id", room_id);
                object.put("sender", "pet");
            }catch (JSONException e){
                e.printStackTrace();
            }
            mSocket.emit("message", object);

        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

//    private class UploadFileToServer extends AsyncTask<String, Integer, String> {
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//        }
//
//        @Override
//        protected String doInBackground(String... params) {
//            return uploadFile(params[0]);
//        }
//
//        private String uploadFile(String opFilePath) {
//            String responseString = null;
//            Log.d("Log", "File path" + opFilePath);
//            HttpClient httpclient = new DefaultHttpClient();
//            HttpPost httppost = new HttpPost(Util.LOCAL_URL+"/uploadImage");
//            try {
//                MultipartEntity entity = new MultipartEntity(
//                        new MultipartEntity.ProgressListener() {
//
//                            @Override
//                            public void transferred(long num) {
//                                publishProgress((int) ((num / (float) totalSize) * 100));
//                            }
//                        });
//                ExifInterface newIntef = new ExifInterface(opFilePath);
//                newIntef.setAttribute(ExifInterface.TAG_ORIENTATION,String.valueOf(2));
//                File file = new File(opFilePath);
//                entity.addPart("pic", new FileBody(file));
//                totalSize = entity.getContentLength();
//                httppost.setEntity(entity);
//
//                // Making server call
//                HttpResponse response = httpclient.execute(httppost);
//                HttpEntity r_entity = response.getEntity();
//
//
//                int statusCode = response.getStatusLine().getStatusCode();
//                if (statusCode == 200) {
//                    // Server response
//                    responseString = EntityUtils.toString(r_entity);
//                    Log.d("Log", responseString);
//                } else {
//                    responseString = "Error occurred! Http Status Code: "
//                            + statusCode + " -> " + response.getStatusLine().getReasonPhrase();
//                    Log.d("Log", responseString);
//                }
//
//            } catch (ClientProtocolException e) {
//                responseString = e.toString();
//            } catch (IOException e) {
//                responseString = e.toString();
//            }
//
//            return responseString;
//        }
//    }
//

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
}

