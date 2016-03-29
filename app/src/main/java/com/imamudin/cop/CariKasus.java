package com.imamudin.cop;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import Config.CBerkasSatu;
import Config.GlobalConfig;
import Volley.JsonArrayPostRequest;
import Volley.JsonObjectPostRequest;
import app.MyAppController;
import listAdapter.ListAdapterCariKasus;
import model.CariKasusItem;

/**
 * Created by agung on 12/03/2016.
 */
public class CariKasus extends AppCompatActivity
{
    // Movies json url
    private String mParam1;
    private String mParam2;
    String keyword, keyword_jenis;
    SharedPreferences prefs;

    //variabel untuk list view
    // Log tag
    private static final String TAG = CariKasus.class.getSimpleName();
    // Billionaires json url
//    private static final String url = "https://raw.githubusercontent.com/mobilesiri/Android-Custom-Listview-Using-Volley/master/richman.json";
    private static final String url = "/cop/carikasus.php";
    private static final String url2 = "http://192.168.1.130/cop/lokasi.php";
    private ProgressDialog pDialog;
    private List<CariKasusItem> cariKasusList = new ArrayList<CariKasusItem>();
    private ListView listView;
    private ListAdapterCariKasus adapter;

    private SwipeRefreshLayout swipeContainer;
    private Toolbar mToolbar;

    int no, offSet=0, kasus_urut=0;

    Runnable runnable;
    Boolean disableSwipeDown = false;       //untuk mendisable swipe down list view

