package mobile.computing.expressstore;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.PasswordAuthentication;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import okhttp3.Authenticator;

import static android.provider.Telephony.Carriers.PASSWORD;

public class PaymentDetails extends AppCompatActivity {

    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_details);

        // hide actionbar to display success in full screen
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        // Toast to let user know
        Toast.makeText(this,"Back to home-screen in 2 second", Toast.LENGTH_SHORT).show();

        updateData();

        // Make delay of 2500ms before going to other activity
        handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                generatePDF();

            }
        },2500);

    }

    private void generatePDF() {

        String date = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.getDefault()).format(new Date());

        final SharedPreferences prefs = getApplicationContext().getSharedPreferences("mobile.computing.expressstore", Context.MODE_PRIVATE);
        SharedPreferences prefs2 = getApplicationContext().getSharedPreferences("userdata", Context.MODE_PRIVATE);
        String userID = prefs2.getString("customerID","0");

        SharedPreferences.Editor editor = prefs.edit();
        editor.clear().apply();
        CartActivity.total_amt=0.0;

        Intent intent=new Intent(PaymentDetails.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void updateData(){

        String final_url="https://expressstorecsci.000webhostapp.com/update_orders.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, final_url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);

                        if(response.contains("Item not found!"))
                        {
                            Toast.makeText(getApplicationContext(), "The item deos not exists!", Toast.LENGTH_SHORT).show();
                        }
                        else if(response.contains("Out of stock!"))
                        {
                            Toast.makeText(getApplicationContext(), "The item is out of stock or in less quantity!", Toast.LENGTH_SHORT).show();
                        }
                        else if(response.length()>0)
                        {
                            Toast.makeText(getApplicationContext(), "Server Error! Please try again later.", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", String.valueOf(error));

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                //Shared Preferences
                SharedPreferences prefs = getApplicationContext().getSharedPreferences("mobile.computing.expressstore", Context.MODE_PRIVATE);
                SharedPreferences prefs2 = getApplicationContext().getSharedPreferences("userdata", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();

                String userID = prefs2.getString("customerID","0");
                String json_proid = prefs.getString(userID+"_product_id", "NA");
                String json_proqty = prefs.getString(userID+"_product_qty", "0");

//                ArrayList<String> pro_id = new ArrayList<String>();
//                ArrayList<Integer> pro_qty = new ArrayList<>();
//                for (int i = 0; i < items.size(); i++) {
//                    pro_id.add(items.get(i).getProid());
//                    pro_qty.add(items.get(i).getQty());
//                }
//
//                Gson gson = new Gson();
//                String jsonproid = gson.toJson(pro_id);
//                String jsonproqty = gson.toJson(pro_qty);

                Map<String, String>  args = new HashMap<String, String>();
                args.put("userID",userID);
                args.put("json_qty",json_proqty);
                args.put("json_proid",json_proid);

                return args;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);

    }

}
