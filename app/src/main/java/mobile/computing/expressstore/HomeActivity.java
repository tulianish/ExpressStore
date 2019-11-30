package mobile.computing.expressstore;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {


    private ExpandableListView expandableListView;
    private ExpandableListAdapter expandableListAdapter;

    private FloatingActionButton shop;

    private TextView noOrders;

    private List<String> dates;
    private List<Items> itemsList;
    private LinkedHashMap<String, List<Items>> orderDetails;

    private static String url="https://expressstorecsci.000webhostapp.com/api.php";
 


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        expandableListView = findViewById(R.id.order1);
        shop = findViewById(R.id.startShop);
        noOrders = findViewById(R.id.noOrders);

        //Get cust_id from user_login activity;
        String cust_id="101";


        getData();
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener(){
            @Override
            public void onGroupExpand(int groupPosition) {

            }
        });

        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int i) {

            }
        });

        shop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               startActivity(new Intent(HomeActivity.this,ScannerScreen.class));
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.action_menu, menu);
        MenuItem item = menu.findItem(R.id.btn_home);
        item.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id){

            case R.id.btn_cart:
                startActivity(new Intent(HomeActivity.this,CartActivity.class));
                break;
            case R.id.btn_settings:
                startActivity(new Intent(HomeActivity.this,SettingsActivity.class));
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    
    


    /*
    fetches customer order details from api, stores it in a Linked Hash Map and sets the adapter
    of expandableListView for display
     */
    private void getData(){
        StringRequest stringRequest=new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {

            String oldDate="";
            @Override
            public void onResponse(String response) {
                try {
                    orderDetails=new LinkedHashMap<>();
                    JSONArray orders=new JSONArray(response);
                    for(int i=0;i<orders.length();i++){
                        JSONObject order=orders.getJSONObject(i);
                        String date=order.getString("date");
                        if(date.equals(oldDate))
                        {
                            String prod_name=order.getString("name");
                            String imgUrl=order.getString("imgUrl");
                            int price=order.getInt("price");
                            String priceS="$"+price;
                            System.out.println("if  or "+ imgUrl+"  "+prod_name);
                            Items item=new Items(prod_name,priceS,imgUrl);
                            itemsList.add(item);
                        }
                        else
                        {
                            itemsList=new ArrayList<>();
                            String prod_name=order.getString("name");
                            String imgUrl=order.getString("imgUrl");
                            int price=order.getInt("price");
                            String priceS="$"+price;
                            System.out.println("else  or "+ imgUrl+"  "+prod_name);
                            Items item=new Items(prod_name,priceS,imgUrl);
                            itemsList.add(item);
                        }
                        System.out.println("date"+date);
                        System.out.println("oldDate"+oldDate);
                        for (Items a:itemsList) {
                            System.out.println(a.getName()+"  "+a.getPrice());

                        }
                        orderDetails.put(date,itemsList);
                        oldDate=date;

                    }
                    System.out.println();
                    System.out.println(orderDetails);

                    /*for (Map.Entry mapElement : orderDetails.entrySet()) {
                        String key = (String)mapElement.getKey();

                        List<Items> val=(List<Items>)mapElement.getValue();

                        System.out.println(key);
                        for (Items a:itemsList) {
                            System.out.println(a.getName()+"  "+a.getPrice());

                        }

                    }*/

                    if(orderDetails.isEmpty())
                    {
                        noOrders.setVisibility(View.VISIBLE);
                    }

                    dates = new ArrayList<>(orderDetails.keySet());
                    expandableListAdapter = new ExpandableListAdapter(HomeActivity.this, dates, orderDetails);
                    expandableListView.setAdapter(expandableListAdapter);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(HomeActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String,String> getParams()
            {
                Map<String,String> params = new HashMap<>();
                params.put("custId","101");
                return params;
            }
        };


        RequestQueueSingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);




    }


}
