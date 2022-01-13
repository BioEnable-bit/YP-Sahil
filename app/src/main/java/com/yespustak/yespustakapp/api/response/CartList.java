package com.yespustak.yespustakapp.api.response;

import com.google.gson.annotations.SerializedName;
import com.yespustak.yespustakapp.models.CartModel;

import java.util.List;

public class CartList extends BaseResponse {

    @SerializedName("result")
    List<CartModel> cartModelList;

    public List<CartModel> getCartModelList() {
        return cartModelList;
    }
}
