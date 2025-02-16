package com.example.netra_ai;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.ai.client.generativeai.type.Content;

import java.util.ArrayList;

public class Chat_adapter extends BaseAdapter {
    ArrayList array_rec,array_sent;
    TextView text_rec,text_sent;
    Context context;
    LayoutInflater layoutInflater;

    public Chat_adapter(ArrayList array_rec, ArrayList array_sent, Context context) {
        this.array_rec = array_rec;
        this.array_sent = array_sent;
        this.context = context;
        layoutInflater= LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return array_rec.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = layoutInflater.inflate(R.layout.chat_tile,null);
        text_rec = (TextView) convertView.findViewById(R.id.text_chat_rec);
        text_sent=(TextView) convertView.findViewById(R.id.text_chat_sent);

        text_rec.setText(array_rec.get(position).toString());
        text_sent.setText(array_sent.get(position).toString());

        return convertView;
    }
}
