package com.tfb.cbit.models.dbmodel;

import android.os.Parcel;
import android.os.Parcelable;

public class UpcomingContestModel implements Parcelable {

    private long id;
    private String contestDateTime = "";
    private String serverDateTime = "";
    private int isEnable = 0;
    private String contestID = "";
    private String contestName = "";
    private String contestType = "";

    public String getContestType() {
        return contestType;
    }

    public void setContestType(String contestType) {
        this.contestType = contestType;
    }

    public UpcomingContestModel(){

    }

    public UpcomingContestModel(String contestDateTime,String serverDateTime,int isEnable,String contestID,String contestName,String contestType){
        this.contestDateTime = contestDateTime;
        this.serverDateTime = serverDateTime;
        this.isEnable = isEnable;
        this.contestID = contestID;
        this.contestName = contestName;
        this.contestType = contestType;
    }

    protected UpcomingContestModel(Parcel in) {
        id = in.readLong();
        contestDateTime = in.readString();
        serverDateTime = in.readString();
        isEnable = in.readInt();
        contestID = in.readString();
        contestName = in.readString();
        contestType = in.readString();
    }

    public static final Creator<UpcomingContestModel> CREATOR = new Creator<UpcomingContestModel>() {
        @Override
        public UpcomingContestModel createFromParcel(Parcel in) {
            return new UpcomingContestModel(in);
        }

        @Override
        public UpcomingContestModel[] newArray(int size) {
            return new UpcomingContestModel[size];
        }
    };

    public int getIsEnable() {
        return isEnable;
    }

    public void setIsEnable(int isEnable) {
        this.isEnable = isEnable;
    }

    public String getContestDateTime() {
        return contestDateTime;
    }

    public void setContestDateTime(String contestDateTime) {
        this.contestDateTime = contestDateTime;
    }

    public void setServerDateTime(String serverDateTime) {
        this.serverDateTime = serverDateTime;
    }

    public String getServerDateTime() {
        return serverDateTime;
    }

    public long getId(){
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContestID() {
        return contestID;
    }

    public String getContestName() {
        return contestName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeLong(id);
        dest.writeString(contestDateTime);
        dest.writeString(serverDateTime);
        dest.writeInt(isEnable);
        dest.writeString(contestID);
        dest.writeString(contestName);
        dest.writeString(contestType);
    }

}
