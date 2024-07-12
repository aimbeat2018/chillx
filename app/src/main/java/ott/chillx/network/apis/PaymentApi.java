package ott.chillx.network.apis;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface PaymentApi {


    @FormUrlEncoded
    @POST("store_payment_info")
    Call<ResponseBody> savePayment(@Header("API-KEY") String apiKey,
                                   @Field("plan_id") String planId,
                                   @Field("user_id") String userId,
                                   @Field("paid_amount") String paidAmount,
                                   @Field("payment_info") String paymentInfo,
                                   @Field("age") String age,
                                   @Field("payment_method") String paymentMethod);



    @FormUrlEncoded
    @POST("store_event_purchase_info")
    Call<ResponseBody> saveEventPayment(@Header("API-KEY") String apiKey,
                                   @Field("event_id") String planId,
                                   @Field("user_id") String userId,
                                   @Field("paid_amount") String paidAmount,
                                   @Field("payment_info") String paymentInfo,
                                   @Field("payment_method") String paymentMethod);

    @FormUrlEncoded
    @POST("store_gold_payment_info")
    Call<ResponseBody> saveGoldPayment(@Header("API-KEY") String apiKey,
                                        @Field("videos_id") String planId,
                                        @Field("user_id") String userId,
                                        @Field("paid_amount") String paidAmount,
                                        @Field("payment_info") String paymentInfo,
                                        @Field("payment_method") String paymentMethod);
    @FormUrlEncoded
    @POST("check_phonepe_Status")
    Call<ResponseBody> check_phonepe_Status(@Header("API-KEY") String apiKey,
                                            @Field("x_verify") String marchant_id,
                                            @Field("merchantTransactionId") String marchant_trn_id
    );


    @FormUrlEncoded
    @POST("s2s_event")
    Call<ResponseBody> save_eventmmp(@Header("API-KEY") String apiKey,
                                     @Field("app_token") String app_token,
                                     @Field("tr_id") String tr_id,
                                     @Field("event_id") String event_id,
                                     @Field("ip") String ip,
                                     @Field("customer_uid") String customer_uid,
                                     @Field("customer_email") String customer_email,
                                     @Field("customer_name") String customer_name,
                                     @Field("customer_phone") String customer_phone,
                                     @Field("revenue") int revenue,
                                     @Field("discount") String discount,

                                     @Field("currency") String currency,
                                     @Field("event_value") String event_value,
                                     @Field("gaid") String gaid,
                                     @Field("idfa") String idfa,
                                     @Field("idfv") String idfv,

                                     @Field("amazon_fire_id") String amazon_fire_id,
                                     @Field("imei1") String imei1,

                                     @Field("imei2") String imei2,
                                     @Field("mac") String mac,
                                     @Field("order_id") String order_id,

                                     @Field("c_code") String c_code,
                                     @Field("status") String status);

}
