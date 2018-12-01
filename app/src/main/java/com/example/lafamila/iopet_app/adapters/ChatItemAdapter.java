package com.example.lafamila.iopet_app.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lafamila.iopet_app.R;
import com.example.lafamila.iopet_app.util.Util;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

import static android.view.View.GONE;

public class ChatItemAdapter extends BaseAdapter {
    public class Message{
        String msg;

        int type;
        boolean isImage;
        Message(String _msg, int _type, boolean _isImage)
        {
            this.msg = _msg;
            this.type = _type;
            this.isImage = _isImage;
        }
    }

    private ArrayList<Message> m_List;
    public ChatItemAdapter() {
        m_List = new ArrayList<Message>();
    }


    public void add(String _msg,int _type, boolean _isImage) {
        m_List.add(new Message(_msg,_type, _isImage));
    }

    @Override
    public int getCount() {
        return m_List.size();
    }

    @Override
    public Object getItem(int position) {
        return m_List.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        TextView text = null;
        ImageView image = null;
        CustomHolder holder  = null;
        LinearLayout layout  = null;
        View viewRight = null;
        View viewLeft = null;



        if ( convertView == null ) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_chat, parent, false);

            layout = (LinearLayout) convertView.findViewById(R.id.layout);
            text = (TextView) convertView.findViewById(R.id.text);
            image = (ImageView) convertView.findViewById(R.id.image);
            viewRight = (View) convertView.findViewById(R.id.imageViewright);
            viewLeft = (View) convertView.findViewById(R.id.imageViewleft);


            // 홀더 생성 및 Tag로 등록
            holder = new CustomHolder();
            holder.m_TextView = text;
            holder.m_ImageView = image;
            holder.layout = layout;
            holder.viewRight = viewRight;
            holder.viewLeft = viewLeft;
            convertView.setTag(holder);
        }
        else {
            holder = (CustomHolder) convertView.getTag();
            text = holder.m_TextView;
            image = holder.m_ImageView;
            layout = holder.layout;
            viewRight = holder.viewRight;
            viewLeft = holder.viewLeft;
        }

        // Text 등록
        if(!m_List.get(position).isImage) {
            text.setVisibility(View.VISIBLE);
            image.setVisibility(GONE);

            text.setText(m_List.get(position).msg);
        }
            //이미지 세팅
        else {
            text.setVisibility(GONE);
            image.setVisibility(View.VISIBLE);
//            File imgFile = new  File(m_List.get(position).msg);
//            if(imgFile.exists()){
//
//                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
//                image.setImageBitmap(myBitmap);
//
//            }
            Log.d("lafamilafamila", Util.LOCAL_URL+m_List.get(position).msg.substring(1));
            new DownloadImageTask(image).execute(Util.LOCAL_URL+m_List.get(position).msg.substring(1));
        }
        if( m_List.get(position).type == 0 ) {
            text.setBackgroundResource(R.drawable.inbox2);
            layout.setGravity(Gravity.LEFT);
            viewRight.setVisibility(GONE);
            viewLeft.setVisibility(GONE);
        }else if(m_List.get(position).type == 1){
            text.setBackgroundResource(R.drawable.outbox2);
            layout.setGravity(Gravity.RIGHT);
            viewRight.setVisibility(GONE);
            viewLeft.setVisibility(GONE);
        }else if(m_List.get(position).type == 2){
            text.setBackgroundResource(R.drawable.datebg);
            layout.setGravity(Gravity.CENTER);
            viewRight.setVisibility(View.VISIBLE);
            viewLeft.setVisibility(View.VISIBLE);
        }



//        convertView.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(context, "리스트 클릭 : "+m_List.get(pos), Toast.LENGTH_SHORT).show();
//            }
//        });



//        convertView.setOnLongClickListener(new View.OnLongClickListener() {
//
//            @Override
//            public boolean onLongClick(View v) {
//                Toast.makeText(context, "리스트 롱 클릭 : "+m_List.get(pos), Toast.LENGTH_SHORT).show();
//                return true;
//            }
//        });

        return convertView;
    }

    private class CustomHolder {
        TextView m_TextView;
        ImageView m_ImageView;
        LinearLayout layout;
        View viewRight;
        View viewLeft;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap bmp = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                bmp = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bmp;
        }
        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
