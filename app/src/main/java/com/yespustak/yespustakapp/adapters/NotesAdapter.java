package com.yespustak.yespustakapp.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yespustak.yespustakapp.R;
import com.yespustak.yespustakapp.models.NoteModel;
import com.yespustak.yespustakapp.utils.AdapterItemClickListener;
import com.yespustak.yespustakapp.utils.OnMenuItemClickListener;
import com.yespustak.yespustakapp.utils.SharedVariables;
import com.yespustak.yespustakapp.utils.utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {

    private static final int VIEW_TYPE_UNPINNED = 0;
    private static final int VIEW_TYPE_PINED = 1;

    List<NoteModel> noteList = new ArrayList<>();
    AdapterItemClickListener clickListener;
    OnMenuItemClickListener menuItemClickListener;

    public NotesAdapter(AdapterItemClickListener clickListener, OnMenuItemClickListener menuItemClickListener) {
        this.clickListener = clickListener;
        this.menuItemClickListener = menuItemClickListener;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item5, parent, false);
        return new ViewHolder(view, menuItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        NoteModel note = noteList.get(position);
        holder.tvNumber.setText(String.valueOf(position + 1));
        holder.ivPin.setVisibility(note.isPinned() ? View.VISIBLE : View.GONE);
        holder.tvTitle.setText(note.getTitle());
        holder.tvDesc.setText(note.getDescription());
        holder.tvTimestamp.setText(utils.dateToString(note.getCreatedAt(), SharedVariables.DF_NOTES));


        holder.itemView.setOnClickListener(v -> clickListener.onClick(note));

       holder.tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("TAG", "onClick: " );
                //utils.openPdfActivity();
            }
        });
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return noteList.get(position).isPinned() ? VIEW_TYPE_PINED : VIEW_TYPE_UNPINNED;
    }

    public void setNotes(List<NoteModel> notes, boolean notify) {
        noteList.clear();
        noteList.addAll(notes);
        if (notify)
            notifyDataSetChanged();
    }

    public NoteModel getNoteAt(int pos) {
        return noteList.get(pos);
    }

    public int getUnpinnedItemPos() {
        for (int i = 0; i < noteList.size(); i++) {
            if (!noteList.get(i).isPinned())
                return i;
        }

        return noteList.size() - 1;
    }

    // Initializing the Views
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {
        TextView tvNumber, tvTitle, tvDesc, tvTimestamp;
        ImageView ivPin;
        ImageButton ibMore;
        OnMenuItemClickListener menuItemClickListener;

        public ViewHolder(View view, OnMenuItemClickListener menuItemClickListener) {
            super(view);
            this.menuItemClickListener = menuItemClickListener;
            ivPin = view.findViewById(R.id.iv_pin);
            tvNumber = view.findViewById(R.id.tv_number);
            tvTitle = view.findViewById(R.id.tv_title);
            tvDesc = view.findViewById(R.id.tv_desc);
            tvTimestamp = view.findViewById(R.id.tv_timestamp);
            ibMore = view.findViewById(R.id.ib_more);

            ibMore.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {
            showPopupMenu(v);
        }

        private void showPopupMenu(View view) {
            PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
            popupMenu.inflate(R.menu.notes_menu);
            popupMenu.getMenu().findItem(R.id.mi_pin).setVisible(getItemViewType() == VIEW_TYPE_UNPINNED);
            popupMenu.getMenu().findItem(R.id.mi_unpin).setVisible(getItemViewType() == VIEW_TYPE_PINED);
            popupMenu.setOnMenuItemClickListener(this);
            popupMenu.show();
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            return menuItemClickListener.onMenuItemClick(item, getAbsoluteAdapterPosition());
        }
    }
}
