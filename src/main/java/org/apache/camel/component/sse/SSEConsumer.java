package org.apache.camel.component.sse;

import java.net.URI;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.support.DefaultConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.launchdarkly.eventsource.EventHandler;
import com.launchdarkly.eventsource.EventSource;
import com.launchdarkly.eventsource.MessageEvent;

/**
 * The Server-Sent Events consumer.
 */
public class SSEConsumer extends DefaultConsumer implements EventHandler {
	
	private static final Logger LOG = LoggerFactory.getLogger(SSEConsumer.class);
	
	private EventSource eventSource;
	
    public SSEConsumer(SSEEndpoint endpoint, Processor processor) {
        super(endpoint, processor);
    }
    

    @Override
    protected void doInit() throws Exception {
        super.doInit();
        
        URI url = URI.create(getEndpoint().getUrl());
    	
        EventSource.Builder builder = new EventSource.Builder(this, url);
        
        eventSource = builder.build();
    
    }
    
    @Override
    protected void doStart() throws Exception {
        super.doStart();
        
        eventSource.start();
    }

    @Override
    protected void doStop() throws Exception {
        super.doStop();

        eventSource.close();
        
    }
    
    @Override
    public SSEEndpoint getEndpoint() {
    	return (SSEEndpoint) super.getEndpoint();
    }
    
    @Override
	public void onOpen() throws Exception {
		
		if (LOG.isTraceEnabled()) {
			LOG.trace("onOpen");
		}
		
	}

	@Override
	public void onClosed() throws Exception {
		
		if (LOG.isTraceEnabled()) {
			LOG.trace("onClosed");
		}
		
	}

	@Override
	public void onMessage(String event, MessageEvent messageEvent) throws Exception {
		
		if (LOG.isTraceEnabled()) {
			LOG.trace("onMessage - " + messageEvent.getData());
		}

		Exchange exchange = getEndpoint().createExchange(messageEvent);
		

		try {
			// send message to next processor in the route
			getProcessor().process(exchange);
		} catch (Exception e) {
			
			LOG.error("Exception", e);
			
			exchange.setException(e);
			
		} finally {
			if (exchange.getException() != null) {
				getExceptionHandler().handleException("Error processing exchange", exchange,
						exchange.getException());
			}
		}
	}

	@Override
	public void onComment(String comment) throws Exception {
		
		if (LOG.isTraceEnabled()) {
			LOG.trace("onComment");
		}
		
	}

	@Override
	public void onError(Throwable t) {
		
		LOG.error("onError", t);

		Exchange exchange = getEndpoint().createExchange();

		exchange.setException(t);
		
		getExceptionHandler().handleException("Error processing exchange", exchange, t);

	}
    
}
