package com.example.user.knowyourgovernment;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private static final String TAG = "MainActivity";
    private static final int MY_PERM_REQUEST_CODE = 1111;
    private String setLocationText = "";
    private TextView tv_loc;

    private ArrayList<GovtOfficial> list_govt_officials = new ArrayList<>();
    private RecyclerView recyclerView;
    private GovtOfficialsAdapter govtOfficialsAdapter;
    private Locator locator;
    private ArrayList<GovtOfficial> officials_arr = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate: ");
        tv_loc = (TextView) findViewById(R.id.tv_location);

        recyclerView = (RecyclerView) findViewById(R.id.rv_list_govt);
        govtOfficialsAdapter = new GovtOfficialsAdapter(this, list_govt_officials);
        recyclerView.setAdapter(govtOfficialsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if(!amConnected())
        {
            tv_loc.setText(getString(R.string.no_data_for_loc));
            //create an alert dialog
            createAckDialog(getString(R.string.no_network), getString(R.string.no_netwrok_msg));
        }
        else {
            //The application continues after asking the permission to user and does not wait for the response.
            //So initially the checkPermission returns false.
            locator = new Locator(this);
        }
    }

    public void noLocationAvailable() {
        Toast.makeText(this, "No location providers are available!!", Toast.LENGTH_SHORT).show();
    }

    //Get the location - city, zip from lat/long
    public void doLocationWork(double latitude, double longitude)
    {
        Log.d(TAG, "doLocationWork: Latitude = " + latitude + ", Longitude = " + longitude);
        List<Address> addresses = null;

        //Create the geocoder object to fetch the zip code
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            Log.d(TAG, "doLocationWork: Fetching the address");
            addresses = geocoder.getFromLocation(latitude, longitude, 1);

            String zip = addresses.get(0).getPostalCode();
            setLocationText = setLocationTitleText(addresses.get(0).getLocality(), addresses.get(0).getAdminArea(), addresses.get(0).getPostalCode());

            doAsyncTask(zip);
        }
        catch (Exception e) {
            Log.d(TAG, "doLocationWork: " + e.getMessage());
            Toast.makeText(this, "No location can be found from specified latitude/longitude!", Toast.LENGTH_SHORT).show();
        }
    }

    //This method is automatically called when the user allow or denys any permission request made by the app.
    //If there are multiple user requests in the app then it can be distinguished by requestCode
    //permissions[] is an array of requested permissions in the app
    //grantResults[] is an array of whether the permissions are granted or not corresponding to the request made in permissions[]
    //Here in this app we have only one permission ACCESS_FINE_LOCATION
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: ");

        if(requestCode == MY_PERM_REQUEST_CODE)
        {
            //Check this to ensure proper behaviour
            if(grantResults.length == 0) {
                Log.d(TAG, "onRequestPermissionsResult: Somehow got an empty grantResults array");
                return;
            }
            for(int i=0; i<permissions.length; i++) {
                if (permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        locator.setUpLocationManager();
                        locator.determineLocation();
                        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                    } else
                        Toast.makeText(this, "Permission denied! Cannot determine the address", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void doAsyncTask(String zipCode)
    {
        setLocationText = zipCode;

        //Get the data from API
        CivicInfoDownloader asyncTask = new CivicInfoDownloader(this);
        asyncTask.execute(zipCode);
    }

    public void setOfficialList(Object[] resultData)
    {
        Log.d(TAG, "setOfficialList: ");
        if(resultData == null) {

            tv_loc.setText(getString(R.string.no_data_for_loc));
            list_govt_officials.clear();
        }
        else
        {
            list_govt_officials.clear();
            officials_arr.clear();
            officials_arr.addAll((ArrayList<GovtOfficial>)resultData[1]);
            list_govt_officials.addAll((ArrayList)resultData[1]);

            //Set the location in title
            tv_loc.setText(resultData[0].toString());
        }
        govtOfficialsAdapter.notifyDataSetChanged();
    }

    private String setLocationTitleText(String city, String state, String zip) {
        String setText = city;
        if(!setText.equals("") && !state.equals(""))
            setText += ", " + state + " " + zip;
        else
            setText += state + " " + zip;
        return setText;
    }

    public boolean amConnected()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if(networkInfo != null && networkInfo.isConnectedOrConnecting())
            return true;
        else
            return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_menu_home_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId())
        {
            case R.id.menu_add:

                //Create a new dialog to add area
                AlertDialog.Builder diag_builder = new AlertDialog.Builder(this);

                final EditText ed = new EditText(this);
                ed.setInputType(InputType.TYPE_CLASS_TEXT);
                ed.setGravity(Gravity.CENTER_HORIZONTAL);
                ed.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
                diag_builder.setView(ed);

                diag_builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if(amConnected())
                        {
                            //Search the location according to the specified string
                            String searchKey = ed.getText().toString();
                            doAsyncTask(searchKey);
                        }
                        else
                        {
                            //create an alert dialog
                            createAckDialog(getString(R.string.no_network), getString(R.string.no_netwrok_msg));
                        }
                    }
                });
                diag_builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Cancelled, do nothing
                    }
                });

                diag_builder.setMessage(R.string.add_msg);

                AlertDialog dialog = diag_builder.create();
                dialog.show();
                break;

            case R.id.menu_about:

                Intent it = new Intent(this, AboutActivity.class);
                startActivity(it);
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public void createAckDialog(String title, String msg)
    {
        AlertDialog.Builder diag_builder = new AlertDialog.Builder(this);
        diag_builder.setMessage(msg);
        diag_builder.setTitle(title);
        AlertDialog dialog = diag_builder.create();
        dialog.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: ");

        //When first the list is empty then check the connection and populate the list
        //else keep the same list
        if(amConnected()) {
            if(locator == null) {
                locator = new Locator(this);
            }
        }
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        int pos = recyclerView.getChildLayoutPosition(view);

        Intent intent = new Intent(this, PersonActivity.class);
        intent.putExtra("title", tv_loc.getText());
        intent.putExtra("officialObj", officials_arr.get(pos));
        startActivity(intent);
    }

    @Override
    public boolean onLongClick(View view) {
        onClick(view);
        return true;
    }
}
