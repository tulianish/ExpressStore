package mobile.computing.expressstore;

import android.app.ProgressDialog;
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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
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
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;


public class PaymentDetails extends AppCompatActivity {

    Handler handler;
    String cid, cname, cemail, cnumber;
    String date;
    String targetPdf;

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

        date = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.getDefault()).format(new Date());

        final SharedPreferences prefs = getApplicationContext().getSharedPreferences("mobile.computing.expressstore", Context.MODE_PRIVATE);
        SharedPreferences prefs2 = getApplicationContext().getSharedPreferences("userdata", Context.MODE_PRIVATE);
        cid = prefs2.getString("customerID","0");
        cname = prefs2.getString("name","Customer Name");
        cemail = prefs2.getString("email","Customer E-mail");
        cnumber = prefs2.getString("number","Customer Contact");
        String userID = "Customer Name: "+cid;
        String userName = "Customer Name: "+ cname;
        String userEmail = "Customer Email: "+ cemail;
        String userContact = "Customer Contact: "+ cnumber;

        Gson gson = new Gson();
        String json_totamt = prefs.getString(cid+"_total_amount", "0");
        String json_proname = prefs.getString(cid+"_product_names", "NA");
        String json_prosaleprice = prefs.getString(cid+"_product_sale_price", "0.0");
        String json_proqty = prefs.getString(cid+"_product_qty", "0");

        ArrayList<String> tempnames = new ArrayList<>();
        ArrayList<String> tempsaleprice = new ArrayList<>();
        ArrayList<String> tempqty = new ArrayList<>();
        ArrayList<String> names = gson.fromJson(json_proname, tempnames.getClass());
        ArrayList<String> sale_price = gson.fromJson(json_prosaleprice, tempsaleprice.getClass());
        ArrayList<String> qty = gson.fromJson(json_proqty, tempqty.getClass());


        PdfDocument document = new PdfDocument();
        // crate a page description
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(300, 600, 1).create();
        // start a page
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();
        Paint paint2 = new Paint();

        paint.setTextSize(5.5f);
        paint2.setTextSize(5.5f);

        paint2.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        Bitmap b=BitmapFactory.decodeResource(getResources(), R.drawable.logo1);
        paint.setColor(Color.RED);
        canvas.drawBitmap(b, null, new RectF(8, 5, 80, 60), null);
        paint.setColor(Color.BLACK);

        canvas.drawText(date, 230, 55, paint);

        canvas.drawText(userID, 20, 80, paint2);
        canvas.drawText(userName, 20, 90, paint2);
        canvas.drawText(userEmail, 20, 100, paint2);
        canvas.drawText(userContact, 20, 110, paint2);

        canvas.drawLine(10,120,290,120,paint);

        canvas.drawText("ORDER", 20, 130, paint);

        int y=140;
        String content="";
        for (int i = 0; i < names.size(); i++) {
            y+=10;
            content=(i+1)+". "+names.get(i)+"    -    [  "+qty.get(i)+"  ]   x   [  "+sale_price.get(i)+"  ]";
            canvas.drawText(content, 20, y , paint);
        }

        canvas.drawLine(10,y+10,290,y+10,paint);
        String tot = "Total: "+String.format("%.2f", Double.parseDouble(json_totamt));
        canvas.drawText(tot,20,y+20,paint2);

        //canvas.drawRect(10,y+60,360,y+60,paint3);

        canvas.drawLine(10,60,290,60,paint); //top
        canvas.drawLine(10,y+30,290,y+30,paint); //bottom
        canvas.drawLine(10,60,10,y+30,paint); //left
        canvas.drawLine(290,60,290,y+30,paint); // right
        //canvas.drawRect(10,60,360,y+60,paint3);

        document.finishPage(page);

        // write the document content
        String directory_path = Environment.getExternalStorageDirectory().getPath() + "/ExpressStore/";
        File file = new File(directory_path);
        if (!file.exists()) {
            file.mkdirs();
        }
        targetPdf = directory_path+userID+", "+date+", Bill.pdf";
        File filePath = new File(targetPdf);
        try {
            document.writeTo(new FileOutputStream(filePath));
            Toast.makeText(this, "Bill saved under ExpressStore folder!", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Log.e("main", "error "+e.toString());
            Toast.makeText(this, "Something's wrong: " + e.toString(),  Toast.LENGTH_LONG).show();
        }
        // close the document
        document.close();
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear().apply();
        CartActivity.total_amt=0.0;


        final String command = "ping -c 1 google.com";
        try {
            if(Runtime.getRuntime().exec(command).waitFor() < 5){
                new sendemail().execute();
            }else{
                Toast.makeText(this, "Could not send the bill via mail! Weak Internet connectivity detected.", Toast.LENGTH_LONG).show();
                Intent intent=new Intent(PaymentDetails.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

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

    public class sendemail extends AsyncTask<String, Integer, Integer> {

        ProgressDialog progressDialog;
        private StringBuilder all_email;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(PaymentDetails.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Sending you the bill via e-mail, please wait....");
            progressDialog.show();
        }

        @Override
        protected Integer doInBackground(String... strings) {

            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.socketFactory.port", "465");
            props.put("mail.smtp.socketFactory.class",
                    "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.port", "25");
            props.put("mail.smtp.starttls.enable",true);

            Session session = Session.getDefaultInstance(props,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication("expressstoreapp@gmail.com", "ExpressStore@123");
                        }
                    });

            try {

                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress("expressstoreapp@gmail.com"));
                message.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse("krutarth.kanan@gmail.com"));
                message.setSubject("Order Information");
                Multipart multipart = new MimeMultipart();
                BodyPart msg = new MimeBodyPart();
                BodyPart messageBodyPart = new MimeBodyPart();

                msg.setContent("Dear " + cname + "," + "\n\n Please find the attachment of your bill for your oder on " + date + ".", "text/html; charset=utf-8");
                String filename = cid+", "+date+", Bill.pdf";
                DataSource source = new FileDataSource(targetPdf);
                messageBodyPart.setDataHandler(new DataHandler(source));
                messageBodyPart.setFileName(filename);

                multipart.addBodyPart(msg);
                multipart.addBodyPart(messageBodyPart);
                message.setContent(multipart);

                Transport.send(message);

            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
            return 1;
        }

        @Override
        protected void onPostExecute(Integer val) {
            super.onPostExecute(val);
            progressDialog.dismiss();
            Intent intent=new Intent(PaymentDetails.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }

}
