package com.cilatare.barcodescanner.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cilatare.barcodescanner.R;
import com.cilatare.barcodescanner.activities.ListProductsActivity;
import com.cilatare.barcodescanner.activities.ScanProductsActivity;
import com.cilatare.barcodescanner.activities.SearchProductsActivity;
import com.cilatare.barcodescanner.model.NavigationDrawerItem;

import java.util.Collections;
import java.util.List;

public class NavigationDrawerAdapter extends RecyclerView.Adapter<NavigationDrawerAdapter.MyViewHolder> {

    private List<NavigationDrawerItem> mDataList = Collections.emptyList();
    private LayoutInflater inflater;
    private Context context;

    public NavigationDrawerAdapter(Context context, List<NavigationDrawerItem> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.mDataList = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.nav_drawer_list_item, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final NavigationDrawerItem current = mDataList.get(position);

        holder.imgIcon.setImageResource(current.getImageId());
        holder.title.setText(current.getTitle());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;

                if (holder.title.getText().toString().equals(context.getResources().getString(R.string.list_products))) {
                    intent = new Intent(context, ListProductsActivity.class);
                    context.startActivity(intent);
                }
                else if (holder.title.getText().toString().equals(context.getResources().getString(R.string.search_product))) {
                    intent = new Intent(context, SearchProductsActivity.class);
                    context.startActivity(intent);
                }
                else if (holder.title.getText().toString().equals(context.getResources().getString(R.string.scan_product))) {
                    intent = new Intent(context, ScanProductsActivity.class);
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView imgIcon;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            imgIcon = (ImageView) itemView.findViewById(R.id.imgIcon);
        }
    }
}
