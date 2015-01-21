package com.shaheed.codewarior.checkboxdevelopers;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class AccountActivity extends ActionBarActivity {

    private static final String VOLLEYTAG = "LoginOrRegistration";

    private SessionManager sessionManager;

    private EditText account_name,account_email,account_address,account_phone;

    private ProgressDialog progressDialog;

    private Activity currentActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        currentActivity = this;
        sessionManager = new SessionManager(getApplicationContext());

        if(sessionManager.checkLogin()) {
            finish();
        }

        Constants.userId = sessionManager.getUserId();
        progressDialog = new ProgressDialog(this);

        findViewsById();
        implementButtons();

        fetchUserData();
    }

    private void fetchUserData() {

        Constants.showProgressDialogue(progressDialog, "Logging In", "Please wait while we check...");
        progressDialog.setCancelable(false);

        String id = Constants.userId;

        HashMap<String, String> userInfo = new HashMap<>();
        userInfo.put("userid", id);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,Constants.URL_FETCH, new JSONObject(userInfo), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Constants.closeProgressDialogue(progressDialog);
                String reply;
                String replyMsg = "";
                Boolean isDone = false;
                try{
                    reply = jsonObject.getString("reply");

                    if(reply.equals("done")){
                        isDone = true;
                    }else{
                        replyMsg = reply;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                gotUserFetchResponse(isDone, replyMsg, jsonObject);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Constants.closeProgressDialogue(progressDialog);
                Constants.makeToast(currentActivity, "Network Error\\nCould not Retrieve user information", true);
                Log.e("Volley Error", volleyError.toString());
            }
        });
        VolleyController.getInstance().addNewToRequestQueue(jsonObjectRequest,VOLLEYTAG);

    }

    private void gotUserFetchResponse(Boolean isDone, String replyMsg,JSONObject fetchedData) {
        if(isDone){
            try{
                account_name.setText(fetchedData.getString("username"));
                account_email.setText(fetchedData.getString("useremail"));
                account_address.setText(fetchedData.getString("useraddress"));
                account_phone.setText(fetchedData.getString("userphone"));
            }catch (JSONException e){
                Log.e("JSON Exception", e.getMessage());
                Constants.makeToast(this,"Error Occurred in fetching data", true);
            }
        }else {
            Constants.makeToast(this,replyMsg, true);
        }
    }

    private void findViewsById(){
        account_name = (EditText) findViewById(R.id.account_editText_name);
        account_email = (EditText) findViewById(R.id.account_editText_email);
        account_address = (EditText) findViewById(R.id.account_editText_address);
        account_phone = (EditText) findViewById(R.id.account_editText_phonenumber);
    }

    private void implementButtons() {

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_account, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_account_logout :
                sessionManager.logoutUser();
                finish();
                return true;
            case R.id.action_account_edit :
                Intent in = new Intent(this, AccountEditActivity.class);
                in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(in);
        }

        return super.onOptionsItemSelected(item);
    }
}
