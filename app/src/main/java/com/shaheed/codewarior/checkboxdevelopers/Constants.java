package com.shaheed.codewarior.checkboxdevelopers;

import android.app.Fragment;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Shaheed on 1/13/2015.
 */
public class Constants {

    public static final String URL_BASE = "http://tomcat-sdewan64.rhcloud.com/";
    public static String URL_REGISTRATION = URL_BASE + "RegistrationServlet";
    public static String URL_LOGIN = URL_BASE + "LoginServlet";

    public static final String METHOD_GET = "GET";
    public static final String METHOD_POST = "POST";

    public static String username = "";

    public static void makeToast(Fragment fragment,String message, boolean isError){
        if(!isError){
            Toast toast = Toast.makeText(fragment.getActivity(),message,Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM,0,20);
            TextView toastView = (TextView) toast.getView().findViewById(android.R.id.message);
            toastView.setGravity(Gravity.CENTER);
            toast.show();

        }else{
            Toast toast = Toast.makeText(fragment.getActivity(), message, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.BOTTOM,0,20);
            TextView toastView = (TextView) toast.getView().findViewById(android.R.id.message);
            toastView.setTextColor(Color.RED);
            toastView.setGravity(Gravity.CENTER);
            toast.show();
        }
    }

}
