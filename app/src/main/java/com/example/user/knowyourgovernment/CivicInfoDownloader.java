package com.example.user.knowyourgovernment;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by user on 01-04-2018.
 */

public class CivicInfoDownloader extends AsyncTask<String, Void, String> {

    private static final String TAG = "CivicInfoDownloader";
    private static final String civicInfoURL = "https://www.googleapis.com/civicinfo/v2/representatives";
    private static final String apiKey = "AIzaSyAyanMxgrnlSX3BccvdJwijazR_8HPNt2g";
    MainActivity mainActivity;
    private ArrayList<GovtOfficial> officials_list;
    private static String locationText;
    private Object[] retData;

    public CivicInfoDownloader(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    protected String doInBackground(String... strings) {
        Log.d(TAG, "doInBackground: ");

        String searchString = strings[0];
        String jsonString = "";

        Uri.Builder buildUri = Uri.parse(civicInfoURL).buildUpon();

        buildUri.appendQueryParameter("key", apiKey);
        buildUri.appendQueryParameter("address", searchString);

        String urlToUse = buildUri.build().toString();
        Log.d(TAG, "doInBackground: Generated url is "+ urlToUse);

        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(urlToUse);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");

            InputStream inpStrm = conn.getInputStream();
            BufferedReader buf_reader = new BufferedReader(new InputStreamReader(inpStrm));

            String line;
            while((line = buf_reader.readLine()) != null)
                sb.append(line).append("\n");

            //Log.d(TAG, "doInBackground: The json string obtained from the internet is "+ sb.toString());
        }
        catch (FileNotFoundException e)
        {
            //Error code 400
            Log.d(TAG, "doInBackground: Exception while fetching the data through api");
            e.printStackTrace();
            return "";
        }
        catch (Exception e)
        {
            Log.d(TAG, "doInBackground: Exception while fetching the data through api");
            e.printStackTrace();
            return null;
        }

        jsonString = sb.toString();
        Log.d(TAG, "doInBackground: The json string obtained from the internet is "+ jsonString);
        return jsonString;
    }
    
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Log.d(TAG, "onPostExecute: ");

