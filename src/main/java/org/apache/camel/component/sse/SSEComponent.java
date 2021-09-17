package org.apache.camel.component.sse;

import java.util.Map;

import org.apache.camel.Endpoint;

import org.apache.camel.support.DefaultComponent;

/**
 * This component connects to a Server-Sent Events and receives automatic updates from the Server
 */
@org.apache.camel.spi.annotations.Component("sse")
public class SSEComponent extends DefaultComponent {
    
	@Override
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
    	Endpoint endpoint = new SSEEndpoint(uri, remaining, this);
        setProperties(endpoint, parameters);
        return endpoint;
    }
}
