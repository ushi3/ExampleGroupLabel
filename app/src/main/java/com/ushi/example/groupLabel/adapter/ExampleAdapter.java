package com.ushi.example.groupLabel.adapter;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ushi.example.groupLabel.R;
import com.ushi.example.groupLabel.data.HolidayOf2017;
import com.ushi.example.groupLabel.widget.GroupingItemDecoration;


public class ExampleAdapter extends RecyclerView.Adapter<ExampleAdapter.Holder>
        implements GroupingItemDecoration.Categorizable {

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_example, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        HolidayOf2017 data = HolidayOf2017.values()[position];

        holder.textDate.setText(data.month + "月" + data.day + "日");
        holder.textName.setText(data.name);
        holder.textDesc.setText(data.desc);
    }

    @Override
    public int getItemCount() {
        return HolidayOf2017.values().length;
    }

    @Nullable
    @Override
    public CharSequence getItemGroupName(int position) {
        return HolidayOf2017.values()[position].month + "月";
    }

    static class Holder extends RecyclerView.ViewHolder {

        TextView textDate;

        TextView textName;

        TextView textDesc;

        public Holder(View itemView) {
            super(itemView);
            textDate = (TextView) itemView.findViewById(R.id.text_date);
            textName = (TextView) itemView.findViewById(R.id.text_name);
            textDesc = (TextView) itemView.findViewById(R.id.text_desc);
        }
    }
}
