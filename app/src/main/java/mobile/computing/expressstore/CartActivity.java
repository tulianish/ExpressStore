package mobile.computing.expressstore;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import mobile.computing.expressstore.Config.Config;

public class CartActivity extends AppCompatActivity {

    RecyclerView items_list;
    TextView tv_total_items;
    public static TextView tv_total_amt;
    ImageButton btn_clear, btn_submit;
    public static List<Items_Model> items;
    public static Custom_adap adap;

    public static Double total_amt=0.0;


    private static final int PAYPAL_REQUEST_CODE = 7171;
    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(Config.PAYPAL_CLIENT_ID);

    @Override
    protected void onResume() {
        super.onResume();
        total_amt=0.0;
        adap.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        total_amt=0.0;
        save_cart_state();
        this.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        //actionBar.hide();
        //actionBar.setTitle(Html.fromHtml("<font color='#000000'>Cart</font>"));

        items_list = findViewById(R.id.item_list);
        tv_total_items = findViewById(R.id.total_items);
        tv_total_amt = findViewById(R.id.total_amt);
        btn_clear = findViewById(R.id.btn_clear);
        btn_submit = findViewById(R.id.btn_checkout);

        LinearLayoutManager layoutManager = new LinearLayoutManager(CartActivity.this);
        //layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        items_list.setLayoutManager(layoutManager);

        final String url="https://helpx.adobe.com/content/dam/help/en/stock/how-to/visual-reverse-image-search/jcr_content/main-pars/image/visual-reverse-image-search-v2_intro.jpg";

        items = new ArrayList<>();

        //Shared Preferences
        final SharedPreferences prefs = getApplicationContext().getSharedPreferences("mobile.computing.expressstore", Context.MODE_PRIVATE);
        SharedPreferences prefs2 = getApplicationContext().getSharedPreferences("userdata", Context.MODE_PRIVATE);
        String userID = prefs2.getString("customerID","0");

        if(prefs.contains(userID+"_total_amount")) {

            Gson gson = new Gson();
            String json_totamt = prefs.getString(userID+"_total_amount", "0");
            String json_proid = prefs.getString(userID+"_product_id", "NA");
            String json_proname = prefs.getString(userID+"_product_names", "NA");
            String json_proprice = prefs.getString(userID+"_product_price", "0.0");
            String json_prosaleprice = prefs.getString(userID+"_product_sale_price", "0.0");
            String json_proqty = prefs.getString(userID+"_product_qty", "0");
            String json_prourl = prefs.getString(userID+"_product_url", "NA");

            //Toast.makeText(this, ""+json_proname, Toast.LENGTH_SHORT).show();
            //total_amt = Double.valueOf(json_totamt);

            ArrayList<String> tempid = new ArrayList<>();
            ArrayList<String> tempnames = new ArrayList<>();
            ArrayList<String> tempprice = new ArrayList<>();
            ArrayList<String> tempsaleprice = new ArrayList<>();
            ArrayList<String> tempqty = new ArrayList<>();
            ArrayList<String> tempurl = new ArrayList<>();
            ArrayList<String> id = gson.fromJson(json_proid, tempid.getClass());
            ArrayList<String> names = gson.fromJson(json_proname, tempnames.getClass());
            ArrayList<String> price = gson.fromJson(json_proprice, tempprice.getClass());
            ArrayList<String> sale_price = gson.fromJson(json_prosaleprice, tempsaleprice.getClass());
            ArrayList<String> qty = gson.fromJson(json_proqty, tempqty.getClass());
            ArrayList<String> turl = gson.fromJson(json_prourl, tempurl.getClass());

            for (int i = 0; i < names.size(); i++) {
                //Toast.makeText(this, ""+String.valueOf(qty.get(i)), Toast.LENGTH_SHORT).show();
                items.add(new Items_Model(id.get(i),turl.get(i), names.get(i), Double.parseDouble(String.valueOf(price.get(i))), Double.parseDouble(String.valueOf(sale_price.get(i))), Integer.parseInt(String.valueOf(qty.get(i)))));
            }
        }

        ScannerScreen ss = new ScannerScreen();
        ArrayList<Items_Model> lst = new ArrayList<>();
        lst = ss.scannedProductList;

        if(lst.size()>0){
            for(int i=0;i<lst.size();i++) {
                String scan_id = lst.get(i).getProid();
                String scan_url = lst.get(i).getImg();
                String scan_name = lst.get(i).getName();
                double scan_price = lst.get(i).getPrice();
                double scan_sale_price = lst.get(i).getSale_price();
                int scan_qty = lst.get(i).getQty();

                int flag=1;

                for(int j=0;j<items.size();j++){
                    if(items.get(j).getName().equals(scan_name)) {
                        flag = 0;
                        items.get(j).setQty(items.get(j).getQty()+1);
                        break;
                    }
                }

                if(flag==1){
                    items.add(new Items_Model(scan_id, scan_url, scan_name, scan_price, scan_sale_price, scan_qty));
                }
            }
        }

        ss.scannedProductList.clear();

        tv_total_items.setText(items.size() + " Items");
        tv_total_amt.setText("$" + String.format("%.2f", total_amt));

        adap = new Custom_adap(CartActivity.this, items);
        //Toast.makeText(this, " "+adap.getItemCount(), Toast.LENGTH_SHORT).show();
        items_list.setAdapter(adap);

        adap.notifyDataSetChanged();

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                //Toast.makeText(CartActivity.this, "on Move", Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                //Toast.makeText(CartActivity.this, "on Swiped ", Toast.LENGTH_SHORT).show();
                //Remove swiped item from list and notify the RecyclerView

                final int position = viewHolder.getAdapterPosition();

                new AlertDialog.Builder(CartActivity.this)
                        //.setTitle("Remove Item")
                        .setMessage("Are you sure you want to remove this item?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                total_amt = 0.0;
                                tv_total_amt.setText("$"+String.format("%.2f", total_amt));
                                items.remove(position);
                                adap.notifyDataSetChanged();
                                save_cart_state();
                                tv_total_items.setText(items.size() + " Item(s)");

                                if(items.size()==0){
                                    tv_total_items.setText("No Item(s)");
                                    tv_total_amt.setText("$0.0");
                                }
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                total_amt = 0.0;
                                tv_total_amt.setText("$"+String.format("%.2f", total_amt));
                                adap.notifyDataSetChanged();
                            }
                        })
                        .setCancelable(false)
                        .show();

            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(items_list);

        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new AlertDialog.Builder(CartActivity.this)
                        //.setTitle("Remove Item")
                        .setMessage("Are you sure you want to clear the cart?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                items.clear();
                                adap.notifyDataSetChanged();
                                tv_total_items.setText("No Item(s)");
                                tv_total_amt.setText("$0.0");
                                prefs.edit().clear().apply();

                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setCancelable(false)
                        .show(); }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//         items.add(new Items_Model(url,"BMW Cycle X2451",750,600, 1));
