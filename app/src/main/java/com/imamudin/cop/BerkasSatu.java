package com.imamudin.cop;

import android.app.ActionBar;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import Config.CBerkasSatu;
import GPS.GPSTracker;

/**
 * Created by agung on 23/02/2016.
 */
public class BerkasSatu extends AppCompatActivity{

    Button btnTambahPertanyaan, btnSimpan, btnLokasi;
    ImageButton btnSearch, btnTambahGambar;
    LinearLayout llPertanyaan, llGambar;
    TextView tv_kasus_id, tv_kasus_nama, tv_keterangan_gambar;
    ImageView img_kasus_nama, img_nama, img_alamat, img_lokasi;
    EditText et_nama, et_lokasi, et_alamat;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_GALLERY = 2;
    static final int MAX_FOTO = 5;
    String imgDecodableString;                                              //untuk mengambil image dari gallery


    //untuk mendapatakan lokasi
    private LocationManager locationManager;
    private String provider;
    private MyLocationListener mylistener;
    private Criteria criteria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.berkas_satu);

        //inisialisasi variabel
        btnTambahPertanyaan = (Button) findViewById(R.id.btn_tambah_pertanyaan);
        btnTambahGambar = (ImageButton) findViewById(R.id.btn_tambah_gambar);
        btnSimpan = (Button) findViewById(R.id.btn_simpan);
        btnSearch = (ImageButton) findViewById(R.id.btn_search);
        btnLokasi = (Button) findViewById(R.id.btnLokasi);

        llPertanyaan = (LinearLayout) findViewById(R.id.ll_pertanyaan);
        llGambar = (LinearLayout) findViewById(R.id.ll_tambah_gambar_dalam);

        tv_kasus_id = (TextView) findViewById(R.id.tv_kasus_id);
        tv_kasus_nama = (TextView) findViewById(R.id.tv_kasus_nama);
        tv_keterangan_gambar = (TextView) findViewById(R.id.tv_keterangan_gambar);

        img_kasus_nama = (ImageView) findViewById(R.id.img_kasus_nama);
        img_lokasi = (ImageView) findViewById(R.id.img_kasus_lokasi);
        img_alamat = (ImageView) findViewById(R.id.img_alamat);
        img_nama = (ImageView) findViewById(R.id.img_nama);

        et_nama = (EditText) findViewById(R.id.et_nama);
        et_lokasi = (EditText) findViewById(R.id.et_lokasi);
        et_alamat = (EditText) findViewById(R.id.et_alamat);


        //konfigurasi toolbar
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Form Berkas 1");
        //actionBar.setIcon(R.drawable.search_24);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(false);
        //toolbar.setTitleTextColor(getResources().getColor(R.color.putih));

        btnTambahPertanyaan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showdialogTambahPertanyaan();
            }
        });
        btnLokasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLokasi();
            }
        });

        btnTambahGambar.setOnClickListener(btnClick);


        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //linear layout level 1
                int total1 = 0, total2 = 0, total3 = 0;
                int count = llPertanyaan.getChildCount();
                View vChild = null;
                Log.d("tanya : total ", "" + count);
                for (int i = 0; i < count; i++) {
                    vChild = llPertanyaan.getChildAt(i);
                    //linear layout level2
                    if (vChild instanceof LinearLayout) {
                        View vChild2 = null;
                        total2 = ((LinearLayout) vChild).getChildCount();
                        //Log.d("tanya : total2 ",""+total2);
                        for (int j = 0; j < ((LinearLayout) vChild).getChildCount(); j++) {
                            vChild2 = ((LinearLayout) vChild).getChildAt(j);
                            //linear layout level3
                            if (vChild2 instanceof LinearLayout) {
                                View vChild3 = null;
                                total3 = ((LinearLayout) vChild2).getChildCount();
                                //Log.d("tanya : total3 ",""+total3);

                                //disini pada vchild2(1) lokasi text view pertanyaan dan jawab
                                View vchild_tanya = null;
                                vchild_tanya = ((LinearLayout) vChild2).getChildAt(1);

                                for (int k = 0; k < ((LinearLayout) vchild_tanya).getChildCount(); k++) {
                                    vChild3 = ((LinearLayout) vchild_tanya).getChildAt(k);
                                    if (vChild3 instanceof TextView) {
                                        Log.d("tanya : tj :", "" + ((TextView) vChild3).getText().toString());
                                    }
                                }
                            }
                        }
                    }
                    //do something with your child element
                }

                //Toast.makeText(BerkasSatu.this,"child1 : "+count+", child2:"+total2,Toast.LENGTH_SHORT).show();
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showdialogCariKasus();
            }
        });
    }

    View.OnClickListener btnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == btnTambahGambar) {
                if (llGambar.getChildCount() < MAX_FOTO) {
                    showdialogFoto();
                } else {
                    Toast.makeText(getApplicationContext(), "Jumlah foto maksimal " + MAX_FOTO, Toast.LENGTH_SHORT).show();
                }

            }
        }
    };

    //untuk mendapatakan lokasi longitude latitude
    public void getLocation() {
        // Get the location manager
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, false);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(bestProvider);
        Double lat,lon;
        try {
            lat = location.getLatitude ();
            lon = location.getLongitude ();
            Toast.makeText(getApplicationContext(), lat+" "+lon,Toast.LENGTH_SHORT).show();
            //return new LatLng(lat, lon);
        }
        catch (NullPointerException e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), " "+e.getMessage(),Toast.LENGTH_SHORT).show();
            //return null;
        }
    }
    public void getLokasi() {
        GPSTracker gpsTracker = new GPSTracker(getApplicationContext());

        if (gpsTracker.getIsGPSTrackingEnabled())
        {
            String stringLatitude = String.valueOf(gpsTracker.latitude);
//            textview = (TextView)findViewById(R.id.fieldLatitude);
//            textview.setText(stringLatitude);

            String stringLongitude = String.valueOf(gpsTracker.longitude);
//            textview = (TextView)findViewById(R.id.fieldLongitude);
//            textview.setText(stringLongitude);

            Toast.makeText(getApplicationContext(),"lat: "+stringLatitude+";  long: "+stringLongitude,Toast.LENGTH_SHORT).show();

            String country = gpsTracker.getCountryName(this);
//            textview = (TextView)findViewById(R.id.fieldCountry);
//            textview.setText(country);

            String city = gpsTracker.getLocality(this);
//            textview = (TextView)findViewById(R.id.fieldCity);
//            textview.setText(city);

            String postalCode = gpsTracker.getPostalCode(this);
//            textview = (TextView)findViewById(R.id.fieldPostalCode);
//            textview.setText(postalCode);

            String addressLine = gpsTracker.getAddressLine(this);
//            textview = (TextView)findViewById(R.id.fieldAddressLine);
//            textview.setText(addressLine);
        }
        else
        {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gpsTracker.showSettingsAlert();
        }
    }
    //untuk membuka intent mengambil foto dari kamera
    private void loadImagefromFoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
    //untuk membuka intent mengambil foto dari gallery
    public void loadImagefromGallery() {
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, REQUEST_IMAGE_GALLERY);
    }

    public void showdialogCariKasus() {           //untuk mencari kasus
        LayoutInflater layoutInflater = (LayoutInflater) getLayoutInflater();
        View promptView = layoutInflater.inflate(R.layout.dialog_cari_kasus, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BerkasSatu.this);
        alertDialogBuilder.setView(promptView);

        final TextView t_title  = (TextView) promptView.findViewById(R.id.t_title_dialog);
        final RadioGroup jenis  = (RadioGroup)promptView.findViewById(R.id.radiojenis);
        final EditText t_cari   = (EditText) promptView.findViewById(R.id.et_ip);

        final RadioButton rb_no_lp  = (RadioButton)promptView.findViewById(R.id.radio_nomor_lp);
        final RadioButton rb_nama_pelapor  = (RadioButton)promptView.findViewById(R.id.radio_nama_pelapor);

        t_title.setText("Cari Kasus");

        // setup a dialog window
        alertDialogBuilder.setCancelable(true)
                .setPositiveButton("Cari",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (t_cari.getText().toString() != "") {
                                    //buka activity cari kasus
                                    //melihat jenis pencarian
                                    int selectedId = jenis.getCheckedRadioButtonId();
                                    String sjenis="";       //untuk menyimpan jenis pencarian

                                    if(selectedId==rb_no_lp.getId()){
                                        sjenis = CBerkasSatu.NO_LP;
                                    }else
                                        sjenis = CBerkasSatu.NAMA_PELAPOR;

                                    Toast.makeText(getApplicationContext(),""+getResources().getString(R.string.nama_pelapor), Toast.LENGTH_SHORT).show();
                                    Intent iCariKasus = new Intent(BerkasSatu.this,CariKasus.class);
                                    iCariKasus.putExtra(CBerkasSatu.KEYWORD_JENIS,sjenis);
                                    iCariKasus.putExtra(CBerkasSatu.KEYWORD, t_cari.getText().toString());
                                    startActivityForResult(iCariKasus, CBerkasSatu.KODE_BERKAS1_CARIKASUS);
                                }else{
                                    Toast.makeText(BerkasSatu.this, "Masukan kata kunci!" + t_cari.getText(), Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                .setNegativeButton("Batal",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        //jika ingin menghapus kasus
        if(tv_kasus_nama.getText().toString().trim()!="") {
            alertDialogBuilder.setNeutralButton("Hapus Kasus",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            tv_kasus_id.setText("");
                            tv_kasus_nama.setText("");

                            img_kasus_nama.setImageDrawable(ContextCompat.getDrawable(BerkasSatu.this, R.drawable.ic_remove));
                        }
                    });
        }
        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CBerkasSatu.KODE_BERKAS1_CARIKASUS && resultCode == RESULT_OK && data != null) {
            String kasus_nama = data.getStringExtra(CBerkasSatu.KASUS_NAMA);
            int kasus_id = data.getIntExtra(CBerkasSatu.KASUS_ID,0);

            tv_kasus_id.setText(""+kasus_id);
            tv_kasus_nama.setText(""+kasus_nama);

            img_kasus_nama.setImageDrawable(ContextCompat.getDrawable(BerkasSatu.this, R.drawable.ic_done));
        }
        //untuk menangkap image dari kamera
        else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            add_layout_gambar(imageBitmap);
            //mImageView.setImageBitmap(imageBitmap);
        }
        else if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == RESULT_OK
                && null != data) {
            // Get the Image from data

            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            // Get the cursor
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            // Move to first row
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            imgDecodableString = cursor.getString(columnIndex);
            add_layout_gambar(BitmapFactory
                    .decodeFile(imgDecodableString));
            cursor.close();
        }
    }
    public void add_layout_gambar(final Bitmap imageBitmap){
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        final View inflatedLayout= inflater.inflate(R.layout.view_gambar, llGambar, false);

        final ImageView img_berkas  = (ImageView) inflatedLayout.findViewById(R.id.img_berkas);
        img_berkas.setImageBitmap(imageBitmap);

        img_berkas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"show pic", Toast.LENGTH_SHORT);
            }
        });

        llGambar.addView(inflatedLayout);

        int count = llGambar.getChildCount();
        String keterangan="";
        if(count>0){
            keterangan = "Ditambahkan "+count+" dari "+MAX_FOTO+" foto";
        }else{
            keterangan = "Pilih "+MAX_FOTO+" foto";
        }
        tv_keterangan_gambar.setText(keterangan);
    }
    public void showdialogFoto(){
        // get prompts.xml view
        LayoutInflater layoutInflater = (LayoutInflater)getLayoutInflater();
        View promptView = layoutInflater.inflate(R.layout.dialog_pilih_foto, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BerkasSatu.this);
        alertDialogBuilder.setView(promptView);

        final TextView t_title  = (TextView) promptView.findViewById(R.id.t_title_dialog);
        final LinearLayout t_kamera   = (LinearLayout) promptView.findViewById(R.id.t_kamera);
        final LinearLayout t_galeri   = (LinearLayout) promptView.findViewById(R.id.t_galeri);

        t_title.setText("Pilih Foto");


        final AlertDialog alert = alertDialogBuilder.create();

        t_kamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadImagefromFoto();
                alert.cancel();
            }
        });
        t_galeri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadImagefromGallery();
                alert.cancel();
            }
        });
        alert.show();
    }
    //untuk mengambil foto dari galery

    public void showdialogTambahPertanyaan() {
        LayoutInflater layoutInflater = (LayoutInflater) getLayoutInflater();
        View promptView = layoutInflater.inflate(R.layout.dialog_pertanyaan, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BerkasSatu.this);
        alertDialogBuilder.setView(promptView);

        final TextView t_title = (TextView) promptView.findViewById(R.id.t_title_dialog);
        final EditText et_pertanyaan= (EditText) promptView.findViewById(R.id.et_dialog_pertanyaan);
        final EditText et_jawaban   = (EditText) promptView.findViewById(R.id.et_dialog_jawaban);

        t_title.setText("Pertanyaan");

        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("Simpan",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String s_jawab  = et_jawaban.getText().toString();
                                String s_tanya  = et_pertanyaan.getText().toString();
                                if (!s_jawab.trim().equals("") && !s_tanya.trim().equals("")) {
                                    //menambahkan ke layout
                                    add_layout_pertanyaan(s_tanya, s_jawab);
                                }else{
                                    Toast.makeText(BerkasSatu.this, "Pertanyaan dan jawaban tidak boleh kosong!", Toast.LENGTH_SHORT).show();
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
    public void add_layout_pertanyaan(final String tanya, final String jawab){
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        final View inflatedLayout= inflater.inflate(R.layout.view_pertanyaan, llPertanyaan, false);

        final TextView t_tanya  = (TextView) inflatedLayout.findViewById(R.id.et_pertanyaan);
        final TextView t_jawab  = (TextView) inflatedLayout.findViewById(R.id.et_jawaban);

        final ImageButton btnDelete = (ImageButton) inflatedLayout.findViewById(R.id.img_hapus);
        final ImageButton btnEdit   = (ImageButton) inflatedLayout.findViewById(R.id.img_edit);

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showdialogHapusPertanyaan(tanya, inflatedLayout);
            }
        });
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showdialogEditPertanyaan(tanya,t_tanya,jawab,t_jawab);
                //Toast.makeText(BerkasSatu.this, "Edit : "+tanya, Toast.LENGTH_SHORT).show();
            }
        });

        t_tanya.setText(tanya);
        t_jawab.setText(jawab);

        llPertanyaan.addView(inflatedLayout);
    }
    public void showdialogEditPertanyaan(String tanya, final TextView t_tanya, String jawab, final TextView t_jawab) {
        LayoutInflater layoutInflater = (LayoutInflater) getLayoutInflater();
        View promptView = layoutInflater.inflate(R.layout.dialog_pertanyaan, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BerkasSatu.this);
        alertDialogBuilder.setView(promptView);

        final TextView t_title = (TextView) promptView.findViewById(R.id.t_title_dialog);
        final EditText et_pertanyaan= (EditText) promptView.findViewById(R.id.et_dialog_pertanyaan);
        final EditText et_jawaban   = (EditText) promptView.findViewById(R.id.et_dialog_jawaban);

        t_title.setText("Pertanyaan");
        et_pertanyaan.setText(tanya);
        et_jawaban.setText(jawab);

        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("Simpan",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String s_jawab  = et_jawaban.getText().toString();
                                String s_tanya  = et_pertanyaan.getText().toString();
                                if (!s_jawab.trim().equals("") && !s_tanya.trim().equals("")) {
                                    //mengganti ke layout
                                    t_tanya.setText(s_tanya);
                                    t_jawab.setText(s_jawab);
                                }else{
                                    Toast.makeText(BerkasSatu.this, "Pertanyaan dan jawaban tidak boleh kosong!", Toast.LENGTH_SHORT).show();
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
    private void showdialogHapusPertanyaan(String text, final View view){            //untuk menghapus pertanyaan pada view
        // get prompts.xml view
        LayoutInflater layoutInflater = (LayoutInflater)getLayoutInflater();
        View promptView = layoutInflater.inflate(R.layout.notif_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BerkasSatu.this);
        alertDialogBuilder.setView(promptView);

        final TextView t_title  = (TextView) promptView.findViewById(R.id.t_title_dialog);
        final TextView t_text   = (TextView) promptView.findViewById(R.id.t_text_dialog);

        //untuk membuat ... jika lebih dari 25
        if(text.length() >= 25){
            text = text.substring(0,25)+"...";
        }
        t_title.setText("Hapus Pertanyaan");
        t_text.setText("Apakah anda yakin menghapus pertanyaan \""+text+"\"?");
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //hapus view pertanyaan

                        ((ViewGroup) view.getParent()).removeView(view);
                    }
                })
                .setNegativeButton("Batal",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();

    }

//    @Override
//    public void onBackPressed() {
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }
//    }

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
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            // Initialize the location fields
            Toast.makeText(BerkasSatu.this,  ""+location.getLatitude()+location.getLongitude(),
                    Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Toast.makeText(BerkasSatu.this, provider + "'s status changed to "+status +"!",
                    Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onProviderEnabled(String provider) {
            Toast.makeText(BerkasSatu.this, "Provider " + provider + " enabled!",
                    Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onProviderDisabled(String provider) {
            Toast.makeText(BerkasSatu.this, "Provider " + provider + " disabled!",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
