package com.shaheed.codewarior.checkboxdevelopers;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

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
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class SocialSignUpActivity extends Activity
{
    private Activity activity;
    private Button shareButton;
    private UiLifecycleHelper uihelper;
    private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
    private static final String PENDING_PUBLISH_KEY = "pendingPublishReauthorization";
    private boolean pendingPublishReauthorization = false;


    void showMsg(String string)
    {
        Toast.makeText(getApplicationContext(), string, Toast.LENGTH_SHORT).show();
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
            shareButton.setVisibility(View.VISIBLE);
            Log.i("facebook", "Logged in...");
            Request.newMeRequest(session, new Request.GraphUserCallback()
            {

                @Override
                public void onCompleted(GraphUser user, Response response)
                {

                    if(user!=null)
                    {
                        if(getIntent().getExtras().getString("isShare").equals("false")){
                            Bundle fragmentArgs = new Bundle();
                            fragmentArgs.putString("FragmentId", String.valueOf(R.layout.registration_fragment));
                            fragmentArgs.putString("isFb", "true");
                            fragmentArgs.putString("fbName", user.getName());
                            fragmentArgs.putString("fbEmail", user.getProperty("email")+"");
                            fragmentArgs.putString("fbAddress",user.getLocation().getName());

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
            shareButton.setVisibility(View.INVISIBLE);
            Log.i("facebook", "Logged out...");
        }
        shareButton.setVisibility(View.VISIBLE);
        if (pendingPublishReauthorization &&
                state.equals(SessionState.OPENED_TOKEN_UPDATED)) {
            pendingPublishReauthorization = false;
            publishStory();
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
        outState.putBoolean(PENDING_PUBLISH_KEY, pendingPublishReauthorization);
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

        //force hide
        if(getIntent().getExtras().getString("isShare").equals("false")){
            shareButton.setVisibility(View.INVISIBLE);
        }else{
            shareButton.setVisibility(View.VISIBLE);
        }

        if(savedInstanceState!=null){
            pendingPublishReauthorization = savedInstanceState.getBoolean(PENDING_PUBLISH_KEY,false);
        }

        ArrayList<String> permission =new ArrayList<>();
        permission.add("email");
        permission.add("public_profile");
        permission.add("user_friends");

        LoginButton btn=(LoginButton)findViewById(R.id.fbbtn);
        btn.setPublishPermissions(permission);

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
        shareButton = (Button) findViewById(R.id.social_button_fbShare);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publishStory();
            }
        });


    }
    private void publishStory() {
        Session session = Session.getActiveSession();

        if (session != null){

            // Check for publish permissions
            List<String> permissions = session.getPermissions();
            if (!isSubsetOf(PERMISSIONS, permissions)) {
                pendingPublishReauthorization = true;
                Session.NewPermissionsRequest newPermissionsRequest = new Session
                        .NewPermissionsRequest(this, PERMISSIONS);
                session.requestNewPublishPermissions(newPermissionsRequest);
                return;
            }

            Bundle postParams = new Bundle();
            postParams.putString("name", "Profiler by Checkbox Developers");
            postParams.putString("caption", "Profiler by Checkbox Developers");
            postParams.putString("description", "Profiler by Checkbox Developers");
            postParams.putString("link", "https://github.com/sdewan64/Profiler");
            postParams.putString("picture", "https://raw.github.com/fbsamples/ios-3.x-howtos/master/Images/iossdk_logo.png");

            Request.Callback callback= new Request.Callback() {
                public void onCompleted(Response response) {
                    JSONObject graphResponse = response
                            .getGraphObject()
                            .getInnerJSONObject();
                    String postId = null;
                    try {
                        postId = graphResponse.getString("id");
                    } catch (JSONException e) {
                        Log.i("TAG",
                                "JSON error "+ e.getMessage());
                    }
                    FacebookRequestError error = response.getError();
                    if (error != null) {
                        Toast.makeText(getApplicationContext(),
                                error.getErrorMessage(),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Posted",
                                Toast.LENGTH_LONG).show();
                    }
                }
            };

            Request request = new Request(session, "me/feed", postParams,
                    HttpMethod.POST, callback);

            RequestAsyncTask task = new RequestAsyncTask(request);
            task.execute();
        }

    }
    private boolean isSubsetOf(Collection<String> subset, Collection<String> superset) {
        for (String string : subset) {
            if (!superset.contains(string)) {
                return false;
            }
        }
        return true;
    }



}
