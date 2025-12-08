package com.tfb.cbit.api;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

public interface APIInterface {

    @FormUrlEncoded
    @POST("getAllContest")
    Call<ResponseBody> getAllContest(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author,
            @Field("start") int start,
            @Field("limit") String limit
    );

    @POST("getAllSpecialContest")
    Call<ResponseBody> getAllSpecialContest(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author
    );
    @POST("getautopilotcontent")
    Call<ResponseBody> getautopilotcontent(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author
    );
    @POST("getDemoScreen")
    Call<ResponseBody> getDemoScreen(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author
    );
   /* @FormUrlEncoded
    @POST("signIn")
    Call<ResponseBody> signIn(
            @Field("email") String email,
            @Field("password") String password,
            @Field("deviceId") String deviceId,
            @Field("deviceType") String deviceType);*/

    @FormUrlEncoded
    @POST("signIn_new")
    Call<ResponseBody> signIn(
            @Field("data") String data);


   /* @FormUrlEncoded
    @POST("signUp")
    Call<ResponseBody> signUp(
            @Field("name") String name,
            @Field("email") String email,
            @Field("password") String password,
            @Field("mobile_no") String mobile_no,
            @Field("deviceId") String deviceId,
            @Field("deviceType") String deviceType
    );*/

    @FormUrlEncoded
    @POST("signUp")
    Call<ResponseBody> signUp(
            @Field("data") String data);

    @FormUrlEncoded
    @POST("LoginOtp")
    Call<ResponseBody> signUpwithMobile(
            @Field("data") String data);

   /* @FormUrlEncoded
    @POST("joinContest")
    Call<ResponseBody> joinContest(
       @Header("Authorization") String Authorization,
       @Header("Author") String Author,
       @Field("contest_id") String contest_id,
       @Field("tickets") String tickets
    );*/

    @FormUrlEncoded
    @POST("joinContest")
    Call<ResponseBody> joinContest(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author,
            @Field("data") String data);

    @FormUrlEncoded
    @POST("AnyTimejoinContest")
    Call<ResponseBody> AnyTimejoinContest(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author,
            @Field("data") String data);

    @FormUrlEncoded
    @POST("setUserDefaultTicketPrice")
    Call<ResponseBody> setUserDefaultTicketPrice(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author,
            @Field("data") String data);
    @FormUrlEncoded
    @POST("easyjoinContestPrice")
    Call<ResponseBody> easyjoinContestPrice(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author,
            @Field("data") String data);

    @FormUrlEncoded
    @POST("setAutoRenewEasyJoin")
    Call<ResponseBody> setAutoRenewEasyJoin(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author,
            @Field("data") String data);

     @FormUrlEncoded
    @POST("setAutoRenewEasyJoinStatus")
    Call<ResponseBody> setAutoRenewEasyJoinStatus(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author,
            @Field("data") String data);

    @POST("getMyContest")
    Call<ResponseBody> getMyContest(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author
    );

 /*@FormUrlEncoded
 @POST("changePassword")
 Call<ResponseBody> changePassword(
         @Header("Authorization") String Authorization,
         @Header("Author") String Author,
         @Field("oldPassword") String oldPassword,
         @Field("newPassword") String newPassword
 );*/

    @FormUrlEncoded
    @POST("changePassword")
    Call<ResponseBody> changePassword(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author,
            @Field("data") String data);

 /*@FormUrlEncoded
 @POST("forgotPassword")
 Call<ResponseBody> forgotPassword(
         @Field("email") String email
 );*/


    @FormUrlEncoded
    @POST("forgotPassword")
    Call<ResponseBody> forgotPassword(
            @Field("data") String data
    );

 /*@FormUrlEncoded
 @POST("addMoney")
 Call<ResponseBody> addMoney(
         @Header("Authorization") String Authorization,
         @Header("Author") String Author,
         @Field("amount") String amount
 );*/

    @FormUrlEncoded
    @POST("addMoney")
    Call<ResponseBody> addMoney(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author,
            @Field("data") String data
    );

