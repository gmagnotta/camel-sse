package camelinaction.component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.ExecutorService;

import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Processor;
import org.apache.camel.component.sse.SSEEndpoint;
import org.apache.camel.support.DefaultConsumer;

public class MyConsumerOriginal extends DefaultConsumer implements Runnable {
	
    private ExecutorService executorService;

    public MyConsumerOriginal(SSEEndpoint endpoint, Processor processor) {
        super(endpoint, processor);
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();

        // start a single threaded pool to monitor events
        
        executorService = getEndpoint().getCamelContext().getExecutorServiceManager().newSingleThreadExecutor(this, "MyConsumer");
        
        // submit task to the thread pool
        executorService.submit(this);
    }

    @Override
    protected void doStop() throws Exception {
        super.doStop();

        // shutdown the thread pool gracefully
        getEndpoint().getCamelContext().getExecutorServiceManager().shutdownNow(executorService);
    }

    @Override
	public void run() {
		
		HttpClient client = HttpClient.newHttpClient();
		
		BufferedReader bufferedReader = null;

		while (!Thread.interrupted()) {

			try {
				
				String uri = getEndpoint().getEndpointUri();

//				System.out.println("Starting Reading SSE");

				HttpRequest request = HttpRequest.newBuilder().uri(URI.create(uri.substring("mycomponent://".length()))).GET().build();

				HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

				bufferedReader = new BufferedReader(new InputStreamReader(response.body()));
				String line = "";

				while ((line = bufferedReader.readLine()) != null) {

					// LOGIC HERE
					if (line.length() != 0 && line.startsWith("data")) {

//						System.out.println("Read SSE: " + line);

						Exchange exchange = getEndpoint().createExchange(ExchangePattern.InOnly);
				        
//				        final Exchange exchange = createExchange(false);
//				    	Exchange exchange = new DefaultExchange(getEndpoint(), ExchangePattern.InOnly);

				        exchange.getIn().setBody(line);

				        try {
				            // send message to next processor in the route
				            getProcessor().process(exchange);
				        } catch (Exception e) {
				            exchange.setException(e);
				        } finally {
				            if (exchange.getException() != null) {
				                getExceptionHandler().handleException("Error processing exchange", exchange, exchange.getException());
				            }
//				            releaseExchange(exchange, false);
				        }

					}
					
				}

			} catch (Exception ex) {

				System.err.println("Exception" + ex);

				try {
					if (bufferedReader != null) {
						bufferedReader.close();
					}
				} catch (IOException e) {
					System.err.println("Exception" + ex);
				}

			}
			
		}
		
		System.out.println("exiting");
		
	}
}
