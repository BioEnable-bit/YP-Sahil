package com.yespustak.yespustakapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.razorpay.Checkout;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;
import com.yespustak.yespustakapp.Constants;
import com.yespustak.yespustakapp.R;
import com.yespustak.yespustakapp.api.Retrofit2Client;
import com.yespustak.yespustakapp.api.Retrofit4Razorpay;
import com.yespustak.yespustakapp.api.request.SavePaymentRequest;
import com.yespustak.yespustakapp.api.response.AddToCart;
import com.yespustak.yespustakapp.api.response.BaseResponse;
import com.yespustak.yespustakapp.api.response.RazorpayPaymentErrorResponse;
import com.yespustak.yespustakapp.api.response.SavePaymentResponse;
import com.yespustak.yespustakapp.fragments.PaymentStatusFragment;
import com.yespustak.yespustakapp.fragments.WebContentFragment;
import com.yespustak.yespustakapp.models.BookDetailModel;
import com.yespustak.yespustakapp.models.NoteModel;
import com.yespustak.yespustakapp.models.OrdersModel;
import com.yespustak.yespustakapp.models.PaymentModel;
import com.yespustak.yespustakapp.models.UserModel;
import com.yespustak.yespustakapp.services.DownloadService;
import com.yespustak.yespustakapp.utils.App;
import com.yespustak.yespustakapp.utils.ModelSharedPref;
import com.yespustak.yespustakapp.utils.SharedPref;
import com.yespustak.yespustakapp.utils.SharedVariables;
import com.yespustak.yespustakapp.utils.utils;
import com.yespustak.yespustakapp.viewmodels.EditProfileFragmentViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FragmentActivity extends AppCompatActivity implements PaymentResultWithDataListener {
    private static final String TAG = "FragmentActivity";
    int amount;
    List<String> bookIds = new ArrayList<>();

    public UserModel user;

    String deviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //this is used to access user for payment and editProfile
        EditProfileFragmentViewModel viewModel = new ViewModelProvider(this).get(EditProfileFragmentViewModel.class);
        viewModel.getUserModelLiveData().observe(this, userModel -> user = userModel);

        // Get the NavController for your NavHostFragment
        NavController navController = Navigation.findNavController(this, R.id.fragment_activity_nav_host_fragment);
        // Set up the ActionBar to stay in sync with the NavController
        NavigationUI.setupActionBarWithNavController(this, navController);

        String fragment = getIntent().getStringExtra("fragment");
        boolean isCheckout = getIntent().getBooleanExtra("is_checkout", false);
        if (isCheckout)
            Checkout.preload(this);

        SharedPref.saveString("FragmentActivity", getApplicationContext(), SharedPref.fragment, fragment);
        NavGraph navGraph = navController.getGraph();

        //create data to send
        Bundle bundle = new Bundle();

        String title = null;
        switch (fragment) {
            case "bookDetails":
                navGraph.setStartDestination(R.id.bookDetailFragment);
                //other titles was static they where set using nav graph file
                BookDetailModel bookDetailModel = ModelSharedPref.getInstance().getModel(BookDetailModel.class);
                title = getString(R.string.text_action_bar_title, bookDetailModel.getTitle());
//            getSupportActionBar().setTitle("YesPustak | " + getIntent().getStringExtra("name"));
                break;
            case "cart":
                navGraph.setStartDestination(R.id.cartFragment);
                break;
            case "tnc":
                title = getString(R.string.text_action_bar_title, getString(R.string.terms_and_conditions));
                bundle.putInt(WebContentFragment.CONTENT_TYPE, WebContentFragment.CT_TERMS_AND_CONDITIONS);
                navGraph.setStartDestination(R.id.webContentFragment);
                break;
            case "pp":
                title = getString(R.string.text_action_bar_title, getString(R.string.title_privacy_policy));
                bundle.putInt(WebContentFragment.CONTENT_TYPE, WebContentFragment.CT_PRIVACY_POLICY);
                navGraph.setStartDestination(R.id.webContentFragment);
                break;
            case "notification":
                navGraph.setStartDestination(R.id.notificationFragment);
                break;
            case "assignments":
                navGraph.setStartDestination(R.id.assignments);
                break;

            case "teacher_assignment":
                title = "YES Pustak | "+getIntent().getStringExtra("teacher_name")+" Assignment";
                navGraph.setStartDestination(R.id.teacher_assignment);
                break;

            case "notice":
                navGraph.setStartDestination(R.id.notice);
                break;

            case "examtimetable":
                navGraph.setStartDestination(R.id.examTimeTable);
                break;

            case "requestABook":
                navGraph.setStartDestination(R.id.requestABookFragment);
                break;
            case "offers":
                navGraph.setStartDestination(R.id.offersFragment);
                break;
            case "favourite":
                navGraph.setStartDestination(R.id.favouriteBooksFragment);
                break;
            case "contact":
                navGraph.setStartDestination(R.id.contactFragment);
                break;
            case "feedback":
                navGraph.setStartDestination(R.id.feedbackFragment);
                break;
            case "faq":
                navGraph.setStartDestination(R.id.faqFragment);
                break;
            case "help":
                navGraph.setStartDestination(R.id.helpFragment);
                break;
            case "about":
                title = getString(R.string.text_action_bar_title, getString(R.string.title_our_story));
                bundle.putInt(WebContentFragment.CONTENT_TYPE, WebContentFragment.CT_OUR_STORY);
                navGraph.setStartDestination(R.id.webContentFragment);
                break;
            case "editProfile":
                navGraph.setStartDestination(R.id.editProfileFragment);
//                viewModel.getUserModelLiveData().observe(this, userModel -> user = userModel);
                break;
            case "purchasedBooks":
                navGraph.setStartDestination(R.id.purchasedFragment);
//                viewModel.getUserModelLiveData().observe(this, userModel -> user = userModel);
                break;
            case "editNote":
                navGraph.setStartDestination(R.id.editNoteFragment);
                NoteModel note = ModelSharedPref.getInstance().getModel(NoteModel.class);
                title = getString(R.string.text_action_bar_title, note.getId() == 0 ? getString(R.string.text_add_note) : getString(R.string.text_edit_note));
                break;
        }

        navController.setGraph(navGraph, bundle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (title != null)
            getSupportActionBar().setTitle(title);

        //get device id
        deviceId = utils.getIMEINumber();
    }

    public void startPayment(int amount, List<String> bookIds) {
        this.amount = amount;
        this.bookIds = bookIds;

        SweetAlertDialog progressDialog = utils.showProgressBar(this);
        Checkout checkout = new Checkout();

        /*
         * Set your logo here
         */
        checkout.setImage(R.drawable.ic_logo);

//        String credentials = Credentials.basic(DEMO_RAZORPAY_KEY, DEMO_RAZORPAY_SECRET);
        Call<OrdersModel> call = Retrofit4Razorpay.getInstance().getApiService().getOrders(
                SharedVariables.getRazorpayCredentials(), amount, Constants.INR);
        call.enqueue(new Callback<OrdersModel>() {
            @Override
            public void onResponse(Call<OrdersModel> call, Response<OrdersModel> response) {
                Log.e("Response", "" + response.message());
                if (response.body() != null && response.isSuccessful()) {
                    String orderId = response.body().getId();

                    checkout.setKeyID(SharedVariables.getRazorpayKey());
                    try {
                        JSONObject options = new JSONObject();
                        options.put("name", "Yes Pustak");
//                        options.put("description", "Book ids: " + bookIds.toString());
                        options.put("order_id", orderId);
//                        options.put("theme.color",  "#" + Integer.toHexString(getResources().getColor(R.color.colorPrimary)));
                        options.put("theme.color", "#F6925C");
                        options.put("currency", Constants.INR);
                        options.put("send_sms_hash", true);


                        JSONObject preFill = new JSONObject();
                        preFill.put("email", user.getEmail());
                        preFill.put("contact", user.getMobileNo());
                        options.put("prefill", preFill);

                        JSONObject retryObj = new JSONObject();
                        retryObj.put("enabled", false);
                        retryObj.put("max_count", 2);
                        options.put("retry", retryObj);

                        checkout.open(FragmentActivity.this, options);

                    } catch (JSONException exception) {
                        Log.e("JSON Exception: ", exception.getLocalizedMessage());
                    }

                    utils.hideProgressBar(progressDialog);
                }
            }

            @Override
            public void onFailure(Call<OrdersModel> call, Throwable t) {
                Log.e("Order Error", t.getLocalizedMessage());
                utils.hideProgressBar(progressDialog);
            }
        });

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem miCart = menu.findItem(R.id.mi_cart);
        int cartItemsCount = SharedPref.getInt(Constants.CART_ITEMS_COUNT);
        if (cartItemsCount == 0) {
            miCart.setActionView(null);
        } else {
            miCart.setActionView(R.layout.layout_cart_badge);
            View view = miCart.getActionView();
            TextView tvBadgeCounter = view.findViewById(R.id.badge_counter);
            tvBadgeCounter.setText(String.valueOf(cartItemsCount));
            view.setOnClickListener(v -> {
                Intent intent = new Intent(this, FragmentActivity.class);
                intent.putExtra("fragment", "cart");
                startActivity(intent);
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
            });
        }

//        MenuItem miHome = menu.findItem(R.id.mi_home);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.fragment_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.mi_cart) {
            Intent intent = new Intent(this, FragmentActivity.class);
            intent.putExtra("fragment", "cart");
            startActivity(intent);
            overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
        } else if (item.getItemId() == R.id.mi_home) {
            SharedPref.saveBoolean(TAG, Constants.OPEN_HOMEPAGE, true);
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("EXIT", true);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        invalidateOptionsMenu();
        super.onResume();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments.size() > 1)
            getSupportFragmentManager().popBackStack();
        else
            super.onBackPressed();
    }

    @Override
    public void onPaymentSuccess(String razorpayPaymentID, PaymentData paymentData) {
//        Toast.makeText(this, razorpayPaymentID, Toast.LENGTH_SHORT).show();
        //fetch transaction details
        Log.i(TAG, "onPaymentSuccess: payment data: " + paymentData.getData().toString());
        fetchPaymentDetails(razorpayPaymentID);
    }

