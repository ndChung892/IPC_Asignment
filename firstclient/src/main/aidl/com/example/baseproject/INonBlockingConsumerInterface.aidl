// INonBlockingConsumerInterface.aidl
package com.example.baseproject;

// Declare any non-default types here with import statements

interface INonBlockingConsumerInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    oneway void responseInt(int intNonBlockProduct);
}