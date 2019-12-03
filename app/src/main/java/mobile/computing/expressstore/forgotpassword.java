package mobile.computing.expressstore;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.scottyab.aescrypt.AESCrypt;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.io.IOException;
import java.util.Properties;

import es.dmoral.toasty.Toasty;

public class forgotpassword extends AppCompatActivity {

    private Button SendEmail;
    private EditText email;
    String messageAfterDecrypt="";
    String semail = "";
    private static final String KEY = "1Hbfh667adfDEJ78";

    final String url = "https://expressstorecsci.000webhostapp.com/forgotpassword.php?user_email=";
    String furl;
    String pass="-1";

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);

        SendEmail = findViewById(R.id.Btn_sendemail);
        email = findViewById(R.id.Email);

        SendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                semail = email.getText().toString();
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

                furl = url.concat(email.getText().toString().trim());

                if (semail.equals("")) {
                    email.setError("Email Cannot be empty");
                    email.requestFocus();
                } else if (!semail.matches(emailPattern)) {
                    email.setError("Invalid Email Address");
                    email.requestFocus();
                }
                else {

                    StringRequest stringRequest = new StringRequest(furl, new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            try {

                                if (response.equals("-1")) {
                                    Toasty.error(forgotpassword.this, "Email not exist", Toast.LENGTH_SHORT, true).show();
                                } else {

                                    try {
                                        //Decryption
                                        messageAfterDecrypt = AESCrypt.decrypt(KEY, response);
                                        pass = messageAfterDecrypt;

                                        new sendpassemail().execute();

                                    }
                                    catch (Exception e)
                                    {
                                        Toasty.error(forgotpassword.this, ""+e.getMessage(), Toast.LENGTH_SHORT, true).show();
                                        e.printStackTrace();
                                    }

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toasty.error(forgotpassword.this, "error:" + error.getMessage(), Toast.LENGTH_SHORT, true).show();
                        }
                    });
                    Volley.newRequestQueue(forgotpassword.this).add(stringRequest);
                }
            }
        });

    }
    public class sendpassemail extends AsyncTask<String, Integer, Integer> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(forgotpassword.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Sending the password on the registered email address. Please wait....");
            progressDialog.show();
        }

        @Override
        protected Integer doInBackground(String... strings) {

            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.socketFactory.port", "465");
            props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
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
                        InternetAddress.parse(semail));
                message.setSubject("Password Recovery");
                message.setText("Password : "+messageAfterDecrypt);

                String command = "ping -c i google.com";
                int time = Runtime.getRuntime().exec(command).waitFor();
                if(time < 5){
                    Transport.send(message);
                }
                else
                {
                    Toasty.error(forgotpassword.this, "Weak Internet connectivity! Please try again.", Toast.LENGTH_LONG, true).show();
                }

            } catch (MessagingException | IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
            return 1;
        }

        @Override
        protected void onPostExecute(Integer val) {
            super.onPostExecute(val);
            progressDialog.dismiss();
            Toasty.info(forgotpassword.this, "Password sent to this email", Toast.LENGTH_LONG, true).show();
            startActivity(new Intent(forgotpassword.this,LoginActivity.class));
        }
    }
}

