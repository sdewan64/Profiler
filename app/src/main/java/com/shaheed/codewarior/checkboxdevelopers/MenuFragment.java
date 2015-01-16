package com.shaheed.codewarior.checkboxdevelopers;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shaheed on 1/14/2015.
 */
public class MenuFragment extends Fragment implements View.OnClickListener{

    Button registration_signUpButton,login_loginButton;
    EditText registration_fullName,registration_password,registration_confirmPassword,registration_email,registration_phone,login_email,login_password;
    Fragment currentFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        String fragmentId = getArguments().getString("FragmentId");
        View view = inflater.inflate(Integer.parseInt(fragmentId), container, false);
        findViewsById(view);
        addClickListeners();
        return view;
    }


    private void findViewsById(View view) {
        registration_signUpButton = (Button) view.findViewById(R.id.registration_button_signup);
        login_loginButton = (Button) view.findViewById(R.id.login_button_login);

        registration_fullName = (EditText) view.findViewById(R.id.registration_edittext_fullName);
        registration_password = (EditText) view.findViewById(R.id.registration_edittext_password);
        registration_confirmPassword = (EditText) view.findViewById(R.id.registration_edittext_confirmPassword);
        registration_email = (EditText) view.findViewById(R.id.registration_edittext_email);
        registration_phone = (EditText) view.findViewById(R.id.registration_edittext_phone);

        login_email = (EditText) view.findViewById(R.id.login_edittext_email);
        login_password = (EditText) view.findViewById(R.id.login_edittext_password);
    }

    private void addClickListeners() {
        if(registration_signUpButton!=null) registration_signUpButton.setOnClickListener(this);
        if(login_loginButton!=null) login_loginButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        currentFragment = this;

        switch (v.getId()){
            case R.id.registration_button_signup: registration_signUpButtonClicked();
                break;
            case R.id.login_button_login: login_loginButtonClicked();
                break;
        }
    }

    private void registration_signUpButtonClicked(){
       if(registration_fullName.getText().toString().equals("") || registration_password.getText().toString().equals("") || registration_confirmPassword.getText().toString().equals("") || registration_email.getText().toString().equals("") || registration_phone.getText().toString().equals("")){
           Constants.makeToast(this, "All fields are required!", true);
       }else{
           if(registration_password.getText().toString().equals(registration_confirmPassword.getText().toString())){
               new RegisterUserToDatabase().execute(registration_fullName.getText().toString(),registration_email.getText().toString(),registration_password.getText().toString(),registration_phone.getText().toString());
           }else{
               Constants.makeToast(this, "Password and Confirm Password did not match!", true);
           }
       }
    }

    private void login_loginButtonClicked() {
        if(login_email.getText().toString().equals("") || login_password.getText().toString().equals("")){
            Constants.makeToast(this, "All fields are required!", true);
        }else {
            new LoginUser().execute(login_email.getText().toString(),login_password.getText().toString());
        }
    }

    class RegisterUserToDatabase extends AsyncTask<String, String, String>{

        private boolean isDone = false;
        private JSONObject jsonObject;
        private JsonParser jsonParser = new JsonParser();

        @Override
        protected String doInBackground(String... strings) {
            String fullName = strings[0];
            String email = strings[1];
            String password = strings[2];
            String phone = strings[3];

            String passwordHash = null;

            try {
                MessageDigest md = MessageDigest.getInstance("SHA-1");
                md.update(password.getBytes());
                byte[] bytes = md.digest();
                StringBuilder sb = new StringBuilder();
                for(int i=0; i< bytes.length ;i++)
                {
                    sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
                }
                passwordHash = sb.toString();
            }
            catch (NoSuchAlgorithmException e)
            {
                Log.e("HASH_ERROR","HASHING FAILED!");
            }

            List<NameValuePair> registrationInfo = new ArrayList<>();
            registrationInfo.add(new BasicNameValuePair("name", fullName));
            registrationInfo.add(new BasicNameValuePair("email", email));
            registrationInfo.add(new BasicNameValuePair("password", passwordHash));
            registrationInfo.add(new BasicNameValuePair("phone", phone));

            jsonObject = jsonParser.makeHTTPRequest(Constants.URL_REGISTRATION,Constants.METHOD_POST,registrationInfo);

            String reply;

            try{
                reply = jsonObject.getString("reply");

                if(reply.equals("done")){
                    isDone = true;
                }
            } catch (JSONException e) {
                Log.e("JSON_ERROR","Could not retrieve return data");
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(isDone) {
                Constants.makeToast(currentFragment, "Registration was successful.\nYou can login now", false);
            }
            else{
                Constants.makeToast(currentFragment, "Registration was unsuccessful!", true);
            }
        }
    }

    private class LoginUser extends AsyncTask<String, String, String>{
        private boolean isDone = false;
        private JSONObject jsonObject;
        private JsonParser jsonParser = new JsonParser();
        String replyMsg;

        @Override
        protected String doInBackground(String... strings) {

            String email = strings[0];
            String password = strings[1];

            String passwordHash = null;

            try {
                MessageDigest md = MessageDigest.getInstance("SHA-1");
                md.update(password.getBytes());
                byte[] bytes = md.digest();
                StringBuilder sb = new StringBuilder();
                for(int i=0; i< bytes.length ;i++)
                {
                    sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
                }
                passwordHash = sb.toString();
            }
            catch (NoSuchAlgorithmException e)
            {
                Log.e("HASH_ERROR","HASHING FAILED!");
            }

            List<NameValuePair> loginInfo = new ArrayList<>();

            loginInfo.add(new BasicNameValuePair("email", email));
            loginInfo.add(new BasicNameValuePair("password", passwordHash));

            jsonObject = jsonParser.makeHTTPRequest(Constants.URL_LOGIN,Constants.METHOD_POST,loginInfo);
            String reply;

            try{
                reply = jsonObject.getString("reply");

                if(reply.equals("done")){
                    isDone = true;
                    Constants.userName = jsonObject.getString("username");
                }else{
                    replyMsg = reply;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(isDone){
                //user found redirect to account menu
                Constants.makeToast(currentFragment,"Login Successful.\nRedirecting to Account Page...",false);
                Intent in = new Intent(currentFragment.getActivity(), AccountActivity.class);
                in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(in);
                getActivity().finish();
            }else{
                //setting the statusText as the error replied from server
                Constants.makeToast(currentFragment, replyMsg, true);
            }

        }
    }
}
