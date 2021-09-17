package org.apache.camel.component.sse;

import org.apache.camel.Category;
import org.apache.camel.Consumer;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.RuntimeCamelException;
import org.apache.camel.api.management.ManagedResource;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriPath;
import org.apache.camel.support.DefaultEndpoint;

import com.launchdarkly.eventsource.MessageEvent;

/**
 * Receives automatic updates form a server via an HTTP connection
 */
@ManagedResource(description = "Managed SSEEndpoint")
@UriEndpoint(firstVersion = "1.0-SNAPSHOT", scheme = "sse", title = "Server-Sent Events", syntax = "sse:url", consumerOnly = true, category = {
		Category.MESSAGING })
public class SSEEndpoint extends DefaultEndpoint {
	
	@UriPath(description = "Destination server")
	@Metadata(required = true)
	private final String url;

	public SSEEndpoint(String uri, String url, SSEComponent component) {
		super(uri, component);
		this.url = url;
	}

	@Override
	public Producer createProducer() throws Exception {
		throw new RuntimeCamelException("Cannot produce to a SSEEndpoint: " + getEndpointUri());
	}

	@Override
	public Consumer createConsumer(Processor processor) throws Exception {
		Consumer consumer = new SSEConsumer(this, processor);
		configureConsumer(consumer);
		return consumer;
	}
	
	@Override
	public SSEComponent getComponent() {
		return (SSEComponent) super.getComponent();
	}

	public String getUrl() {
		return url;
	}
	
	public Exchange createExchange(MessageEvent messageEvent) {
		Exchange exchange = createExchange();
		
		SSEMessage sseMessage = new SSEMessage(exchange.getContext(), messageEvent);
		sseMessage.setBody(messageEvent.getData());
		sseMessage.setHeader(SSEConstants.LAST_EVENT_ID, messageEvent.getLastEventId());
		sseMessage.setHeader(SSEConstants.ORIGIN, messageEvent.getOrigin());
		
		exchange.setIn(sseMessage);
		return exchange;
	}

}
