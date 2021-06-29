package com.scaleton.dfinity.agent.http;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequests;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.io.CloseMode;
import org.apache.hc.core5.reactor.IOReactorConfig;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;

import com.scaleton.dfinity.agent.AgentError;
import com.scaleton.dfinity.agent.ReplicaTransport;
import com.scaleton.dfinity.agent.requestid.RequestId;
import com.scaleton.dfinity.types.Principal;

public class ReplicaHttpTransport implements ReplicaTransport {
	static final String DFINITY_CONTENT_TYPE = "application/cbor";
	static final String API_VERSION_URL_PART = "/api/v2/";
	static final String STATUS_URL_PART = "status";
	static final String QUERY_URL_PART = "canister/%s/query";
	static final String CALL_URL_PART = "canister/%s/call";
	static final String READ_STATE_URL_PART = "canister/%s/read_state";

	protected static final Logger LOG = LoggerFactory.getLogger(ReplicaHttpTransport.class);

	final IOReactorConfig ioReactorConfig;
	final CloseableHttpAsyncClient client;

	URI uri;
	private final ContentType dfinityContentType = ContentType.create(DFINITY_CONTENT_TYPE);

	ReplicaHttpTransport(URI url) {
		this.uri = url;

		ioReactorConfig = IOReactorConfig.custom().setSoTimeout(Timeout.ofSeconds(5)).build();

		client = HttpAsyncClients.custom().setIOReactorConfig(ioReactorConfig).build();
	}

	public static ReplicaTransport create(String url) throws URISyntaxException {
		return new ReplicaHttpTransport(new URI(url));
	}

	public CompletableFuture<byte[]> status() {

		HttpHost target = HttpHost.create(uri);

		SimpleHttpRequest httpRequest = SimpleHttpRequests.get(target, API_VERSION_URL_PART + STATUS_URL_PART);

		return this.execute(httpRequest, Optional.empty());

	}

	public CompletableFuture<byte[]> query(Principal containerId, byte[] envelope) {

		HttpHost target = HttpHost.create(uri);

		SimpleHttpRequest httpRequest = SimpleHttpRequests.post(target,
				API_VERSION_URL_PART + String.format(QUERY_URL_PART, containerId.toString()));

		return this.execute(httpRequest, Optional.of(envelope));

	}
	
	public CompletableFuture<byte[]> call(Principal containerId, byte[] envelope, RequestId requestId) {

		HttpHost target = HttpHost.create(uri);

		SimpleHttpRequest httpRequest = SimpleHttpRequests.post(target,
				API_VERSION_URL_PART + String.format(CALL_URL_PART, containerId.toString()));

		return this.execute(httpRequest, Optional.of(envelope));

	}
	
	public CompletableFuture<byte[]> readState(Principal containerId, byte[] envelope) {

		HttpHost target = HttpHost.create(uri);

		SimpleHttpRequest httpRequest = SimpleHttpRequests.post(target,
				API_VERSION_URL_PART + String.format(READ_STATE_URL_PART, containerId.toString()));

		return this.execute(httpRequest, Optional.of(envelope));

	}	

	CompletableFuture<byte[]> execute(SimpleHttpRequest httpRequest, Optional<byte[]> payload) throws AgentError {

		try {
			client.start();

			URI requestUri = httpRequest.getUri();

			LOG.debug("Executing request " + httpRequest.getMethod() + " " + requestUri);

			if (payload.isPresent())
				httpRequest.setBody(payload.get(), dfinityContentType);
			else
				httpRequest.setHeader(HttpHeaders.CONTENT_TYPE, DFINITY_CONTENT_TYPE);

			CompletableFuture<byte[]> response = new CompletableFuture<byte[]>();

			client.execute(httpRequest, new FutureCallback<SimpleHttpResponse>() {

				@Override
				public void completed(SimpleHttpResponse httpResponse) {
					LOG.debug(requestUri + "->" + httpResponse.getCode());
					LOG.debug(httpResponse.getBody().getBodyText());

					response.complete(httpResponse.getBodyBytes());
				}

				@Override
				public void failed(Exception ex) {
					LOG.debug(requestUri + "->" + ex);
					response.completeExceptionally(
							AgentError.create(AgentError.AgentErrorCode.HTTP_ERROR, ex, ex.getLocalizedMessage()));
				}

				@Override
				public void cancelled() {
					LOG.debug(requestUri + " cancelled");
					response.completeExceptionally(
							AgentError.create(AgentError.AgentErrorCode.TRANSPORT_ERROR, requestUri));
				}

			});

			return response;

		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			LOG.debug(e.getLocalizedMessage());
			throw AgentError.create(AgentError.AgentErrorCode.URL_PARSE_ERROR, e);
		}

	}

}