    @FormUrlEncoded
    @POST("startTransaction")
    Call<ResponseBody> startTransaction(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author,
            @Field("data") String data
    );

    @FormUrlEncoded
    @POST("generateToken")
    Call<ResponseBody> generateToken(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author,
            @Field("data") String data
    );
    @FormUrlEncoded
    @POST("endTransaction")
    Call<ResponseBody> endTransaction(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author,
            @Field("data") String data
    );
    @FormUrlEncoded
    @POST("addJTicket")
    Call<ResponseBody> AddJRedeem(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author,
            @Field("data") String data
    );

    @FormUrlEncoded
    @POST("ApplyJtciket")
    Call<ResponseBody> ApplyJtciket(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author,
            @Field("data") String data
    );

 /*@FormUrlEncoded
 @POST("contestDetails")
 Call<ResponseBody> contestDetails(
         @Header("Authorization") String Authorization,
         @Header("Author") String Author,
         @Field("contest_id") String contest_id
 );*/

    @FormUrlEncoded
    @POST("getWaitingList")
    Call<ResponseBody> getWaitingList(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author,
            @Field("data") String data
    );

    @FormUrlEncoded
    @POST("contestDetails")
    Call<ResponseBody> contestDetails(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author,
            @Field("data") String data
    );
    @FormUrlEncoded
    @POST("contestDetailsAnyTimeGame")
    Call<ResponseBody> contestDetailsAnyTimeGame(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author,
            @Field("data") String data
    );
    @FormUrlEncoded
    @POST("updateIsWatch")
    Call<ResponseBody> updateIsWatch(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author,
            @Field("data") String data
    );
    @FormUrlEncoded
    @POST("getPassbook")
    Call<ResponseBody> getPassbook(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author,
            @Field("data") String data
    );

    @FormUrlEncoded
    @POST("getCCPassbook")
    Call<ResponseBody> getCCPassbook(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author,
            @Field("data") String data
    );

    @FormUrlEncoded
    @POST("contestHistory")
    Call<ResponseBody> contestHistory(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author, @Field("data") String data
    );

    @POST("referralDetail")
    Call<ResponseBody> ReferralDetails(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author
    );

 /*@FormUrlEncoded
 @POST("winnerList")
 Call<ResponseBody> winnerList(
         @Header("Authorization") String Authorization,
         @Header("Author") String Author,
         @Field("contestPriceId") String contestPriceId
 );*/

    @FormUrlEncoded
    @POST("winnerList")
    Call<ResponseBody> winnerList(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author,
            @Field("data") String data
    );
    @FormUrlEncoded
    @POST("anytimeWinnerList")
    Call<ResponseBody> anytimeWinnerList(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author,
            @Field("data") String data
    );
    @Multipart
    @POST("uploadProfileImage")
    Call<ResponseBody> uploadProfileImage(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author,
            @Part MultipartBody.Part photo
    );

 /*@FormUrlEncoded
 @POST("addPrivateContest")
 Call<ResponseBody> addPrivateContest(
         @Header("Authorization") String Authorization,
         @Header("Author") String Author,
         @Field("level") int level,
         @Field("type") int type,
         @Field("name") String name,
         @Field("startDate") String startDate,
         @Field("startTime") String startTime,
         @Field("ticketJson") String ticketJson,
         @Field("maxWinner") String maxWinner,
         @Field("isNotify") boolean isNotify
 );*/

    @FormUrlEncoded
    @POST("addPrivateContest")
    Call<ResponseBody> addPrivateContest(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author,
            @Field("data") String data
    );

 /*@FormUrlEncoded
 @POST("getByCode")
 Call<ResponseBody> getByCode(
         @Header("Authorization") String Authorization,
         @Header("Author") String Author,
         @Field("contestCode") String contestCode
 );*/

    @FormUrlEncoded
    @POST("getByCode")
    Call<ResponseBody> getByCode(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author,
            @Field("data") String data
    );

    @POST("getPrivateContest")
    Call<ResponseBody> getPrivateContest(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author
    );

