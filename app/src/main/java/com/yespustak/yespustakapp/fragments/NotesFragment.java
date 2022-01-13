package com.yespustak.yespustakapp.fragments;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.yespustak.yespustakapp.R;
import com.yespustak.yespustakapp.activities.FragmentActivity;
import com.yespustak.yespustakapp.adapters.NotesAdapter;
import com.yespustak.yespustakapp.models.NoteModel;
import com.yespustak.yespustakapp.utils.AdapterItemClickListener;
import com.yespustak.yespustakapp.utils.ModelSharedPref;
import com.yespustak.yespustakapp.utils.OnMenuItemClickListener;
import com.yespustak.yespustakapp.utils.utils;
import com.yespustak.yespustakapp.viewmodels.NotesFragmentViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class NotesFragment extends BaseFragment implements View.OnClickListener, AdapterItemClickListener, OnMenuItemClickListener {
    private static final String TAG = "NotesFragment";


    RecyclerView rvNotes;
    LinearLayout llEmpty;
    FloatingActionButton fabAddNote;
    TextView tvStateDesc;
    NotesFragmentViewModel viewModel;

    NotesAdapter adapter;

    SearchView searchView;
    MenuItem miSearch;

    List<NoteModel> notes;
    String query = "";

    private final int PINNED = 1;
    private final int UNPINNED = 2;
    private final int DELETED = 3;

    private int itemPos;
    private int adapterChange = 0;

    public NotesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        viewModel = new ViewModelProvider(this).get(NotesFragmentViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupViews(view);

        //set observer and update view
        viewModel.getNotesLiveData().observe(getViewLifecycleOwner(), noteModels -> {
            notes = noteModels;
            filterNotesAndUpdateViews(false);

            switch (adapterChange) {
                case PINNED:
                    adapter.notifyItemMoved(itemPos, 0);
                    notifyDataSetChangeDelayed();
                    break;
                case UNPINNED:
                    adapter.notifyItemMoved(itemPos, adapter.getUnpinnedItemPos());
                    notifyDataSetChangeDelayed();
                    break;
                case DELETED:
                    adapter.notifyItemRemoved(itemPos);
                    notifyDataSetChangeDelayed();
                    break;

                default:
                    adapter.notifyDataSetChanged();
            }
            adapterChange = 0;


        });
    }

    private void notifyDataSetChangeDelayed() {
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> adapter.notifyDataSetChanged(), 350);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        miSearch = menu.findItem(R.id.mi_search);

    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
//        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.mi_cart).setVisible(true);
        menu.findItem(R.id.mi_search).setVisible(true);

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) miSearch.getActionView();

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setQueryHint(getString(R.string.text_search_title));

        searchView.setOnCloseListener(() -> {
            query = "";
            filterNotesAndUpdateViews(true);
            return false;
        });
        searchView.setOnQueryTextListener(queryTextListener);

        if (!query.isEmpty()) {
            searchView.post(() -> {
                searchView.setIconified(false);
                searchView.setQuery(query, false);
                searchView.clearFocus();
            });
        }
    }

    private void setupViews(View view) {
        rvNotes = view.findViewById(R.id.rv_notes);
        llEmpty = view.findViewById(R.id.ll_empty);
        tvStateDesc = view.findViewById(R.id.tv_state_desc);
        fabAddNote = view.findViewById(R.id.fab_add_note);

        fabAddNote.setOnClickListener(this);

        // Setting the layout as Staggered Grid for vertical orientation
        //TODO this is already configured in layout xml
//        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(3, LinearLayoutManager.VERTICAL);
//        rvNotes.setLayoutManager(staggeredGridLayoutManager);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3, LinearLayoutManager.VERTICAL, false);
        rvNotes.setLayoutManager(gridLayoutManager);

        adapter = new NotesAdapter(this, this);

        rvNotes.setAdapter(adapter);

    }

    private void openEditNote(NoteModel noteModel) {
        Intent intent = new Intent(getContext(), FragmentActivity.class);
        if (noteModel == null)
            noteModel = new NoteModel();

        ModelSharedPref.getInstance().saveModel(noteModel);
//        viewModel.setSelectedNote(noteModel);
        intent.putExtra("fragment", "editNote");
        requireActivity().startActivity(intent);
        requireActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
    }

    private List<NoteModel> getFilterNotes() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (searchView == null)
                return notes;

            //else
            return notes.stream().filter(book -> utils.containsIgnoreCase(book.getTitle(), searchView.getQuery().toString())
                    || utils.containsIgnoreCase(book.getDescription(), searchView.getQuery().toString())).collect(Collectors.toList());
        }

        return notes;
    }

    private void filterNotesAndUpdateViews(boolean notifyAdapter) {
        adapter.setNotes(getFilterNotes(), notifyAdapter);

        boolean isNotesFound = adapter.getItemCount() > 0;
        rvNotes.setVisibility(isNotesFound ? View.VISIBLE : View.GONE);
        llEmpty.setVisibility(isNotesFound ? View.GONE : View.VISIBLE);

        tvStateDesc.setVisibility(query.isEmpty() ? View.VISIBLE : View.GONE);


    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fab_add_note) {
            //create intent
            openEditNote(null);
//                viewModel.insert(new NoteModel("Shortflim Script", "Hello all its me insane developer here. In this tutorial we gonna see Hello all its me insane developer here. In this tutorial we gonna see"));
//                viewModel.insert(new NoteModel("What is Lorem Ipsum?", "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book.", true));
//                viewModel.insert(new NoteModel("Where can I get some", "There are many variations of passages of Lorem Ipsum available", true));
//                viewModel.insert(new NoteModel("Lorem Ipsum", "It is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout. The point of using Lorem Ipsum is that it has a more-or-less normal distribution of letters"));
//                viewModel.insert(new NoteModel("The Elves and The Shoemaker", "A shoemaker, by no fault of his own, had become so poor that at last he had nothing left but leather for one pair of shoes. So in the evening, he cut out the shoes which he wished to begin to make the next morning, and as he had a good conscience, he lay down quietly in his bed, commended himself to God, and fell asleep. In the morning, after he had said his prayers, and was just going to sit down to work, the two shoes stood quite finished on his table. He was astounded, and knew not what to say to it. He took the shoes in his hands to observe them closer, and they were so neatly made that there was not one bad stitch in them, just as if they were intended as a masterpiece. Soon after, a buyer came in, and as the shoes pleased him so well, he paid more for them than was customary, and, with the money, the shoemaker was able to purchase leather for two pairs of shoes."));
//                viewModel.insert(new NoteModel("Why should we longer cobblers be", "There was once a poor servant-girl, who was industrious and cleanly, and swept the house every day, and emptied her sweepings on the great heap in front of the door. One morning when she was just going back to her work, she found a letter on this heap, and as she could not read, she put her broom in the corner, and took the letter to her master and mistress, and behold it was an invitation from the elves, who asked the girl to hold a child for them at its christening. The girl did not know what to do, but at length, after much persuasion, and as they told her that it was not right to refuse an invitation of this kind, she consented. Then three elves came and conducted her to a hollow mountain, where the little folks lived. Everything there was small, but more elegant and beautiful than can be described. The baby's mother lay in a bed of black ebony ornamented with pearls, the coverlids were embroidered with gold, the cradle was of ivory, the bath of gold. The girl stood as "));
//                viewModel.insert(new NoteModel("Third Story", "A certain mother's child had been taken away out of its cradle by the elves, and a changeling with a large head and staring eyes, which would do nothing but eat and drink, laid in its place. In her trouble she went to her neighbour, and asked her advice"));
//                viewModel.insert(new NoteModel("A certain mother", "set it down on the hearth, light a fire, and boil some water in two egg-shells", true));
//                viewModel.insert(new NoteModel("Whilst he was laughing", "who brought the right child"));
        }
    }

    @Override
    public void onClick(Object object) {
        NoteModel note = (NoteModel) object;
        openEditNote(note);
    }

    @Override
    public void onDelete(Object object) {

    }

    @Override
    public void onDownload(Object object) {

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onMenuItemClick(MenuItem item, int position) {
        NoteModel note = adapter.getNoteAt(position);
        itemPos = position;

        switch (item.getItemId()) {
            case R.id.mi_pin:
                note.setPinned(true);
                viewModel.update(note);
                adapterChange = PINNED;
                return true;

            case R.id.mi_unpin:
                note.setPinned(false);
                viewModel.update(note);
                adapter.notifyItemChanged(position);
                adapterChange = UNPINNED;
                return true;

            case R.id.mi_delete:
                utils.showAlert(requireContext(), null, getString(R.string.msg_delete_warning, "Note"), SweetAlertDialog.WARNING_TYPE, sweetAlertDialog -> {
                    viewModel.delete(note);
                    adapterChange = DELETED;

                    sweetAlertDialog.dismiss();
                }, false);

                return true;

            default:
                return false;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (searchView != null)
            query = searchView.getQuery().toString().trim();
    }

    @Override
    public boolean onBackPressed() {
        if (searchView != null && !searchView.isIconified()) {
            searchView.onActionViewCollapsed();
            return true;
        } else
            return super.onBackPressed();
    }

    private final SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            filterNotesAndUpdateViews(true);
            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            query = newText;
            if (searchView.hasFocus())
                filterNotesAndUpdateViews(true);
            return true;
        }
    };
}
