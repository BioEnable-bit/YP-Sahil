package com.yespustak.yespustakapp.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.tonyodev.fetch2.Status;
import com.yespustak.yespustakapp.R;
import com.yespustak.yespustakapp.activities.FragmentActivity;
import com.yespustak.yespustakapp.activities.MainActivity;
import com.yespustak.yespustakapp.adapters.AppsAdapter;
import com.yespustak.yespustakapp.adapters.DownloadAdapter;
import com.yespustak.yespustakapp.adapters.ExtrasAppAdapter;
import com.yespustak.yespustakapp.adapters.GamesAppAdapter;
import com.yespustak.yespustakapp.api.Retrofit2Client;
import com.yespustak.yespustakapp.api.response.GetThirdPartyApps;
import com.yespustak.yespustakapp.api.response.GetThirdPartyExtrasApps;
import com.yespustak.yespustakapp.api.response.GetThirdPartyGamesApps;
import com.yespustak.yespustakapp.models.AppModel;
import com.yespustak.yespustakapp.models.DownloadBook;
import com.yespustak.yespustakapp.models.ExtrasAppModel;
import com.yespustak.yespustakapp.models.GamesAppModel;
import com.yespustak.yespustakapp.services.DownloadService;
import com.yespustak.yespustakapp.utils.AdapterItemClickListener;
import com.yespustak.yespustakapp.utils.FilesUtil;
import com.yespustak.yespustakapp.utils.GridSpacingItemDecoration;
import com.yespustak.yespustakapp.utils.utils;
import com.yespustak.yespustakapp.viewmodels.LibraryFragmentViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LibraryFragment extends BaseFragment implements AdapterItemClickListener, View.OnClickListener {
    String TAG = this.getClass().getSimpleName();

    SwipeRefreshLayout swipeRefreshLayout;
    View llAppsData, expandedView,expandedView2,expandedView3, llErrorEmpty, llBooksData;
    CardView cvAppsThumb, cvExtrasThumb, cvGamesThumb;
    RecyclerView rvAppsExpanded, rvAppsCollapsed, rvExtrasAppsExpanded,rvExtrasCollapsed, /*rvGamesAppsExpanded,rvGamesCollapsed,*/ rvDownloads;
    TextView tvAppsError, tvStateDesc;
    ProgressBar pbLoadingApps;
    Button btnStart, btnAdd, btnDelete, btnPurchased, btnBrowse;

    LibraryFragmentViewModel viewModel;

    AppsAdapter appsAdapterExpanded, appsAdapterCollapsed;

    ExtrasAppAdapter extraAppsAdapterExpanded, extraAppsAdapterCollapsed;
   // GamesAppAdapter gamesAppsAdapterExpanded, gamesAppsAdapterCollapsed;

    DownloadAdapter downloadAdapter;

    ArrayList<AppModel> appList, appListFiltered;
    ArrayList<ExtrasAppModel> extrasAppList, extrasAppListFiltered;
   // ArrayList<GamesAppModel> gamesAppList, gamesAppListFiltered;
    List<DownloadBook> downloadBooks, bookListFiltered;

    private SearchView searchView = null;

    //animation related
    // Hold a reference to the current animator,
    // so that it can be canceled mid-way.
    private Animator currentAnimator;

    // The system "short" animation time duration, in milliseconds. This
    // duration is ideal for subtle animations or animations that occur
    // very frequently.
    private int shortAnimationDuration;

    private String queryText;

    public LibraryFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(LibraryFragmentViewModel.class);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_library, container, false);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem searchViewItem = menu.findItem(R.id.mi_search);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        if (searchViewItem != null) {
            searchView = (SearchView) searchViewItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
            searchView.setQueryHint(getString(R.string.text_type_book_name));

            searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
//                    filterApps("");
                    queryText = null;
                    setBooksData();
//                    downloadAdapter.setDownloadBooks(getFilteredBooks());
                    return false;
                }
            });

            SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    queryText = query;
                    setBooksData();
//                    downloadAdapter.setDownloadBooks(getFilteredBooks());
                    return  true;
//                    return filterApps(query);
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    Log.i("onQueryTextChange", newText);
                    queryText = newText;
                    setBooksData();
