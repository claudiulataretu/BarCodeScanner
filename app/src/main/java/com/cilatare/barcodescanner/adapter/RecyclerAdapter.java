package com.cilatare.barcodescanner.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cilatare.barcodescanner.model.Product;
import com.cilatare.barcodescanner.R;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {
	List<Product> mData;
	private LayoutInflater inflater;

	public RecyclerAdapter(Context context, List<Product> data) {
		inflater = LayoutInflater.from(context);
		this.mData = data;
	}

	@Override
	public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = inflater.inflate(R.layout.activity_main_list_item, parent, false);

		return new MyViewHolder(view);
	}

	@Override
	public void onBindViewHolder(MyViewHolder holder, int position) {
		Product current = mData.get(position);
		holder.setData(current, position);
		holder.setListeners();
	}

	@Override
	public int getItemCount() {
		return mData.size();
	}

	public void removeItem(int position) {
		mData.remove(position);
		notifyItemRemoved(position);
		notifyItemRangeChanged(position, mData.size());
	}

	public void addItem(int position, Product product) {
		mData.add(position, product);
		notifyItemInserted(position);
		notifyItemRangeChanged(position, mData.size());
	}

	class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		TextView title;
		ImageView imgDelete, imgAdd;
		int position;
		Product current;

		public MyViewHolder(View itemView) {
			super(itemView);
			title       = (TextView)  itemView.findViewById(R.id.tvTitle);
			imgDelete   = (ImageView) itemView.findViewById(R.id.img_row_delete);
			imgAdd      = (ImageView) itemView.findViewById(R.id.img_row_add);
		}

		public void setData(Product current, int position) {
			this.title.setText(current.getName());
			this.position = position;
			this.current = current;
		}

		public void setListeners() {
			imgDelete.setOnClickListener(MyViewHolder.this);
			imgAdd.setOnClickListener(MyViewHolder.this);
		}

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.img_row_delete:
					removeItem(position);
					break;

				case R.id.img_row_add:
					addItem(position, current);
					break;
			}
		}
	}
}
