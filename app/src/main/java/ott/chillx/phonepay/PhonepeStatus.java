package ott.chillx.phonepay;

import static net.one97.paytm.nativesdk.BasePaytmSDK.getApplicationContext;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.ResponseBody;
import ott.chillx.AppConfig;
import ott.chillx.MainActivity;
import ott.chillx.PhonepayKotline;
import ott.chillx.database.DatabaseHelper;
import ott.chillx.network.RetrofitClient;
import ott.chillx.network.apis.PaymentApi;
import ott.chillx.network.apis.SubscriptionApi;
import ott.chillx.network.model.ActiveStatus;
import ott.chillx.network.model.Package;
import ott.chillx.network.model.SubscriptionHistory;
import ott.chillx.utils.PreferenceUtils;
import ott.chillx.utils.ToastMsg;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class PhonepeStatus {

    static String user_id = "";
    static String plantamount = "";


    public static void checkStatus(PhonepayKotline phonepayKotline, String MERCHANT_TID, String xVerify, String uid, Package aPackage, DatabaseHelper databaseHelper) {
        // progressBar.setVisibility(View.VISIBLE);

        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        PaymentApi paymentApi = retrofit.create(PaymentApi.class);
        Call<ResponseBody> call = paymentApi.check_phonepe_Status(AppConfig.API_KEY, xVerify, MERCHANT_TID
        );

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {

                if (response.code() == 200) {
                    try {
                        JSONObject jsonObject = new JSONObject(Objects.requireNonNull(response.body()).string());
                        String pay_status = jsonObject.getString("success");
                        String status_code = jsonObject.getString("code");
                        String message = jsonObject.getString("message");

                        if (status_code.equals("PAYMENT_SUCCESS")) {

                            JSONObject getdata = jsonObject.getJSONObject("data");

                            String transactionId = String.valueOf(getdata.get("merchantTransactionId"));

//                            TrackierSDK.getTrackierId();
//                            String trkid = TrackierSDK.getTrackierId();
//                            String trkid = TrackierSDK.getTrackierId();
//
//                            mmp_paymentsucces_event(trkid, phonepayKotline, uid, aPackage, databaseHelper, "success");

                            saveChargeData(transactionId, "phonepegateway", phonepayKotline, uid, aPackage, databaseHelper);

                            // Toast.makeText(phonepayKotline.getApplicationContext(), "Payment Success", Toast.LENGTH_SHORT).show();
                            Toast.makeText(phonepayKotline.getApplicationContext(), transactionId, Toast.LENGTH_SHORT).show();

                        } else if (status_code.equals("PAYMENT_PENDING")) {

//                            TrackierSDK.getTrackierId();
//                            String trkid = TrackierSDK.getTrackierId();
//                            mmp_pendingpayment_event(trkid, phonepayKotline, uid, aPackage, databaseHelper, "pending");

                            Toast.makeText(phonepayKotline.getApplicationContext(), status_code, Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(phonepayKotline.getApplicationContext(), MainActivity.class);
                            phonepayKotline.startActivity(intent);
                            phonepayKotline.finish();

                        } else {


//                            TrackierSDK.getTrackierId();
//                            String trkid = TrackierSDK.getTrackierId();
//                            mmp_failedpayment_event(trkid, phonepayKotline, uid, aPackage, databaseHelper, "failed");

                            Toast.makeText(phonepayKotline.getApplicationContext(), "Payment Failed", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(phonepayKotline.getApplicationContext(), MainActivity.class);
                            phonepayKotline.startActivity(intent);
                            phonepayKotline.finish();

                        }

                        Toast.makeText(phonepayKotline.getApplicationContext(), message, Toast.LENGTH_SHORT).show();


                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    // new ToastMsg(phonepayKotline).toastIconError(getString(R.string.something_went_wrong));
                    // finish();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                // new ToastMsg(phonepayKotline).toastIconError(getString(R.string.something_went_wrong));
                //   finish();
                //progressBar.setVisibility(View.GONE);
            }
        });

    }



    public static void mmp_paymentsucces_event(String installtrkid, PhonepayKotline phonepayKotline, String uid, Package aPackage, DatabaseHelper databaseHelper, String pay_status) {
        // progressBar.setVisibility(View.VISIBLE);
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        PaymentApi paymentApi = retrofit.create(PaymentApi.class);
        Call<ResponseBody> call = paymentApi.save_eventmmp(AppConfig.API_KEY, "363f4a80-efde-47c9-aef4-9d5a32d30923", installtrkid,
                "zsWezLxRnw", "192.168.1.77", uid, "mail", databaseHelper.getUserData().getName(), databaseHelper.getUserData().getPhone(), Integer.parseInt(aPackage.getPrice()),
                "", "INR", "paymentsuccess", "", "", "", "", "", "", "", "", "", pay_status);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.code() == 200) {

                    Toast.makeText(phonepayKotline.getApplicationContext(), "Payment Success", Toast.LENGTH_SHORT).show();

                } else {
                    new ToastMsg(phonepayKotline.getApplicationContext()).toastIconError("\"Something went wrong.\"");

                    //  finish();
                    //  progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                new ToastMsg(phonepayKotline.getApplicationContext()).toastIconError("Something went wrong." + t.getMessage());
                // finish();
                // progressBar.setVisibility(View.GONE);
            }
        });

    }



    public static void mmp_failedpayment_event(String installtrkid, PhonepayKotline phonepayKotline, String uid, Package aPackage, DatabaseHelper databaseHelper, String pay_status) {
        // progressBar.setVisibility(View.VISIBLE);
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        PaymentApi paymentApi = retrofit.create(PaymentApi.class);
       // Call<ResponseBody> call = paymentApi.save_eventmmp(AppConfig.API_KEY, "0f9d8afa-7935-4296-b180-be7758bd03d7", installtrkid,
        Call<ResponseBody> call = paymentApi.save_eventmmp(AppConfig.API_KEY, "363f4a80-efde-47c9-aef4-9d5a32d30923", installtrkid,
                "fK916QNVRH", "192.168.1.77", uid, "mail", databaseHelper.getUserData().getName(), databaseHelper.getUserData().getPhone(), Integer.parseInt(aPackage.getPrice()),
                "", "INR", "paymentfailed", "", "", "", "", "", "", "", "", "", pay_status);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.code() == 200) {

                   // Toast.makeText(phonepayKotline.getApplicationContext(), "Payment Success", Toast.LENGTH_SHORT).show();


                } else {
                    new ToastMsg(phonepayKotline.getApplicationContext()).toastIconError("\"Something went wrong.\"");

                    //  finish();
                    //  progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                new ToastMsg(phonepayKotline.getApplicationContext()).toastIconError("Something went wrong." + t.getMessage());
                // finish();
                // progressBar.setVisibility(View.GONE);
            }
        });

    }


    public static void mmp_pendingpayment_event(String installtrkid, PhonepayKotline phonepayKotline, String uid, Package aPackage, DatabaseHelper databaseHelper, String pay_status) {
        // progressBar.setVisibility(View.VISIBLE);
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        PaymentApi paymentApi = retrofit.create(PaymentApi.class);
        Call<ResponseBody> call = paymentApi.save_eventmmp(AppConfig.API_KEY, "363f4a80-efde-47c9-aef4-9d5a32d30923", installtrkid,
                "3YXhGAmIT5", "192.168.1.77", uid, "mail", databaseHelper.getUserData().getName(), databaseHelper.getUserData().getPhone(), Integer.parseInt(aPackage.getPrice()),
                "", "INR", "paymentpending", "", "", "", "", "", "", "", "", "", pay_status);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.code() == 200) {

                    // Toast.makeText(phonepayKotline.getApplicationContext(), "Payment Success", Toast.LENGTH_SHORT).show();


                } else {
                    new ToastMsg(phonepayKotline.getApplicationContext()).toastIconError("\"Something went wrong.\"");

                    //  finish();
                    //  progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                new ToastMsg(phonepayKotline.getApplicationContext()).toastIconError("Something went wrong." + t.getMessage());
                // finish();
                // progressBar.setVisibility(View.GONE);
            }
        });

    }


    public static void saveChargeData(String token, String from, PhonepayKotline phonepayKotline, String uid, Package aPackage, DatabaseHelper databaseHelper) {
        // progressBar.setVisibility(View.VISIBLE);
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        PaymentApi paymentApi = retrofit.create(PaymentApi.class);
        Call<ResponseBody> call = paymentApi.savePayment(AppConfig.API_KEY, aPackage.getPlanId(),
                databaseHelper.getUserData().getUserId(),
                aPackage.getPrice(),
                // "1",
                token, "20", from);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.code() == 200) {

                    plantamount = aPackage.getPrice();
                    updateActiveStatus(phonepayKotline);
                    getSubscriptionHistory(plantamount, uid);

                    /*firebase purchase event*/
                    FirebaseAnalytics mFirebaseAnalytics;
                    mFirebaseAnalytics = FirebaseAnalytics.getInstance(phonepayKotline.getApplicationContext());
                    Bundle bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Param.ITEM_ID, aPackage.getPlanId());
                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, aPackage.getName());
                    bundle.putString(FirebaseAnalytics.Param.CURRENCY, "INR");
                    bundle.putString(FirebaseAnalytics.Param.VALUE, aPackage.getPrice());
                    mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.PURCHASE, bundle);

                } else {
                    new ToastMsg(phonepayKotline.getApplicationContext()).toastIconError("\"Something went wrong.\"");

                    //  finish();
                    //  progressBar.setVisibility(View.GONE);
                }
            }


            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                new ToastMsg(phonepayKotline.getApplicationContext()).toastIconError("Something went wrong." + t.getMessage());
                // finish();
                // progressBar.setVisibility(View.GONE);
            }
        });

    }


    private static void getSubscriptionHistory(String plantamount, String uid) {
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        SubscriptionApi subscriptionApi = retrofit.create(SubscriptionApi.class);
        Call<SubscriptionHistory> call = subscriptionApi.getSubscriptionHistory(AppConfig.API_KEY, uid);
        call.enqueue(new Callback<SubscriptionHistory>() {
            @Override
            public void onResponse(Call<SubscriptionHistory> call, retrofit2.Response<SubscriptionHistory> response) {
                SubscriptionHistory subscriptionHistory = response.body();
                if (response.code() == 200) {

                    try {

                        if (subscriptionHistory.getActiveSubscription().size() > 0) {

                          /*  HashMap<String, Object> paymentAction = new HashMap<String, Object>();
                            paymentAction.put("payment mode", "phonepe");
                            paymentAction.put("amount", plantamount);
                            paymentAction.put("subscription plan", subscriptionHistory.getActiveSubscription().get(0).getPlanTitle());
                            paymentAction.put("Payment ID", subscriptionHistory.getActiveSubscription().get(0).getPaymentInfo());
                            paymentAction.put("Subscription ID", subscriptionHistory.getActiveSubscription().get(0).getSubscriptionId());
                            paymentAction.put("subscription plan id", subscriptionHistory.getActiveSubscription().get(0).getPlanId());
                            paymentAction.put("subscription Start date", subscriptionHistory.getActiveSubscription().get(0).getStartDate());
                            paymentAction.put("subscription End date", subscriptionHistory.getActiveSubscription().get(0).getExpireDate());
                            clevertapChergedInstance.pushEvent("Charged", paymentAction);*/

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<SubscriptionHistory> call, Throwable t) {
                // progressBar.setVisibility(View.GONE);
                t.printStackTrace();
            }
        });

    }



    private static void updateActiveStatus(PhonepayKotline phonepayKotline) {
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        SubscriptionApi subscriptionApi = retrofit.create(SubscriptionApi.class);

        Call<ActiveStatus> call = subscriptionApi.getActiveStatus(AppConfig.API_KEY, PreferenceUtils.getUserId(phonepayKotline.getApplicationContext()));
        call.enqueue(new Callback<ActiveStatus>() {
            @Override
            public void onResponse(Call<ActiveStatus> call, retrofit2.Response<ActiveStatus> response) {
                if (response.code() == 200) {
                    ActiveStatus activeStatus = response.body();
                    DatabaseHelper db = new DatabaseHelper(getApplicationContext());
                    db.deleteAllActiveStatusData();
                    db.insertActiveStatusData(activeStatus);
                    // new ToastMsg(OneUPIPaymentActivity.this).toastIconSuccess(getResources().getString(R.string.payment_success));
                    new ToastMsg(phonepayKotline.getApplicationContext()).toastIconSuccess("Payment Success.");
                    //    progressBar.setVisibility(View.GONE);
                    Intent intent = new Intent(phonepayKotline.getApplicationContext(), MainActivity.class);
                    phonepayKotline.startActivity(intent);
                    phonepayKotline.finish();
                }
            }


            @Override
            public void onFailure(Call<ActiveStatus> call, Throwable t) {
                t.printStackTrace();
                new ToastMsg(phonepayKotline.getApplicationContext()).toastIconError("Something went wrong." + t.getMessage());
                phonepayKotline.finish();
                //progressBar.setVisibility(View.GONE);
            }
        });

    }


}