//                    downloadAdapter.setDownloadBooks(getFilteredBooks());
                    return true;
//                    return filterApps(newText);
                }
            };
            searchView.setOnQueryTextListener(queryTextListener);
        }

    }


    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        menu.findItem(R.id.mi_cart).setVisible(true);
        menu.findItem(R.id.mi_search).setVisible(true);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.mi_cart) {
            Intent intent = new Intent(getContext(), FragmentActivity.class);
            intent.putExtra("fragment", "cart");
            getActivity().startActivity(intent);
            requireActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews(view);
       // getGames(false);
        getExtras(false);
        getApps(false);
    }

    private void setupViews(View view) {
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        rvDownloads = view.findViewById(R.id.rv_downloads);
        llAppsData = view.findViewById(R.id.ll_apps_data);
        llErrorEmpty = view.findViewById(R.id.ll_error_empty);
        llBooksData = view.findViewById(R.id.ll_books_data);
        cvAppsThumb = view.findViewById(R.id.cv_apps_thumb);
        cvExtrasThumb = view.findViewById(R.id.cv_extras_thumb);
      //  cvGamesThumb = view.findViewById(R.id.cv_games_thumb);
        rvAppsExpanded = view.findViewById(R.id.rv_apps_expanded);
        rvExtrasAppsExpanded = view.findViewById(R.id.rv_extras_expanded);
      //  rvGamesAppsExpanded = view.findViewById(R.id.rv_games_expanded);
        rvAppsCollapsed = view.findViewById(R.id.rv_apps_collapsed);
        rvExtrasCollapsed = view.findViewById(R.id.rv_extras_collapsed);
      //  rvGamesCollapsed = view.findViewById(R.id.rv_games_collapsed);
        pbLoadingApps = view.findViewById(R.id.pb_loading_apps);
        tvAppsError = view.findViewById(R.id.tv_apps_error);
        tvStateDesc = view.findViewById(R.id.tv_state_desc);
        btnAdd = view.findViewById(R.id.btn_add);
        btnStart = view.findViewById(R.id.btn_start);
        btnDelete = view.findViewById(R.id.btn_delete);
        btnPurchased = view.findViewById(R.id.btn_purchased);
        btnBrowse = view.findViewById(R.id.btn_empty_action);

        // Load the high-resolution "zoomed-in" image.
        expandedView = view.findViewById(R.id.ll_expanded);
        expandedView2 = view.findViewById(R.id.ll_expanded2);
      //  expandedView3 = view.findViewById(R.id.ll_expanded3);
        CardView cvAppsThumb = view.findViewById(R.id.cv_apps_thumb);
        CardView cvExtrasThumb = view.findViewById(R.id.cv_extras_thumb);
    //    CardView cvGamesThumb = view.findViewById(R.id.cv_games_thumb);

        appList = new ArrayList<>();
        extrasAppList = new ArrayList<>();
       // gamesAppList = new ArrayList<>();

        appListFiltered = new ArrayList<>();
        extrasAppListFiltered = new ArrayList<>();
      //  gamesAppListFiltered = new ArrayList<>();

        bookListFiltered = new ArrayList<>();





        int spanCount = 3;

        //setup apps view

        //setup apps view
        rvAppsExpanded.setHasFixedSize(true);
        rvAppsExpanded.setLayoutManager(new GridLayoutManager(requireContext(), spanCount));
        rvAppsExpanded.addItemDecoration(new GridSpacingItemDecoration(spanCount, 30, true));
        appsAdapterExpanded = new AppsAdapter(getContext(), appListFiltered, this, 0);
        rvAppsExpanded.setAdapter(appsAdapterExpanded);

        //setup extras view
        rvExtrasAppsExpanded.setHasFixedSize(true);
        rvExtrasAppsExpanded.setLayoutManager(new GridLayoutManager(requireContext(), spanCount));
        rvExtrasAppsExpanded.addItemDecoration(new GridSpacingItemDecoration(spanCount, 30, true));
        extraAppsAdapterExpanded = new ExtrasAppAdapter(getContext(), extrasAppListFiltered, this, 0);
        rvExtrasAppsExpanded.setAdapter(extraAppsAdapterExpanded);

//        //setup games view
//        rvGamesAppsExpanded.setHasFixedSize(true);
//        rvGamesAppsExpanded.setLayoutManager(new GridLayoutManager(requireContext(), spanCount));
//        rvGamesAppsExpanded.addItemDecoration(new GridSpacingItemDecoration(spanCount, 30, true));
//        gamesAppsAdapterExpanded = new GamesAppAdapter(getContext(), gamesAppListFiltered, this, 0);
//        rvGamesAppsExpanded.setAdapter(gamesAppsAdapterExpanded);



        rvAppsCollapsed.setHasFixedSize(true);
        rvAppsCollapsed.setLayoutManager(new GridLayoutManager(requireContext(), spanCount));
        appsAdapterCollapsed = new AppsAdapter(getContext(), appList, this, 1);
        rvAppsCollapsed.setAdapter(appsAdapterCollapsed);
        rvAppsCollapsed.suppressLayout(true);//IMP

        rvExtrasCollapsed.setHasFixedSize(true);
        rvExtrasCollapsed.setLayoutManager(new GridLayoutManager(requireContext(), spanCount));
        extraAppsAdapterCollapsed = new ExtrasAppAdapter(getContext(), extrasAppList, this, 1);
        rvExtrasCollapsed.setAdapter(extraAppsAdapterCollapsed);
        rvExtrasCollapsed.suppressLayout(true);//IMP

//        rvGamesCollapsed.setHasFixedSize(true);
//        rvGamesCollapsed.setLayoutManager(new GridLayoutManager(requireContext(), spanCount));
//        gamesAppsAdapterCollapsed = new GamesAppAdapter(getContext(), gamesAppList, this, 1);
//        rvGamesCollapsed.setAdapter(gamesAppsAdapterCollapsed);
//        rvGamesCollapsed.suppressLayout(true);//IMP

        // Hook up clicks on the thumbnail views.

        cvAppsThumb.setOnClickListener(view1 ->{

            zoomImageFromThumb(view, cvAppsThumb);

        });


        cvExtrasThumb.setOnClickListener(view1 ->{

            zoomImageFromThumb2(view, cvExtrasThumb);

        });

//        cvGamesThumb.setOnClickListener(view1 ->{
//
//            zoomImageFromThumb3(view, cvGamesThumb);
//
//        });



        // Retrieve and cache the system's default "short" animation time.
        shortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);

        //set downloads
        rvDownloads.setHasFixedSize(false);
