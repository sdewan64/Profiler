package com.shaheed.codewarior.checkboxdevelopers;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;


public class AccountActivity extends ActionBarActivity {

    private static final String VOLLEYTAG = "Account";

    private SessionManager sessionManager;

    private EditText account_name,account_email,account_address,account_phone,account_oldPassword,account_newPassword,account_newConfirmPassword;
    private TextView account_textoldPassword,account_textNewPassword,account_textConfirmNewPassword;
    private Button account_editButton;

    private ProgressDialog progressDialog;

    private Activity currentActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        getSupportActionBar().setIcon(R.drawable.logo2);
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

        Constants.showProgressDialogue(progressDialog, "Fetching Data", "Please wait while we fetch...");
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
                Constants.makeToast(currentActivity, "Network Error\nCould not Retrieve user information", true);
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
        account_oldPassword = (EditText) findViewById(R.id.account_editText_oldPassword);
        account_newPassword = (EditText) findViewById(R.id.account_editText_newPassword);
        account_newConfirmPassword = (EditText) findViewById(R.id.account_editText_newConfirmPassword);

        account_textoldPassword = (TextView) findViewById(R.id.account_texview_oldPassword);
        account_textNewPassword = (TextView) findViewById(R.id.account_texview_newPassword);
        account_textConfirmNewPassword = (TextView) findViewById(R.id.account_texview_newConfrimPassword);

