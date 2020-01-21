package com.buggy.tripbook;


import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.View;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Call<ArrayList<TripItemModel>> getUsers;
    private GetDataRetrofit service;

    static ArrayList<TripItemModel> mtripList = new ArrayList<>();


    TextView mAddTripStartDate;
    TextView mAddTripEndDate;
    TextView mEditTripStartDate;
    TextView mEditTripEndDate;
    Uri mSelectPhoto;


    ImageView mSelectTripImage;

    //static public SeekBar mAddTripPrice;

    TextView tvUserEmail;
    FirebaseUser user;
    String sUser;


    private static final String USER_KEY = "user";
    private static final String APP_KEY = "tripbook_key";
    private static final int CAMERA_REQUEST_ADD_FRAGMENT = 1850;
    private static final int GALLERY_REQUEST_ADD_FRAGMENT = 1860;
    private static final int CAMERA_REQUEST_EDIT_FRAGMENT = 1870;
    private static final int GALLERY_REQUEST_EDIT_FRAGMENT = 1880;

    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static final int MY_GALLERY_PERMISSION_CODE = 101;


    private static final int RC_SIGN_IN = 123;
    List<AuthUI.IdpConfig> emailBuilder = Arrays.asList(
            new AuthUI.IdpConfig.EmailBuilder().build());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //user
        if (getSharedPreferences(APP_KEY, Context.MODE_PRIVATE).getString(USER_KEY, null) == null) {
            sUser = "local";
            getSharedPreferences(APP_KEY, Context.MODE_PRIVATE).edit().putString(USER_KEY, "local").commit();
        } else {
            sUser = getSharedPreferences(APP_KEY, Context.MODE_PRIVATE).getString(USER_KEY, null);
        }




        //Action Bar
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //Default trips
        /*
        if(mtripList.size() == 0){
            createPlaceholderTrips();
        }
        if(user!=null){
            tvUserEmail.setText(user.getEmail());
        }
        */


        //Drawer
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        //Navigation view
        NavigationView navigationView = findViewById(R.id.nav_view);

        //Menu
        Menu menu = navigationView.getMenu();


        //Sets the Menu item Listner
        navigationView.setNavigationItemSelectedListener(this);

        //Poza de profil
        View headView = navigationView.getHeaderView(0);
        ImageView imgProfile = headView.findViewById(R.id.imgProfile);
        tvUserEmail = headView.findViewById(R.id.userEmail);
        tvUserEmail.setText(sUser);
        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent i = new Intent(MainActivity.this,ProfileActivity.class);
                //startActivity(i);

                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setAvailableProviders(emailBuilder)
                                .setIsSmartLockEnabled(false)
                                .build(),
                        RC_SIGN_IN);
            }
        });

        //default fragment for home
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.flMain, new HomeFragment());
        ft.commit();

        navigationView.setCheckedItem(R.id.nav_home);


        //Retrofit
        service = RetrofitClient.getRetrofit().create(GetDataRetrofit.class);
        //getUsers(service);
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (isConnected(cm)) {
            if (mtripList.isEmpty()) {
                getUsers(service);
                // HomeFragment.adapter.notifyDataSetChanged();

            }
        }

    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
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

        if (id == R.id.nav_home) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flMain, new HomeFragment());
            ft.commit();
        } else if (id == R.id.nav_setting) {
            Intent i = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_about) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flMain, new InboxFragment());
            ft.commit();
        } else if (id == R.id.nav_contact) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flMain, new InboxFragment());
            ft.commit();
        } else if (id == R.id.nav_share) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flMain, new InboxFragment());
            ft.commit();
        }


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //Date Picker fargments
    public void pickEndDate(View view) {
        DatePickerDialog dialog = new DatePickerDialog(MainActivity.this, R.style.Theme_AppCompat_Dialog,
                mEndDateSetListner, 2019, 11, 27);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.GRAY));
        dialog.show();
    }

    public void pickStartDate(View view) {

        DatePickerDialog dialog = new DatePickerDialog(MainActivity.this, R.style.Theme_AppCompat_Dialog,
                mStartDateSetListner, 2019, 11, 27);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.GRAY));
        dialog.show();
    }

    public void editPickStartDate(View view) {
        DatePickerDialog dialog = new DatePickerDialog(MainActivity.this, R.style.Theme_AppCompat_Dialog,
                mEditStartDateSetListner, 2019, 11, 27);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.GRAY));
        dialog.show();
    }

    public void editPickEndDate(View view) {
        DatePickerDialog dialog = new DatePickerDialog(MainActivity.this, R.style.Theme_AppCompat_Dialog,
                mEditEndDateSetListner, 2019, 11, 27);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.GRAY));
        dialog.show();
    }





    //pick photo from gallery click event
    public void selectPhotoEditFragment(View view) {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_GALLERY_PERMISSION_CODE);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.

        } else {
            // Permission has already been granted
            //  Intent galleryIntent = new Intent();
            //  galleryIntent.setType("image/*");
            //  galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
            //  startActivityForResult(Intent.createChooser(galleryIntent,"Select Picture"),GALLERY_REQUEST);
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, GALLERY_REQUEST_EDIT_FRAGMENT);
        }
    }

    public void selectPhotoAddFragment(View view) {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_GALLERY_PERMISSION_CODE);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.

        } else {
            // Permission has already been granted
            //  Intent galleryIntent = new Intent();
            //  galleryIntent.setType("image/*");
            //  galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
            //  startActivityForResult(Intent.createChooser(galleryIntent,"Select Picture"),GALLERY_REQUEST);
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, GALLERY_REQUEST_ADD_FRAGMENT);
        }

    }

    //take a photo click event
    public void takePhotoEditFragment(View view) {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {


            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    MY_CAMERA_PERMISSION_CODE);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.

        } else {
            // Permission has already been granted
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST_EDIT_FRAGMENT);
        }
    }

    public void takePhotoAddFragment(View view) {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {


            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    MY_CAMERA_PERMISSION_CODE);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.

        } else {
            // Permission has already been granted
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST_ADD_FRAGMENT);
        }
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_CAMERA_PERMISSION_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    //Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    //startActivityForResult(cameraIntent, CAMERA_REQUEST);
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            case MY_GALLERY_PERMISSION_CODE: {
                //check if permission is granted
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Intent galleryIntent = new Intent();
                    //galleryIntent.setType("image/*");
                    // galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                    //startActivityForResult(Intent.createChooser(galleryIntent,"Select Picture"),GALLERY_REQUEST);
                    // Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    //intent.setType("image/*");
                    //startActivityForResult(intent,GALLERY_REQUEST);
                }
                //else is not granted
                else {

                }
                return;
            }
        }
    }


    public String saveToInternalStorage(Bitmap bitmapImage, int no) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath = new File(directory, "trip_" + no + ".png");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 90, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();

            }
        }
        return directory.getAbsolutePath();

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == GALLERY_REQUEST_ADD_FRAGMENT && resultCode == RESULT_OK) {
            if (data == null) {
                Log.d("gallery", "data null");
                return;
            }
            mSelectTripImage = findViewById(R.id.addTripImage);
            mSelectPhoto = data.getData();
             Glide.with(this).load(mSelectPhoto).into(mSelectTripImage);


        }

        if (requestCode == GALLERY_REQUEST_EDIT_FRAGMENT && resultCode == RESULT_OK) {
            if (data == null) {
                Log.d("gallery", "data null");
                return;
            }

            mSelectTripImage = findViewById(R.id.editTripImage);
            // Log.d("gallery",(String)data.getExtras().get("data"));
            mSelectPhoto = data.getData();
            Glide.with(this).load(mSelectPhoto).into(mSelectTripImage);

        }


        if (requestCode == CAMERA_REQUEST_ADD_FRAGMENT && resultCode == Activity.RESULT_OK) {
            mSelectTripImage = findViewById(R.id.addTripImage);
            Bitmap photo = (Bitmap) data.getExtras().get("data");

            String imageUri = this.saveToInternalStorage(photo,mtripList.size());
           // mSelectTripImage.setImageBitmap(photo);
            Glide.with(this).load(imageUri).into(mSelectTripImage);

        }
        if (requestCode == CAMERA_REQUEST_EDIT_FRAGMENT && resultCode == Activity.RESULT_OK) {
            mSelectTripImage = findViewById(R.id.editTripImage);
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            String imageUri = this.saveToInternalStorage(photo,RecyclerViewAdapter.pos);
            Glide.with(this).load(imageUri).into(mSelectTripImage);

        }


        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                user = FirebaseAuth.getInstance().getCurrentUser();
                String email = user.getEmail();
                tvUserEmail = findViewById(R.id.userEmail);
                if (tvUserEmail != null) {
                    tvUserEmail.setText(email);
                    sUser = email;
                    SharedPreferences sp = getSharedPreferences(APP_KEY, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString(USER_KEY, sUser);
                    editor.commit();

                }

            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }


    }


    static void createPlaceholderTrips() {
        for (int i = 0; i < 1; i++)
            mtripList.add(new TripItemModel(i,null, "Trip no" + i
                    , "Sahara", 990, (float) 2.5, 0, "Sea Side", 0, 0
                    , 0, 1, 1, 1));


    }


    DatePickerDialog.OnDateSetListener mEndDateSetListner = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            month = month + 1;
            Log.d("test1", "DD/MM/YYYY: " + day + "/" + month + "/" + year);
            mAddTripEndDate = findViewById(R.id.addTripEndDate);
            mAddTripEndDate.setText(day + "/" + month + "/" + year);

        }
    };

    DatePickerDialog.OnDateSetListener mStartDateSetListner = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            month = month + 1;
            Log.d("test1", "DD/MM/YYYY: " + day + "/" + month + "/" + year);
            mAddTripStartDate = findViewById(R.id.addTripStartDate);
            mAddTripStartDate.setText(day + "/" + month + "/" + year);

        }

    };
    DatePickerDialog.OnDateSetListener mEditStartDateSetListner = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            month = month + 1;
            Log.d("test1", "DD/MM/YYYY: " + day + "/" + month + "/" + year);
            mEditTripStartDate = findViewById(R.id.editTripStartDate);
            mEditTripStartDate.setText(day + "/" + month + "/" + year);

        }

    };
    DatePickerDialog.OnDateSetListener mEditEndDateSetListner = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            month = month + 1;
            Log.d("test1", "DD/MM/YYYY: " + day + "/" + month + "/" + year);
            mEditTripEndDate = findViewById(R.id.editTripEndDate);
            mEditTripEndDate.setText(day + "/" + month + "/" + year);

        }

    };


    public void addTripCLickEvent(View view) {
        //get trip name
        String tripName = ((EditText) findViewById(R.id.addTripName)).getText().toString();
        //get trip destination
        String tripDestination = ((EditText) findViewById(R.id.addTripDestination)).getText().toString();
        //get trip price
        SeekBar a = findViewById(R.id.addTripPrice);
        int tripPrice = a.getProgress() * 10;
        //get trip rating
        float tripRating = ((RatingBar) findViewById(R.id.addTripRating)).getRating();
        //get if fav or not
        int tripFav = 0;
        //get trip type
        RadioGroup rg = findViewById(R.id.addTripType);
        RadioButton rb = findViewById(rg.getCheckedRadioButtonId());
        String tripType = null;
        if (rb != null) {
            tripType = rb.getText().toString();
        }
        // get trip starting date
        //is date?
        boolean endDateOk = false;
        boolean startDateOk = false;

        String tripStartDate = ((TextView) findViewById(R.id.addTripStartDate)).getText().toString();
        int tripStartDay = 0;
        int tripStartMonth = 0;
        int tripStartYear = 0;
        if (tripStartDate.equals("")) {

        } else {
            startDateOk = true;
            StringTokenizer startdate = new StringTokenizer(tripStartDate, "/");
            tripStartDay = Integer.parseInt(startdate.nextToken());
            tripStartMonth = Integer.parseInt(startdate.nextToken());
            tripStartYear = Integer.parseInt(startdate.nextToken());
        }

        //get trip end date
        String tripEndDate = ((TextView) findViewById(R.id.addTripEndDate)).getText().toString();
        int tripEndDay = 0;
        int tripEndMonth = 0;
        int tripEndYear = 0;
        if (tripEndDate.equals("")) {

        } else {
            endDateOk = true;
            StringTokenizer multiTokenizer = new StringTokenizer(tripEndDate, "/");
            tripEndDay = Integer.parseInt(multiTokenizer.nextToken());
            tripEndMonth = Integer.parseInt(multiTokenizer.nextToken());
            tripEndYear = Integer.parseInt(multiTokenizer.nextToken());
        }

        //get trip image path



        if (tripName != null && tripDestination != null &&
                tripType != null && tripPrice != 0 &&
                endDateOk == true && startDateOk == true
        ) {
            //add to list
            mtripList.add(new TripItemModel(mtripList.size(), mSelectPhoto, tripName
                    , tripDestination, tripPrice, tripRating, tripFav, tripType, tripStartYear, tripStartMonth
                    , tripStartDay, tripEndYear, tripEndMonth, tripEndDay));
            //exit fragment
            getSupportFragmentManager().popBackStack();

        } else {
            Toast messToast = Toast.makeText(this, "Fill in all the field", Toast.LENGTH_LONG);
            messToast.show();
        }


    }


    public void editTripCLickEvent(View view) {
        //get trip name
        String tripName = ((EditText) findViewById(R.id.editTripName)).getText().toString();
        //get trip destination
        String tripDestination = ((EditText) findViewById(R.id.editTripDestination)).getText().toString();
        //get trip price
        int tripPrice = ((SeekBar) findViewById(R.id.editTripPrice)).getProgress() * 10;
        //get trip rating
        float tripRating = ((RatingBar) findViewById(R.id.editTripRating)).getRating();
        //is fav?
        int tripFav = mtripList.get(RecyclerViewAdapter.pos).getTripFav();
        //get trip type
        RadioGroup rg = findViewById(R.id.editTripType);
        RadioButton rb = findViewById(rg.getCheckedRadioButtonId());
        String tripType = null;
        if (rb != null) {
            tripType = rb.getText().toString();
        }
        //get trip start date
        boolean endDateOk = false;
        boolean startDateOk = false;

        String tripStartDate = ((TextView) findViewById(R.id.editTripStartDate)).getText().toString();
        int tripStartDay = 0;
        int tripStartMonth = 0;
        int tripStartYear = 0;
        if (tripStartDate.equals("")) {

        } else {
            startDateOk = true;
            StringTokenizer startdate = new StringTokenizer(tripStartDate, "/");
            tripStartDay = Integer.parseInt(startdate.nextToken());
            tripStartMonth = Integer.parseInt(startdate.nextToken());
            tripStartYear = Integer.parseInt(startdate.nextToken());
        }

        //get trip end date
        String tripEndDate = ((TextView) findViewById(R.id.editTripEndDate)).getText().toString();
        int tripEndDay = 0;
        int tripEndMonth = 0;
        int tripEndYear = 0;
        if (tripEndDate.equals("")) {

        } else {
            endDateOk = true;
            StringTokenizer multiTokenizer = new StringTokenizer(tripEndDate, "/");
            tripEndDay = Integer.parseInt(multiTokenizer.nextToken());
            tripEndMonth = Integer.parseInt(multiTokenizer.nextToken());
            tripEndYear = Integer.parseInt(multiTokenizer.nextToken());
        }

        //get trip image path


        if (tripName != null && tripDestination != null &&
                tripType != null && tripPrice != 0 &&
                endDateOk == true && startDateOk == true
        ) {
            //add trip
            TripItemModel a = new TripItemModel(mtripList.size(), mSelectPhoto, tripName
                    , tripDestination, tripPrice, tripRating, tripFav, tripType, tripStartYear, tripStartMonth
                    , tripStartDay, tripEndYear, tripEndMonth, tripEndDay);
            mtripList.set(RecyclerViewAdapter.pos, a);
            HomeFragment.adapter.notifyDataSetChanged();
            //get back
            getSupportFragmentManager().popBackStack();
        } else {
            Toast messToast = Toast.makeText(this, "Fiil in all the field", Toast.LENGTH_LONG);
            messToast.show();
        }


    }

    //Retrofit Functions
    void getUsers(GetDataRetrofit service) {
        getUsers = service.getUsers();
        getUsers.enqueue(new Callback<ArrayList<TripItemModel>>() {
            @Override
            public void onResponse(Call<ArrayList<TripItemModel>> call, Response<ArrayList<TripItemModel>> response) {
                if (response.isSuccessful()) {
                    ArrayList<TripItemModel> trips = response.body();
                    if (trips != null) {
                        Log.d("DEBUG", "Recived users");
                        for (TripItemModel trip : trips) {
                            Log.d("DEBUG", "user " + trip);

                            mtripList.add(trip);
                        }
                    } else {
                        Log.d("DEBUG", "No users");
                    }

                } else {
                    Log.d("DEBUG", "Failed to fetch");
                    Log.d("DEBUG", response.message());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<TripItemModel>> call, Throwable t) {
                Log.d("DEBUG", "Failed to reach server");
            }

        });
    }

    boolean isConnected(ConnectivityManager cm) {
        if (cm == null)
            return false;
        else {
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }
    }


}
