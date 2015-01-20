package com.shaheed.codewarior.checkboxdevelopers;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Created by Shaheed on 1/13/2015.
 * Shaheed Ahmed Dewan Sagar
 * Ahsanullah University of Science & Technology
 * Email : sdewan64@gmail.com
 */
public class Constants {

    public static final String URL_BASE = "http://tomcat-sdewan64.rhcloud.com/";
    public static String URL_REGISTRATION = URL_BASE + "RegistrationServlet";
    public static String URL_LOGIN = URL_BASE + "LoginServlet";

    public static String userId = "";

    public static void makeToast(Activity activity,String message, boolean isError){
        if(!isError){
            Toast toast = Toast.makeText(activity,message,Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM,0,20);
            TextView toastView = (TextView) toast.getView().findViewById(android.R.id.message);
            toastView.setGravity(Gravity.CENTER);
            toast.show();

        }else{
            Toast toast = Toast.makeText(activity, message, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM,0,20);
            TextView toastView = (TextView) toast.getView().findViewById(android.R.id.message);
            toastView.setTextColor(Color.RED);
            toastView.setGravity(Gravity.CENTER);
            toast.show();
        }
    }
}