//        rvDownloads.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvDownloads.setLayoutManager(new GridLayoutManager(requireContext(), 4));
        downloadBooks = new ArrayList<>();
        downloadAdapter = new DownloadAdapter(downloadBooks, this);
        rvDownloads.setAdapter(downloadAdapter);

        //after all setup -> setup observer
        viewModel.getAllDownloadBooks().observe(getViewLifecycleOwner(), downloadBooks -> {
//            Utils.showToast("on Change " + downloadBooks.size());
            this.downloadBooks = downloadBooks;
            setBooksData();
//            this.downloadBooks.clear();
//            this.downloadBooks.addAll(downloadBooks);
//            downloadAdapter.notifyDataSetChanged();
        });

        //set click listeners
        btnDelete.setOnClickListener(this);
        btnAdd.setOnClickListener(this);
        btnStart.setOnClickListener(this);
        btnPurchased.setOnClickListener(this);
        btnBrowse.setOnClickListener(this);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            getExtras(true);
            getApps(true);
            // getGames(true);
            viewModel.syncBooks().observe(getViewLifecycleOwner(), isSyncing -> {
                swipeRefreshLayout.setRefreshing(isSyncing);
            });
        });

    }

    private void setBooksData() {
        List<DownloadBook> filteredBooks = getFilteredBooks();

        boolean isBookListEmpty = filteredBooks == null || filteredBooks.isEmpty();
        boolean queryEmpty = queryText == null || queryText.isEmpty();
//        boolean isPurchased = !isBookListEmpty && queryEmpty;

        boolean showNotPurchasedError = queryEmpty && isBookListEmpty;

        llErrorEmpty.setVisibility(isBookListEmpty ? View.VISIBLE : View.GONE);
        llBooksData.setVisibility(isBookListEmpty ? View.GONE : View.VISIBLE);

        if (queryEmpty && isBookListEmpty) {
            tvStateDesc.setVisibility(View.VISIBLE);
            btnBrowse.setVisibility(View.VISIBLE);
        } else if (!queryEmpty) {
            tvStateDesc.setVisibility(View.GONE);
            btnBrowse.setVisibility(View.GONE);
        }
//        else
//            tvStateDesc.setVisibility(View.GONE);

//        tvStateDesc.setVisibility(showNotPurchasedError ? View.GONE : View.VISIBLE);
//        btnBrowse.setVisibility(isPurchased ? View.VISIBLE : View.GONE);

        downloadAdapter.setDownloadBooks(filteredBooks);
    }


    private List<DownloadBook> getFilteredBooks() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return downloadBooks.stream().filter(book -> {
                if (queryText != null && !queryText.isEmpty())
                    return book.getStatus() != Status.DELETED && utils.containsIgnoreCase(book.getTitle(), queryText);
                else
                    return book.getStatus() != Status.DELETED;
            }).collect(Collectors.toList());
        }

        return null;
    }


