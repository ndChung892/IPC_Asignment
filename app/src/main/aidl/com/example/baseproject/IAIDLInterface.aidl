// IAIDLInterface.aidl
package com.example.baseproject;
import com.example.baseproject.IBlockingConsumerInterface;
import com.example.baseproject.INonBlockingConsumerInterface;
interface IAIDLInterface {
    void createProduct();

    void blockingConsumer(in IBlockingConsumerInterface blockConsumer);
//    void blocking(in IBlocking

    oneway void nonBlockingConsumer(in INonBlockingConsumerInterface nonBlockConsumer);
}