        account_editButton = (Button) findViewById(R.id.account_button_edit);

    }

    private void implementButtons() {
        account_editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(account_name.getText().toString().equals("") || account_email.getText().toString().equals("") || account_address.getText().toString().equals("") || account_phone.getText().toString().equals("") || account_oldPassword.getText().toString().equals("") || account_newPassword.getText().toString().equals("") || account_textConfirmNewPassword.getText().toString().equals("")){
                    Constants.makeToast(currentActivity, "All fields required!", true);
                }else{
                    if(!account_newPassword.getText().toString().equals(account_newConfirmPassword.getText().toString())){
                        Constants.makeToast(currentActivity, "New password and confirm password didn't match!", true);
                    }else {
                        Constants.showProgressDialogue(progressDialog,"Updating User","Please wait while we update your information");
                        progressDialog.setCancelable(false);

                        String fullName = account_name.getText().toString();
                        String email = account_email.getText().toString();
                        String oldPassword = account_oldPassword.getText().toString();
                        String newPassword = account_newPassword.getText().toString();
                        String address = account_address.getText().toString();
                        String phone = account_phone.getText().toString();

                        String oldPasswordHash = null;

                        try {
                            MessageDigest md = MessageDigest.getInstance("SHA-1");
                            md.update(oldPassword.getBytes());
                            byte[] bytes = md.digest();
                            StringBuilder sb = new StringBuilder();
                            for (byte aByte : bytes) {
                                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
                            }
                            oldPasswordHash = sb.toString();
                        }
                        catch (NoSuchAlgorithmException e)
                        {
                            Log.e("HASH_ERROR","HASHING FAILED!");
                        }

                        String newPasswordHash = null;

                        try {
                            MessageDigest md = MessageDigest.getInstance("SHA-1");
                            md.update(newPassword.getBytes());
                            byte[] bytes = md.digest();
                            StringBuilder sb = new StringBuilder();
                            for (byte aByte : bytes) {
                                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
                            }
                            newPasswordHash = sb.toString();
                        }
                        catch (NoSuchAlgorithmException e)
                        {
                            Log.e("HASH_ERROR","HASHING FAILED!");
                        }

                        HashMap<String, String> updateInfo = new HashMap<>();
                        updateInfo.put("userid", Constants.userId);
                        updateInfo.put("fullName", fullName);
                        updateInfo.put("email", email);
                        updateInfo.put("oldpassword", oldPasswordHash);
                        updateInfo.put("newpassword", newPasswordHash);
                        updateInfo.put("address", address);
                        updateInfo.put("phone", phone);

                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constants.URL_UPDATE, new JSONObject(updateInfo), new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject jsonObject) {
                                Constants.closeProgressDialogue(progressDialog);
                                Boolean isDone = false;
                                String reply = null;

                                try{
                                    reply = jsonObject.getString("reply");

                                    isDone = reply.equals("done");
                                } catch (JSONException e) {
                                    Log.e("JSON_ERROR","Could not retrieve return data");
                                }
                                gotUpdateResponse(isDone,reply);
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                Constants.closeProgressDialogue(progressDialog);
                                Constants.makeToast(currentActivity,"Network Error",true);
                                Log.e("Volley Error", volleyError.toString());
                            }
                        });

                        VolleyController.getInstance().addNewToRequestQueue(jsonObjectRequest, VOLLEYTAG);
                    }
                }

            }
        });
    }

    private void gotUpdateResponse(Boolean isDone, String replyMsg) {
        if(isDone){
            Constants.makeToast(currentActivity,"Profile update was successful", false);
            Intent in = new Intent(this, AccountActivity.class);
            in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(in);
            finish();
        }else {
            Constants.makeToast(currentActivity,replyMsg, true);
        }
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
                getEditView();
                return true;
            case R.id.action_account_share :
                Intent in = new Intent(this, SocialSignUpActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("isShare","true");
                in.putExtras(bundle);
                in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(in);
                return  true;
        }
        return super.onOptionsItemSelected(item);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void getEditTextBackground(){
        account_oldPassword.setBackground(getResources().getDrawable(R.drawable.abc_edit_text_material));
        account_newPassword.setBackground(getResources().getDrawable(R.drawable.abc_edit_text_material));
        account_newConfirmPassword.setBackground(getResources().getDrawable(R.drawable.abc_edit_text_material));
        account_name.setBackground(getResources().getDrawable(R.drawable.abc_edit_text_material));
        account_email.setBackground(getResources().getDrawable(R.drawable.abc_edit_text_material));
        account_address.setBackground(getResources().getDrawable(R.drawable.abc_edit_text_material));
        account_phone.setBackground(getResources().getDrawable(R.drawable.abc_edit_text_material));
        account_name.setBackground(getResources().getDrawable(R.drawable.abc_edit_text_material));
        account_name.setBackground(getResources().getDrawable(R.drawable.abc_edit_text_material));
        account_name.setBackground(getResources().getDrawable(R.drawable.abc_edit_text_material));
    }

    private void getEditView() {
        account_oldPassword.setVisibility(View.VISIBLE);
        account_oldPassword.setFocusableInTouchMode(true);
        account_oldPassword.setClickable(true);

        account_newPassword.setVisibility(View.VISIBLE);
        account_newPassword.setFocusableInTouchMode(true);
        account_newPassword.setClickable(true);

        account_newConfirmPassword.setVisibility(View.VISIBLE);
        account_newConfirmPassword.setFocusableInTouchMode(true);
        account_newConfirmPassword.setClickable(true);


        account_textoldPassword.setVisibility(View.VISIBLE);
        account_textNewPassword.setVisibility(View.VISIBLE);
        account_textConfirmNewPassword.setVisibility(View.VISIBLE);

        account_editButton.setVisibility(View.VISIBLE);

        account_name.setFocusable(true);
        account_name.setFocusableInTouchMode(true);
        account_name.setClickable(true);


        account_email.setFocusable(true);
        account_email.setFocusableInTouchMode(true);
        account_email.setClickable(true);

        account_address.setFocusable(true);
        account_address.setFocusableInTouchMode(true);
        account_address.setClickable(true);

        account_phone.setFocusable(true);
        account_phone.setFocusableInTouchMode(true);
        account_phone.setClickable(true);

        account_name.setFocusable(true);
        account_name.setFocusableInTouchMode(true);
        account_name.setClickable(true);

        account_name.setFocusable(true);
        account_name.setFocusableInTouchMode(true);
        account_name.setClickable(true);

        account_name.setFocusable(true);
        account_name.setFocusableInTouchMode(true);
        account_name.setClickable(true);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) getEditTextBackground();
    }
}