    @FormUrlEncoded
    @POST("getPackages")
    Call<ResponseBody> getPackages(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author,
            @Field("StartTime") String StartTime,
            @Field("validity") String validity
    );

    @POST("contestDays")
    Call<ResponseBody> getContestDays(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author
    );

    @POST("contestTime")
    Call<ResponseBody> getContestTime(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author
    );

    @POST("getAllJTicket")
    Call<ResponseBody> getAllJTicket(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author
    );
    @POST("getJAssets")
    Call<ResponseBody> getJAssets(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author
    );
    @POST("getJhitsTotalAmount")
    Call<ResponseBody> getJhitsTotalAmount(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author
    );
    @GET("AnytimeGameNotificationCount")
    Call<ResponseBody> AnytimeGameNotificationCount(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author
    );
    @POST("getReferralCommitionTotalAmount")
    Call<ResponseBody> getReferralCommitionTotalAmount(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author
    );
    @FormUrlEncoded
    @POST("getUserJTicket")
    Call<ResponseBody> getUserJTicket(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author,
            @Field("data") String data
    );

    @FormUrlEncoded
    @POST("getUserJTicketName")
    Call<ResponseBody> getUserJTicketName(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author,
            @Field("data") String data
    );

    @POST("getAllJticketDatas")
    Call<ResponseBody> getAllJticketDatas(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author
    );
    @FormUrlEncoded
    @POST("getPopUpNotification")
    Call<ResponseBody> getPopUpNotification(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author,
            @Field("data") String data
    );
    @FormUrlEncoded
    @POST("getUserInfo")
    Call<ResponseBody> getUserInfo(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author,
            @Field("data") String data
    );
    @GET("getAppMaintenanceStatus")
    Call<ResponseBody> getAppMaintenanceStatus(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author
    );
    @FormUrlEncoded
    @POST("setPopUpNotification")
    Call<ResponseBody> setPopUpNotification(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author,
            @Field("data") String data
    );
    @FormUrlEncoded
    @POST("easyJoinContest")
    Call<ResponseBody> easyJoinContest(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author,
            @Field("data") String data
    );
    @POST("getdefaultJoinTicket")
    Call<ResponseBody> getdefaultJoinTicket(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author
    );
    @POST("getAutoRenewEasyJoin")
    Call<ResponseBody> getAutoRenewEasyJoin(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author
    );
    @POST("myPackage")
    Call<ResponseBody> myPackage(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author
    );

 /*@FormUrlEncoded
 @POST("buyPackage")
 Call<ResponseBody> buyPackage(
         @Header("Authorization") String Authorization,
         @Header("Author") String Author,
         @Field("packageId") String packageId
 );*/

    @FormUrlEncoded
    @POST("buyPackage")
    Call<ResponseBody> buyPackage(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author,

            @Field("packageId") String data
    );

/* @FormUrlEncoded
 @POST("getPrivateContestDetails")
 Call<ResponseBody> getPrivateContestDetails(
         @Header("Authorization") String Authorization,
         @Header("Author") String Author,
         @Field("contestId") String contestId
 );*/

    @FormUrlEncoded
    @POST("getPrivateContestDetails")
    Call<ResponseBody> getPrivateContestDetails(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author,
            @Field("data") String data
    );

    @POST("logout")
    Call<ResponseBody> logout(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author
    );

 /*@FormUrlEncoded
 @POST("addWallate")
 Call<ResponseBody> addWallate(
         @Header("Authorization") String Authorization,
         @Header("Author") String Author,
         @Field("mobile") String mobile,
         @Field("amount") String amount,
         @Field("type") String type,
         @Field("otpId") String otpId,
         @Field("otp") String otp
 );*/

    @FormUrlEncoded
    @POST("addWallate")
    Call<ResponseBody> addWallate(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author,
            @Field("data") String data
    );

    @POST("sendOtp")
    Call<ResponseBody> sendOtp(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author
    );

    @POST("getNotification")
    Call<ResponseBody> getNotification(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author
    );

