package ott.chillx


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.phonepe.intent.sdk.api.B2BPGRequestBuilder
import com.phonepe.intent.sdk.api.PhonePe
import com.phonepe.intent.sdk.api.PhonePe.init
import com.phonepe.intent.sdk.api.PhonePeInitException
import com.phonepe.intent.sdk.api.models.PhonePeEnvironment
import org.json.JSONObject
import ott.chillx.database.DatabaseHelper
import ott.chillx.network.model.Package
import ott.chillx.phonepay.PhonepeStatus
import java.nio.charset.Charset
import java.security.MessageDigest
import java.util.*


class PhonepayKotline : AppCompatActivity() {

    private var aPackage: Package? = null
    private var databaseHelper: DatabaseHelper? = null

    var uid = ""
    var email = ""
    var order_id = ""
    var orderIdstr = ""
    var plantamount = ""
    var mobile = ""
    var uname = ""

    var mFirebaseAnalytics: FirebaseAnalytics? = null

    //   var apiEndPoint = "https://api.phonepe.com/apis/hermes/pg/v1/pay"
    var apiEndPoint = "/pg/v1/pay"
    //  var apiEndPoint = "https://api.phonepe.com/apis/hermes"

    val salt = "153701d0-55e2-46a3-a983-6f4d40c84e3e" //hulch7ul production salt key
    val MERCHANT_ID = "M22OW170WC7KP"  //production  Merhcant id

    //for test
    //   //val salt = "099eb0cd-02cf-4e2a-8aca-3e6c6aff0399" //test salt key
    //  val MERCHANT_ID = "PGTESTPAYUAT"  //test Merhcant id

    val MERCHANT_TID = UUID.randomUUID().toString().substring(0, 34)

    val BASE_URL = "https://api-preprod.phonepe.com/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phonepay_kotline)

        //   transactionIdView = (TextView) findViewById(R.id.transactionid);
        //transactionStatusView = (TextView) findViewById(R.id.status);
        val tvprice = findViewById<TextView>(R.id.ppprice)


        init(this, PhonePeEnvironment.RELEASE, "M22OW170WC7KP", null)
        // val string_signature1 = PhonePe.getPackageSignature();

        /* PhonePe.init(
             this,
             PhonePeEnvironment.RELEASE,
             "M1GQPJSNH1HW",
             "75ebc415eb7f4ff9b9573178de893edb"
         )*/
        /*
                PhonePe.init(
                    this,
                    PhonePeEnvironment.STAGE_SIMULATOR,
                    "PGTESTPAYUAT",
                    ""
                )*/


        if (intent != null) {
            aPackage = intent.getSerializableExtra("package") as Package?

            databaseHelper = DatabaseHelper(this)
        }
        plantamount = aPackage!!.price

        tvprice.text = aPackage!!.price


        val user = databaseHelper!!.userData
        uid = user.userId
        uname = user.name
//        mobile = "0000000000";
        //        mobile = "0000000000";
        mobile = user.phone
        email = user.email

        try {
            PhonePe.getUpiApps();
        } catch (exception: PhonePeInitException) {
            exception.printStackTrace();
        }

       val sub_amount = (aPackage!!.price.toLong() * 100).toString()
       // val sub_amount = (1 * 100).toString()

        val data = JSONObject()
        data.put("merchantTransactionId", MERCHANT_TID)//String. Mandatory
        data.put("merchantId", MERCHANT_ID) //String. Mandatory
        data.put("amount", sub_amount)//Long. Mandatory
//        data.put("amount", 100)//Long. Mandatory
//        data.put("mobileNumber", "9373700636") //String. Optional
        data.put("merchantUserId", uid) //add user id dyb=namic

        // Log.d("marchant_trid", "marchant trid : $MERCHANT_TID")

