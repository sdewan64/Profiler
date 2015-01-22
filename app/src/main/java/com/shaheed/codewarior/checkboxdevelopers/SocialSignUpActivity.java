package com.shaheed.codewarior.checkboxdevelopers;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

import java.security.MessageDigest;
import java.util.ArrayList;

public class SocialSignUpActivity extends Activity
{
    private Activity activity;
    private Button shareButton;
    private UiLifecycleHelper uihelper;

    private EditText social_message,social_link;

    void showMsg(String msg)
    {
        Constants.makeToast(activity, msg, true);
    }


    private Session.StatusCallback callback =new Session.StatusCallback()
    {

        @Override
        public void call(Session session, SessionState state, Exception exception)
        {
            onSessionStateChange(session,state,exception);
        }
    };


    void onSessionStateChange(Session session, SessionState state, Exception exception)
    {
        if (state.isOpened())
        {
            Log.i("facebook", "Logged in...");
            Request.newMeRequest(session, new Request.GraphUserCallback()
            {

                @Override
                public void onCompleted(GraphUser user, Response response)
                {
                    if(user!=null)
                    {
                        if(getIntent().getExtras().getString("isShare").equals("false")){
                            Constants.makeToast(activity,"Found your information.\nPlease wait while we redirect you to registration page.",false);
                            Bundle fragmentArgs = new Bundle();
                            fragmentArgs.putString("FragmentId", String.valueOf(R.layout.registration_fragment));
                            fragmentArgs.putString("isFb", "true");
                            if(user.getName()!=null) fragmentArgs.putString("fbName", user.getName());
                            if(user.getProperty("email")!=null) fragmentArgs.putString("fbEmail", user.getProperty("email")+"");
                            if(user.getLocation()!=null && user.getLocation().getName()!=null) fragmentArgs.putString("fbAddress",user.getLocation().getName());

                            Intent in = new Intent(activity, MainMenuActivity.class);
                            in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            in.putExtras(fragmentArgs);
                            startActivity(in);
                            finish();
                        }
                    }
                    else
                    {
                        showMsg("No user found!");
                        showMsg(response.getError().getErrorMessage());
                    }
                }
            }).executeAsync();

        }
        else if (state.isClosed())
        {
            Log.i("facebook", "Logged out...");
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        uihelper.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uihelper.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        uihelper.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        uihelper.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uihelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_sign_up);

        uihelper =new UiLifecycleHelper(this,callback);
        uihelper.onCreate(savedInstanceState);

        activity = this;
        shareButton = (Button) findViewById(R.id.social_button_fbShare);

        social_message = (EditText) findViewById(R.id.social_edittext_message);
        social_link = (EditText) findViewById(R.id.social_edittext_link);

        ArrayList<String> permission =new ArrayList<>();
        permission.add("email");
        permission.add("public_profile");
        permission.add("user_location");
        permission.add("publish_actions");

        LoginButton socialLoginButton=(LoginButton) findViewById(R.id.fbbtn);
        socialLoginButton.setPublishPermissions(permission);

        if(getIntent().getExtras().getString("isShare").equals("true")){
        shareButton.setVisibility(View.VISIBLE);
        social_message.setVisibility(View.VISIBLE);
        social_link.setVisibility(View.VISIBLE);

        socialLoginButton.setVisibility(View.GONE);
    }else {
        shareButton.setVisibility(View.GONE);
        social_message.setVisibility(View.GONE);
        social_link.setVisibility(View.GONE);

        socialLoginButton.setVisibility(View.VISIBLE);
    }


        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.shaheed.codewarior.checkboxdevelopers",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                handleShare(social_message.getText().toString(),social_link.getText().toString());
            }
        });

        social_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                social_message.setText("");
            }
        });

        social_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                social_link.setText("");
            }
        });
    }
    private void handleShare(String msg,String link) {
        if(msg == null || msg.equals("")){
            msg = getString(R.string.share_default_message);
        }
        if(link == null || link.equals("") || !link.startsWith("http")){
            link = getString(R.string.share_default_link);
        }

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, msg+"\n"+link);
        startActivity(Intent.createChooser(shareIntent, "Share with..."));
    }




}
