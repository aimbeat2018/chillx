package ott.chillx.network.apis;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface PaymentGatewayApi {
    //    @FormUrlEncoded
    @GET("payumoney")
    Call<ResponseBody> payumoney(@Header("API-KEY") String apiKey,
                                 @Query("key") String key);

    @FormUrlEncoded
    @POST("merchant_details")
    Call<ResponseBody> googlePayData(@Header("API-KEY") String apiKey, @Field("userId") String userId);


    @FormUrlEncoded
    @POST("paymentgatway_status")
    Call<ResponseBody> paymentgatway_status(@Header("API-KEY") String apiKey,
                                            @Field("userId") String userId);

    @FormUrlEncoded
    @POST("payment_initiate")
    Call<ResponseBody> payment_initiate(@Header("API-KEY") String apiKey,
                                        @Field("customerName") String customer_name,
                                        @Field("user_id") String user_id,
                                        @Field("amount") String amount,
                                        @Field("description") String description
                                        // @Field("plan_id") String plan_id

    );

}
