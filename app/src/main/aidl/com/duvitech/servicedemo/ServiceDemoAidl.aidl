// ServiceDemoAidl.aidl
package com.duvitech.servicedemo;

// Declare any non-default types here with import statements

import com.duvitech.servicedemo.Freq;
import com.duvitech.servicedemo.IServiceCallback;

interface ServiceDemoAidl {

	boolean isInited();

	void registerCallback(IServiceCallback cb);
    void unregisterCallback(IServiceCallback cb);

	Freq getFreq();
	void setFreq(in Freq freq);
}