//         items.add(new Items_Model(url,"Diet Pepsi",2.5,0.97, 1));
//        items.add(new Items_Model(url,"GO Cheese 150g",7,7, 1));
//        items.add(new Items_Model(url,"Nike Running Shoes",70,55, 1));
//        items.add(new Items_Model(url,"Nike Casual Jacket",95,95, 1));

                if(items.size()>0) {

                    save_cart_state();

                /*
                Toast.makeText(CartActivity.this, ""+prefs.getString("total_amount",null), Toast.LENGTH_SHORT).show();
                Toast.makeText(CartActivity.this, ""+prefs.getString("product_names",null), Toast.LENGTH_SHORT).show();
                Toast.makeText(CartActivity.this, ""+prefs.getString("product_qty",null), Toast.LENGTH_SHORT).show();
                Toast.makeText(CartActivity.this, ""+prefs.getString("product_price",null), Toast.LENGTH_SHORT).show();
                   */

                    //Intent
                    new AlertDialog.Builder(CartActivity.this)
                            //.setTitle("Remove Item")
                            .setMessage("Are you sure you want to proceed to pay?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(CartActivity.this, PayPalService.class);
                                    intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
                                    startService(intent);
                                    paymentProcess();

                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setCancelable(false)
                            .show();
                }else{
                    Toasty.error(CartActivity.this, "Please add items to the cart!", Toast.LENGTH_SHORT, true).show();
                }
            }
        });


    }

    public void paymentProcess()
    {
        PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(String.valueOf(total_amt))
                , "CAD", "Purchases", PayPalPayment.PAYMENT_INTENT_SALE);

        Intent intent = new Intent(CartActivity.this, com.paypal.android.sdk.payments.PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        intent.putExtra(com.paypal.android.sdk.payments.PaymentActivity.EXTRA_PAYMENT, payPalPayment);
        startActivityForResult(intent, PAYPAL_REQUEST_CODE);
    }

    // create an action bar button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // R.menu.mymenu is a reference to an xml file named mymenu.xml which should be inside your res/menu directory.
        // If you don't have res/menu, just create a directory named "menu" inside res

        getMenuInflater().inflate(R.menu.action_menu, menu);
        MenuItem item = menu.findItem(R.id.btn_cart);
        item.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        save_cart_state();
        ScannerScreen ss = new ScannerScreen();
        ss.scannedProductList.clear();
        switch(id){
            case R.id.btn_home:
                startActivity(new Intent(CartActivity.this,HomeActivity.class));
                break;
            case R.id.btn_settings:
                startActivity(new Intent(CartActivity.this,SettingsActivity.class));
                break;
            case android.R.id.home:
                startActivity(new Intent(CartActivity.this,ScannerScreen.class));
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    void save_cart_state(){
        //Shared Preferences
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("mobile.computing.expressstore", Context.MODE_PRIVATE);
        SharedPreferences prefs2 = getApplicationContext().getSharedPreferences("userdata", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        String userID = prefs2.getString("customerID","0");

        //Set the values
        Gson gson = new Gson();
        String jsonProductid = "";
        String jsonProductNames = "";
        String jsonProductQty = "";
        String jsonProductPrice = "";
        String jsonProductSalePrice = "";
        String jsonProductURL = "";

        ArrayList<String> items_id = new ArrayList<String>();
        ArrayList<String> items_name = new ArrayList<String>();
        ArrayList<String> items_qty = new ArrayList<>();
        ArrayList<String> items_price = new ArrayList<>();
        ArrayList<String> items_saleprice = new ArrayList<>();
        ArrayList<String> items_URL = new ArrayList<String>();
        for (int i = 0; i < items.size(); i++) {
            items_id.add(items.get(i).getProid());
            items_name.add(items.get(i).getName());
            items_qty.add(String.valueOf(items.get(i).getQty()));
            items_price.add(String.valueOf(items.get(i).getPrice()));
            items_saleprice.add(String.valueOf(items.get(i).getSale_price()));
            items_URL.add(items.get(i).getImg());
        }


        jsonProductid = gson.toJson(items_id);

        jsonProductNames = gson.toJson(items_name);
        jsonProductQty = gson.toJson(items_qty);
        jsonProductPrice = gson.toJson(items_price);
        jsonProductSalePrice = gson.toJson(items_saleprice);
        jsonProductURL = gson.toJson(items_URL);

        editor.clear();
        editor.putString(userID+"_total_amount", total_amt + "");
        editor.putString(userID+"_product_id", jsonProductid);
        editor.putString(userID+"_product_names", jsonProductNames);
        editor.putString(userID+"_product_price", jsonProductPrice);
        editor.putString(userID+"_product_sale_price", jsonProductSalePrice);
        editor.putString(userID+"_product_qty", jsonProductQty);
        editor.putString(userID+"_product_url", jsonProductURL);
        editor.apply();
    }

    // TODO : Need to be removed
    /*private void updateData(){

        String final_url="https://expressstorecsci.000webhostapp.com/update_orders.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, final_url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);

                        Toast.makeText(CartActivity.this, ""+response, Toast.LENGTH_SHORT).show();
                        if(response.contains("Item not found!")){
                            Toast.makeText(CartActivity.this, "The item deos not exists!", Toast.LENGTH_SHORT).show();
                        }else if(response.contains("Out of stock!")){
                            Toast.makeText(CartActivity.this, "The item is out of stock or in less quantity!", Toast.LENGTH_SHORT).show();
                        }else if(response.length()>0){
                            Toast.makeText(CartActivity.this, "Server Error! Please try again later.", Toast.LENGTH_SHORT).show();
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

                ArrayList<String> pro_id = new ArrayList<String>();
                ArrayList<Integer> pro_qty = new ArrayList<>();
                for (int i = 0; i < items.size(); i++) {
                    pro_id.add(items.get(i).getProid());
                    pro_qty.add(items.get(i).getQty());
                }

                Gson gson = new Gson();
                String jsonproid = gson.toJson(pro_id);
                String jsonproqty = gson.toJson(pro_qty);

                Map<String, String>  args = new HashMap<String, String>();
                args.put("userID","102");
                args.put("json_qty",jsonproqty);
                args.put("json_proid",jsonproid);

                return args;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);

    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAYPAL_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                PaymentConfirmation confirmation = data.getParcelableExtra(com.paypal.android.sdk.payments.PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirmation != null) {
//                    try {
//                        String paymentDetails = confirmation.toJSONObject().toString(4);
//                        Toast.makeText(CartActivity.this,"Payment: "+paymentDetails,Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, PaymentDetails.class));
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toasty.warning(this, "Canceled", Toast.LENGTH_SHORT, true).show();
            }
        } else if (resultCode == com.paypal.android.sdk.payments.PaymentActivity.RESULT_EXTRAS_INVALID) {
            Toasty.error(this, "Invalid", Toast.LENGTH_SHORT, true).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
