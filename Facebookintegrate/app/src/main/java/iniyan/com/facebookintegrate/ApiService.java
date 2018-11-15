package iniyan.com.facebookintegrate;

import iniyan.com.facebookintegrate.model.Getgroups;
import iniyan.com.facebookintegrate.model.JoinAddResponse;
import io.reactivex.Single;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Murugan on 07-11-2018.
 */


public interface ApiService {

    @FormUrlEncoded
    @POST("addCustomer")
    Single<String> register(@Field("ContactNo") String ContactNo,
                            @Field("EmailID") String EmailID,
                            @Field("FirstName") String FirstName,
                            @Field("LastName") String LastName,
                            @Field("FireBaseID") String FireBaseID,
                            @Field("imageurl") String imageurl
                            );




    @GET("https://smsapi.engineeringtgr.com/send/")
    Single<SMSResponse>  sendSMS(@Query("Mobile") String mobileno,@Query("Password") String Password,@Query("Key") String key,
                            @Query("Message") String message,@Query("To") String to);



    @GET("http://18.224.1.148:3000/api/v1/getGroups")
    @Headers({"Authorization: Basic eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyIjp7ImlkIjoxfSwiaWF0IjoxNTQxODI1MzIyfQ.kHhDydxbVz41m-OkI2lQ2ewvO6YgorYozDztdsSbf0s"})
    Single<Getgroups> getGroup();


    @POST("http://18.224.1.148:3000/api/v1/addGroupJoin")
    //@Headers({"Authorization: Basic eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyIjp7ImlkIjoxfSwiaWF0IjoxNTQxODI1MzIyfQ.kHhDydxbVz41m-OkI2lQ2ewvO6YgorYozDztdsSbf0s"})
    Single<JoinAddResponse> addGroupJoin(@Query("pGroup_id") int group_id, @Query("pCustomer_id") int customer_id, @Query("pJoin_status") String join_status,
                                         @Query("pNo_multy") int no_multy, @Query("pPayment_status") String payment_status);




}
