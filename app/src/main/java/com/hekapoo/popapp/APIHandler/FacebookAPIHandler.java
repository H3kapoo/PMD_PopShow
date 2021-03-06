package com.hekapoo.popapp.APIHandler;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.hekapoo.popapp.R;

import java.util.List;

public class FacebookAPIHandler {

    private static CallbackManager callbackManager;
    private static FacebookAPIHandler instance = null;

    private FacebookAPIHandler() {
        callbackManager = CallbackManager.Factory.create();
    }

    public void login(Button btn, Activity act, List<String> perms, FacebookCallback<LoginResult> re) {

        btn.setOnClickListener(e -> {
            if (!hasToken())
                LoginManager.getInstance().logInWithReadPermissions(act, perms);
            else {
                fastLogout();
                AccessToken.setCurrentAccessToken(null);
                btn.setBackgroundResource(R.color.app_light_green); //check why this
                btn.setText("LOG INTO FACEBOOK");
            }

        });

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                btn.setBackgroundResource(R.color.app_btn_gray);
                btn.setText("LOG OUT FACEBOOK");
                re.onSuccess(loginResult);
            }

            @Override
            public void onCancel() {
                re.onCancel();
                Log.d("app", "onCancel: ");
            }

            @Override
            public void onError(FacebookException exception) {
                Log.d("app", "onError: ");
                re.onError(exception);

            }
        });
    }

    //Function to send api calls and get the response
    public void sendGraphRequest(String endPoint, Bundle bundle, FacebookAPIHandlerCB cb) {
        if (hasToken()) {
            GraphRequest request = GraphRequest.newGraphPathRequest(
                    AccessToken.getCurrentAccessToken(),
                    endPoint,
                    cb::result);
            request.setParameters(bundle);
            request.executeAsync();
        }
    }

    public static FacebookAPIHandler getInstance() {
        if (instance == null)
            instance = new FacebookAPIHandler();
        return instance;
    }

    public void fastLogout() {
        LoginManager.getInstance().logOut();
        AccessToken.setCurrentAccessToken(null);
    }

    public boolean hasToken() {
        return AccessToken.getCurrentAccessToken() == null ? false : true;
    }

    public CallbackManager getCallbackManager() {
        return callbackManager;
    }

    public interface FacebookAPIHandlerCB {
        void result(GraphResponse obj);

    }
}
