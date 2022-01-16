package com.yespustak.yespustakapp.api.response;


import com.google.gson.annotations.SerializedName;
import com.yespustak.yespustakapp.models.ExtrasAppModel;
import com.yespustak.yespustakapp.models.GamesAppModel;

import java.util.List;

public class GetThirdPartyGamesApps extends BaseResponse {


    @SerializedName("apps")
    private List<GamesAppModel> thirdPartyAppsGames;

    public List<GamesAppModel> getThirdPartyAppsGames() {
        return thirdPartyAppsGames;
    }


}