//    private void capturePayment(String razorpayPaymentID) {
////        String credentials = Credentials.basic(DEMO_RAZORPAY_KEY, DEMO_RAZORPAY_SECRET);
//        Call<PaymentModel> call = new Retrofit4Razorpay().getApiService().capturePayment(razorpayPaymentID,
//                SharedVariables.getRazorpayCredentials(), amount, "INR");
//        call.enqueue(new Callback<PaymentModel>() {
//            @Override
//            public void onResponse(Call<PaymentModel> call, Response<PaymentModel> response) {
//                if (!response.isSuccessful()) {
//                    Log.e(TAG, "onResponse: request unsuccessful: " + response.raw().toString());
//                    return;
//                }
//
//                assert response.body() != null;
//                if (response.body().isCaptured()) {
//                    Log.e("Response", "captured");
//                    savePaymentInfo(response.body());
//                } else {
//                    Log.e("Response", "not captured");
//                }
//            }
//
//            @Override
//            public void onFailure(Call<PaymentModel> call, Throwable t) {
//                Log.e("Response", t.getLocalizedMessage());
//            }
//        });
//    }

    private void fetchPaymentDetails(String paymentId) {
//        String credentials = Credentials.basic(DEMO_RAZORPAY_KEY, DEMO_RAZORPAY_SECRET);
        Call<PaymentModel> call = Retrofit4Razorpay.getInstance().getApiService().fetchPayment(paymentId,
                SharedVariables.getRazorpayCredentials());
        call.enqueue(new Callback<PaymentModel>() {
            @Override
            public void onResponse(Call<PaymentModel> call, Response<PaymentModel> response) {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "onResponse: request unsuccessful: " + response.raw().toString());
                    return;
                }

                assert response.body() != null;
                if (response.body().isCaptured()) {
                    Log.i(TAG, "fetchPaymentDetails onResponse captured");
                    savePaymentInfo(response.body());
                } else {
                    Log.e(TAG, "fetchPaymentDetails not captured");
                }
            }

            @Override
            public void onFailure(Call<PaymentModel> call, Throwable t) {
                Log.e(TAG, "onFailure: ", t);
            }
        });
    }


    @Override
    public void onPaymentError(int code, String response, PaymentData paymentData) {
        Log.i(TAG, "onPaymentError: response: " + response);
        //It must be bug in razorpay
        if (response.charAt(0) == '&')
            response = response.substring(1);
        RazorpayPaymentErrorResponse paymentError = App.gson.fromJson(response, RazorpayPaymentErrorResponse.class);
        openPaymentStatus(false, paymentError.getError().getDescription());
    }

    /**
     * save payment info to our server
     *
     * @param paymentModel contains payment details received by razorpay
     */
    private void savePaymentInfo(PaymentModel paymentModel) {
        SweetAlertDialog progressAlert = utils.showProgressBar(this);

        //create request
        SavePaymentRequest savePaymentRequest = new SavePaymentRequest(paymentModel);
        savePaymentRequest.setBookIds(bookIds);
        savePaymentRequest.setDeviceId(deviceId);
//        savePaymentRequest.setStatus("Success");


        Call<SavePaymentResponse> callPayment = Retrofit2Client.getInstance().getApiService().savePayment(savePaymentRequest);

        //enqueue request
        callPayment.enqueue(new Callback<SavePaymentResponse>() {
            @Override
            public void onResponse(Call<SavePaymentResponse> call, Response<SavePaymentResponse> response) {
                Log.i(TAG, "onResponse: " + paymentModel.getStatus());

                if (paymentModel.getStatus() == null)
                    Log.e(TAG, "onResponse: payment status null");
                else {
                    //capture status already checked before calling savePaymentInfo(), so don't need to check here
                    openPaymentStatus(true, paymentModel.getOrderId());

                    //remove purchased item from cart
                    if (bookIds.size() == 1 && SharedVariables.isBookExistInCart(Integer.parseInt(bookIds.get(0))))
                        removeBookFromCart();
                    else
                        removeAllBooksFromCart();
                    startService(new Intent(FragmentActivity.this, DownloadService.class));
                    Toast.makeText(FragmentActivity.this, "Books download has been started. Go to My Pustakalay for download status", Toast.LENGTH_LONG).show();
//                    utils.showToast(getString(R.string.text_download_start_shortly));
                }

                utils.hideProgressBar(progressAlert);
            }

            @Override
            public void onFailure(Call<SavePaymentResponse> call, Throwable t) {
                Log.e("Response", t.getLocalizedMessage());
                utils.hideProgressBar(progressAlert);
            }
        });
    }

    private void removeAllBooksFromCart() {
        Call<BaseResponse> call = Retrofit2Client.getInstance().getApiService().removeCartItems(deviceId);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                //update local cart info
                SharedVariables.cartItemIds.clear();
                SharedPref.saveInt(Constants.CART_ITEMS_COUNT, 0);
                invalidateOptionsMenu();
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: ", t);
            }
        });
    }

    private void removeBookFromCart() {
        Call<AddToCart> call = Retrofit2Client.getInstance().getApiService().removeFromCartList(deviceId, Integer.parseInt(bookIds.get(0)));
        call.enqueue(new Callback<AddToCart>() {
            @Override
            public void onResponse(Call<AddToCart> call, Response<AddToCart> response) {
                Log.i("CartAdapter", "onResponse: is success: " + response.isSuccessful());

                if (response.isSuccessful() && response.body() != null) {

                    if (response.body().getStatus() == Constants.STATUS_SUCCESS) {
                        SharedVariables.updateCartItemIds(response.body().getResult());
                        SharedPref.saveInt(Constants.CART_ITEMS_COUNT, response.body().getResult().size());
                        invalidateOptionsMenu();
                    }

                } else
                    utils.showToast(getString(R.string.msg_something_went_wrong) + "while removing item from cart");
            }

            @Override
            public void onFailure(Call<AddToCart> call, Throwable t) {
                Log.e(TAG, "onFailure: ", t);
            }
        });
    }

    private void openPaymentStatus(boolean isSuccess, String message) {
        Log.i(TAG, "openPaymentStatus: opening payment status fragment");

        // Get the NavController for your NavHostFragment
        NavController navController = Navigation.findNavController(this, R.id.fragment_activity_nav_host_fragment);

        Bundle args = PaymentStatusFragment.getBundleArgs(
                isSuccess ? PaymentStatusFragment.STATUS_SUCCESS : PaymentStatusFragment.STATUS_FAIL,
                amount, SharedVariables.user.getName(), getString(R.string.app_name), message);

        navController.navigate(navController.getCurrentDestination().getId() == R.id.bookDetailFragment ?
                R.id.action_bookDetailFragment_to_paymentStatusFragment : R.id.action_cartFragment_to_paymentStatusFragment, args);

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(getString(R.string.app_name) + " | " + (isSuccess ? "Thank you!" : "We are sorry"));
    }

}