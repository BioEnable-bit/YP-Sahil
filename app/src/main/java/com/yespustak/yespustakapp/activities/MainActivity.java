package com.yespustak.yespustakapp.activities;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;
import com.tonyodev.fetch2.Status;
import com.yespustak.yespustakapp.Constants;
import com.yespustak.yespustakapp.R;
import com.yespustak.yespustakapp.api.Retrofit2Client;
import com.yespustak.yespustakapp.api.response.CartList;
import com.yespustak.yespustakapp.fragments.BaseFragment;
import com.yespustak.yespustakapp.fragments.HomeFragment;
import com.yespustak.yespustakapp.fragments.LibraryFragment;
import com.yespustak.yespustakapp.fragments.NotesFragment;
import com.yespustak.yespustakapp.fragments.ProfileFragment;
import com.yespustak.yespustakapp.fragments.SettingsFragment;
import com.yespustak.yespustakapp.models.DownloadBook;
import com.yespustak.yespustakapp.models.UserModel;
import com.yespustak.yespustakapp.repos.UserRepo;
import com.yespustak.yespustakapp.services.DownloadService;
import com.yespustak.yespustakapp.utils.SharedPref;
import com.yespustak.yespustakapp.utils.SharedVariables;
import com.yespustak.yespustakapp.utils.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    private final String TAG = this.getClass().getSimpleName();

    private boolean doubleBackToExitPressedOnce = false;

    @SuppressLint("StaticFieldLeak")
    private static ImageView ivProfile;
    private ConstraintLayout clNavHeader, llDownloadContainer;
    private LinearLayout llRemoveDownload;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private TextView tvUserName;
    private TextView tvYpPustakalay, tvMyPustakalay, tvProfile, tvPurchasedBooks,
            tvNotification, tvCart, tvGullak, tvRequestABook,tvAssignments,
            tvExtras,tvNotice,tvExamTimetable, tvOffers, tvFavourite, tvSettings, tvContactUs, tvFeedback, tvAboutUs, tvLogout, txtVersion;

    private CardView cvDownloadProgress;
    private ProgressBar pbDownload;
    private TextView tvBookName, tvStatus, tvProgress, tvProgressSize, tvEta;
    private ImageView ivClose,ivExpand;
    private final List<Integer> hiddenDownloads = new ArrayList<>();

    TextView badgeCounter;
    MenuItem miCart;

    UserRepo userRepo;
    boolean expanded = false;



//    @SuppressLint("UseCompatLoadingForDrawables")
//    public static void setProfileImg(Context ctx) {
//        String imgUri = SharedPref.getString("Main", ctx, SharedPref.profileImgUri);
//        if (imgUri != null) {
//            ivProfile.setImageBitmap(utils.decodeBase64(imgUri));
//        } else {
//            ivProfile.setImageDrawable(ctx.getResources().getDrawable(R.drawable.ic_launcher_background));
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userRepo = UserRepo.getInstance(getApplicationContext());
        userRepo.syncUser();
        SharedVariables.init(getApplicationContext());
        userRepo.getUserModelLiveData().observe(this, this::updateUi);

        getCartData();
        initialise();

    }

    private void updateUi(UserModel user) {
        if (user == null)
            return;

        if (user.getProfilePhoto() != null) {
            Picasso.get().load(Constants.DASHBOARD_URL + user.getProfilePhoto())
                    .placeholder(R.drawable.ic_baseline_person_24)
                    .error(R.drawable.img_not_available_thumb)
                    .into(ivProfile);
        }
        tvUserName.setText(user.getName());
    }

    private void getCartData() {
        String deviceId = utils.getIMEINumber();
        Call<CartList> call = Retrofit2Client.getInstance().getApiService().getCartList(deviceId);
        call.enqueue(new Callback<CartList>() {
            @Override
            public void onResponse(Call<CartList> call, Response<CartList> response) {
                Log.i(TAG, "onResponse: is success: " + response.body());
                if (response.isSuccessful()) {
                    SharedVariables.updateCartItemIds(response.body().getCartModelList());
                    SharedPref.saveInt(Constants.CART_ITEMS_COUNT, response.body().getCartModelList().size());
//                    invalidateOptionsMenu();
                    updateCartBadge();

                } else
                    utils.showToast("Fail to load cart");

            }

            @Override
            public void onFailure(Call<CartList> call, Throwable t) {

            }
        });
    }


    private void initialise() {
        setupViews();
    }

    @SuppressLint("SetTextI18n")
    private void setupViews() {
        llRemoveDownload = findViewById(R.id.ll_remove_download);
        llDownloadContainer = findViewById(R.id.ll_download_container);
        clNavHeader = findViewById(R.id.nav_header);
        viewPager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tabs);
        toolbar = findViewById(R.id.toolbar);
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        ivProfile = findViewById(R.id.iv_profile);
        tvUserName = findViewById(R.id.tv_user_name);
        txtVersion = findViewById(R.id.tvVersion);

        tvYpPustakalay = findViewById(R.id.tvYpPustakalay);
        tvMyPustakalay = findViewById(R.id.tvMyPustakalay);
        tvProfile = findViewById(R.id.tvProfile);
        tvPurchasedBooks = findViewById(R.id.tvPurchasedBooks);
        tvNotification = findViewById(R.id.tvNotification);
        tvCart = findViewById(R.id.tvCart);
        tvGullak = findViewById(R.id.tvGullak);
        tvRequestABook = findViewById(R.id.tvRequestABook);
        tvAssignments = findViewById(R.id.tvAssignments);
        tvExtras = findViewById(R.id.tvExtras);
        tvExamTimetable = findViewById(R.id.tvExamTimetable);
        tvNotice = findViewById(R.id.tvNotice);
        tvOffers = findViewById(R.id.tvOffers);
        tvFavourite = findViewById(R.id.tvFavourite);
        tvSettings = findViewById(R.id.tvSettings);
        tvContactUs = findViewById(R.id.tvContactUs);
        tvFeedback = findViewById(R.id.tvFeedback);
       // tvAboutUs = findViewById(R.id.tvAboutUs);
        tvLogout = findViewById(R.id.tv_logout);
        ivExpand = findViewById(R.id.iv_expand);

        cvDownloadProgress = findViewById(R.id.cv_download_progress);
        pbDownload = findViewById(R.id.pb_download);
        tvBookName = findViewById(R.id.tv_book_name);
        tvStatus = findViewById(R.id.tv_status);
        tvProgress = findViewById(R.id.tv_progress);
        tvProgressSize = findViewById(R.id.tv_progress_size);
        tvEta = findViewById(R.id.tv_eta);
        ivClose = findViewById(R.id.iv_close_download_popup);



