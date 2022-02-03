package com.yespustak.yespustakapp.fragments;

import android.app.SearchManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tonyodev.fetch2.Status;
import com.yespustak.yespustakapp.R;
import com.yespustak.yespustakapp.adapters.PurchasedBooksAdapter;
import com.yespustak.yespustakapp.models.DownloadBook;
import com.yespustak.yespustakapp.services.DownloadService;
import com.yespustak.yespustakapp.utils.AdapterItemClickListener;
import com.yespustak.yespustakapp.utils.utils;
import com.yespustak.yespustakapp.viewmodels.LibraryFragmentViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class PurchasedFragment extends Fragment implements AdapterItemClickListener {

    private static final String TAG = "PurchasedFragment";

    LibraryFragmentViewModel viewModel;
    PurchasedBooksAdapter adapter;
    RecyclerView rvBooks;
    List<DownloadBook> books;

    MenuItem miSearch;
    SearchView searchView;

    public PurchasedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        viewModel = new ViewModelProvider(this).get(LibraryFragmentViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_purchased, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews(view);

        viewModel.getAllDownloadBooks().observe(getViewLifecycleOwner(), downloadBooks -> {
            books = downloadBooks;
            adapter.setBooks(getFilteredBooks());
        });
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
            adapter.setBooks(getFilteredBooks());
            return false;
        });
        searchView.setOnQueryTextListener(queryTextListener);

    }

    private List<DownloadBook> getFilteredBooks() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (searchView == null)
                return books;

            //else
            return books.stream().filter(book -> utils.containsIgnoreCase(book.getTitle(), searchView.getQuery().toString())).collect(Collectors.toList());
        }

        return books;
    }

    private void setupViews(View view) {
        rvBooks = view.findViewById(R.id.rv_books);

        //setup adapter and set books
        rvBooks.setHasFixedSize(true);
        rvBooks.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
//        rvBooks.setLayoutManager(new GridLayoutManager(requireContext(), 4));
        books = new ArrayList<>();
        adapter = new PurchasedBooksAdapter(books, this);
        rvBooks.setAdapter(adapter);
    }

    @Override
    public void onClick(Object object) {
        DownloadBook book = (DownloadBook) object;
        if (book.getStatus() == Status.COMPLETED)
            utils.openPdfActivity2(requireActivity(), Uri.parse(book.getFileUri()), book.getRid(),book.getPassword(),"null",0);
        else
            utils.showToast(getString(R.string.msg_book_being_downloaded));
    }

    @Override
    public void onDelete(Object object) {

    }

    @Override
    public void onDownload(Object object) {
        DownloadBook book = (DownloadBook) object;
        if (book != null) {
            book.setStatus(Status.NONE);
            book.setProgress(0);
            book.setSpeed("");
            book.setEtaInMilliSeconds(0);
            book.setDownloadedBytes(0);
            DownloadService.forceDownloadAgain = true;
            viewModel.update(book);
            utils.showToast("Book download has bees started");

        } else
            utils.showToast("Unable to start Download");
    }

    private final SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            adapter.setBooks(getFilteredBooks());
            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            adapter.setBooks(getFilteredBooks());
            return true;
        }
    };
}