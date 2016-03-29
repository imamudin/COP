package com.imamudin.cop;

/**
 * Created by agung on 19/02/2016.
 */
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

//untuk google cloud messaging
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import Config.CLogin;
import Config.GlobalConfig;

public class Login extends AppCompatActivity {
    Button btn_login;
    EditText et_user_name, et_password;
    String s_user_name, s_password;

    GoogleCloudMessaging gcm;

    // Resgistration Id from GCM
    private SharedPreferences prefs;
    // Your project number and web server url. Please change below.
    private static final String WEB_SERVER_URL = "/cop/register_user.php";

    //untuk handler GCM
    private static final int ACTION_PLAY_SERVICES_DIALOG = 100;
    protected static final int MSG_REGISTER_WITH_GCM = 101;
    protected static final int MSG_REGISTER_WEB_SERVER = 102;
    protected static final int MSG_REGISTER_WEB_SERVER_SUCCESS = 103;
    protected static final int MSG_REGISTER_WEB_SERVER_FAILURE = 104;

    private String gcmRegId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_form);

        btn_login   = (Button)findViewById(R.id.btn_login);
        et_user_name    = (EditText)findViewById(R.id.et_user_name);
        et_password = (EditText)findViewById(R.id.et_password);

        if(getSharedPreferences().getBoolean(CLogin.IS_LOGIN, false)){
            openMainActivity(getSharedPreferences().getInt(CLogin.USER_ROLE,0));
        }

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //cek apakah hp konek internet
                ConnectivityManager cm =
                        (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null &&
                        activeNetwork.isConnectedOrConnecting();
                if(isConnected){
                    //cek email dan passwod tidak boleh kosong
                    s_user_name     = et_user_name.getText().toString();
                    s_password      = et_password.getText().toString();
                    if(s_user_name.length()>0 && s_password.length()>0){
                        //melakukan login
                        //Toast.makeText(Login.this, "Email : "+s_user_name+", password : "+s_password, Toast.LENGTH_LONG).show();

                        if (isGoogelPlayInstalled()) {
//                    gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
//
//                    // Read saved registration id from shared preferences.
//                    gcmRegId = getSharedPreferences().getString(PREF_GCM_REG_ID, "");
//
//                    if (TextUtils.isEmpty(gcmRegId)) {
//                        handler.sendEmptyMessage(MSG_REGISTER_WITH_GCM);
//                    }else{
//                        regIdView.setText(gcmRegId);
//                        Toast.makeText(getApplicationContext(), "Already registered with GCM", Toast.LENGTH_SHORT).show();
//                    }
                            gcm = GoogleCloudMessaging.getInstance(getApplicationContext());

                            // Read saved registration id from shared preferences.
                            //gcmRegId = getSharedPreferences().getString(GlobalConfig.PREF_GCM_REG_ID, "");
                            //Toast.makeText(Login.this,""+gcmRegId, Toast.LENGTH_SHORT).show();

                            if (TextUtils.isEmpty(gcmRegId)) {
                                handler.sendEmptyMessage(MSG_REGISTER_WITH_GCM);
                            } else {
                                handler.sendEmptyMessage(MSG_REGISTER_WEB_SERVER);
                            }
                        }
                    }else{
                        Toast.makeText(Login.this, "Email atau password tidak boleh kosong!", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(Login.this, "Aplikasi membutuhkan koneksi internet!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    private boolean isGoogelPlayInstalled() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        ACTION_PLAY_SERVICES_DIALOG).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "Google Play Service is not installed",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;
        }
        return true;

    }
    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_REGISTER_WITH_GCM:
                    new GCMRegistrationTask().execute();
                    break;
                case MSG_REGISTER_WEB_SERVER:
                    new WebServerRegistrationTask().execute();
                    break;
                case MSG_REGISTER_WEB_SERVER_SUCCESS:
                    Toast.makeText(getApplicationContext(),
                            "registered with web server", Toast.LENGTH_LONG).show();
                    break;
                case MSG_REGISTER_WEB_SERVER_FAILURE:
                    Toast.makeText(getApplicationContext(),
                            "registration with web server failed",
                            Toast.LENGTH_LONG).show();
                    break;
            }
        }

        ;
    };
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (item.getItemId()) {
            case R.id.action_ip:
                showdialog_settingip();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void showdialog_settingip() {           //1 untuk pasang, 2 untuk cabut
        LayoutInflater layoutInflater = (LayoutInflater) getLayoutInflater();
        View promptView = layoutInflater.inflate(R.layout.dialog_ip, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Login.this);
        alertDialogBuilder.setView(promptView);

        final TextView t_title = (TextView) promptView.findViewById(R.id.t_title_dialog);
        final EditText et_ip = (EditText) promptView.findViewById(R.id.et_ip);

        t_title.setText("Masukan Nomor IP");
        //get session on sharepreferences
        String uri = getSharedPreferences().getString(GlobalConfig.IP_KEY, "");
        et_ip.setText(uri);

        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("Simpan",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (et_ip.getText().toString() != "") {
                                    //save ip to shared preferences
                                    SharedPreferences.Editor edit = getSharedPreferences().edit();
                                    edit.putString(GlobalConfig.IP_KEY, "" + et_ip.getText());
                                    edit.commit();

                                    Toast.makeText(Login.this, "IP set : " + et_ip.getText(), Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                .setNegativeButton("Batal",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        ;
        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }
    private class GCMRegistrationTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            // TODO Auto-generated method stub
            if (gcm == null && isGoogelPlayInstalled()) {
                gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
            }
            try {
                gcmRegId = gcm.register(GlobalConfig.GCM_SENDER_ID);
                Log.d("gcmregId",""+gcmRegId);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return gcmRegId;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                //Toast.makeText(getApplicationContext(), "registered with GCM",Toast.LENGTH_LONG).show();
                saveInSharedPref(result);
                handler.sendEmptyMessage(MSG_REGISTER_WEB_SERVER);
            }
        }
    }
    private SharedPreferences getSharedPreferences() {
        if (prefs == null) {
            prefs = getApplicationContext().getSharedPreferences(
                    GlobalConfig.KEY_PREFERENCES, Context.MODE_PRIVATE);
        }
        return prefs;
    }
    public void saveInSharedPref(String result) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        //editor.putString(GlobalConfig.PREF_GCM_REG_ID, result);
        editor.commit();
    }
    private class WebServerRegistrationTask extends AsyncTask<Void, Void, Void> {
        private String Content;
        private ProgressDialog Dialog = new ProgressDialog(Login.this);

        protected void onPreExecute() {
            Dialog.setMessage("Mohon tunggu...");
            Dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            BufferedReader reader = null;
            URL url = null;
            try {
                String uri = getSharedPreferences().getString(GlobalConfig.IP_KEY, "");
                if(uri.length()==0){
                    uri = GlobalConfig.IP;
                }
                uri = "http://" + uri + "" + WEB_SERVER_URL;
                Log.d("url",""+uri);
                url = new URL(uri);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                handler.sendEmptyMessage(MSG_REGISTER_WEB_SERVER_FAILURE);
            }
            Map<String, String> dataMap = new HashMap<String, String>();
            dataMap.put(CLogin.USER_NAME, s_user_name);
            dataMap.put(CLogin.USER_PASWORD, s_password);
            dataMap.put(CLogin.USER_REGID, gcmRegId);

            StringBuilder postBody = new StringBuilder();
            Iterator iterator = dataMap.entrySet().iterator();

            while (iterator.hasNext()) {
                Map.Entry param = (Map.Entry) iterator.next();
                postBody.append(param.getKey()).append('=')
                        .append(param.getValue());
                if (iterator.hasNext()) {
                    postBody.append('&');
                }
            }
            String body = postBody.toString();
            byte[] bytes = body.getBytes();

            HttpURLConnection conn = null;
            try {
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setUseCaches(false);
                conn.setFixedLengthStreamingMode(bytes.length);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded;charset=UTF-8");

                OutputStream out = conn.getOutputStream();
                out.write(bytes);
                out.close();

                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                StringBuilder sb = new StringBuilder();
                String line = null;

                // Read Server Response
                while ((line = reader.readLine()) != null) {
                    // Append server response in string
                    sb.append(line + "");
                }

                // Append Server Response To Content String
                Content = sb.toString();

                int status = conn.getResponseCode();
                if (status == 200) {
                    // Request success
                    //handler.sendEmptyMessage(MSG_REGISTER_WEB_SERVER_SUCCESS);
                } else {
                    throw new IOException("Request failed with error code "
                            + status);
                }
            } catch (ProtocolException pe) {
                pe.printStackTrace();
                handler.sendEmptyMessage(MSG_REGISTER_WEB_SERVER_FAILURE);
            } catch (IOException io) {
                io.printStackTrace();
                handler.sendEmptyMessage(MSG_REGISTER_WEB_SERVER_FAILURE);
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }

            return null;
        }

        protected void onPostExecute(Void unused) {
            Log.d("Content",""+Content);
            //Toast.makeText(Login.this,""+Content,Toast.LENGTH_LONG).show();
            //output.setText(Content);
            JSONObject jsonResponse;
            Dialog.dismiss();

            try {
                /****** Creates a new JSONObject with name/value mappings from the JSON string. ********/
                jsonResponse = new JSONObject(Content);

                int status = jsonResponse.optInt(CLogin.STATUS);
                //Log.d("status",""+status);

                if (status == 1) {
                    JSONObject data = jsonResponse.getJSONObject(CLogin.DATA);

                    //menyimpan data ke sharepreferences
                    SharedPreferences.Editor edit = getSharedPreferences().edit();
                    edit.putBoolean(CLogin.IS_LOGIN, true);
                    edit.putString(CLogin.USER_ID, data.optString(CLogin.USER_ID, ""));
                    edit.putString(CLogin.USER_NAME, data.optString(CLogin.USER_NAME,""));
                    edit.putString(CLogin.USER_REGID, data.optString(CLogin.USER_REGID,""));
                    edit.putInt(CLogin.USER_ROLE, data.optInt(CLogin.USER_ROLE, 0));

                    edit.commit();

                    openMainActivity(data.optInt(CLogin.USER_ROLE, 0));
                    //Toast.makeText(Login.this, jsonResponse.optString(CLogin.MESSAGE), Toast.LENGTH_SHORT);
                } else {
                    Toast.makeText(Login.this, jsonResponse.optString(CLogin.MESSAGE), Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                Log.d("json_erros", ""+e.getMessage().toString());
                //output.setText("" + e.getMessage());
            }
        }
    }
    private void openMainActivity(int role){
        Intent Input = new Intent(Login.this, MainActivity.class);
        Intent Monitor = new Intent(Login.this, MainActivityMonitor.class);

        if(role==GlobalConfig.ROLE_MONITOR){
            startActivity(Monitor);
        }else{
            startActivity(Input);
        }
        Login.this.finish();
    }
}