//

    private void getApps(boolean refresh) {
        if (!refresh)
            showProgress(true);
        Call<GetThirdPartyApps> call = Retrofit2Client.getInstance().getApiService().getThirdPartyApps(utils.getIMEINumber());
        call.enqueue(new Callback<GetThirdPartyApps>() {
            @Override
            public void onResponse(Call<GetThirdPartyApps> call, Response<GetThirdPartyApps> response) {
                showProgress(false);
                appList.clear();

                if (!response.isSuccessful()) {
                    showAppsData(true);
                    return;
                }

                if (response.body() != null) {
                    for (AppModel app : response.body().getThirdPartyApps()) {
                        app.setIcon(getIcon(app.getPackageName()));
                        Log.e(TAG, "onResponse app: "+app.getPackageName() );

                        appList.add(app);
                    }
                }

                appListFiltered.clear();
                appListFiltered.addAll(appList);
                appsAdapterExpanded.notifyDataSetChanged();
                showAppsData(false);

            }

            @Override
            public void onFailure(Call<GetThirdPartyApps> call, Throwable t) {
                showProgress(false);
                showAppsData(true);
            }
        });
    }
    private void getExtras(boolean refresh) {
        if (!refresh)
            showProgress(true);
        Call<GetThirdPartyExtrasApps> call = Retrofit2Client.getInstance().getApiService().getThirdPartyExtrasApps(utils.getIMEINumber());
        call.enqueue(new Callback<GetThirdPartyExtrasApps>() {
            @Override
            public void onResponse(Call<GetThirdPartyExtrasApps> call, Response<GetThirdPartyExtrasApps> response) {
                showProgress(false);
                extrasAppList.clear();

                if (!response.isSuccessful()) {
                    showAppsData(true);
                    return;
                }

                if (response.body() != null) {
                    for (ExtrasAppModel app : response.body().getThirdPartyExtraApps()) {
                        app.setIcon(getIcon(app.getPackageName()));
                        Log.e(TAG, "onResponse: extra "+app.getPackageName() );
                        extrasAppList.add(app);

                    }
                }

                extrasAppListFiltered.clear();
                extrasAppListFiltered.addAll(extrasAppList);
                extraAppsAdapterExpanded.notifyDataSetChanged();
                showAppsData(false);
            }

            @Override
            public void onFailure(Call<GetThirdPartyExtrasApps> call, Throwable t) {
                showProgress(false);
                showAppsData(true);
            }
        });
    }

