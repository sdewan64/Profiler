package com.shaheed.codewarior.checkboxdevelopers;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class AccountActivity extends ActionBarActivity {

    Button account_logout;
    TextView tv;

    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        sessionManager = new SessionManager(getApplicationContext());

        if(sessionManager.checkLogin()) {
            finish();
        }

        Constants.userId = sessionManager.getUserId();

        findViewsById();
        implementButtons();
        TextView tv = (TextView) findViewById(R.id.account_dummy);

        tv.setText(Constants.userId);
    }

    private void findViewsById(){
        tv = (TextView) findViewById(R.id.account_dummy);
        account_logout = (Button) findViewById(R.id.account_button_logout);

    }

    private void implementButtons() {
        account_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionManager.logoutUser();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_account, menu);
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
}
