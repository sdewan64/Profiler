package com.shaheed.codewarior.checkboxdevelopers;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

public class MainMenuActivity extends ActionBarActivity {

    private final String MENU_LOGIN = "Login";
    private final String MENU_SIGNUP = "Sign Up";

    private DrawerLayout mDrawerLayout;

    private ListView mDrawerList;

    private String[] mMenu  = new String[]{MENU_LOGIN, MENU_SIGNUP};;
    private ArrayAdapter<String> adapter;

    private SessionManager sessionManager;

    private ImageButton main_swipeButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        sessionManager = new SessionManager(getApplicationContext());

        if(sessionManager.isUserLoggedIn()){
            Constants.makeToast(this,"Login Information Found.\nRedirecting to Account Page...",false);
            Intent in = new Intent(this, AccountActivity.class);
            in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(in);
            finish();
        }

        if(getIntent().getExtras() != null){
            Fragment menuFragment = new MenuFragment();
            Bundle fragmentArgs = new Bundle();
            fragmentArgs.putString("FragmentId", String.valueOf(R.layout.registration_fragment));
            fragmentArgs.putString("isFb", "true");
            fragmentArgs.putString("fbName", getIntent().getExtras().getString("fbName"));
            fragmentArgs.putString("fbEmail", getIntent().getExtras().getString("fbEmail"));
            fragmentArgs.putString("fbAddress",getIntent().getExtras().getString("fbAddress"));
            menuFragment.setArguments(fragmentArgs);
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, menuFragment).commit();
        }

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.drawer_list);
        main_swipeButton = (ImageButton) findViewById(R.id.main_button_swipe);

        adapter = new ArrayAdapter<>(this, R.layout.menu_fragment, mMenu);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mDrawerLayout.closeDrawers();
                Fragment menuFragment = null;
                Bundle fragmentArgs = new Bundle();

                if(mMenu[position].equals(MENU_LOGIN)){
                    menuFragment = new MenuFragment();
                    fragmentArgs.putString("FragmentId", String.valueOf(R.layout.login_fragment));
                }else if(mMenu[position].equals(MENU_SIGNUP)){
                    menuFragment = new MenuFragment();
                    fragmentArgs.putString("FragmentId", String.valueOf(R.layout.registration_fragment));
                }

                menuFragment.setArguments(fragmentArgs);

                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, menuFragment).commit();

            }
        });

        main_swipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(Gravity.START);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //if (id == R.id.action_settings) {
        //    return true;
        //}

        return super.onOptionsItemSelected(item);
    }
}