 /*@FormUrlEncoded
 @POST("setNotification")
 Call<ResponseBody> setNotification(
         @Header("Authorization") String Authorization,
         @Header("Author") String Author,
         @Field("setNotification") int setNotification
 );*/


    @FormUrlEncoded
    @POST("setNotification")
    Call<ResponseBody> setNotification(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author,
            @Field("data") String data
    );

    @FormUrlEncoded
    @POST("AutoPilotUpdate")
    Call<ResponseBody> AutoPilotUpdate(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author,
            @Field("data") String data
    );

    @FormUrlEncoded
    @POST("RedeemDailyQoutaUpdate")
    Call<ResponseBody> RedeemDailyQoutaUpdate(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author,
            @Field("data") String data
    );

    @POST("getAds")
    Call<ResponseBody> getAds(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author
    );

    @Multipart
    @POST("updateKYC")
    Call<ResponseBody> updateKYC(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author,
            @PartMap() Map<String, RequestBody> partMap,
            @Part MultipartBody.Part photo
    );
    @Multipart
    @POST("setUserVoiceIssues")
    Call<ResponseBody> setUserVoiceIssues(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author,
            @Part MultipartBody.Part photo
    );
    @Multipart
    @POST("setUserIssues")
    Call<ResponseBody> setUserIssues(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author,
            @PartMap() Map<String, RequestBody> partMap,
            @Part MultipartBody.Part photo
    );

    @FormUrlEncoded
    @POST("sendOtpAuth")
    Call<ResponseBody> sendOtpAuth(
            @Field("data") String data
    );

    @FormUrlEncoded
    @POST("LoginSendOtp")
    Call<ResponseBody> loginwithmob(
            @Field("data") String data
    );
    @FormUrlEncoded
    @POST("checkForReferral")
    Call<ResponseBody> checkForReferral(
            @Field("data") String data
    );
    @FormUrlEncoded
    @POST("CheckVersion")
    Call<ResponseBody> CheckVersion(
            @Field("data") String data
    );

    @FormUrlEncoded
    @POST("getSpinningMachine")
    Call<ResponseBody> getSpinningMachine(@Header("Authorization") String Authorization,
                                          @Header("Author") String Author,
                                          @Field("test") String data);
    @FormUrlEncoded
    @POST("getSpinningMachineitemByDate")
    Call<ResponseBody> getSpinningMachineitemByDate(@Header("Authorization") String Authorization,
                                          @Header("Author") String Author,
                                          @Field("data") String dates);
    @FormUrlEncoded
    @POST("getlatestupdateapp")
    Call<ResponseBody> getLatestUpdateApp(@Header("Authorization") String Authorization,
                                                    @Header("Author") String Author,
                                                    @Field("data") String dates);

    @POST("getaddmoneystatus")
    Call<ResponseBody> getAddmoneyStatus(@Header("Authorization") String Authorization,
                                          @Header("Author") String Author);

    @POST("getUserJoinDateTime")
    Call<ResponseBody> getUserJoinDateTime(@Header("Authorization") String Authorization,
                                         @Header("Author") String Author);

    @FormUrlEncoded
    @POST("checkUserName")
    Call<ResponseBody> checkUserName(
            @Field("data") String data
    );

    @POST("profile")
    Call<ResponseBody> profile(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author
    );
    @POST("getUserQrCode")
    Call<ResponseBody> getUserQrCode(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author
    );
    @POST("checkPanStatus")
    Call<ResponseBody> checkPanStatus(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author
    );
    @POST("reportslisting")
    Call<ResponseBody> reportsListing(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author
    );
    @FormUrlEncoded
    @POST("setUserIssues")
    Call<ResponseBody> setUserIssues(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author,
            @Field("data") String data
    );
    @FormUrlEncoded
    @POST("getUsersIssueList")
    Call<ResponseBody> getUsersIssueList(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author,
            @Field("data") String data
    );
    @FormUrlEncoded
    @POST("addBank")
    Call<ResponseBody> addBank(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author,
            @Field("bank_name") String bank_name,
            @Field("account_no") String account_no,
            @Field("ifsc_code") String ifsc_code
    );

