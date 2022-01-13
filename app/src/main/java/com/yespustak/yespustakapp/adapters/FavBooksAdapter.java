package com.yespustak.yespustakapp.adapters;

import android.annotation.SuppressLint;
import android.graphics.Paint;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.yespustak.yespustakapp.Constants;
import com.yespustak.yespustakapp.R;
import com.yespustak.yespustakapp.models.BookModel;
import com.yespustak.yespustakapp.utils.AdapterItemClickListener;
import com.yespustak.yespustakapp.utils.OnMenuItemClickListener;
import com.yespustak.yespustakapp.utils.utils;

import java.util.ArrayList;
import java.util.List;

public class FavBooksAdapter extends RecyclerView.Adapter<FavBooksAdapter.ViewHolder> {
    private static final String TAG = "FavBooksAdapter";

    ArrayList<BookModel> arrayList;
    AdapterItemClickListener clickListener;
    OnMenuItemClickListener menuItemClickListener;

    public FavBooksAdapter(ArrayList<BookModel> arrayList, AdapterItemClickListener clickListener, OnMenuItemClickListener menuItemClickListener) {
        this.arrayList = arrayList;
        this.clickListener = clickListener;
        this.menuItemClickListener = menuItemClickListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book3, parent, false);
        return new ViewHolder(view, menuItemClickListener);
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        BookModel bookModel = arrayList.get(position);
        holder.textViewTitle.setText(bookModel.getTitle());
        holder.tvPublication.setText(utils.getStringResource(R.string.text_by_publication_name, bookModel.getPublication()));
        holder.tvMrp.setText(utils.getStringResource(R.string.text_mrp, bookModel.getMrp()));
        holder.tvMrp.setPaintFlags(holder.tvMrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        holder.tvYpp.setText(utils.getStringResource(R.string.text_ypp, bookModel.getYpp()));
        holder.clCard.setBackground(AppCompatResources.getDrawable(holder.clCard.getContext(), bookModel.getCardBgDrawable()));


        holder.itemView.setOutlineAmbientShadowColor(holder.itemView.getContext().getColor(bookModel.getCardShadowColor()));
        holder.itemView.setOutlineSpotShadowColor(holder.itemView.getContext().getColor(bookModel.getCardShadowColor()));

        holder.itemView.setOnClickListener(v -> clickListener.onClick(bookModel));


        Picasso.get().load(Constants.DASHBOARD_URL + bookModel.getImageUrl()).placeholder(R.drawable.ic_baseline_image_24).error(R.drawable.img_not_available_thumb).into(holder.imageViewThumb, new Callback() {
            @Override
            public void onSuccess() {
                holder.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "onError: ", e);
                holder.progressBar.setVisibility(View.GONE);
                utils.showToast(e + "\n" + bookModel.getImageUrl());
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public void setBooks(List<BookModel> books) {
        arrayList.clear();
        arrayList.addAll(books);
        notifyDataSetChanged();
    }

    public BookModel getItemAt(int position) {
        return arrayList.get(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

        TextView textViewTitle, tvPublication, tvMrp, tvYpp;
        ImageView imageViewThumb;
        ProgressBar progressBar;
        View clCard; //constrain layout
        ImageButton ibMore;
        OnMenuItemClickListener menuItemClickListener;

        public ViewHolder(@NonNull View view, OnMenuItemClickListener menuItemClickListener) {
            super(view);
            this.menuItemClickListener = menuItemClickListener;
            clCard = itemView.findViewById(R.id.cl_card);
            textViewTitle = itemView.findViewById(R.id.tv_title);
            tvPublication = itemView.findViewById(R.id.tv_publication);
            tvMrp = itemView.findViewById(R.id.tvMrp);
            tvYpp = itemView.findViewById(R.id.tvYpp);
            imageViewThumb = itemView.findViewById(R.id.iv_thumb);
            progressBar = itemView.findViewById(R.id.pb_thumb);

            ibMore = view.findViewById(R.id.ib_more);

            ibMore.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            showPopupMenu(v);
        }

        private void showPopupMenu(View view) {
            PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
            popupMenu.inflate(R.menu.fav_book_menu);
            popupMenu.setOnMenuItemClickListener(this);
            popupMenu.show();
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            return menuItemClickListener.onMenuItemClick(item, getAbsoluteAdapterPosition());
        }
    }

}
