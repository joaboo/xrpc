package com.xrpc.rpc;

import java.io.Serializable;

public interface Request extends Serializable {

	long getRequestId();

	String getInterfaceName();

	String getMethodName();

	Class<?>[] getParameterTypes();

	Object[] getArguments();

}
