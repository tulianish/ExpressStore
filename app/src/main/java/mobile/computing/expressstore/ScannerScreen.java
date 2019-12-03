package mobile.computing.expressstore;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.Result;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScannerScreen extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    /**
     *
     * Class : ScannerScreen
     * Description : Barcode Scanning Activity is supported using this class. It fetches the detail
     * of a product corresponding to its barcode.
     * This class sets the ProductId, ProductName, Product Price, Image URL to the Cart Activity.
     *
     * Attributes :
     *
     * scannerView (ZXingScannerView) : Scanner View as part of ZZXing Library.
     *
     * product_api_caller (String) : It contains the URL string for calling Product API.
     *
     * scannedCode (String) : It stores the scanned barcode as obtained from the camera scan screen.
     *
     * scannedProductList (ArrayList) : It stores the array list of the items scanned.
     *
     */

    ZXingScannerView scannerView;
    String product_api_caller = "https://expressstorecsci.000webhostapp.com/getprod.php?prodId=";
    String scannedCode="";
    public Context cc;
    public static ArrayList<Items_Model> scannedProductList = new ArrayList<>();

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);
        ActionBar actionBar = getSupportActionBar();
    }

    @Override
    public void handleResult(Result result) {

        if (result != null) {
            scannedCode = result.getText();
            scannedCode = scannedCode.substring(0,scannedCode.length()-1);
            System.out.println(scannedCode);

            fetchProductDetails(scannedCode, ScannerScreen.this);

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Do something after 5s = 5000ms
                    //Toast.makeText(ScannerScreen.this, scannedProductList.get(1).getName() + " list ", Toast.LENGTH_SHORT).show();

                    finish();
                    startActivity(getIntent());
                }
            }, 3000);

        }else{
            Toasty.error(this, "Item not found! Please contact the representative.", Toast.LENGTH_SHORT, true).show();
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }

    public void fetchProductDetails(final String scannedCode, Context con)
    {
        /**
         *
         * Function : ScanBarcodeScreen
         *
         * Description : Barcode Scanning Activity is supported using this class. It fetches the detail
         * of a product corresponding to its barcode.
         * This class sets the ProductId, ProductName, Product Price, Image URL to the Cart Activity.
         *
         * @addParams scannedCode String The scanned barcode string received from the ScannerView screen
         *
         * @addResult None
         *
         */

        cc = con;

        StringRequest stringRequest=new StringRequest(product_api_caller+scannedCode, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    if(response.equals("[]")){
                        Toasty.error(cc,"The item does not exists! Please contact a representative.", Toast.LENGTH_SHORT, true).show();
                    }else {

                        JSONArray product = new JSONArray(response);
                        JSONObject each_product = product.getJSONObject(0);
                        String store_id = each_product.getString("store_id");
                        String productName = each_product.getString("name");
                        String price = each_product.getString("price");
                        String sale_price = each_product.getString("sale_price");
                        String isOnSale = each_product.getString("on_sale");
                        String imageURL = each_product.getString("imgUrl");

                        System.out.println(isOnSale);
                        System.out.println(scannedCode);

                        SharedPreferences prefs = cc.getSharedPreferences("mobile.computing.expressstore", Context.MODE_PRIVATE);
                        if (store_id.equals(prefs.getString("store_id", "1"))) {
                            if (isOnSale.equals("1")) {
                                scannedProductList.add(new Items_Model(scannedCode, imageURL, productName, Double.parseDouble(price), Double.parseDouble(sale_price), 1));
                            } else if (isOnSale.equals("0")) {
                                scannedProductList.add(new Items_Model(scannedCode, imageURL, productName, Double.parseDouble(price), Double.parseDouble(price), 1));
                            }

                            //Toasty.info(cc, productName + " successfully added to the cart.", Toast.LENGTH_SHORT, true).show();
                            Toasty.info(cc, "Product added to the cart.", Toast.LENGTH_SHORT, true).show();

                        } else {
                            Toasty.error(cc, "This item does not belong to " + prefs.getString("store_name", "the store") + ". Please contact a representative!", Toast.LENGTH_SHORT, true).show();
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toasty.error(cc,"The item does not exists! Please contact a representative.", Toast.LENGTH_SHORT, true).show();
            }
        });

        Volley.newRequestQueue(cc).add(stringRequest);

    }

    // create an action bar button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.action_menu, menu);
        MenuItem item = menu.findItem(R.id.btn_settings);
        item.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id){
            case R.id.btn_home:
                startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                break;
            case R.id.btn_settings:
                startActivity(new Intent(getApplicationContext(),SettingsActivity.class));
                break;
            case R.id.btn_cart:
                startActivity(new Intent(getApplicationContext(),CartActivity.class));
                break;

        }
        return super.onOptionsItemSelected(item);
    }
}
