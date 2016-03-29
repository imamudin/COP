package com.imamudin.cop;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import Config.CMain;
import Config.GlobalConfig;

public class MainActivityMonitor extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, FragmentImport.OnFragmentInteractionListener{

    private SharedPreferences prefs;
    TextView t_user_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_monitor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        t_user_name    = (TextView)findViewById(R.id.t_user_name);

        //mengatur profil pada navigation view
        if(getSharedPreferences().getBoolean(CMain.IS_LOGIN, false)){
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
            View header=navigationView.getHeaderView(0);

            t_user_name = (TextView)header.findViewById(R.id.t_user_name);
            t_user_name.setText(getSharedPreferences().getString(CMain.USER_NAME, ""));
        }

        if (savedInstanceState == null) {
            Fragment fragment = null;
            Class fragmentClass = null;
            fragmentClass = FragmentImport.class;
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_monitoring);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;
        Class fragmentClass = null;

        if (id == R.id.nav_monitoring) {
            Toast.makeText(MainActivityMonitor.this, "monitoring",Toast.LENGTH_LONG).show();
            fragmentClass = FragmentImport.class;
        }else if (id == R.id.nav_logout) {
            ConnectivityManager cm =
                    (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();
            if(isConnected) {
                new WebServerRegistrationTask().execute();
            }else{
                Toast.makeText(MainActivityMonitor.this, "Aplikasi membutuhkan koneksi internet.", Toast.LENGTH_LONG).show();
            }
            return true;
        } else if (id == R.id.nav_del_sp) {
            logout();
        }
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
    private class WebServerRegistrationTask extends AsyncTask<Void, Void, Void> {
        private String Content;
        private ProgressDialog Dialog = new ProgressDialog(MainActivityMonitor.this);
        protected void onPreExecute() {
            Dialog.setMessage("Mohon tunggu...");
            Dialog.show();
        }
        @Override
        protected Void doInBackground(Void... params) {
            BufferedReader reader=null;
            URL url = null;
            try {
                String uri = getSharedPreferences().getString(GlobalConfig.IP_KEY,"");
                if(uri.length()==0){
                    uri = GlobalConfig.IP;
                }
                uri = "http://"+uri+""+ CMain.URL_LOGOUT;
                url = new URL(uri);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            Map<String, String> dataMap = new HashMap<String, String>();
            dataMap.put(CMain.USER_ID, ""+getSharedPreferences().getString(CMain.USER_ID,""));
            dataMap.put(CMain.USER_REGID,""+getSharedPreferences().getString(CMain.USER_REGID,""));

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
                while((line = reader.readLine()) != null)
                {
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
                Log.w(GlobalConfig.TAG, "po"+ pe.getMessage());
                //handler.sendEmptyMessage(MSG_REGISTER_WEB_SERVER_FAILURE);
            } catch (IOException io) {
                io.printStackTrace();
                Log.w(GlobalConfig.TAG, "io" + io.getMessage());
                //handler.sendEmptyMessage(MSG_REGISTER_WEB_SERVER_SUCCESS);
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }

            return null;
        }
        protected void onPostExecute(Void unused) {
            JSONObject jsonResponse;
            Dialog.dismiss();

            //Toast.makeText(MainActivityMonitor.this,""+Content, Toast.LENGTH_LONG).show();

            Log.d(GlobalConfig.TAG,""+Content);
            try {
                /****** Creates a new JSONObject with name/value mappings from the JSON string. ********/
                jsonResponse = new JSONObject(Content);

                int status = jsonResponse.optInt(CMain.STATUS);
                String message = jsonResponse.optString(CMain.MESSAGE);

                if(status==1){
                    logout();
                    Toast.makeText(MainActivityMonitor.this,""+message, Toast.LENGTH_LONG).show();
                }else{
                    //Toast.makeText(Dashboard.this,""+message, Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Toast.makeText(MainActivityMonitor.this,""+e.getMessage(), Toast.LENGTH_LONG).show();
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
    public void logout(){
        SharedPreferences.Editor edit = getSharedPreferences().edit();
        edit.clear();
        edit.commit();

        //getApplicationContext().deleteDatabase("plnhilus.db");      //delete database

        Intent myIntentA1A2 = new Intent(MainActivityMonitor.this, Login.class);

        MainActivityMonitor.this.startActivity(myIntentA1A2);
        MainActivityMonitor.this.finish();

        finish();
    }
}
