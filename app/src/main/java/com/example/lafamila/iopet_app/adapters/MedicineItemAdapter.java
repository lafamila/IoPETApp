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

import com.example.lafamila.iopet_app.R;
import com.example.lafamila.iopet_app.util.Util;

import java.io.InputStream;
import java.util.ArrayList;

import static android.view.View.GONE;

public class MedicineItemAdapter extends BaseAdapter{

    public class Medicine{
        String msg;

        int type;
        boolean morning, lunch, dinner;
        Medicine(String _msg, int _type, boolean _morning, boolean _lunch, boolean _dinner)
        {
            this.msg = _msg;
            this.type = _type;
            this.morning = _morning;
            this.lunch = _lunch;
            this.dinner = _dinner;
        }
    }

    private ArrayList<Medicine> m_List;
    public MedicineItemAdapter() {
        m_List = new ArrayList<Medicine>();
    }


    public void add(String _msg,int _type, boolean _morning, boolean _lunch, boolean _dinner) {
        m_List.add(new Medicine(_msg,_type, _morning, _lunch, _dinner));
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

        TextView name = null;
        TextView morning = null;
        TextView lunch = null;
        TextView dinner = null;
        CustomHolder holder  = null;



        if ( convertView == null ) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_medicine, parent, false);

            name = (TextView) convertView.findViewById(R.id.name);
            morning = (TextView) convertView.findViewById(R.id.morning);
            lunch = (TextView) convertView.findViewById(R.id.lunch);
            dinner = (TextView) convertView.findViewById(R.id.dinner);


            // 홀더 생성 및 Tag로 등록
            holder = new CustomHolder();
            holder.m_name = name;
            holder.m_morning = morning;
            holder.m_lunch = lunch;
            holder.m_dinner = dinner;
            convertView.setTag(holder);
        }
        else {
            holder = (CustomHolder) convertView.getTag();
            name = holder.m_name;
            morning = holder.m_morning;
            lunch = holder.m_lunch;
            dinner = holder.m_dinner;
        }
        name.setText(m_List.get(pos).msg);
        if(m_List.get(pos).morning){
            morning.setText(""+m_List.get(pos).type);
        }else{
            morning.setText("0");
        }
        if(m_List.get(pos).lunch){
            lunch.setText(""+m_List.get(pos).type);
        }else{
            lunch.setText("0");
        }
        if(m_List.get(pos).dinner){
            dinner.setText(""+m_List.get(pos).type);
        }else{
            dinner.setText("0");
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
        TextView m_name, m_morning, m_lunch, m_dinner;
    }


}