    //untuk handler AsyncTask
    protected static final int MSG_REGISTER_WEB_SERVER_SUCCESS = 103;
    protected static final int MSG_REGISTER_WEB_SERVER_FAILURE = 104;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cari_kasus);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Pilih Kasus");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(false);

        Intent intent = getIntent();
        keyword         = intent.getStringExtra(CBerkasSatu.KEYWORD);
        keyword_jenis   = intent.getStringExtra(CBerkasSatu.KEYWORD_JENIS);

        //untuk list view
        listView = (ListView)findViewById(R.id.lv_cari_kasus);
        cariKasusList.clear();
        adapter = new ListAdapterCariKasus(CariKasus.this, cariKasusList);
        listView.setAdapter(adapter);

        pDialog = new ProgressDialog(CariKasus.this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading…");
        pDialog.show();

        //untuk swipelist
        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout)findViewById(R.id.swipeContainer);

        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                //fetchTimelineAsync(0);
                cariKasusList.clear();
                adapter.notifyDataSetChanged();
                callNews(0);
                Toast.makeText(CariKasus.this,"refresh",Toast.LENGTH_LONG).show();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item text from ListView
                Object o = listView.getItemAtPosition(position);
                CariKasusItem kasusItem = (CariKasusItem) o;

                //return value to before Activity
                Intent output = new Intent();
                output.putExtra(CBerkasSatu.KASUS_ID, kasusItem.getKasus_id());
                output.putExtra(CBerkasSatu.KASUS_NAMA, kasusItem.getNama_kasus());
                setResult(RESULT_OK, output);
                finish();
            }
        });

        swipeContainer.post(new Runnable() {
            @Override
            public void run() {
                swipeContainer.setRefreshing(true);
                cariKasusList.clear();
                adapter.notifyDataSetChanged();
                callNews(0);
            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {

            private int currentVisibleItemCount;
            private int currentScrollState;
            private int currentFirstVisibleItem;
            private int totalItem;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                this.currentScrollState = scrollState;
                this.isScrollCompleted();
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                this.currentFirstVisibleItem = firstVisibleItem;
                this.currentVisibleItemCount = visibleItemCount;
                this.totalItem = totalItemCount;
            }
            private void isScrollCompleted() {
                if (totalItem - currentFirstVisibleItem == currentVisibleItemCount
                        && this.currentScrollState == SCROLL_STATE_IDLE) {

                    if(!disableSwipeDown) {
                        swipeContainer.setRefreshing(true);
                        handler = new Handler();

                        runnable = new Runnable() {
                            public void run() {
                                callNews(offSet);

                            }
                        };
                        //untuk menerlambatkan 1 detik
                        handler.postDelayed(runnable, 1000);
                    }else{
                        Toast.makeText(CariKasus.this,"Data telah ditampilkan semua.",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }
    public String getUrl(String url){
        String uri = getSharedPreferences().getString(GlobalConfig.IP_KEY, "");
        if(uri.length()==0){
            uri = GlobalConfig.IP;
        }
        uri = "http://" + uri + "" + url;
        return uri;
    }

    private void callNews(int page){

        swipeContainer.setRefreshing(true);

        Map<String, String> params = new HashMap<String, String>();
        params.put(CBerkasSatu.KEYWORD, keyword);
        params.put(CBerkasSatu.KEYWORD_JENIS, keyword_jenis);
        params.put(CBerkasSatu.OFFSET, ""+page);
        params.put(CBerkasSatu.USER_ID, ""+getSharedPreferences().getString(CBerkasSatu.USER_ID,""));
        params.put(CBerkasSatu.USER_REGID, ""+getSharedPreferences().getString(CBerkasSatu.USER_REGID,""));

        // Creating volley request obj
        JsonObjectPostRequest jsonArrRequest = new JsonObjectPostRequest(getUrl(url),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG+"Array", ""+response);
                        try {
                            int status = response.getInt("status");
                            if(status==1){
                                JSONArray kasuss = response.getJSONArray("kasus");
                                if(kasuss.length()<25){
                                    disableSwipeDown = true;
                                }

                                if(kasuss.length()>=0) {
                                    for (int i = 0; i < kasuss.length(); i++) {
                                        JSONObject kasus = kasuss.getJSONObject(i);
                                        //Log.d(TAG + "kasus_nama", "" + kasus.get("kasus_nama"));

                                        CariKasusItem cariKasusItem = new CariKasusItem();
                                        cariKasusItem.setNama_kasus(kasus.getString(CBerkasSatu.KASUS_NAMA));
                                        cariKasusItem.setKasus_id(kasus.getString(CBerkasSatu.KASUS_ID));
                                        cariKasusItem.setNo_lp(kasus.getString(CBerkasSatu.KASUS_NO_LP));
                                        cariKasusItem.setTgl_kasus(kasus.getString(CBerkasSatu.KASUS_TANGGAL));
                                        cariKasusItem.setNama_pelapor(kasus.getString(CBerkasSatu.KASUS_NAMA_PELAPOR));

                                        kasus_urut = kasus.getInt(CBerkasSatu.KASUS_URUT);

                                        // adding news to news array
                                        cariKasusList.add(cariKasusItem);

                                        if (kasus_urut > offSet)
                                            offSet = kasus_urut;

                                        Log.d(TAG, "offSet " + offSet);

                                        swipeContainer.setRefreshing(false);
                                        hidePDialog();

                                        // notifying list adapter about data changes
                                        // so that it renders the list view with updated data
                                        adapter.notifyDataSetChanged();
                                    }
                                }else{
                                    Toast.makeText(getApplicationContext(),"Kasus tidak ditemukan.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                swipeContainer.setRefreshing(false);
                hidePDialog();
            }
        }, params);
        // Adding request to request queue
        MyAppController.getInstance().addToRequestQueue(jsonArrRequest);
    }
    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    public void submitMessage(View V)
    {
        // get the Entered  message
        //String message=editTextMessage.getText().toString();
        Intent intentMessage=new Intent();


        // put the message to return as result in Intent
        intentMessage.putExtra("MESSAGE","");
        // Set The Result in Intent
        setResult(2,intentMessage);
        // finish The activity
        finish();

    }
    private SharedPreferences getSharedPreferences() {
        if (prefs == null) {
            prefs = getApplicationContext().getSharedPreferences(
                    GlobalConfig.KEY_PREFERENCES, Context.MODE_PRIVATE);
        }
        return prefs;
    }
    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_REGISTER_WEB_SERVER_SUCCESS:
                    Toast.makeText(getApplicationContext(),
                            "Tidak mendapatkan respon.", Toast.LENGTH_LONG).show();
                    break;
                case MSG_REGISTER_WEB_SERVER_FAILURE:
                    Toast.makeText(getApplicationContext(),
                            "Tidak dapat mengakses server.",
                            Toast.LENGTH_LONG).show();
                    break;
            }
        }

        ;
    };
//    private class CariKasusTask extends AsyncTask<Void, Void, Void> {
//        private String Content;
//        private ProgressDialog Dialog = new ProgressDialog(CariKasus.this);
//
//        protected void onPreExecute() {
//            Dialog.setMessage("Mohon tunggu...");
//            Dialog.show();
//        }
//
//        @Override
//        protected Void doInBackground(Void... params) {
//            BufferedReader reader = null;
//            URL url = null;
//            try {
//                String uri = getSharedPreferences().getString(GlobalConfig.IP_KEY, "");
//                if(uri.length()==0){
//                    uri = GlobalConfig.IP;
//                }
//                uri = "http://" + uri + "" + WEB_SERVER_URL;
//                Log.d("url",""+uri);
//                url = new URL(uri);
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//                handler.sendEmptyMessage(MSG_REGISTER_WEB_SERVER_FAILURE);
//            }
//            Map<String, String> dataMap = new HashMap<String, String>();
//            dataMap.put(CBerkasSatu.KEYWORD, keyword);
//
//            StringBuilder postBody = new StringBuilder();
//            Iterator iterator = dataMap.entrySet().iterator();
//
//            while (iterator.hasNext()) {
//                Map.Entry param = (Map.Entry) iterator.next();
//                postBody.append(param.getKey()).append('=')
//                        .append(param.getValue());
//                if (iterator.hasNext()) {
//                    postBody.append('&');
//                }
//            }
//            String body = postBody.toString();
//            byte[] bytes = body.getBytes();
//
//            HttpURLConnection conn = null;
//            try {
//                conn = (HttpURLConnection) url.openConnection();
//                conn.setDoOutput(true);
//                conn.setUseCaches(false);
//                conn.setFixedLengthStreamingMode(bytes.length);
//                conn.setRequestMethod("POST");
//                conn.setRequestProperty("Content-Type",
//                        "application/x-www-form-urlencoded;charset=UTF-8");
//
//                OutputStream out = conn.getOutputStream();
//                out.write(bytes);
//                out.close();
//
//                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//
//                StringBuilder sb = new StringBuilder();
//                String line = null;
//
//                // Read Server Response
//                while ((line = reader.readLine()) != null) {
//                    // Append server response in string
//                    sb.append(line + "");
//                }
//
//                // Append Server Response To Content String
//                Content = sb.toString();
//
//                int status = conn.getResponseCode();
//                if (status == 200) {
//                    // Request success
//                    handler.sendEmptyMessage(MSG_REGISTER_WEB_SERVER_SUCCESS);
//                } else {
//                    throw new IOException("Request failed with error code "
//                            + status);
//                }
//            } catch (ProtocolException pe) {
//                pe.printStackTrace();
//                handler.sendEmptyMessage(MSG_REGISTER_WEB_SERVER_FAILURE);
//            } catch (IOException io) {
//                io.printStackTrace();
//                handler.sendEmptyMessage(MSG_REGISTER_WEB_SERVER_FAILURE);
//            } finally {
//                if (conn != null) {
//                    conn.disconnect();
//                }
//            }
//
//            return null;
//        }
//
//        protected void onPostExecute(Void unused) {
//            Log.d("ContentCariKasus",""+Content);
//            //Toast.makeText(Login.this,""+Content,Toast.LENGTH_LONG).show();
//            //output.setText(Content);
//            JSONObject jsonResponse;
//            Dialog.dismiss();
//
//            try {
//                /****** Creates a new JSONObject with name/value mappings from the JSON string. ********/
//                jsonResponse = new JSONObject(Content);
//
//                int status = jsonResponse.optInt(CBerkasSatu.STATUS);
//                Dialog.dismiss();
//                //Log.d("status",""+status);
//
//                if (status == 1) {
//                    JSONArray kasus_array = jsonResponse.getJSONArray(CBerkasSatu.KASUS);
//
//                    for(int i =0; i<kasus_array.length();i++){
//                        JSONObject kasus = kasus_array.getJSONObject(i);
//
//
//                    }
//
//                    //menyimpan data ke sharepreferences
//                    SharedPreferences.Editor edit = getSharedPreferences().edit();
//                    edit.putBoolean(CLogin.IS_LOGIN, true);
//                    edit.putInt(CLogin.USER_ID, data.optInt(CLogin.USER_ID, 0));
//                    edit.putString(CLogin.USER_NAMA, data.optString(CLogin.USER_NAMA,""));
//                    edit.putString(CLogin.USER_EMAIL, data.optString(CLogin.USER_EMAIL,""));
//                    edit.putString(CLogin.USER_REGID, data.optString(CLogin.USER_REGID,""));
//                    edit.putInt(CLogin.USER_ROLE, data.optInt(CLogin.USER_ROLE, 0));
//
//                    edit.commit();
//
//                    //Toast.makeText(Login.this, jsonResponse.optString(CLogin.MESSAGE), Toast.LENGTH_SHORT);
//                } else {
//                    Toast.makeText(CariKasus.this, jsonResponse.optString(CLogin.MESSAGE), Toast.LENGTH_LONG).show();
//                }
//            } catch (JSONException e) {
//                Log.d("json_erros", ""+e.getMessage().toString());
//                //output.setText("" + e.getMessage());
//            }
//        }
//    }

}