//    private void getGames(boolean refresh) {
//        if (!refresh)
//            showProgress(true);
//        Call<GetThirdPartyGamesApps> call = Retrofit2Client.getInstance().getApiService().getThirdPartyGamesApps(utils.getIMEINumber());
//        call.enqueue(new Callback<GetThirdPartyGamesApps>() {
//            @Override
//            public void onResponse(Call<GetThirdPartyGamesApps> call, Response<GetThirdPartyGamesApps> response) {
//                showProgress(false);
//                gamesAppList.clear();
//
//                if (!response.isSuccessful()) {
//                    showAppsData(true);
//                    return;
//                }
//
//                if (response.body() != null) {
//                    for (GamesAppModel app : response.body().getThirdPartyAppsGames()) {
//                        app.setIcon(getIcon(app.getPackageName()));
//                        gamesAppList.add(app);
//
//                    }
//                }
//
//                gamesAppListFiltered.clear();
//                gamesAppListFiltered.addAll(gamesAppList);
//                gamesAppsAdapterExpanded.notifyDataSetChanged();
//                showAppsData(false);
//            }
//
//            @Override
//            public void onFailure(Call<GetThirdPartyGamesApps> call, Throwable t) {
//                showProgress(false);
//                showAppsData(true);
//            }
//        });
//    }



    private void showProgress(boolean show) {
        pbLoadingApps.setVisibility(show ? View.VISIBLE : View.GONE);
        llAppsData.setVisibility(show ? View.GONE : View.VISIBLE);

        if (!show)
            swipeRefreshLayout.setRefreshing(false);
    }

    private void showAppsData(boolean isNetworkError) {
        boolean show = appListFiltered.size() > 0;
        boolean show2 = extrasAppListFiltered.size() > 0;

        tvAppsError.setText(isNetworkError ? R.string.text_unable_to_load_apps : R.string.text_no_apps_found);

        cvAppsThumb.setVisibility(show ? View.VISIBLE : View.GONE);
        cvExtrasThumb.setVisibility(show2 ? View.VISIBLE : View.GONE);
//        cvGamesThumb.setVisibility(show2 ? View.VISIBLE : View.GONE);
        tvAppsError.setVisibility(show ? View.GONE : View.VISIBLE);
        tvAppsError.setVisibility(show2 ? View.GONE : View.VISIBLE);
    }

    private Drawable getIcon(String packageName) {
        if (packageName.equalsIgnoreCase("com.Extramarks.Smartstudy"))
            return AppCompatResources.getDrawable(requireContext(), R.drawable.ic_extramarks);
        try {
            return requireContext().getPackageManager().getApplicationIcon(packageName);
        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();

        }
        return null;
    }

    private void openExternalApp(String packageName) {
        Intent intent = requireActivity().getPackageManager().getLaunchIntentForPackage(packageName);
        if (intent != null) {
            startActivity(intent);
        } else {
            try {
                Intent playStoreIntent = new Intent(Intent.ACTION_VIEW);
                playStoreIntent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + packageName));
                playStoreIntent.setPackage("com.android.vending");
                startActivity(playStoreIntent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onBackPressed() {
        if (expandedView.getVisibility() == View.VISIBLE) {
            expandedView.callOnClick();
            return true;
        }else if (expandedView2.getVisibility() == View.VISIBLE) {
            expandedView2.callOnClick();
            return true;}
//        }else if (expandedView3.getVisibility() == View.VISIBLE) {
//            expandedView3.callOnClick();
//            return true;
//        }
        return  false;
    }

    @Override
    public void onClick(Object object) {
        if (object instanceof AppModel) {
            AppModel app = (AppModel) object;
            Log.e(TAG, "onClick: "+app.getPackageName());
            openExternalApp(app.getPackageName());
        }
        else if(object instanceof ExtrasAppModel)
        {
            ExtrasAppModel app = (ExtrasAppModel) object;
            Log.e(TAG, "onClick: "+app.getPackageName());
            openExternalApp(app.getPackageName());
        }
//
//        else if(object instanceof GamesAppModel)
//        {
//            GamesAppModel app = (GamesAppModel) object;
//            Log.e(TAG, "onClick: "+app.getPackageName());
//            openExternalApp(app.getPackageName());
//        }

//        else if (object instanceof BooksModel) {
//            BooksModel book = (BooksModel) object;
//            if (book.getId().equals("1")) {
//                openPdfInAdobe("editable4_1_page.pdf");
//            } else if (book.getId().equals("2"))
//                openPdfInAdobe("automotive.pdf");
//        } else if (object instanceof BookListModel) {
//            BookListModel book = (BookListModel) object;
////            if (book.getId() == 1) {
////                openPdfActivity("editable4_1_page.pdf", 1);
////            } else if (book.getId() == 2)
////                openPdfActivity("editable_2.pdf", 2);
//        }
        else if (object instanceof DownloadBook) {
            DownloadBook book = (DownloadBook) object;
            if (book.getFileUri() == null) {
                utils.showAlert(requireContext(), "Error", book.toString(), SweetAlertDialog.ERROR_TYPE, null, false);
                return;
            }
            Log.i(TAG, "onClick: " + book.toString());
            utils.openPdfActivity2(requireActivity(), Uri.parse(book.getFileUri()), book.getRid(),book.getPassword(),"null",0);
        }
    }

    @Override
    public void onDelete(Object object) {
        utils.showAlert(requireContext(), null, getString(R.string.msg_delete_warning, getString(R.string.title_book)),
                SweetAlertDialog.WARNING_TYPE, sweetAlertDialog -> {
                    DownloadBook book = (DownloadBook) object;
                    if (FilesUtil.deleteFile(Uri.parse(book.getFileUri()))) {
                        book.setStatus(Status.DELETED);
                        viewModel.update(book);
                    } else
                        utils.showToast(getString(R.string.msg_error_while_deleting));
                    sweetAlertDialog.dismiss();
                }, false);
    }

    @Override
    public void onDownload(Object object) {

    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add:
                //add dummy urls
//                viewModel.insert(new DownloadBook("xerox", "English", 100, 0, "", "https://www.office.xerox.com/latest/SFTBR-04.PDF"));

//                viewModel.insert(new DownloadBook("Lorem ipsum", "Madhubun", "https://ars.els-cdn.com/content/image/1-s2.0-S0092867415012702-mmc6.pdf", "uploads/publishers/books/5/1623417110math_plus.jpg"));
//                viewModel.insert(new DownloadBook("Lorem ipsum", "Madhubun", "https://download.support.xerox.com/pub/docs/FlowPort2/userdocs/any-os/en/fp_dc_setup_guide.pdf", "uploads/publishers/books/5/1623417110math_plus.jpg"));
//                viewModel.insert(new DownloadBook("Xerox", "NCRT", "https://www.hq.nasa.gov/alsj/a17/A17_FlightPlan.pdf", "uploads/publishers/books/6/1623417535compacta.jpg"));
              //  viewModel.insert(new DownloadBook("Xerox", "NCRT", "https://www.eurofound.europa.eu/sites/default/files/ef_publication/field_ef_document/ef1710en.pdf", "uploads/publishers/books/6/1623417535compacta.jpg"));

//                viewModel.insert(new DownloadBook("The little price", "English", 100, 0, "", "https://freeditorial.com/en/books/the-little-prince/downloadbookepub/pdf"));
//                viewModel.insert(new DownloadBook("The inferno", "English", 100, 0, "", "https://ia800300.us.archive.org/12/items/inferno00dant_2/inferno00dant_2.pdf"));
//                viewModel.insert(new DownloadBook("Godaan", "Hindi", 100, 0, "", "https://ia802907.us.archive.org/10/items/in.ernet.dli.2015.489953/2015.489953.Godaan.pdf"));

                requireActivity().startService(new Intent(getActivity(), DownloadService.class));
                break;
            case R.id.btn_start:
                requireActivity().startService(new Intent(getActivity(), DownloadService.class));
                break;
            case R.id.btn_delete:
                viewModel.deleteAll();
                FilesUtil.deleteDir(FilesUtil.BOOKS_CACHE);
                FilesUtil.deleteDir(FilesUtil.BOOKS);
                break;
            case R.id.btn_empty_action:
                ((MainActivity) requireActivity()).showHomepage();
                break;
//            case R.id.btn_purchased:
//                requireActivity().startService(new Intent(getActivity(), DownloadService.class));
//                break;
        }
    }

    private void zoomImageFromThumb(View rootView, final View thumbView) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (currentAnimator != null) {
            currentAnimator.cancel();
        }

        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds);
        rootView.findViewById(R.id.container).getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height() > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        thumbView.setAlpha(0f);
        expandedView.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        expandedView.setPivotX(0f);
        expandedView.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedView, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedView, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedView, View.SCALE_X,
                        startScale, 1f))
                .with(ObjectAnimator.ofFloat(expandedView,
                        View.SCALE_Y, startScale, 1f));
        set.setDuration(shortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                currentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                currentAnimator = null;
            }
        });
        set.start();
        currentAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        final float startScaleFinal = startScale;
        expandedView.setOnClickListener(view -> {
            if (currentAnimator != null) {
                currentAnimator.cancel();
            }

            // Animate the four positioning/sizing properties in parallel,
            // back to their original values.
            AnimatorSet set1 = new AnimatorSet();
            set1.play(ObjectAnimator
                    .ofFloat(expandedView, View.X, startBounds.left))
                    .with(ObjectAnimator
                            .ofFloat(expandedView,
                                    View.Y, startBounds.top))
                    .with(ObjectAnimator
                            .ofFloat(expandedView,
                                    View.SCALE_X, startScaleFinal))
                    .with(ObjectAnimator
                            .ofFloat(expandedView,
                                    View.SCALE_Y, startScaleFinal));
            set1.setDuration(shortAnimationDuration);
            set1.setInterpolator(new DecelerateInterpolator());
            set1.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    thumbView.setAlpha(1f);
                    expandedView.setVisibility(View.GONE);
                    currentAnimator = null;
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    thumbView.setAlpha(1f);
                    expandedView.setVisibility(View.GONE);
                    currentAnimator = null;
                }
            });
            set1.start();
            currentAnimator = set1;
        });
    }

    private void zoomImageFromThumb2(View rootView, final View thumbView) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (currentAnimator != null) {
            currentAnimator.cancel();
        }

        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds);
        rootView.findViewById(R.id.container).getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height() > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        thumbView.setAlpha(0f);
        expandedView2.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        expandedView2.setPivotX(0f);
        expandedView2.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedView2, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedView2, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedView2, View.SCALE_X,
                        startScale, 1f))
                .with(ObjectAnimator.ofFloat(expandedView2,
                        View.SCALE_Y, startScale, 1f));
        set.setDuration(shortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                currentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                currentAnimator = null;
            }
        });
        set.start();
        currentAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        final float startScaleFinal = startScale;
        expandedView2.setOnClickListener(view -> {
            if (currentAnimator != null) {
                currentAnimator.cancel();
            }

            // Animate the four positioning/sizing properties in parallel,
            // back to their original values.
            AnimatorSet set1 = new AnimatorSet();
            set1.play(ObjectAnimator
                    .ofFloat(expandedView2, View.X, startBounds.left))
                    .with(ObjectAnimator
                            .ofFloat(expandedView2,
                                    View.Y, startBounds.top))
                    .with(ObjectAnimator
                            .ofFloat(expandedView2,
                                    View.SCALE_X, startScaleFinal))
                    .with(ObjectAnimator
                            .ofFloat(expandedView2,
                                    View.SCALE_Y, startScaleFinal));
            set1.setDuration(shortAnimationDuration);
            set1.setInterpolator(new DecelerateInterpolator());
            set1.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    thumbView.setAlpha(1f);
                    expandedView2.setVisibility(View.GONE);
                    currentAnimator = null;
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    thumbView.setAlpha(1f);
                    expandedView2.setVisibility(View.GONE);
                    currentAnimator = null;
                }
            });
            set1.start();
            currentAnimator = set1;
        });
    }

