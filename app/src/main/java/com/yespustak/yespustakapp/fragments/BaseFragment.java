package com.yespustak.yespustakapp.fragments;

import android.content.Intent;
import android.util.Log;

import androidx.fragment.app.Fragment;

import com.yespustak.yespustakapp.Constants;
import com.yespustak.yespustakapp.R;
import com.yespustak.yespustakapp.activities.FragmentActivity;
import com.yespustak.yespustakapp.api.Retrofit2Client;
import com.yespustak.yespustakapp.api.response.AddToCart;
import com.yespustak.yespustakapp.api.response.BookDetail;
import com.yespustak.yespustakapp.models.BookDetailModel;
import com.yespustak.yespustakapp.models.BookModel;
import com.yespustak.yespustakapp.utils.ModelSharedPref;
import com.yespustak.yespustakapp.utils.utils;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaseFragment extends Fragment {
    private static final String TAG = "BaseFragment";

    /**
     * Could handle back press.
     *
     * @return true if back press was handled
     */
    public boolean onBackPressed() {
        return false;
    }

    public void getBookDetails(int bookId) {
        SweetAlertDialog progressDialog = utils.showProgressBar(getContext());
        Call<BookDetail> call = Retrofit2Client.getInstance().getApiService().getBookDetails(bookId, utils.getIMEINumber());
        call.enqueue(new Callback<BookDetail>() {
            @Override
            public void onResponse(Call<BookDetail> call, Response<BookDetail> response) {
                Log.i(TAG, "onResponse: is success: " + response.isSuccessful());
                utils.hideProgressBar(progressDialog);
                if (response.isSuccessful()) {
                    BookDetailModel bookDetailModel = response.body().getBookDetailModels();
                    ModelSharedPref.getInstance().saveModel(bookDetailModel);

                    //create intent
                    Intent intent = new Intent(getContext(), FragmentActivity.class);
                    intent.putExtra("fragment", "bookDetails");
                    requireActivity().startActivity(intent);
                    requireActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                }
            }

            @Override
            public void onFailure(Call<BookDetail> call, Throwable t) {
                requestFailure(progressDialog, t);

            }
        });
    }

    public void openBookDetail(Object object) {
        BookModel bookModel = (BookModel) object;

        BookDetailModel bookDetailModel = new BookDetailModel();
        bookDetailModel.setId(bookModel.getId());
        bookDetailModel.setTitle(bookModel.getTitle());
        ModelSharedPref.getInstance().saveModel(bookDetailModel);


        Log.e(TAG, "openBookDetail: "+bookModel.getNcrt_boook_flag());

        //create intent
        Intent intent = new Intent(getContext(), FragmentActivity.class);
        intent.putExtra("fragment", "bookDetails");
        requireActivity().startActivity(intent);
        requireActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
    }

    public void reqToggleFavourite(boolean addToFavourite, int bookId) {
        //disable button to avoid multiple clicks

//        final SweetAlertDialog progressDialog = utils.showProgressBar(getContext());
        String deviceId = utils.getIMEINumber();
        Call<AddToCart> call = Retrofit2Client.getInstance().getApiService().toggleFavourite(deviceId, bookId, addToFavourite);
        call.enqueue(new Callback<AddToCart>() {
            @Override
            public void onResponse(Call<AddToCart> call, Response<AddToCart> response) {
                Log.i(TAG, "onResponse: is success: " + response.isSuccessful());
//                utils.hideProgressBar(progressDialog);

                String message = getString(R.string.msg_something_went_wrong);
                if (response.isSuccessful() && response.body() != null && response.body().getStatus() == Constants.STATUS_SUCCESS) {
                    message = response.body().getMessage();

                    //following method is overridden to handle further
                    handleToggleFavResp(addToFavourite, bookId);
                } else
                    message = "Failed to " + (addToFavourite ? "add in " : "remove from ") + "Favourite";

                utils.showToast(message);
            }

            @Override
            public void onFailure(Call<AddToCart> call, Throwable t) {
                utils.showToast("Failed to " + (addToFavourite ? "add in " : "remove from ") + "Favourite");
//                requestFailure(progressDialog, t);
            }
        });
    }

    void handleToggleFavResp(boolean addToFavourite, int bookId) {
        //TODO override this method to handle
    }

    ;

    public void requestFailure(SweetAlertDialog progressDialog, Throwable t) {
        utils.hideProgressBar(progressDialog);
        Log.e(TAG, "requestFailure: ", t);
        utils.showToast(t.getMessage());
    }
}
