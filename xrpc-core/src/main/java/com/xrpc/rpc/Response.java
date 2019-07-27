package com.xrpc.rpc;

import java.io.Serializable;

public interface Response extends Serializable {

	long getRequestId();

	Object getResult();

	Throwable getException();

}
