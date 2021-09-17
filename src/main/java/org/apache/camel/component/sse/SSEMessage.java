package org.apache.camel.component.sse;

import org.apache.camel.CamelContext;
import org.apache.camel.support.DefaultMessage;

import com.launchdarkly.eventsource.MessageEvent;

public class SSEMessage extends DefaultMessage {
	
	private transient MessageEvent messageEvent;
	
	public SSEMessage(CamelContext camelContext, MessageEvent messageEvent) {
		super(camelContext);
		this.messageEvent = messageEvent;
	}
	
	public MessageEvent getMessageEvent() {
		return messageEvent;
	}
	
	public void setMessageEvent(MessageEvent messageEvent) {
		this.messageEvent = messageEvent;
	}
	
	@Override
	public SSEMessage newInstance() {
		return new SSEMessage(getCamelContext(), messageEvent);
	}

}
