package com.example.fariseev_ps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PixelFormat;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.telephony.TelephonyManager;
import android.util.ArrayMap;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class CallReceiver extends BroadcastReceiver {

        static  WindowManager windowManager;
        static  ViewGroup windowLayout;

        static int XX,YY;
        static String phoneNumber;
        static boolean checkCall, incomingCall, ready, outgoingCall;



        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override

        public void onReceive(Context context, Intent intent) {
            //     if (!ready) getusers(context);
            if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
                if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(R.string.outgoing), false))
                {
                    phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);

                }
            } else {
                if (intent.getAction().equals("android.intent.action.PHONE_STATE"))

                        if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                            if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(R.string.callreceiver), false))
                                phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                            //         if (getContactID(context.getContentResolver(), phoneNumber) < 0) {
                            //newClient = client.get(phoneNumber);
                            if (phoneNumber != null) { //  if (newClient != null) {
                                //    addContact(context, newClient, phoneNumber);
                                if (!incomingCall) {
                                    incomingCall = true;
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        if (Settings.canDrawOverlays(context)) getuser(context);
                                    } else getuser(context);
                                    //     showWindow(context, phoneNumber);
                                    //   onActivityResult(context,7777,1, new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION));
                                }
                            }
                        } else if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {

                            if (incomingCall) {
                                // deleteContact(context.getContentResolver(), phoneNumber);
                              //  closeWindow(context);
                             //   phoneNumber = null;
                            //    incomingCall = false;
                            } else {
                                if (!outgoingCall) {
                                    outgoingCall = true;
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        if (Settings.canDrawOverlays(context)) getuser(context);
                                    } else getuser(context);
                                }
                            }
                        } else if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                            if (incomingCall) {
                                //  deleteContact(context.getContentResolver(), phoneNumber);
                                //   if (incomingCall) {
                                //              Log.d("--","Close window.");
                                closeWindow(context);
                                phoneNumber = null;
                                incomingCall = false;
                            }
                            if (outgoingCall) {
                                //  deleteContact(context.getContentResolver(), phoneNumber);
                                //   if (incomingCall) {
                                //              Log.d("--","Close window.");
                                closeWindow(context);
                                phoneNumber = null;
                                outgoingCall = false;
                            }
                        }
            }
        }


        @RequiresApi(api = Build.VERSION_CODES.M)
        protected void onActivityResult(Context context, int requestCode, int resultCode, Intent data) {
            if (requestCode == 7777) {
                if (Settings.canDrawOverlays(context)) {
                    Log.d("--", "есть разрешение ");
                } else {
                    Log.d("--", "нет разрешения ");

                }
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        public static void getuser(Context context) {
            ArrayMap<String, String> client = new ArrayMap<>();
            DatabaseHelper mDBHelper;
            SQLiteDatabase mDb;
            int num_list;
            SharedPreferences prefs = getDefaultSharedPreferences(context);
            num_list = Integer.parseInt(prefs.getString(context.getString(R.string.num_list), "6"));
            mDBHelper = new DatabaseHelper(context);
            mDb = mDBHelper.getWritableDatabase();
            if (phoneNumber!=null) {
                for (int activelist = 1; activelist < num_list + 1; activelist++) {
                    Cursor cursor = mDb.rawQuery("SELECT * FROM Лист" + activelist, null);
                    for (int xx = 4; xx < 6; xx++) {
                        cursor.moveToPosition(2);
                        String ss;
                        while (!cursor.isAfterLast()) {
                            if (cursor.getString(xx) != null) {
                                //             x++;
                                ss = cursor.getString(xx).replaceAll("[^0-9]", "");

                            int lenth = phoneNumber.length();
                              /*  Log.d("--", String.valueOf(lenth)+" "+phoneNumber.substring(lenth - 10, lenth));
                                 {
                                    do { */
                                if (lenth > 10)
                                       if (ss.contains(phoneNumber.substring(lenth - 10, lenth))) {
                                            client.put("name", cursor.getString(0));
                                            client.put("mesto", cursor.getString(6));
                                            client.put("otdel", cursor.getString(7));
                                            client.put("doljnost", cursor.getString(8));
                                        }
                                       /*
                                        lenth = lenth - 11;
                                    } while (lenth > 10);
                                } else {
                                    Log.d("--", ss);
                                } */
                            }
                            cursor.moveToNext();
                        }
                    }
                }
            }
            if (client.get("name")!=null) showWindow(context,client.get("name"),client.get("mesto"),client.get("otdel"),client.get("doljnost") );
        }

        private static void showWindow(Context context, String name, String mesto, String otdel, String doljnost) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            XX = Integer.parseInt(prefs.getString(context.getString(R.string.absXX), "100"));
            YY = Integer.parseInt(prefs.getString(context.getString(R.string.absYY), "200"));
            windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            int layout_parms;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                layout_parms = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            } else {
                layout_parms = WindowManager.LayoutParams.TYPE_PHONE;
            }
            final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    layout_parms,
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                         //   | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                           // | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                            | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                            | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                    PixelFormat.TRANSLUCENT
            );
            params.gravity = Gravity.TOP | Gravity.LEFT;
            params.x = XX;
            params.y = YY;
            windowLayout = (ViewGroup) layoutInflater.inflate(R.layout.info, null);
            TextView info_name = windowLayout.findViewById(R.id.info_name);
            TextView info_mesto = windowLayout.findViewById(R.id.info_mesto);
            TextView info_otdel = windowLayout.findViewById(R.id.info_otdel);
            TextView info_doljnost = windowLayout.findViewById(R.id.info_doljnost);
            TextView info_label = windowLayout.findViewById(R.id.info_label);
            if (incomingCall) info_label.setText("Справочник МЭС. Входящий вызов:");
            if (outgoingCall) info_label.setText("Справочник МЭС. Исходящий вызов:");
            info_name.setText(name);
            info_mesto.setText(mesto);
            info_otdel.setText(otdel);
            info_doljnost.setText(doljnost);
      //      try {
      //          name1 = URLEncoder.encode(name, "UTF-8");
      //          name1 = name1.replace("+", "%20");
      //          name1 = "https://raw.githubusercontent.com/pfariseev/sprkpmes/master/JPG/" + name + ".jpg";
      //          // Name1="http://tcc.fsk-ees.ru/Lists/Employees/AllItems.aspx?InitialTabId=Ribbon%2EList&VisibilityContext=WSSTabPersistence&&SortField=Title&View={C4947BB9-3499-42FE-8A40-AC2804A96D60}&SortField=Title&SortDir=Desc&FilterField1=Title&FilterValue1="+Name1;
      //      } catch (UnsupportedEncodingException e) {
      //          e.printStackTrace();
      //      }
            if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(R.string.imageload), false)) {
                ImageView photo = windowLayout.findViewById(R.id.info_photo);
                users.showAndSavePhoto(context,name, photo);
              //  new users.DownloadImageTask(photo.execute(name1);
            }
                windowManager.addView(windowLayout, params);

            windowLayout.setOnTouchListener(new View.OnTouchListener() {
                private int initialX;
                private int initialY;
                private float initialTouchX;
                private float initialTouchY;

                @Override public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            initialX = params.x;
                            initialY = params.y;
                            initialTouchX = event.getRawX();
                            initialTouchY = event.getRawY();
                            return true;
                        case MotionEvent.ACTION_UP:
                            return true;
                        case MotionEvent.ACTION_MOVE:
                            params.x = initialX + (int) (event.getRawX() - initialTouchX);
                            params.y = initialY + (int) (event.getRawY() - initialTouchY);
                            windowManager.updateViewLayout(windowLayout, params);
                            XX =params.x;
                            YY =params.y;
                            return true;
                    }
                    return false;
                }
            });
        }




        public static void  closeWindow(Context context) {
            if (windowLayout != null) {
                SharedPreferences.Editor editor = getDefaultSharedPreferences(context).edit();
                editor.putString("XX", String.valueOf(XX));
                editor.putString("YY", String.valueOf(YY));
                editor.commit();
                windowManager.removeView(windowLayout);
               windowLayout = null;
            }
        }
