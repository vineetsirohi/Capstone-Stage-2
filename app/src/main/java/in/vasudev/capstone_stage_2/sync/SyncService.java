package in.vasudev.capstone_stage_2.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import in.vasudev.capstone_stage_2.AppConstants;

public class SyncService extends Service {

    private static SyncAdapter sSyncAdapter = null;

    // Object to use as a thread-safe lock
    private static final Object sSyncAdapterLock = new Object();

    public SyncService() {
    }

    @Override
    public void onCreate() {
        Log.d(AppConstants.LOG_TAG, "in.vasudev.capstone_stage_2.sync.SyncService.onCreate");

        // Singleton
        synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null) {
                sSyncAdapter = new SyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(AppConstants.LOG_TAG, "in.vasudev.capstone_stage_2.sync.SyncService.onBind");

        return sSyncAdapter.getSyncAdapterBinder();
    }
}
