package com.duvitech.serviceapp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.duvitech.servicedemo.IServiceCallback;
import com.duvitech.servicedemo.Msg;
import com.duvitech.servicedemo.R;
import com.duvitech.servicedemo.ServiceDemoAidl;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    TextView mTextView;

    private ServiceDemoAidl mService;     // service
    private ServiceConnection mConnection;
    private final Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch(msg.what) {
                case Msg.SHOW_MSG:
                case Msg.HIDE_MSG:
                    Bundle b = new Bundle();
                    String str;
                    b = msg.getData();
                    str = b.getString("MSGTIP");
                    Log.v("appDemo", str);
                    mTextView.setText(str);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mTextView = (TextView) findViewById(R.id.msg);

        initConnection();
        startService();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        exitService();
    }

    public static Intent createExplicitFromImplicitIntent(Context context, Intent implicitIntent) {
        // Retrieve all services that can match the given intent
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent, 0);

        // Make sure only one match was found
        if (resolveInfo == null || resolveInfo.size() != 1) {
            return null;
        }

        // Get component info and create ComponentName
        ResolveInfo serviceInfo = resolveInfo.get(0);
        String packageName = serviceInfo.serviceInfo.packageName;
        String className = serviceInfo.serviceInfo.name;
        ComponentName component = new ComponentName(packageName, className);

        // Create a new intent. Use the old one for extras and such reuse
        Intent explicitIntent = new Intent(implicitIntent);

        // Set the component to be explicit
        explicitIntent.setComponent(component);

        return explicitIntent;
    }

    private void startService() {
        Intent intent = createExplicitFromImplicitIntent(this, new Intent("com.duvitech.servicedemo.START"));
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        //startService(intent);
    }

    private void exitService() {
        try {
            if(mService != null)
            {
                mService.unregisterCallback(mCallback);
            }
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if(mConnection != null) {
            unbindService(mConnection);
            mConnection = null;
        }
    }

    private void initConnection() {
        mConnection = new ServiceConnection(){

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                // TODO Auto-generated method stub
                mService = ServiceDemoAidl.Stub.asInterface(service);
                try {
                    if(mService.isInited()) {
                        mService.registerCallback(mCallback);
                    }
                    else {
                        exitService();
                    }
                } catch (RemoteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                // TODO Auto-generated method stub
                mService = null;
            }

        };
    }

    private IServiceCallback mCallback = new IServiceCallback.Stub() {

        @Override
        public void handlerCommEvent(int msgID, int param) throws RemoteException {
            // TODO Auto-generated method stub
            Message msg = new Message();

            msg.what = msgID;
            msg.arg1 = param;

            if(mHandler != null)
                mHandler.sendMessage(msg);
        }

        @Override
        public void handlerSearchEvent(int msgID, List<String> strList) throws RemoteException {
            // TODO Auto-generated method stub
            Message msg = new Message();
            Bundle b = new Bundle();

            msg.what = msgID;

            switch (msgID) {
                case Msg.SHOW_MSG:
                case Msg.HIDE_MSG:
                    b.putString("MSGTIP",strList.get(0));
                    break;
                default:
                    break;
            }

            msg.setData(b);
            if(mHandler != null)
                mHandler.sendMessage(msg);
        }
    };

}
