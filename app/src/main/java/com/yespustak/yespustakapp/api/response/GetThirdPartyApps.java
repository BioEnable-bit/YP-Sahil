package com.yespustak.yespustakapp.api.response;


import com.google.gson.annotations.SerializedName;
import com.yespustak.yespustakapp.models.AppModel;

import java.util.List;

public class GetThirdPartyApps extends BaseResponse {

    @SerializedName("apps")
    private List<AppModel> thirdPartyApps;

    public List<AppModel> getThirdPartyApps() {
        return thirdPartyApps;
    }
}
