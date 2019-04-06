package com.iopet.lafamila.iopet_app.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.lafamila.iopet_app.R;
import com.iopet.lafamila.iopet_app.util.Util;

import java.io.InputStream;
import java.util.ArrayList;

import static android.view.View.GONE;

public class HomeItemAdapter extends BaseAdapter {
    int cur = -1;
    public class Article{
        String imgSrc;

        String title;
        String content;
        int src;
        Article(String _imgSrc, String _title, String _content, int _src){
            imgSrc = _imgSrc;
            title = _title;
            content = _content;
            src = _src;
        }

    }

    private ArrayList<Article> m_List;
    public HomeItemAdapter() {
        m_List = new ArrayList<Article>();
    }


    public void add(String imgSrc, String title, String content, int src) {
        m_List.add(new Article(imgSrc, title, content, src));
    }

    @Override
    public int getCount() {
        return m_List.size();
    }

    @Override
    public Article getItem(int i) {
        return m_List.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();
        final TextView title, text;
        final ImageView image;
//        final LinearLayout blur;

        CustomHolder holder;


        if ( convertView == null ) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_home, parent, false);

            title = (TextView) convertView.findViewById(R.id.tv_homeTitle);
            text = (TextView) convertView.findViewById(R.id.tv_homeContent);
            image = (ImageView) convertView.findViewById(R.id.iv_homeImage);
//            blur = (LinearLayout) convertView.findViewById(R.id.ll_homeBlur);
            // 홀더 생성 및 Tag로 등록
            holder = new CustomHolder();
            holder.m_ImageView = image;
            holder.tv_title = title;
            holder.tv_content = text;
//            holder.ll = blur;
            convertView.setTag(holder);
        }
        else {
            holder = (CustomHolder) convertView.getTag();
            text = holder.tv_content;
            image = holder.m_ImageView;
            title = holder.tv_title;
//            blur = holder.ll;
        }

        // Text 등록
        if(m_List.get(position).content.length() == 0) {
            image.setVisibility(GONE);

        }
        //이미지 세팅
        else {
            image.setVisibility(View.VISIBLE);
            new DownloadImageTask(image).execute(Util.LOCAL_URL+m_List.get(position).imgSrc.substring(1));
        }
        title.setText(m_List.get(pos).title);
        text.setText(m_List.get(pos).content);
//        image.setImageResource(m_List.get(pos).src);

        image.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(pos == cur){
                    //이미지 정상처리
                    cur = -1;
                    title.setVisibility(View.INVISIBLE);
                    text.setVisibility(View.INVISIBLE);
                    image.setColorFilter(Color.argb(0, 255, 255, 255));
//                    blur.setVisibility(View.INVISIBLE);

                }
                else{
                    //이미지 블러처리
                    cur = pos;
//                    blur.setVisibility(View.VISIBLE);
                    title.setVisibility(View.VISIBLE);
                    text.setVisibility(View.VISIBLE);
                    image.setColorFilter(Color.argb(75, 255, 255, 255));
                }
            }
        });



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
        ImageView m_ImageView;
        TextView tv_title;
        TextView tv_content;
//        LinearLayout ll;
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
