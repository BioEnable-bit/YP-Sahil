package com.yespustak.yespustakapp.api.response;

import com.google.gson.annotations.SerializedName;
import com.yespustak.yespustakapp.models.BoardModel;

import java.util.List;

public class Boards extends BaseResponse {

    @SerializedName("boards")
    List<BoardModel> boardModels;

    public List<BoardModel> getBoardModels() {
        return boardModels;
    }
}
