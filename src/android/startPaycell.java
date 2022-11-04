/**
	com.lampa.startpaycell, ver. 6.1.6
	https://github.com/lampaa/com.lampa.startpaycell
	
	Phonegap plugin for check or launch other application in android device (iOS support).
	bug tracker: https://github.com/lampaa/com.lampa.startpaycell/issues
*/
package com.lampa.startpaycell;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import java.util.HashMap;
import java.util.Iterator;
// import org.apache.cordova.CallbackContext;
// import org.apache.cordova.PluginResult;
import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class startPaycell extends Assets {
  private HashMap<Integer, BroadcastReceiver> broadcastReceiverHashMap = new HashMap<Integer, BroadcastReceiver>();
  private CallbackContext callbackContext;

  /**
   * Executes the request and returns PluginResult.
   *
   * @param action            The action to execute.
   * @param args              JSONArray of arguments for the plugin.
   * @param callbackContext   The callback context used when calling back into JavaScript.
   * @return                  Always return true.
   */
  public boolean execute(
    String action,
    JSONArray args,
    CallbackContext callbackContext
  )
    throws JSONException {
    Toast toast = Toast.makeText(
      this.cordova.getActivity().getApplicationContext(),
      action,
      Toast.LENGTH_LONG
    );
    toast.show();

    // if (action.equals("start")) {
    //   this.start(args, callbackContext);
    // } else if (action.equals("check")) {
    //   this.check(args, callbackContext);
    // } else if (action.equals("receiver")) {
    //   this.receiver(args, callbackContext);
    // } else if (action.equals("unReceiver")) {
    //   this.receiver(args, callbackContext);
    // } else if (action.equals("getExtras")) {
    //   this.getExtras(callbackContext);
    // } else if (action.equals("getExtra")) {
    //   this.getExtra(args, callbackContext);
    // }

    return true;
  }

  /**
   *
   * @param args
   * @param callback
   */
  private void receiver(JSONArray args, final CallbackContext callback) {
    BroadcastReceiver receiver = new BroadcastReceiver() {

      @Override
      public void onReceive(Context context, Intent intent) {
        JSONObject result = new JSONObject();

        try {
          result.put("_ACTION_VALUE_FORMAT_", intent.getAction());

          Bundle bundle = intent.getExtras();
          if (bundle != null) {
            for (String key : bundle.keySet()) {
              result.put(key, bundle.get(key));
            }
          }
        } catch (JSONException e) {
          e.printStackTrace();
        }

        PluginResult pluginResult = new PluginResult(
          PluginResult.Status.OK,
          result
        );
        pluginResult.setKeepCallback(true);

        callback.sendPluginResult(pluginResult);
      }
    };

    try {
      JSONArray values = args.getJSONArray(0);
      IntentFilter filter = new IntentFilter();

      for (int i = 0; i < values.length(); i++) {
        filter.addAction(values.getString(i));
      }

      //cordova.getActivity().getApplicationContext()
      cordova
        .getActivity()
        .getApplicationContext()
        .registerReceiver(receiver, filter);
      broadcastReceiverHashMap.put(receiver.hashCode(), receiver);

      PluginResult pluginResult = new PluginResult(
        PluginResult.Status.OK,
        receiver.hashCode()
      );
      pluginResult.setKeepCallback(true);

      callback.sendPluginResult(pluginResult);
    } catch (Exception ex) {
      ex.printStackTrace();
      callback.error("Error register receiver: " + ex.getMessage());
    }
  }

  /**
   * startPaycell
   */
  public void start(JSONArray args, CallbackContext callback) {
    LaunchMposInterface.launchMpos(
      this.cordova.getActivity().getApplicationContext(),
      this.cordova.getActivity(),
      "{
        customer: {
            customerAddress: 'İstanbul',
            customerAlias: 'Altan Kundura',
            customerIsCorporate: '1',
            customerMail: 'aliozturk@gmail.com',
            customerName: 'Altan Kundura Ltd.Şti.',
            customerPhone: '5551234567',
            customerSurname: '',
            customerTCKN: '',
            customerTaxAdmin: 'İstanbul',
            customerVKN: '1234567891'
        },
        DGPNo: 'DGP123456789',
        endTxnStatus: '0',
        header: {
            application: 'PaycellMPOS',
            ClientKey: 'EAC1167C4',
            ClientPass: 'A6A981CA9',
            Hash: '',
            requestId: '1',
            sequentialNo: '',
            transactionDate: '20220930165544999',
            transactionId: '1f370115-0f43-457c-b6dd-ac7835bff496',
            transactionStep: '1',
            EndtxnStatus: '0',
            timeout: '120'
        },
        invoiceStatus: '1',
        methodType: '2',
        orderType: '',
        PrintSlip: '0',
        products: [
            {
                productAmount: '1000',
                productCount: '3',
                productInfo: 'Ozellik1 : 11111\nOzellik2 : 22222\nOzellik3 : 33333',
                productKDVAmount: '180',
                productKDVRate: '18',
                productName: 'kalem'
            },
            {
                productAmount: '9000',
                productCount: '2',
                productInfo: 'Ozellik1 : 11111\nOzellik2 : 22222\nOzellik3 : 33333',
                productKDVAmount: '780',
                productKDVRate: '18',
                productName: 'kitap'
            }
        ],
        refNo: '2263748362263363',
        slipEstimatedTime: 4000,
        SubMerchantId: '1565469878',
        timeout: 120,
        totalAmount: '21000',
        totalKDVAmount: '3200'
    }",
      1,
      new LaunchMposInterface.LaunchMposLibInterface() {

        @Override
        public void launchMposLibFinished(
          LaunchMposLibResult launchMposLibResult
        ) {}
      }
    );
  }

  /**
   * checkApp
   */
  private void check(JSONArray args, CallbackContext callback) {
    JSONObject params;

    try {
      if (args.get(0) instanceof JSONObject) {
        params = args.getJSONObject(0);

        if (params.has("package")) {
          PackageManager pm = cordova
            .getActivity()
            .getApplicationContext()
            .getPackageManager();

          // get package info
          final PackageInfo PackInfo = pm.getPackageInfo(
            params.getString("package"),
            PackageManager.GET_ACTIVITIES
          );

          // create json object
          JSONObject info = new JSONObject() {

            {
              put("versionName", PackInfo.versionName);
              put("packageName", PackInfo.packageName);
              put("versionCode", PackInfo.versionCode);
              put("applicationInfo", PackInfo.applicationInfo);
            }
          };

          callback.success(info);
        } else {
          callback.error("Value \"package\" in null!");
        }
      } else {
        callback.error("Incorrect params, array is not array object!");
      }
    } catch (Exception e) {
      callback.error(e.getClass() + ": " + e.getMessage());
      e.printStackTrace();
    }
  }

  /**
   * getExtras
   */
  private void getExtras(CallbackContext callback) {
    try {
      Bundle extras = cordova.getActivity().getIntent().getExtras();
      JSONObject info = new JSONObject();

      if (extras != null) {
        for (String key : extras.keySet()) {
          info.put(key, extras.get(key).toString());
        }
      }

      callback.success(info);
    } catch (JSONException e) {
      callback.error(e.getMessage());
      e.printStackTrace();
    }
  }

  /**
   * getExtra
   */
  private void getExtra(JSONArray args, CallbackContext callback) {
    try {
      String extraName = parseExtraName(args.getString(0));
      Intent extraIntent = cordova.getActivity().getIntent();

      if (extraIntent.hasExtra(extraName)) {
        String extraValue = extraIntent.getStringExtra(extraName);

        if (extraValue == null) {
          extraValue = (extraIntent.getParcelableExtra(extraName)).toString();
        }

        callback.success(extraValue);
      } else {
        callback.error("extra field not found");
      }
    } catch (JSONException e) {
      callback.error(e.getMessage());
      e.printStackTrace();
    }
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent intent) {
    if (callbackContext != null) {
      JSONObject result = new JSONObject();

      try {
        result.put("_ACTION_requestCode_", requestCode);
        result.put("_ACTION_resultCode_", resultCode);

        Bundle bundle = intent == null ? null : intent.getExtras();
        if (bundle != null) {
          for (String key : bundle.keySet()) {
            result.put(key, bundle.get(key));
          }
        }
      } catch (JSONException e) {
        e.printStackTrace();
      }

      PluginResult pluginResult = new PluginResult(
        PluginResult.Status.OK,
        result
      );
      pluginResult.setKeepCallback(true);

      callbackContext.sendPluginResult(pluginResult);
    }
  }
}
