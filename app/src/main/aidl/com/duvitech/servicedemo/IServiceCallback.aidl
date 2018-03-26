// IServiceCallback.aidl
package com.duvitech.servicedemo;

// Declare any non-default types here with import statements

oneway interface IServiceCallback {

	/*
	 * handler common message from service
	 */
    void handlerCommEvent(int msgID, int param);

    /*
	 * handler search message from service
	 */
    void handlerSearchEvent(int msgID, in List<String> strList);
}