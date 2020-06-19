package com.ortusolis.listviewwithcard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;


public class CardsAdapter extends ArrayAdapter<CardModel> {
    public CardsAdapter(Context context) {
        super(context, R.layout.card_item);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.card_item, parent, false);
            holder = new ViewHolder(convertView);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        CardModel model = getItem(position);
        holder.imageView.setImageResource(model.getImageId());
        holder.tvTitle.setText(model.getTitle());
//        holder.tvSubtitle.setText(model.getSubtitle());
        holder.waterflow.setImageResource(R.drawable.reddrop);
        holder.waterflow.setImageResource(R.drawable.bluedrop1);

        return convertView;
    }

    static class ViewHolder {
        ImageView imageView;
        TextView tvTitle;
        ImageView waterflow;


        ViewHolder(View view) {
            imageView = (ImageView) view.findViewById(R.id.image);
            tvTitle = (TextView) view.findViewById(R.id.text_title);
waterflow=(ImageView)view.findViewById(R.id.waterdrop);
        }
    }

}
