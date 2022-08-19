package com.example.secondclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;

import com.example.baseproject.IAIDLInterface;
import com.example.baseproject.IBlockingConsumerInterface;
import com.example.baseproject.INonBlockingConsumerInterface;
import com.example.secondclient.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String ACTION = "aidl_service";
    private static final String PACKAGE_NAME = "com.example.baseproject";
    private static final String SERVICES_NAME = "com.example.baseproject.MyService";
    IAIDLInterface iaidlInterface;
    private ServiceConnection mServicesConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected: ");
            iaidlInterface = IAIDLInterface.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            iaidlInterface = null;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        //bind services
        Intent intent = new Intent(ACTION);
        intent.setPackage(PACKAGE_NAME);
//        intent.setClass("com.example.baseproject",)
        intent.setClassName(PACKAGE_NAME,SERVICES_NAME);
        bindService(intent, mServicesConnection, BIND_AUTO_CREATE);
        //Block
        mBinding.btnBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    iaidlInterface.createProduct();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                try {
                    iaidlInterface.blockingConsumer(new IBlockingConsumerInterface.Stub() {
                        @Override
                        public void responseInt(int intBlockProduct) throws RemoteException {
                            mBinding.txtCharBlock.setText(String.valueOf(intBlockProduct));
                        }
                    });
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        //Non Block
        mBinding.btnNonBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    iaidlInterface.createProduct();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                try {
                    iaidlInterface.nonBlockingConsumer(new INonBlockingConsumerInterface.Stub() {
                        @Override
                        public void responseInt(int intNonBlockProduct) throws RemoteException {
                            mBinding.txtCharNonBlock.setText(String.valueOf(intNonBlockProduct));
                        }
                    });
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServicesConnection);
    }

}