    @POST("accounts")
    Call<ResponseBody> accounts(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author
    );

    @GET("state_city")
    Call<ResponseBody> getStateCity(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author
    );

    @FormUrlEncoded
    @POST("redeeem")
    Call<ResponseBody> redeeem(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author,
            @Field("data") String data
    );

    @FormUrlEncoded
    @POST("joinUserList")
    Call<ResponseBody> joinUserList(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author,
            @Field("data") String data
    );

    @FormUrlEncoded
    @POST("addPrivateGroup")
    Call<ResponseBody> createGroup(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author,
            @Field("group_name") String group_name
    );

    @FormUrlEncoded
    @POST("ApplyJtciketApproach")
    Call<ResponseBody> applyJticketApproach(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author,
            @Field("data") String data);

    @FormUrlEncoded
    @POST("ApproachNegotiate")
    Call<ResponseBody> approachNegotiate(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author,
            @Field("data") String data
    );

    @FormUrlEncoded
    @POST("UserApproachNegotiate")
    Call<ResponseBody> UserApproachNegotiate(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author,
            @Field("data") String data
    );
    @FormUrlEncoded
    @POST("ApproachComfirm")
    Call<ResponseBody> approachComfirm(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author,
            @Field("data") String data
    );

    @FormUrlEncoded
    @POST("getAnyTimeGameList")
    Call<ResponseBody> getAnyTimeGameList(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author,
            @Field("data") String data
    );
    @FormUrlEncoded
    @POST("getAnyTimeGameContestList")
    Call<ResponseBody> getAnyTimeGameContestList(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author,
            @Field("data") String data
    );
    @POST("spinningItemCategory")
    Call<ResponseBody> spinningItemCategory(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author);

    @FormUrlEncoded
    @POST("getUserWaitingList")
    Call<ResponseBody> getUserWaitingList(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author,
            @Field("data") String data
    );

    @FormUrlEncoded
    @POST("addPrivateGroup")
    Call<ResponseBody> addPrivateGroup(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author,
            @Field("data") String data
    );
    @POST("allUsersPrivateGroup")
    Call<ResponseBody> allUsersPrivateGroup(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author
    );
    @FormUrlEncoded
    @POST("PrivateGroupUserList")
    Call<ResponseBody> PrivateGroupUserList(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author,
            @Field("data") String data
    );
    @FormUrlEncoded
    @POST("referal_criteria_chart")
    Call<ResponseBody> referalCriteriaChart(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author,
            @Field("data") String data
    );
    @FormUrlEncoded
    @POST("getReferralPopup")
    Call<ResponseBody> getReferralPopup(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author,
            @Field("data") String data
    );
    @GET("referal_criteria")
    Call<ResponseBody> referalCriteria(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author
    );
    @POST("UserJoinedGroupList")
    Call<ResponseBody> UserJoinedGroupList(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author
    );
    @POST("allPrivateGroup")
    Call<ResponseBody> allPrivateGroup(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author
    );
    @FormUrlEncoded
    @POST("privateGroupJoinContest")
    Call<ResponseBody> privateGroupJoinContest(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author,
            @Field("data") String data
    );
    @FormUrlEncoded
    @POST("requestToJoinPrivateGroup")
    Call<ResponseBody> requestToJoinPrivateGroup(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author,
            @Field("data") String data
    );
    @FormUrlEncoded
    @POST("acceptDeclineRequest")
    Call<ResponseBody> acceptDeclineRequest(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author,
            @Field("data") String data
    );
    @FormUrlEncoded
    @POST("allRequestsPrivateGroup")
    Call<ResponseBody> allRequestsPrivateGroup(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author,
            @Field("data") String data
    );
    @POST("allContestRequests")
    Call<ResponseBody> allContestRequests(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author
    );
    @POST("selectCategoryImages")
    Call<ResponseBody> selectCategoryImages(
            @Header("Authorization") String Authorization,
            @Header("Author") String Author
    );
}