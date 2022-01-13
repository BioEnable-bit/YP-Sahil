package com.yespustak.yespustakapp.api.response;


import com.google.gson.annotations.SerializedName;
import com.yespustak.yespustakapp.models.ExtrasAppModel;

import java.util.List;

public class GetThirdPartyExtrasApps extends BaseResponse {


    @SerializedName("apps")
    private List<ExtrasAppModel> thirdPartyAppsExtras;

    public List<ExtrasAppModel> getThirdPartyExtraApps() {
        return thirdPartyAppsExtras;
    }


}
