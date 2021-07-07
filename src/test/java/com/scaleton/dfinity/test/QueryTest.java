package com.scaleton.dfinity.test;


import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.client.NettyHttpClient;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.HttpStatusCode;
import org.mockserver.model.MediaType;
import org.mockserver.proxyconfiguration.ProxyConfiguration;
import org.mockserver.proxyconfiguration.ProxyConfiguration.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.scaleton.dfinity.agent.Agent;
import com.scaleton.dfinity.agent.AgentBuilder;
import com.scaleton.dfinity.agent.AgentError;
import com.scaleton.dfinity.agent.ProxyBuilder;
import com.scaleton.dfinity.agent.QueryBuilder;
import com.scaleton.dfinity.agent.ReplicaTransport;
import com.scaleton.dfinity.agent.http.ReplicaHttpTransport;
import com.scaleton.dfinity.agent.identity.BasicIdentity;
import com.scaleton.dfinity.agent.identity.Identity;
import com.scaleton.dfinity.agent.identity.Secp256k1Identity;
import com.scaleton.dfinity.agent.replicaapi.Envelope;
import com.scaleton.dfinity.agent.replicaapi.QueryResponse;
import com.scaleton.dfinity.candid.parser.IDLArgs;
import com.scaleton.dfinity.candid.parser.IDLValue;
import com.scaleton.dfinity.types.Principal;

public class QueryTest extends MockTest{
	static final Logger LOG = LoggerFactory.getLogger(QueryTest.class);

