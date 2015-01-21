package com.shaheed.codewarior.checkboxdevelopers;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

/**
 * Created by Shaheed on 1/14/2015.
 * Shaheed Ahmed Dewan Sagar
 * Ahsanullah University of Science & Technology
 * Email : sdewan64@gmail.com
 */
public class MenuFragment extends Fragment implements View.OnClickListener{

    private static final String VOLLEYTAG = "LoginOrRegistration";
    Button registration_signUpButton,registration_socialSignupButton,login_loginButton;
    EditText registration_fullName,registration_password,registration_confirmPassword,registration_email,registration_address,registration_phone,login_email,login_password;
    Fragment currentFragment;

    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        String fragmentId = getArguments().getString("FragmentId");
        View view = inflater.inflate(Integer.parseInt(fragmentId), container, false);
        findViewsById(view);
        addClickListeners();
        progressDialog = new ProgressDialog(getActivity());
        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i("VOLLEY","Volley cancel all called");
        VolleyController.getInstance().cancelAllRequest(VOLLEYTAG);
    }

    private void findViewsById(View view) {
        registration_signUpButton = (Button) view.findViewById(R.id.registration_button_signup);
        login_loginButton = (Button) view.findViewById(R.id.login_button_login);
        registration_socialSignupButton = (Button) view.findViewById(R.id.registration_button_socialSignup);

        registration_fullName = (EditText) view.findViewById(R.id.registration_edittext_fullName);
        registration_password = (EditText) view.findViewById(R.id.registration_edittext_password);
        registration_confirmPassword = (EditText) view.findViewById(R.id.registration_edittext_confirmPassword);
        registration_email = (EditText) view.findViewById(R.id.registration_edittext_email);
        registration_address = (EditText) view.findViewById(R.id.registration_edittext_address);
        registration_phone = (EditText) view.findViewById(R.id.registration_edittext_phone);

        login_email = (EditText) view.findViewById(R.id.login_edittext_email);
        login_password = (EditText) view.findViewById(R.id.login_edittext_password);
    }

    private void addClickListeners() {
        if(registration_signUpButton!=null) registration_signUpButton.setOnClickListener(this);
        if(login_loginButton!=null) login_loginButton.setOnClickListener(this);
        if(registration_socialSignupButton!=null) registration_socialSignupButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        currentFragment = this;

        switch (v.getId()){
            case R.id.registration_button_signup: registration_signUpButtonClicked();
                break;
            case R.id.login_button_login: login_loginButtonClicked();
                break;
            case R.id.registration_button_socialSignup:
                Intent in = new Intent(currentFragment.getActivity(), SocialSignUpActivity.class);
                in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(in);
                break;
        }
    }

    private void registration_signUpButtonClicked(){
       if(registration_fullName.getText().toString().equals("") || registration_password.getText().toString().equals("") || registration_confirmPassword.getText().toString().equals("") || registration_email.getText().toString().equals("") || registration_phone.getText().toString().equals("")){
           Constants.makeToast(this.getActivity(), "All fields are required!", true);
       }else{
           if(registration_password.getText().toString().equals(registration_confirmPassword.getText().toString())){
               Constants.showProgressDialogue(progressDialog,"Registering User","Please wait while we register your information");
               progressDialog.setCancelable(false);

               String fullName = registration_fullName.getText().toString();
               String email = registration_email.getText().toString();
               String password = registration_password.getText().toString();
               String address = registration_address.getText().toString();
               String phone = registration_phone.getText().toString();

               String passwordHash = null;

               try {
                   MessageDigest md = MessageDigest.getInstance("SHA-1");
                   md.update(password.getBytes());
                   byte[] bytes = md.digest();
                   StringBuilder sb = new StringBuilder();
                   for (byte aByte : bytes) {
                       sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
                   }
                   passwordHash = sb.toString();
               }
               catch (NoSuchAlgorithmException e)
               {
                   Log.e("HASH_ERROR","HASHING FAILED!");
               }

               HashMap<String, String> registrationInfo = new HashMap<>();
               registrationInfo.put("fullName", fullName);
               registrationInfo.put("email", email);
               registrationInfo.put("password", passwordHash);
               registrationInfo.put("address", address);
               registrationInfo.put("phone", phone);

               JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constants.URL_REGISTRATION, new JSONObject(registrationInfo), new Response.Listener<JSONObject>() {
                   @Override
                   public void onResponse(JSONObject jsonObject) {
                       Constants.closeProgressDialogue(progressDialog);
                       Boolean isDone = false;
                       String reply;

                       try{
                           reply = jsonObject.getString("reply");

                           if(reply.equals("done")){
                               isDone = true;
                           }
                       } catch (JSONException e) {
                           Log.e("JSON_ERROR","Could not retrieve return data");
                       }
                       gotRegistrationResponse(isDone);
                   }
               }, new Response.ErrorListener() {
                   @Override
                   public void onErrorResponse(VolleyError volleyError) {
                       Constants.closeProgressDialogue(progressDialog);
                       Constants.makeToast(currentFragment.getActivity(),"Network Error",true);
                       Log.e("Volley Error", volleyError.toString());
                   }
               });

               VolleyController.getInstance().addNewToRequestQueue(jsonObjectRequest, VOLLEYTAG);

           }else{
               Constants.makeToast(this.getActivity(), "Password and Confirm Password did not match!", true);
           }
       }
    }

    private void gotRegistrationResponse(Boolean isDone){
        if(isDone) {
            Constants.makeToast(currentFragment.getActivity(), "Registration was successful.\nYou can login now", false);
        }
        else{
            Constants.makeToast(currentFragment.getActivity(), "Registration was unsuccessful!", true);
        }
    }

    private void login_loginButtonClicked() {
        if(login_email.getText().toString().equals("") || login_password.getText().toString().equals("")){
            Constants.makeToast(this.getActivity(), "All fields are required!", true);
        }else {
            Constants.showProgressDialogue(progressDialog,"Logging In","Please wait while we check...");
            progressDialog.setCancelable(false);

            String email = login_email.getText().toString();
            String password = login_password.getText().toString();

            String passwordHash = null;

            try {
                MessageDigest md = MessageDigest.getInstance("SHA-1");
                md.update(password.getBytes());
                byte[] bytes = md.digest();
                StringBuilder sb = new StringBuilder();
                for (byte aByte : bytes) {
                    sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
                }
                passwordHash = sb.toString();
            }
            catch (NoSuchAlgorithmException e)
            {
                Log.e("HASH_ERROR","HASHING FAILED!");
            }

            HashMap<String, String> loginInfo = new HashMap<>();
            loginInfo.put("email", email);
            loginInfo.put("password", passwordHash);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,Constants.URL_LOGIN, new JSONObject(loginInfo), new Response.Listener<JSONObject>() {
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
                            Constants.userId = jsonObject.getString("userid");
                        }else{
                            replyMsg = reply;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    gotLoginResponse(isDone, replyMsg);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Constants.closeProgressDialogue(progressDialog);
                    Constants.makeToast(currentFragment.getActivity(),"Network Error",true);
                    Log.e("Volley Error", volleyError.toString());
                }
            });
            VolleyController.getInstance().addNewToRequestQueue(jsonObjectRequest,VOLLEYTAG);
        }
    }

    private void gotLoginResponse(Boolean isDone, String replyMsg){
        if(isDone){
            //user found making session and redirecting to account menu

            SessionManager sessionManager = new SessionManager(getActivity().getApplicationContext());
            sessionManager.createNewLoginSession(Constants.userId);

            Constants.makeToast(currentFragment.getActivity(),"Login Successful.\nRedirecting to Account Page...",false);
            Intent in = new Intent(currentFragment.getActivity(), AccountActivity.class);
            in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(in);
            getActivity().finish();
        }else{
            //setting the statusText as the error replied from server
            Constants.makeToast(currentFragment.getActivity(), replyMsg, true);
        }
    }
}