//    private void zoomImageFromThumb3(View rootView, final View thumbView) {
//        // If there's an animation in progress, cancel it
//        // immediately and proceed with this one.
//        if (currentAnimator != null) {
//            currentAnimator.cancel();
//        }
//
//        // Calculate the starting and ending bounds for the zoomed-in image.
//        // This step involves lots of math. Yay, math.
//        final Rect startBounds = new Rect();
//        final Rect finalBounds = new Rect();
//        final Point globalOffset = new Point();
//
//        // The start bounds are the global visible rectangle of the thumbnail,
//        // and the final bounds are the global visible rectangle of the container
//        // view. Also set the container view's offset as the origin for the
//        // bounds, since that's the origin for the positioning animation
//        // properties (X, Y).
//        thumbView.getGlobalVisibleRect(startBounds);
//        rootView.findViewById(R.id.container).getGlobalVisibleRect(finalBounds, globalOffset);
//        startBounds.offset(-globalOffset.x, -globalOffset.y);
//        finalBounds.offset(-globalOffset.x, -globalOffset.y);
//
//        // Adjust the start bounds to be the same aspect ratio as the final
//        // bounds using the "center crop" technique. This prevents undesirable
//        // stretching during the animation. Also calculate the start scaling
//        // factor (the end scaling factor is always 1.0).
//        float startScale;
//        if ((float) finalBounds.width() / finalBounds.height() > (float) startBounds.width() / startBounds.height()) {
//            // Extend start bounds horizontally
//            startScale = (float) startBounds.height() / finalBounds.height();
//            float startWidth = startScale * finalBounds.width();
//            float deltaWidth = (startWidth - startBounds.width()) / 2;
//            startBounds.left -= deltaWidth;
//            startBounds.right += deltaWidth;
//        } else {
//            // Extend start bounds vertically
//            startScale = (float) startBounds.width() / finalBounds.width();
//            float startHeight = startScale * finalBounds.height();
//            float deltaHeight = (startHeight - startBounds.height()) / 2;
//            startBounds.top -= deltaHeight;
//            startBounds.bottom += deltaHeight;
//        }
//
//        // Hide the thumbnail and show the zoomed-in view. When the animation
//        // begins, it will position the zoomed-in view in the place of the
//        // thumbnail.
//        thumbView.setAlpha(0f);
//        expandedView3.setVisibility(View.VISIBLE);
//
//        // Set the pivot point for SCALE_X and SCALE_Y transformations
//        // to the top-left corner of the zoomed-in view (the default
//        // is the center of the view).
//        expandedView3.setPivotX(0f);
//        expandedView3.setPivotY(0f);
//
//        // Construct and run the parallel animation of the four translation and
//        // scale properties (X, Y, SCALE_X, and SCALE_Y).
//        AnimatorSet set = new AnimatorSet();
//        set
//                .play(ObjectAnimator.ofFloat(expandedView3, View.X,
//                        startBounds.left, finalBounds.left))
//                .with(ObjectAnimator.ofFloat(expandedView3, View.Y,
//                        startBounds.top, finalBounds.top))
//                .with(ObjectAnimator.ofFloat(expandedView3, View.SCALE_X,
//                        startScale, 1f))
//                .with(ObjectAnimator.ofFloat(expandedView3,
//                        View.SCALE_Y, startScale, 1f));
//        set.setDuration(shortAnimationDuration);
//        set.setInterpolator(new DecelerateInterpolator());
//        set.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                currentAnimator = null;
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animation) {
//                currentAnimator = null;
//            }
//        });
//        set.start();
//        currentAnimator = set;
//
//        // Upon clicking the zoomed-in image, it should zoom back down
//        // to the original bounds and show the thumbnail instead of
//        // the expanded image.
//        final float startScaleFinal = startScale;
//        expandedView3.setOnClickListener(view -> {
//            if (currentAnimator != null) {
//                currentAnimator.cancel();
//            }
//
//            // Animate the four positioning/sizing properties in parallel,
//            // back to their original values.
//            AnimatorSet set1 = new AnimatorSet();
//            set1.play(ObjectAnimator
//                    .ofFloat(expandedView3, View.X, startBounds.left))
//                    .with(ObjectAnimator
//                            .ofFloat(expandedView3,
//                                    View.Y, startBounds.top))
//                    .with(ObjectAnimator
//                            .ofFloat(expandedView3,
//                                    View.SCALE_X, startScaleFinal))
//                    .with(ObjectAnimator
//                            .ofFloat(expandedView3,
//                                    View.SCALE_Y, startScaleFinal));
//            set1.setDuration(shortAnimationDuration);
//            set1.setInterpolator(new DecelerateInterpolator());
//            set1.addListener(new AnimatorListenerAdapter() {
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    thumbView.setAlpha(1f);
//                    expandedView3.setVisibility(View.GONE);
//                    currentAnimator = null;
//                }
//
//                @Override
//                public void onAnimationCancel(Animator animation) {
//                    thumbView.setAlpha(1f);
//                    expandedView3.setVisibility(View.GONE);
//                    currentAnimator = null;
//                }
//            });
//            set1.start();
//            currentAnimator = set1;
//        });
//    }


}