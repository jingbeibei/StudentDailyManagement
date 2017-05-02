package com.nuc.jingbeibei.studentdailymanagement.beans;

/**
 * Created by 景贝贝 on 2016/8/27.
 */
public class BannerPic {
    private String ID;
    private String Title;
    private String PublishTime;
//    private String Contents;
    private String TitlePicture;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getPublishTime() {
        return PublishTime;
    }

    public void setPublishTime(String publishTime) {
        PublishTime = publishTime;
    }

    public String getTitlePicture() {
        return TitlePicture;
    }

    public void setTitlePicture(String titlePicture) {
        TitlePicture = titlePicture;
    }
}