//        setProfileImg(getApplicationContext());
        try {
            String versionName = getApplication().getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            txtVersion.setText(getString(R.string.text_version_name, versionName));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        /*
         * ORDER IMP: call setSupportActionBar() before creating ActionBarDrawerToggle
         */
        setSupportActionBar(toolbar);
        setNavigationDrawer();
        setBottomTabLayout();
        setListeners();

        //TODO: check login status in background -> if auth fail -> show error and open login activity

        setShowInitialView();

        //Set drag listener

        llRemoveDownload.setOnDragListener(dragListener);
        llDownloadContainer.setOnDragListener(dragListener);
        cvDownloadProgress.setOnLongClickListener(v -> {
            String clipText = "This is our clip data text";
            ClipData.Item item = new ClipData.Item(clipText);
            String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};
            ClipData clipData = new ClipData(clipText, mimeTypes, item);

            View.DragShadowBuilder dragShadowBuilder = new View.DragShadowBuilder(cvDownloadProgress);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                cvDownloadProgress.startDragAndDrop(clipData, dragShadowBuilder, cvDownloadProgress, 0);
                cvDownloadProgress.setVisibility(View.INVISIBLE);
            }

            return false;
        });
    }

    private void setShowInitialView() {
        if (SharedPref.getBoolean(TAG, SharedPref.openPustakalayOnStart)) {
            tabLayout.selectTab(tabLayout.getTabAt(1));
            setupTabIcons(1);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            }
        }
    }

    private void setNavigationDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        displaySelectedScreen(R.id.nav_item_one);
    }

    @Override
    public void onBackPressed() {
        boolean handled = false;
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            handled = true;
        }

        if (!handled) {
            List<Fragment> fragmentList = getSupportFragmentManager().getFragments();

            //send onBackPressed to all back stack fragments
            for (Fragment f : fragmentList) {
                if (f instanceof BaseFragment) {
                    handled = ((BaseFragment) f).onBackPressed();

                    if (handled) {
                        break;
                    }
                }
            }
        }

        if (!handled) {
            //Handle exit
            if (doubleBackToExitPressedOnce)
                super.onBackPressed();
            else {
                doubleBackToExitPressedOnce = true;
                utils.showToast(getString(R.string.text_press_back_to_exit));

                new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
            }
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        miCart = menu.findItem(R.id.mi_cart);
        int cartItemsCount = SharedPref.getInt(Constants.CART_ITEMS_COUNT);

        miCart.setActionView(R.layout.layout_cart_badge);
        View view = miCart.getActionView();
        badgeCounter = view.findViewById(R.id.badge_counter);
        badgeCounter.setText(String.valueOf(cartItemsCount));
        badgeCounter.setVisibility(cartItemsCount == 0 ? View.VISIBLE : View.GONE);
        view.setOnClickListener(v -> openCart());

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.mi_cart) {
            openCart();
        }
        return super.onOptionsItemSelected(item);
    }

    private void openCart() {
        Intent intent = new Intent(this, FragmentActivity.class);
        intent.putExtra("fragment", "cart");
        startActivity(intent);
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
    }

    private void displaySelectedScreen(int itemId) {
        switch (itemId) {
            case R.id.nav_item_one:
                break;
            case R.id.nav_item_two:
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
    }

    private void setListeners() {
        clNavHeader.setOnClickListener(this);
        tvYpPustakalay.setOnClickListener(this);
        tvMyPustakalay.setOnClickListener(this);
        tvProfile.setOnClickListener(this);
        tvPurchasedBooks.setOnClickListener(this);
        tvNotification.setOnClickListener(this);
        tvCart.setOnClickListener(this);
        tvGullak.setOnClickListener(this);
        tvRequestABook.setOnClickListener(this);
        tvAssignments.setOnClickListener(this);
        tvExtras.setOnClickListener(this);
        tvNotice.setOnClickListener(this);
        tvExamTimetable.setOnClickListener(this);
        tvOffers.setOnClickListener(this);
        tvFavourite.setOnClickListener(this);
        tvSettings.setOnClickListener(this);
        tvContactUs.setOnClickListener(this);
        tvFeedback.setOnClickListener(this);
     //   tvAboutUs.setOnClickListener(this);
        tvLogout.setOnClickListener(this);
        ivExpand.setOnClickListener(this);


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                setupTabIcons(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                setupTabIcons(tab.getPosition());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                setupTabIcons(tab.getPosition());
            }
        });

        ivClose.setOnClickListener(this);
    }

    private void setBottomTabLayout() {
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.selectTab(tabLayout.getTabAt(2));
        setupTabIcons(2);
//        if (SharedPref.getBoolean(TAG, SharedPref.setHomeKey)) {
//            tabLayout.selectTab(tabLayout.getTabAt(1));
//            setupTabIcons(1);
//        } else {
//            tabLayout.selectTab(tabLayout.getTabAt(2));
//            setupTabIcons(2);
//        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(this.getSupportFragmentManager());
        adapter.addFrag(new NotesFragment(), "ONE");
        adapter.addFrag(new LibraryFragment(), "TWO");
        adapter.addFrag(new HomeFragment(), "THREE");
        adapter.addFrag(new ProfileFragment(), "THREE");
        adapter.addFrag(new SettingsFragment(), "THREE");
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);
    }

    private void setupTabIcons(int selectedTabPosition) {
        SharedPref.saveInt(TAG, getApplicationContext(), SharedPref.selectedTabPosition, selectedTabPosition);
        if (selectedTabPosition == 0) {
            toolbar.setTitle("YES Pustak | Notes");
            setTabIcon(0, R.drawable.ic_notes_selected);
            setTabIcon(1, R.drawable.ic_library_unselected);
            setTabIcon(2, R.drawable.ic_home_unselected);
            setTabIcon(3, R.drawable.ic_profile_unselected);
            setTabIcon(4, R.drawable.ic_settings_unselected);
        } else if (selectedTabPosition == 1) {
            toolbar.setTitle("YES Pustak | My Pustakalay");
            setTabIcon(0, R.drawable.ic_notes_unselected);
            setTabIcon(1, R.drawable.ic_library_selected);
            setTabIcon(2, R.drawable.ic_home_unselected);
            setTabIcon(3, R.drawable.ic_profile_unselected);
            setTabIcon(4, R.drawable.ic_settings_unselected);
        } else if (selectedTabPosition == 2) {
            toolbar.setTitle("YES Pustak | Homepage");
            setTabIcon(0, R.drawable.ic_notes_unselected);
            setTabIcon(1, R.drawable.ic_library_unselected);
            setTabIcon(2, R.drawable.ic_home_selected);
            setTabIcon(3, R.drawable.ic_profile_unselected);
            setTabIcon(4, R.drawable.ic_settings_unselected);
        } else if (selectedTabPosition == 3) {
            toolbar.setTitle("YES Pustak | Profile");
            setTabIcon(0, R.drawable.ic_notes_unselected);
            setTabIcon(1, R.drawable.ic_library_unselected);
            setTabIcon(2, R.drawable.ic_home_unselected);
            setTabIcon(3, R.drawable.ic_profile_selected);
            setTabIcon(4, R.drawable.ic_settings_unselected);
        } else if (selectedTabPosition == 4) {
            toolbar.setTitle("YES Pustak | Settings");
            setTabIcon(0, R.drawable.ic_notes_unselected);
            setTabIcon(1, R.drawable.ic_library_unselected);
            setTabIcon(2, R.drawable.ic_home_unselected);
            setTabIcon(3, R.drawable.ic_profile_unselected);
            setTabIcon(4, R.drawable.ic_settings_selected);
        }

    }

    private void setTabIcon(int selectedTabPosition, int ic_notes_selected) {
        Objects.requireNonNull(tabLayout.getTabAt(selectedTabPosition)).setIcon(ic_notes_selected);
    }

    public void updateCartBadge() {
        if (badgeCounter != null) {
            int cartItemsCount = SharedPref.getInt(Constants.CART_ITEMS_COUNT);
            badgeCounter.setVisibility(cartItemsCount == 0 ? View.GONE : View.VISIBLE);
            badgeCounter.setText(String.valueOf(cartItemsCount));
        }
    }

    private void updateDownloadView(Status status, String filesProgress, DownloadBook book) {
        if (book != null && status == Status.DOWNLOADING && !hiddenDownloads.contains(book.getId())) {
            tvBookName.setText(book.getTitle());
            pbDownload.setProgress(book.getProgress());
            tvStatus.setText(filesProgress);
            tvProgress.setText(getString(R.string.percent_progress, book.getProgress()));
            tvProgressSize.setText(getString(R.string.text_size_progress, utils.bytesToReadableStr(book.getDownloadedBytes()), utils.bytesToReadableStr(book.getTotalBytes())));
            tvEta.setText(utils.getETAString(this, book.getEtaInMilliSeconds()));

            cvDownloadProgress.setTag(book.getId());
            ivClose.setTag(book.getId());

            if (cvDownloadProgress.getVisibility() == View.GONE)
                cvDownloadProgress.setVisibility(View.VISIBLE);
        } else {
            if (cvDownloadProgress.getVisibility() == View.VISIBLE)
                cvDownloadProgress.setVisibility(View.GONE);
        }

    }

    public void showHomepage() {
        tabLayout.selectTab(tabLayout.getTabAt(2));
        setupTabIcons(2);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        displaySelectedScreen(item.getItemId());
        return false;
    }

    //Handle view clicks here
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        //remove user logged in status and launch login activity
        switch (v.getId()) {
            case R.id.nav_header:
            case R.id.tvProfile:
                tabLayout.selectTab(tabLayout.getTabAt(3));
                setupTabIcons(3);
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }
                break;

            case R.id.tvYpPustakalay:
                tabLayout.selectTab(tabLayout.getTabAt(2));
                setupTabIcons(2);
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }
                break;

            case R.id.tvMyPustakalay:
                tabLayout.selectTab(tabLayout.getTabAt(1));
                setupTabIcons(1);
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }
                break;

            case R.id.tvPurchasedBooks:
                utils.gotoFragment(MainActivity.this, "purchasedBooks");
                break;

            case R.id.tvNotification:
                utils.gotoFragment(MainActivity.this, "notification");
                break;

            case R.id.tvCart:
                utils.gotoFragment(MainActivity.this, "cart");
                break;