        data.put("callbackUrl", "https://chillx.in") //String. Mandatory
        // data.put("callbackUrl", "https://play.google.com/store/apps/details?id=ott.hulchulapp") //String. Mandatory
        // data.put("callbackUrl", "35.200.221.144") //String. Mandatory
        val paymentInstrument = JSONObject()
        //  paymentInstrument.put("type", "UPI_INTENT")
        paymentInstrument.put("type", "PAY_PAGE")
        //  paymentInstrument.put("targetApp", "com.phonepe.simulator")
        // paymentInstrument.put("targetApp", "com.phonepe.app")

        data.put("paymentInstrument", paymentInstrument)//OBJECT. Mandatory

        //  val deviceContext = JSONObject()
        // deviceContext.put("deviceOS", "ANDROID")
        //data.put("deviceContext", deviceContext)



        val payloadBase64 = android.util.Base64.encodeToString(
                data.toString().toByteArray(Charset.defaultCharset()), android.util.Base64.NO_WRAP
        )

        val checksum = sha256(payloadBase64 + apiEndPoint + salt) + "###1";

        Log.d("payload", "onCreate: $payloadBase64")
        Log.d("payload", "onCreate: $checksum")

        val b2BPGRequest = B2BPGRequestBuilder()
                .setData(payloadBase64)
                .setChecksum(checksum)
                .setUrl(apiEndPoint)
                .build()


        val button = findViewById<Button>(R.id.pppay)
        button.setOnClickListener {
            //For SDK call below function

            Log.d("payloadBase64", "onCreate: $payloadBase64")
            Log.d("checksum", "onCreate: $checksum")

            try {
                // PhonePe.getImplicitIntent(this, b2BPGRequest, "com.phonepe.simulator")
                PhonePe.getImplicitIntent(this, b2BPGRequest, "com.phonepe.app")
                        ?.let { startActivityForResult(it, 1) };
            } catch (e: PhonePeInitException) {
            }

        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {

            //Log.d("data", "onActivityResult: $data")
            //  Log.d("data", "onActivityResult: ${data!!.data}")

            /*
                        if (resultCode == RESULT_OK) {
                            Toast.makeText(this, "Payment Success", Toast.LENGTH_SHORT).show();
                        } else if (resultCode == RESULT_CANCELED) {
                           // Toast.makeText(this, "Payment Cancelled", Toast.LENGTH_SHORT).show();
                        } else {
                           // Toast.makeText(this, "Payment Failed", Toast.LENGTH_SHORT).show();
                        }*/

            // checkStatus()
            val xVerify = sha256("/pg/v1/status/$MERCHANT_ID/${MERCHANT_TID}${salt}") + "###1"

            PhonepeStatus.checkStatus(this, MERCHANT_TID, xVerify, uid, aPackage, databaseHelper)

            /*This callback indicates only about completion of UI flow.
            Inform your server to make the transaction
            status call to get the status. Update your app with the
            success/failure status.*/
        }
    }


    private fun checkStatus() {

        // val xVerify = sha256("/pg/v1/status/$MERCHANT_ID/${MERCHANT_TID}${salt}") + "###1"
        val xVerify = sha256("/pg/v1/status/$MERCHANT_ID/${MERCHANT_TID}${salt}") + "###1"
        //val xVerify = sha256("/status/$MERCHANT_ID/${MERCHANT_TID}${salt}") + "###1"

        Log.d("phonepe", "onCreate  xverify : $xVerify")

        val headers = mapOf(
                "Content-Type" to "application/json",
                "X-VERIFY" to xVerify,
                "X-MERCHANT-ID" to MERCHANT_ID,
        )


        /*  lifecycleScope.launch(Dispatchers.IO) {

              val res = ApiUtilities.getApiInterface().check_phonepe_Status(MERCHANT_ID, MERCHANT_TID, headers)

              withContext(Dispatchers.Main) {

                  Log.d("phonepe", "onCreate: ${res.body()}")

                  if (res.body() != null && res.body()!!.success) {
                      Log.d("phonepe", "onCreate: success")
                      Toast.makeText(this@PhonepayKotline, res.body()!!.message, Toast.LENGTH_SHORT)
                          .show()

                  }
              }
          }*/

    }

    private fun sha256(input: String): String {
        val bytes = input.toByteArray(Charsets.UTF_8)
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }
}