	@Test
	public void test() {
		objectMapper.registerModule(new Jdk8Module());

		
		try {
			this.runMockServer();

		} catch (IOException e) {
			LOG.error(e.getLocalizedMessage(), e);
			Assertions.fail(e.getMessage());

			return;
		}
		

		ReplicaTransport transport;
		try {
			Security.addProvider(new BouncyCastleProvider());
			

			KeyPair keyPair = KeyPairGenerator.getInstance("Ed25519").generateKeyPair();

			Identity identity = BasicIdentity.fromKeyPair(keyPair);
			
			Path path = Paths.get(getClass().getClassLoader().getResource(TestProperties.SECP256K1_IDENTITY_FILE).getPath());
			
			identity = Secp256k1Identity.fromPEMFile(path);
			
			transport = ReplicaHttpTransport.create("http://localhost:" + TestProperties.MOCK_PORT);

			Agent agent = new AgentBuilder()
					.transport(transport)
					.identity(identity)
					.build();
			
			// test integer argument
			List<IDLValue> args = new ArrayList<IDLValue>();

			Integer intValue =new Integer(10000);
			
			args.add(IDLValue.create(intValue));			
			
			IDLArgs idlArgs = IDLArgs.create(args);

			byte[] buf = idlArgs.toBytes();

			Optional<Long> ingressExpiryDatetime = Optional.empty();
			//ingressExpiryDatetime =	Optional.of(Long.parseUnsignedLong("1623389588095477000"));


			CompletableFuture<byte[]> response = agent.queryRaw(Principal.fromString(TestProperties.CANISTER_ID),
					Principal.fromString(TestProperties.CANISTER_ID), "echoInt", buf, ingressExpiryDatetime);

			try {
				byte[] output = response.get();

				IDLArgs outArgs = IDLArgs.fromBytes(output);

				LOG.info(outArgs.getArgs().get(0).getValue().toString());
				Assertions.assertEquals(Integer.valueOf(intValue + 1),outArgs.getArgs().get(0).getValue());
			} catch (Throwable ex) {
				LOG.debug(ex.getLocalizedMessage(), ex);
				Assertions.fail(ex.getLocalizedMessage());
			}			

			args = new ArrayList<IDLValue>();
			
			String value = "x";

			args.add(IDLValue.create(value));

			args.add(IDLValue.create(new Integer(1)));

			idlArgs = IDLArgs.create(args);

			buf = idlArgs.toBytes();

			response = agent.queryRaw(Principal.fromString(TestProperties.CANISTER_ID),
					Principal.fromString(TestProperties.CANISTER_ID), "peek", buf, Optional.empty());

			try {
				byte[] output = response.get();

				IDLArgs outArgs = IDLArgs.fromBytes(output);

				LOG.info(outArgs.getArgs().get(0).getValue());
				Assertions.assertEquals("Hello, " + value + "!",outArgs.getArgs().get(0).getValue());
			} catch (Throwable ex) {
				LOG.debug(ex.getLocalizedMessage(), ex);
				Assertions.fail(ex.getLocalizedMessage());
			}
			
			// test Boolean argument
			
			args = new ArrayList<IDLValue>();

			args.add(IDLValue.create(new Boolean(true)));			
			
			idlArgs = IDLArgs.create(args);

			buf = idlArgs.toBytes();

			response = agent.queryRaw(Principal.fromString(TestProperties.CANISTER_ID),
					Principal.fromString(TestProperties.CANISTER_ID), "echoBool", buf, Optional.empty());

			try {
				byte[] output = response.get();

				IDLArgs outArgs = IDLArgs.fromBytes(output);

				LOG.info(outArgs.getArgs().get(0).getValue().toString());
				Assertions.assertSame(Boolean.TRUE,outArgs.getArgs().get(0).getValue());
			} catch (Throwable ex) {
				LOG.debug(ex.getLocalizedMessage(), ex);
				Assertions.fail(ex.getLocalizedMessage());
			}
			
			// test Double argument
			
			args = new ArrayList<IDLValue>();

			Double doubleValue = new Double(42.42);
			args.add(IDLValue.create(doubleValue));			
			
			idlArgs = IDLArgs.create(args);

			buf = idlArgs.toBytes();

			response = agent.queryRaw(Principal.fromString(TestProperties.CANISTER_ID),
					Principal.fromString(TestProperties.CANISTER_ID), "echoFloat", buf, Optional.empty());

			try {
				byte[] output = response.get();

				IDLArgs outArgs = IDLArgs.fromBytes(output);

				LOG.info(outArgs.getArgs().get(0).getValue().toString());
				Assertions.assertEquals(doubleValue + 1,outArgs.getArgs().get(0).getValue());
			} catch (Throwable ex) {
				LOG.debug(ex.getLocalizedMessage(), ex);
				Assertions.fail(ex.getLocalizedMessage());
			}			
			

			response = agent.queryRaw(Principal.fromString(TestProperties.CANISTER_ID),
					Principal.fromString(TestProperties.CANISTER_ID), "hello", buf, Optional.empty());

			try {
				byte[] output = response.get();

				LOG.info(output.toString());
				Assertions.fail(output.toString());
			} catch (Throwable ex) {
				LOG.debug(ex.getLocalizedMessage(), ex);
				Assertions.assertEquals(ex.getCause().getMessage(),
						"The Replica returned an error: code 3, message: \"IC0302: Canister rrkah-fqaaa-aaaaa-aaaaq-cai has no query method 'hello'\"");

			}
		
			// test QueryBuilder
			
			args = new ArrayList<IDLValue>();

			args.add(IDLValue.create(intValue));
			
			idlArgs = IDLArgs.create(args);
			
			buf = idlArgs.toBytes();
			
			response = QueryBuilder.create(agent, Principal.fromString(TestProperties.CANISTER_ID), "echoInt").expireAfter(Duration.ofMinutes(3)).arg(buf).call();

			try {
				byte[] output = response.get();

				IDLArgs outArgs = IDLArgs.fromBytes(output);

				LOG.info(outArgs.getArgs().get(0).getValue().toString());
				Assertions.assertEquals(Integer.valueOf(intValue + 1),outArgs.getArgs().get(0).getValue());
			} catch (Throwable ex) {
				LOG.debug(ex.getLocalizedMessage(), ex);
				Assertions.fail(ex.getLocalizedMessage());
			}
			
			// test ProxyBuilder
			
			//Hello hello = ProxyBuilder.create(agent, Principal.fromString(TestProperties.CANISTER_ID)).getProxy(Hello.class);
			
			Hello hello = ProxyBuilder.create().getProxy(Hello.class);
			
			String result = hello.peek(value, intValue);
			
			LOG.info(result);
			Assertions.assertEquals("Hello, " + value + "!",result);
			
			Integer intResult = hello.getInt(intValue);
			
			LOG.info(intResult.toString());
			
			Assertions.assertEquals(Integer.valueOf(intValue + 1),intResult);
			
		} catch (URISyntaxException e) {
			LOG.error(e.getLocalizedMessage(), e);
			Assertions.fail(e.getMessage());		
		} catch (AgentError e) {
			LOG.error(e.getLocalizedMessage(), e);
			Assertions.fail(e.getMessage());
		} catch (NoSuchAlgorithmException e) {
			LOG.error(e.getLocalizedMessage(), e);
			Assertions.fail(e.getMessage());	
		} finally {
			mockServerClient.stop();
		}

	}

