package com.example.baseproject;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayDeque;
import java.util.Queue;

public class MyService extends Service {
    HandlerThread handlerThreadCreate, handlerThreadBlocking, handlerThreadNonBlock;
    Handler handlerNonBlock, handlerBlocking, handler;
    private static final String TAG = "MyService";
    Queue<Integer> intQueue = new ArrayDeque<Integer>();
    private int intProduct = 0;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: ");
        return bindServices;
    }

    private final IAIDLInterface.Stub bindServices = new IAIDLInterface.Stub() {
        @Override
        public void createProduct() throws RemoteException {
            intProduct = 0;
            handlerThreadCreate= new HandlerThread("Server");
            handlerThreadCreate.start();
            handler= new Handler(handlerThreadCreate.getLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        synchronized (this){
                            while(intQueue.size() == 5) {
                                try {
                                    wait();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            intProduct = intProduct + 1;
                            intQueue.add(intProduct);
                            notify();
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }


                    }
                }
            });
        }

        @Override
        public void blockingConsumer(IBlockingConsumerInterface blockConsumer) throws RemoteException {
            handlerThreadBlocking = new HandlerThread("Server");
            handlerThreadBlocking.start();
            handlerBlocking = new Handler(handlerThreadBlocking.getLooper());
            handlerBlocking.post(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        synchronized (this){
                            while (intQueue.isEmpty()) {
                                try {
                                    wait();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            int value = intQueue.remove();
                            try {
                                blockConsumer.responseInt(value);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }notify();
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });

        }

        @Override
        public void nonBlockingConsumer(INonBlockingConsumerInterface nonBlockConsumer) throws RemoteException {
            handlerThreadNonBlock = new HandlerThread("Server");
            handlerThreadNonBlock.start();
            handlerNonBlock = new Handler(handlerThreadNonBlock.getLooper());
            handlerNonBlock.post(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        synchronized (this) {
                            while (intQueue.isEmpty())
                                try {
//                                    item = 0;
                                    wait();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            int value = intQueue.remove();
                            try {
                                nonBlockConsumer.responseInt(value);

                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }notify();
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });
        }
    };
}
