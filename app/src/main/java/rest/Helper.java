package rest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;

import com.example.berik.mallappgoods.R;

/**
 * Created by Berik on 15.07.2016.
 */

public class Helper {

        public static final String API_URL = "http://itdamu.kz/MallBackend/v1";
        public static final String API_URL2 =  "http://192.168.5.78:81/MallBackend/v1/goodsRest.php"; //"http://192.168.1.77/MallBackend/v1/goodsRest.php";
        public static final String API_URL3=  "http://192.168.1.77/restservice";

        //"http://192.168.5.78:81/MallBackend/v1/goodsRest.php";
        // Google Project Number
        public static final String ACTION_MULTIPLE_PICK = "mallapp.ACTION_MULTIPLE_PICK";
        public static final String ACTION_PICK = "mallapp.ACTION_PICK";

        public static final String ACTION_MULTIPLE_PICK2 = "mallapp.ACTION_MULTIPLE_PICK2";
        public static final String ACTION_PICK2 = "mallapp.ACTION_PICK2";

        public static final String GOOGLE_PROJ_ID = "811945841836"; //mall-app-project
        public static final String MSG_KEY = "m";


        public static boolean isEmpty(String text){
                boolean flag = true;
                if(text!=null && !text.equals("")){
                        flag = false;
                }
                return flag;
        }

        public static boolean validateParams(String... params){
                for(String s: params){
                        if(s == null && s.trim().equals("")) return false;
                }
                return true;
        }

}