	void runMockServer() throws IOException {

		mockServerClient.when(
				new HttpRequest().withMethod("POST").withPath("/api/v2/canister/" + TestProperties.CANISTER_ID + "/query"))
				.respond(httpRequest -> {
					if (httpRequest.getPath().getValue()
							.endsWith("/api/v2/canister/" + TestProperties.CANISTER_ID + "/query")) {
						byte[] request = httpRequest.getBodyAsRawBytes();

						JsonNode requestNode = objectMapper.readValue(request, JsonNode.class);

						LOG.debug("Query Request:" + requestNode.toPrettyString());

						ObjectReader objectReader = objectMapper.readerFor(Envelope.class).withAttribute("request_type", "query");

						Envelope<Map> envelope = objectReader.readValue(request, Envelope.class);
						
						String methodName = (String) ((Map)envelope.content).get("method_name");
						
						LOG.debug("Method Name: " + methodName);
						
						String responseFileName = null;
						
						switch(methodName)
						{
						 case "echoBool":
							 responseFileName = TestProperties.CBOR_ECHOBOOL_QUERY_RESPONSE_FILE;
							 break;
						 case "echoInt":
							 responseFileName = TestProperties.CBOR_ECHOINT_QUERY_RESPONSE_FILE;
							 break;
						 case "echoFloat":
							 responseFileName = TestProperties.CBOR_ECHOFLOAT_QUERY_RESPONSE_FILE;
							 break;							 
						 case "peek":
							 responseFileName = TestProperties.CBOR_PEEK_QUERY_RESPONSE_FILE;
							 break;
						 case "hello":
							 responseFileName = TestProperties.CBOR_HELLO_QUERY_RESPONSE_FILE;
							 break;							 
						}
						
						byte[] response = {};
						
						LOG.debug("Response File Name: " + responseFileName);
						
						if(responseFileName != null)
						{
							LOG.debug(Paths
									.get(getClass().getClassLoader().getResource(responseFileName).getPath())
									.toString());	
							
							response = Files.readAllBytes(Paths
									.get(getClass().getClassLoader().getResource(responseFileName).getPath()));
						}else
							LOG.debug("Unknown Method " + methodName);


						if(TestProperties.FORWARD)
						{	
							NettyHttpClient client = new NettyHttpClient(null, clientEventLoopGroup, ProxyConfiguration.proxyConfiguration(Type.HTTP, new InetSocketAddress(TestProperties.FORWARD_HOST,TestProperties.FORWARD_PORT)), false);
							
							response = client.sendRequest(HttpRequest.request().withMethod("POST")
									.withHeaders(httpRequest.getHeaders())
									.withPath("/api/v2/canister/" + TestProperties.CANISTER_ID + "/query").withBody(request)).get().getBodyAsRawBytes();
						
							if(TestProperties.STORE)
								Files.write(Paths.get(TestProperties.STORE_PATH + File.separator +
								"cbor." + methodName + ".query.response"), response);
						}
						
						JsonNode responseNode = objectMapper.readValue(response, JsonNode.class);
						
						LOG.debug(responseNode.toPrettyString());	
						
						QueryResponse queryResponse = objectMapper.readValue(response, QueryResponse.class);

						return HttpResponse.response().withStatusCode(HttpStatusCode.OK_200.code())
								.withContentType(MediaType.create("application", "cbor")).withBody(response);

					} else {
						return HttpResponse.notFoundResponse();
					}
				});
	}

}
