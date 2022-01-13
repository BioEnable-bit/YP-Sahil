package com.yespustak.yespustakapp.api.response;

import com.google.gson.annotations.SerializedName;

public class AboutUs extends BaseResponse {

    @SerializedName("msg_our_story_1")
    String msgOurStory1;

    @SerializedName("msg_our_story_2")
    String msgOurStory2;

    @SerializedName("msg_vision")
    String msgVision;

    @SerializedName("msg_mission")
    String msgMission;

    @SerializedName("msg_how")
    String msgHow;

    public String getMsgOurStory1() {
        return msgOurStory1;
    }

    public void setMsgOurStory1(String msgOurStory1) {
        this.msgOurStory1 = msgOurStory1;
    }

    public String getMsgOurStory2() {
        return msgOurStory2;
    }

    public void setMsgOurStory2(String msgOurStory2) {
        this.msgOurStory2 = msgOurStory2;
    }

    public String getMsgVision() {
        return msgVision;
    }

    public void setMsgVision(String msgVision) {
        this.msgVision = msgVision;
    }

    public String getMsgMission() {
        return msgMission;
    }

    public void setMsgMission(String msgMission) {
        this.msgMission = msgMission;
    }

    public String getMsgHow() {
        return msgHow;
    }

    public void setMsgHow(String msgHow) {
        this.msgHow = msgHow;
    }
}