/*
         public static void getusers(Context context) {
            DatabaseHelper mDBHelper;
            SQLiteDatabase mDb;
            int num_list;
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
            num_list = Integer.parseInt(prefs.getString(context.getString(R.string.num_list), "6"));
            mDBHelper = new DatabaseHelper(context);
            mDb = mDBHelper.getWritableDatabase();
            for (int activelist = 1; activelist < num_list + 1; activelist++) {
                Cursor cursor = mDb.rawQuery("SELECT * FROM Лист" + String.valueOf(activelist), null);
                for (int xx = 4; xx < 6; xx++) {
                    cursor.moveToPosition(2);
                    String ss;
                    while (!cursor.isAfterLast()) {
                        if (cursor.getString(xx) != null) {
                            //             x++;
                            ss = cursor.getString(xx).replaceAll("[^0-9]", "");
                            int lenth=ss.length();
                            if (lenth>10) {
                            do {
                              client.put("+" + ss.subSequence(lenth-11, lenth), cursor.getString(0));


                              lenth=lenth-11;
                            } while (lenth>10);
                        } else {
                                Log.d("--",ss);
                            }
                        }
                        cursor.moveToNext();
                    }
                }

            }

            ready = true;
            checkCall = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(R.string.callreceiver), false);
            Log.d("--","Ready in Callreciever now "+ready);
        }
*/


    }



