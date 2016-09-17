package in.vasudev.capstone_stage_2.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import in.vasudev.capstone_stage_2.AppConstants;

public class AuthenticatorService extends Service {
    private Authenticator mAuthenticator;
    public AuthenticatorService() {
    }

    @Override
    public void onCreate() {
        Log.d(AppConstants.LOG_TAG,
                "in.vasudev.capstone_stage_2.sync.AuthenticatorService.onCreate");

        // Create a new authenticator object
        mAuthenticator = new Authenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(AppConstants.LOG_TAG,
                "in.vasudev.capstone_stage_2.sync.AuthenticatorService.onBind" + ": ");

        return mAuthenticator.getIBinder();
    }
}