//            case R.id.tvGullak:
//                break;

            case R.id.tvRequestABook:
                utils.gotoFragment(MainActivity.this, "requestABook");
                break;

            case R.id.tvAssignments:
                utils.gotoFragment(MainActivity.this, "assignments");
                break;

            case R.id.iv_expand:
            case R.id.tvExtras:

                if(!expanded) {
                    expanded = true;
                    ivExpand.setImageDrawable(getDrawable(R.drawable.ic_baseline_keyboard_arrow_up_24));
                    tvNotice.setVisibility(View.VISIBLE);
                    tvExamTimetable.setVisibility(View.VISIBLE);
                }
                else
                {
                    expanded = false;
                    ivExpand.setImageDrawable(getDrawable(R.drawable.ic_baseline_keyboard_arrow_down_24));
                    tvNotice.setVisibility(View.GONE);
                    tvExamTimetable.setVisibility(View.GONE);
                }
               break;
                
                

            case R.id.tvOffers:
                utils.gotoFragment(MainActivity.this, "offers");
                break;

            case R.id.tvNotice:
                utils.gotoFragment(MainActivity.this, "notice");
                break;

            case R.id.tvExamTimetable:
                utils.gotoFragment(MainActivity.this, "examtimetable");
                break;

            case R.id.tvFavourite:
                utils.gotoFragment(MainActivity.this, "favourite");
                break;

            case R.id.tvSettings:
                tabLayout.selectTab(tabLayout.getTabAt(4));
                setupTabIcons(4);
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }
                break;

            case R.id.tvContactUs:
                utils.gotoFragment(MainActivity.this, "contact");
                break;

            case R.id.tvFeedback:
                utils.gotoFragment(MainActivity.this, "feedback");
                break;

