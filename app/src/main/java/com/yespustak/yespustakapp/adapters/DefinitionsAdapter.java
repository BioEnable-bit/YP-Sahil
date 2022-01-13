package com.yespustak.yespustakapp.adapters;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.yespustak.yespustakapp.R;
import com.yespustak.yespustakapp.models.Definition;
import com.yespustak.yespustakapp.utils.utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DefinitionsAdapter extends RecyclerView.Adapter<DefinitionsAdapter.ViewHolder> {

    public static final int NORMAL = 1;
    public static final int FOOTER = 2;

    List<Definition> definitionsOri = new ArrayList<>();
    List<Definition> definitions = new ArrayList<>();

    boolean isLoadMore = false;

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == NORMAL)
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.definition_item, parent, false);
        else
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.definitions_footer, parent, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        if (getItemViewType(position) == NORMAL) {
            Definition definition = definitions.get(position);
            holder.tvDefinitionType.setText(definition.getPartOfSpeech());
            holder.tvDefinition.setText(definition.getDefinition());

            holder.tvExample.setText(definition.getExample());
            holder.tvSynonyms.setText(definition.getSynonyms() != null ? String.join(", ", definition.getSynonyms()) : null);

            holder.llExample.setVisibility(definition.getExample() == null || definition.getExample().isEmpty() ? View.GONE : View.VISIBLE);
            holder.llSynonyms.setVisibility(definition.getSynonyms() == null || definition.getSynonyms().isEmpty() ? View.GONE : View.VISIBLE);

//            if (position == getItemCount() - 1)
            holder.viewBottomBorder.setVisibility((isLoadMore && position == getItemCount() - 1) || ( !isLoadMore && definitionsOri.size() == 1) ? View.GONE : View.VISIBLE);
//        holder.viewBottomBorder.setVisibility(position == getItemCount() - 1 ? View.GONE : View.VISIBLE);
        } else {
            holder.btnLoadMore.setOnClickListener(v -> {
                isLoadMore = true;
                setDefinitions(definitionsOri);
            });
        }
    }

    @Override
    public int getItemCount() {
        return (definitionsOri.size() > 1 && !isLoadMore) ? definitions.size() + 1 : definitions.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 1 && definitionsOri.size() > 1 && !isLoadMore) ? FOOTER : NORMAL;
    }

    public void setDefinitions(List<Definition> definitions) {
        definitionsOri = definitions;
        this.definitions.clear();
        notifyDataSetChanged();
        if (isLoadMore)
            this.definitions.addAll(definitions);
        else
            this.definitions.add(definitions.get(0));

        notifyItemRangeInserted(0, getItemCount());
//        notifyItemRangeChanged(0, getItemCount());
//        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        View llExample, llSynonyms, viewBottomBorder;
        TextView tvDefinitionType, tvDefinition, tvExample, tvSynonyms;
        Button btnLoadMore;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            llExample = itemView.findViewById(R.id.ll_example);
            llSynonyms = itemView.findViewById(R.id.ll_synonyms);
            viewBottomBorder = itemView.findViewById(R.id.view_bottom_border);
            tvDefinitionType = itemView.findViewById(R.id.tv_definition_type);
            tvDefinition = itemView.findViewById(R.id.tv_definition);
            tvExample = itemView.findViewById(R.id.tv_example);
            tvSynonyms = itemView.findViewById(R.id.tv_synonyms);
            btnLoadMore = itemView.findViewById(R.id.btn_load_more);
        }
    }
}
