package com.test.course_haut;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


class SelectAdapter extends BaseAdapter {
    LayoutInflater inflater;

    public  SelectAdapter(Context context)
    {
        inflater = LayoutInflater.from(context); }
    @Override
    public int getCount() {
        return Util.list.size();
    }

    @Override
    public Object getItem(int position) {
        return Util.list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    static class ViewHolder
    {
        public TextView name;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView==null)
        {
            convertView=inflater.inflate(R.layout.course,null);
            viewHolder = new ViewHolder();
            viewHolder.name = convertView.findViewById(R.id.className);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        viewHolder.name.setText(Util.list.get(position).getName());
        return convertView;
    }
}