//            case R.id.tvAboutUs:
//                utils.gotoFragment(MainActivity.this, "about");
//                break;

            case R.id.tv_logout:
                SharedPref.removeshr(TAG, getApplicationContext(), SharedPref.isLoggedIn);
                SharedPref.removeshr(TAG, getApplicationContext(), Constants.AUTH_TOKEN);

                //also clear room
                userRepo.deleteAll();
                utils.gotoNextActivity(this, LoginRegisterActivity.class, true);
                break;

//            case R.id.iv_close_download_popup:
//                hiddenDownloads.add((Integer) v.getTag());
//                cvDownloadProgress.setVisibility(View.GONE);
//                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        startService(new Intent(this, DownloadService.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (SharedPref.getBoolean(TAG, Constants.OPEN_HOMEPAGE)) {
            showHomepage();
            SharedPref.saveBoolean(TAG, Constants.OPEN_HOMEPAGE, false);

        } else if (SharedPref.getBoolean(TAG, Constants.OPEN_MY_PUSTAKALAY)) {
            tabLayout.selectTab(tabLayout.getTabAt(1));
            setupTabIcons(1);
            SharedPref.saveBoolean(TAG, Constants.OPEN_MY_PUSTAKALAY, false);
        }

        // Register to receive messages.
        // We are registering an observer (mMessageReceiver) to receive Intents
        // with actions named DownloadService.PROGRESS_UPDATE
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(DownloadService.KEY_PROGRESS_UPDATE));
    }

    @Override
    protected void onPause() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onPause();
    }

    private final View.OnDragListener dragListener = new View.OnDragListener() {
        @Override
        public boolean onDrag(View view, DragEvent event) {
            // Defines a variable to store the action type for the incoming event
            final int action = event.getAction();

            switch (action) {
                case DragEvent.ACTION_DRAG_STARTED:
                    Log.i(TAG, "onDrag: ACTION_DRAG_STARTED");
                    llRemoveDownload.setVisibility(View.VISIBLE);
                    return true;

                case DragEvent.ACTION_DRAG_ENTERED:
                    Log.i(TAG, "onDrag: ACTION_DRAG_ENTERED into : " + view.getId());
                    if (view.getId() == R.id.ll_remove_download)
                        view.setBackground(AppCompatResources.getDrawable(MainActivity.this, R.drawable.gradient_gb_red));
                    view.invalidate();
                    return true;

                case DragEvent.ACTION_DRAG_ENDED:
                    llRemoveDownload.setVisibility(View.GONE);
                    view.invalidate();
                    Log.i(TAG, "onDrag: ACTION_DRAG_ENDED");
                    return true;

                case DragEvent.ACTION_DRAG_EXITED:
                    Log.i(TAG, "onDrag: ACTION_DRAG_EXITED from: " + view.getId());
                    if (view.getId() == R.id.ll_remove_download)
                        view.setBackground(AppCompatResources.getDrawable(MainActivity.this, R.drawable.gradient_bg_gray));

                    view.invalidate();
                    return true;

                case DragEvent.ACTION_DRAG_LOCATION:
                    // Ignore the event
                    return true;

                case DragEvent.ACTION_DROP:
                    Log.i(TAG, "onDrag: ACTION_DROP in : " + view.getId());
                    view.invalidate();

                    //remove from old layout
                    View draggedView = (View) event.getLocalState();
//
                    if (view.getId() == R.id.ll_download_container) {
                        draggedView.setVisibility(View.VISIBLE);
                    } else {
                        hiddenDownloads.add((Integer) draggedView.getTag());
                        draggedView.setVisibility(View.GONE);
                    }
                    return true;

            }

            return false;
        }
    };

    // Our handler for received Intents. This will be called whenever an Intent
    private final BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            Status status = (Status) intent.getSerializableExtra(DownloadService.KEY_STATUS);
            String filesProgress = intent.getStringExtra(DownloadService.KEY_FILE_PROGRESS);
            DownloadBook book = (DownloadBook) intent.getSerializableExtra(DownloadService.KEY_DOWNLOAD);
//            Log.d("receiver", "Got message: " + book.getId());
            updateDownloadView(status, filesProgress, book);
        }
    };

    static class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {

            // return null to display only the icon
            return null;
        }
    }


}