        boolean isParseSuccessful = parseJSONstring(s);
        if(isParseSuccessful) {
            retData = new Object[2];
            retData[0] = locationText;
            retData[1] = officials_list;
            mainActivity.setOfficialList(retData);
        }
        else
            mainActivity.setOfficialList(null);
    }

    private boolean parseJSONstring(String str) {
        if (str == null) {
            Toast.makeText(mainActivity, "Civic Info service is unavailable", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(str.equals("")) {
            Toast.makeText(mainActivity, "No data is available for specified location", Toast.LENGTH_SHORT).show();
            return false;
        }

        Log.d(TAG, "parseJSONstring: The String is: "+ str);
        officials_list = new ArrayList<>();

        int cnt = 0;

        try {
            JSONObject jObjMain = new JSONObject(str);
            JSONObject jObjNormalizedInp = jObjMain.getJSONObject("normalizedInput");

            String city = jObjNormalizedInp.getString("city");
            String state = jObjNormalizedInp.getString("state");
            String zip = jObjNormalizedInp.getString("zip");

            locationText = setLocationText(city, state, zip);

            JSONArray jOfficeArray = jObjMain.getJSONArray("offices");
            JSONArray jOfficialsArray = jObjMain.getJSONArray("officials");

            Log.d(TAG, "parseJSONstring: length= "+ jOfficialsArray.length());

            //if(jOfficeArray.length() == 0)
            //    Toast.makeText(mainActivity, "No data found for "+ city + " offices", Toast.LENGTH_SHORT).show();
            for (int i=0; i<jOfficeArray.length(); i++)
            {
                JSONObject jObj_office = (JSONObject) jOfficeArray.get(i);

                String office_name = jObj_office.getString("name");
                JSONArray jPersons = jObj_office.getJSONArray("officialIndices");

                //if(jPersons.length() == 0)
                //    Toast.makeText(mainActivity, "No data found for "+ city + " officials", Toast.LENGTH_SHORT).show();
                //Now get the list details of all persons
                for(int j=0; j<jPersons.length(); j++)
                {
                    int officialIndex = jPersons.getInt(j);

                    JSONObject jObj_person = (JSONObject) jOfficialsArray.get(officialIndex);
                    String per_name = jObj_person.getString("name");

                    String party = "Unknown";
                    if(jObj_person.has("party"))
                        party = jObj_person.getString("party");

                    //Now other fetch other details
                    String addr = "";
                    if(jObj_person.has("address")) {
                        JSONArray jAddr = jObj_person.getJSONArray("address");
                        for (int i_add = 0; i_add < jAddr.length(); i_add++) {
                            JSONObject j_add1 = (JSONObject) jAddr.get(i_add);

                            if (j_add1.has("line1"))
                                addr += j_add1.getString("line1") + ", ";
                            if (j_add1.has("line2"))
                                addr += j_add1.getString("line2") + ", ";
                            if (j_add1.has("line3"))
                                addr += j_add1.getString("line3") + "\n";

                            String ct = "", st = "", zp = "";
                            if (j_add1.has("city"))
                                ct = j_add1.getString("city");
                            if (j_add1.has("state"))
                                st = j_add1.getString("state");
                            if (j_add1.has("zip"))
                                zp = j_add1.getString("zip");

                            addr += setLocationText(ct, st, zp);

                            //Get only the first address only
                            break;
                        }
                    }

                    String phone = mainActivity.getString(R.string.no_data_provided);
                    if(jObj_person.has("phones"))
                    {
                        JSONArray jPh = jObj_person.getJSONArray("phones");
                        //Take only first phone number from the array
                        phone = jPh.getString(0);
                    }

                    String web_url = mainActivity.getString(R.string.no_data_provided);
                    if(jObj_person.has("urls"))
                    {
                        JSONArray jUrl = jObj_person.getJSONArray("urls");
                        web_url = jUrl.getString(0);
                    }

                    String email_url = mainActivity.getString(R.string.no_data_provided);
                    if(jObj_person.has("emails"))
                    {
                        JSONArray jEm = jObj_person.getJSONArray("emails");
                        email_url = jEm.getString(0);
                    }

                    String pic_url = "";
                    if(jObj_person.has("photoUrl"))
                    {
                        pic_url = jObj_person.getString("photoUrl");
                    }

                    String id_google = "";
                    String id_fb = "";
                    String id_twitter = "";
                    String id_youtube = "";
                    if(jObj_person.has("channels"))
                    {
                        JSONArray jChannel = jObj_person.getJSONArray("channels");
                        for(int i_ch=0; i_ch<jChannel.length(); i_ch++)
                        {
                            JSONObject jChObj = (JSONObject) jChannel.get(i_ch);
                            if(jChObj.has("type") && (jChObj.getString("type").equals("GooglePlus")))
                                id_google = jChObj.getString("id");
                            else if(jChObj.has("type") && (jChObj.getString("type").equals("Facebook")))
                                id_fb = jChObj.getString("id");
                            else if(jChObj.has("type") && (jChObj.getString("type").equals("Twitter")))
                                id_twitter = jChObj.getString("id");
                            else if(jChObj.has("type") && (jChObj.getString("type").equals("YouTube")))
                                id_youtube = jChObj.getString("id");
                        }
                    }

                    //Now create new Official object and save the data
                    GovtOfficial offObj = new GovtOfficial();
                    offObj.setLoc_city(city);
                    offObj.setLoc_state(state);
                    offObj.setLoc_zip(zip);

                    offObj.setOfficial_name(per_name);
                    offObj.setOffice(office_name);
                    offObj.setParty(party);

                    offObj.setAddress(addr);
                    offObj.setPhone_no(phone);
                    offObj.setWebsite_url(web_url);
                    offObj.setEmail_id(email_url);
                    offObj.setPhotoUrl(pic_url);

                    offObj.setChannel_googlePlus_id(id_google);
                    offObj.setChannel_facebook_id(id_fb);
                    offObj.setChannel_twitter_id(id_twitter);
                    offObj.setChannel_youtube_id(id_youtube);

                    officials_list.add(offObj);
                }
            }

        }
        catch (Exception e) {
            Log.d(TAG, "parseJSONstring: Error while parsing the JSON data");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private String setLocationText(String city, String state, String zip) {
        String setText = city;
        if(!setText.equals("") && !state.equals(""))
            setText += ", " + state + " " + zip;
        else
            setText += state + " " + zip;
        return setText;
    }
}





/*
    private boolean parseJSONstring(String str) {
        if (str == null) {
            Toast.makeText(mainActivity, "Civic Info service is unavailable", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(str.equals("")) {
            Toast.makeText(mainActivity, "No data is available for specified location", Toast.LENGTH_SHORT).show();
            return false;
        }

        Log.d(TAG, "parseJSONstring: The String is: "+ str);
        //GovtOfficial officials_list[] = new GovtOfficial[];
        int cnt = 0;

        try {
            JSONObject jObjMain = new JSONObject(str);
            JSONObject jObjNormalizedInp = jObjMain.getJSONObject("normalizedInput");

            String city = jObjNormalizedInp.getString("city");
            String state = jObjNormalizedInp.getString("state");
            String zip = jObjNormalizedInp.getString("zip");

            JSONArray jOfficeArray = jObjMain.getJSONArray("offices");
            JSONArray jOfficialsArray = jObjMain.getJSONArray("officials");

            officials_list = new GovtOfficial[jOfficialsArray.length()];
            Log.d(TAG, "parseJSONstring: length= "+ jOfficialsArray.length());

            //if(jOfficeArray.length() == 0)
            //    Toast.makeText(mainActivity, "No data found for "+ city + " offices", Toast.LENGTH_SHORT).show();
            for (int i=0; i<jOfficeArray.length(); i++)
            {
                JSONObject jObj_office = (JSONObject) jOfficeArray.get(i);

                String office_name = jObj_office.getString("name");
                JSONArray jPersons = jObj_office.getJSONArray("officialIndices");

                //if(jPersons.length() == 0)
                //    Toast.makeText(mainActivity, "No data found for "+ city + " officials", Toast.LENGTH_SHORT).show();
                //Now get the list details of all persons
                for(int j=0; j<jPersons.length(); j++)
                {
                    int officialIndex = jPersons.getInt(j);

                    JSONObject jObj_person = (JSONObject) jOfficialsArray.get(officialIndex);
                    String per_name = jObj_person.getString("name");

                    String party = "Unknown";
                    if(jObj_person.has("party"))
                        party = jObj_person.getString("party");

                    //Now other fetch other details
                    String addr = "";
                    if(jObj_person.has("address")) {
                        JSONArray jAddr = jObj_person.getJSONArray("address");
                        for (int i_add = 0; i_add < jAddr.length(); i_add++) {
                            JSONObject j_add1 = (JSONObject) jAddr.get(i_add);

                            if (j_add1.has("line1"))
                                addr += j_add1.getString("line1") + ", ";
                            if (j_add1.has("line2"))
                                addr += j_add1.getString("line2") + ", ";
                            if (j_add1.has("line3"))
                                addr += j_add1.getString("line3") + "\n";

                            String ct = "", st = "", zp = "";
                            if (j_add1.has("city"))
                                ct = j_add1.getString("city");
                            //addr += j_add1.getString("city") + ", ";
                            if (j_add1.has("state"))
                                st = j_add1.getString("state");
                            //addr += j_add1.getString("state") + " ";
                            if (j_add1.has("zip"))
                                zp = j_add1.getString("zip");
                            //addr += j_add1.getString("zip");

                            addr += setLocationText(ct, st, zp);

                            //Get only the first address only
                            break;
                        }
                    }

                    String phone = mainActivity.getString(R.string.no_data_provided);
                    if(jObj_person.has("phones"))
                    {
                        JSONArray jPh = jObj_person.getJSONArray("phones");
                        //Take only first phone number from the array
                        phone = jPh.getString(0);
                    }

                    String web_url = mainActivity.getString(R.string.no_data_provided);
                    if(jObj_person.has("urls"))
                    {
                        JSONArray jUrl = jObj_person.getJSONArray("urls");
                        web_url = jUrl.getString(0);
                    }

                    String email_url = mainActivity.getString(R.string.no_data_provided);
                    if(jObj_person.has("emails"))
                    {
                        JSONArray jEm = jObj_person.getJSONArray("emails");
                        email_url = jEm.getString(0);
                    }

                    String pic_url = "";
                    if(jObj_person.has("photoUrl"))
                    {
                        pic_url = jObj_person.getString("photoUrl");
                    }

                    String id_google = "";
                    String id_fb = "";
                    String id_twitter = "";
                    String id_youtube = "";
                    if(jObj_person.has("channels"))
                    {
                        JSONArray jChannel = jObj_person.getJSONArray("channels");
                        for(int i_ch=0; i_ch<jChannel.length(); i_ch++)
                        {
                            JSONObject jChObj = (JSONObject) jChannel.get(i_ch);
                            if(jChObj.has("type") && (jChObj.getString("type").equals("GooglePlus")))
                                id_google = jChObj.getString("id");
                            else if(jChObj.has("type") && (jChObj.getString("type").equals("Facebook")))
                                id_fb = jChObj.getString("id");
                            else if(jChObj.has("type") && (jChObj.getString("type").equals("Twitter")))
                                id_twitter = jChObj.getString("id");
                            else if(jChObj.has("type") && (jChObj.getString("type").equals("YouTube")))
                                id_youtube = jChObj.getString("id");
                        }
                    }

                    //Now create new Official object and save the data
                    officials_list[cnt] = new GovtOfficial();

                    officials_list[cnt].setLoc_city(city);
                    officials_list[cnt].setLoc_state(state);
                    officials_list[cnt].setLoc_zip(zip);

                    officials_list[cnt].setOfficial_name(per_name);
                    officials_list[cnt].setOffice(office_name);
                    officials_list[cnt].setParty(party);

                    officials_list[cnt].setAddress(addr);
                    officials_list[cnt].setPhone_no(phone);
                    officials_list[cnt].setWebsite_url(web_url);
                    officials_list[cnt].setEmail_id(email_url);
                    officials_list[cnt].setPhotoUrl(pic_url);

                    officials_list[cnt].setChannel_googlePlus_id(id_google);
                    officials_list[cnt].setChannel_facebook_id(id_fb);
                    officials_list[cnt].setChannel_twitter_id(id_twitter);
                    officials_list[cnt].setChannel_youtube_id(id_youtube);

                    cnt++;
                }
            }

        }
        catch (Exception e) {
            Log.d(TAG, "parseJSONstring: Error while parsing the JSON data");
            e.printStackTrace();
            return false;
        }
        return true;
    }*/