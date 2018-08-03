package com.example.pegmeister.greennews;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class NewsAdapter extends ArrayAdapter<News> {

    // Create ViewHolder class to improve scrolling performance
    static class ViewHolder {
        TextView title;
        TextView category;
        TextView author;
        TextView date;
    }

    public NewsAdapter(@NonNull Context context, List<News> greenNews) {
        super(context, 0, greenNews);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // create a list view for convertView
        View listItemView = convertView;
        // inflate its layout if convertView is empty
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.news_list_item, parent, false);
        }

        // setup ViewHolder to link corresponding fields to layout
        ViewHolder viewHolder = new ViewHolder();
        News currentNews = getItem(position);

        viewHolder.title = listItemView.findViewById(R.id.title);
        viewHolder.category = listItemView.findViewById(R.id.category);
        viewHolder.author = listItemView.findViewById(R.id.author);
        viewHolder.date = listItemView.findViewById(R.id.pub_date);

        viewHolder.title.setText(currentNews.getNewsTitle());
        viewHolder.category.setText(currentNews.getNewsCategory());
        viewHolder.author.setText(currentNews.getNewsAuthor());
        viewHolder.date.setText(currentNews.getNewsPubDate());

        return listItemView;
    }
}
