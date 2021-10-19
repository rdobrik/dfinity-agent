package com.scaleton.dfinity.test;

import java.math.BigInteger;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.scaleton.dfinity.agent.Agent;
import com.scaleton.dfinity.agent.AgentBuilder;
import com.scaleton.dfinity.agent.AgentError;
import com.scaleton.dfinity.agent.NonceFactory;
import com.scaleton.dfinity.agent.ProxyBuilder;
import com.scaleton.dfinity.agent.ReplicaTransport;
import com.scaleton.dfinity.agent.RequestStatusResponse;
import com.scaleton.dfinity.agent.UpdateBuilder;
import com.scaleton.dfinity.agent.http.ReplicaHttpTransport;
import com.scaleton.dfinity.agent.identity.AnonymousIdentity;
import com.scaleton.dfinity.agent.identity.BasicIdentity;
import com.scaleton.dfinity.agent.identity.Identity;
import com.scaleton.dfinity.agent.identity.Secp256k1Identity;
import com.scaleton.dfinity.agent.requestid.RequestId;
import com.scaleton.dfinity.candid.parser.IDLArgs;
import com.scaleton.dfinity.candid.parser.IDLType;
import com.scaleton.dfinity.candid.parser.IDLValue;
import com.scaleton.dfinity.types.Principal;

public class ICTest {
	static final Logger LOG = LoggerFactory.getLogger(ICTest.class);

	@Test
	public void test() {

		ReplicaTransport transport;
		try {
			Security.addProvider(new BouncyCastleProvider());

			KeyPair keyPair = KeyPairGenerator.getInstance("Ed25519").generateKeyPair();

			Identity identity = BasicIdentity.fromKeyPair(keyPair);
			
			// Test also SECP256k Identity
			Path path = Paths.get(getClass().getClassLoader().getResource(TestProperties.SECP256K1_IDENTITY_FILE).getPath());
			
			//identity = Secp256k1Identity.fromPEMFile(path);

			transport = ReplicaHttpTransport.create(TestProperties.IC_URL);

			Agent agent = new AgentBuilder().transport(transport).identity(identity).nonceFactory(new NonceFactory())
					.build();

			// test String argument
			List<IDLValue> args = new ArrayList<IDLValue>();

			String value = "x";

			args.add(IDLValue.create(new String(value)));

			IDLArgs idlArgs = IDLArgs.create(args);

			byte[] buf = idlArgs.toBytes();

			Optional<Long> ingressExpiryDatetime = Optional.empty();
			// ingressExpiryDatetime =
			// Optional.of(Long.parseUnsignedLong("1623389588095477000"));

			CompletableFuture<RequestId> response = agent.updateRaw(Principal.fromString(TestProperties.IC_CANISTER_ID),
					Principal.fromString(TestProperties.IC_CANISTER_ID), "greet", buf, ingressExpiryDatetime);

			try {
				RequestId requestId = response.get();

				LOG.debug("Request Id:" + requestId.toHexString());

				TimeUnit.SECONDS.sleep(10);

				CompletableFuture<RequestStatusResponse> statusResponse = agent.requestStatusRaw(requestId,
						Principal.fromString(TestProperties.IC_CANISTER_ID));

				RequestStatusResponse requestStatusResponse = statusResponse.get();

				LOG.debug(requestStatusResponse.status.toString());

				Assertions.assertEquals(requestStatusResponse.status.toString(),
						RequestStatusResponse.REPLIED_STATUS_VALUE);

				byte[] output = requestStatusResponse.replied.get().arg;

				IDLArgs outArgs = IDLArgs.fromBytes(output);

				LOG.info(outArgs.getArgs().get(0).getValue().toString());

				Assertions.assertEquals(outArgs.getArgs().get(0).getValue().toString(), "Hello, " + value + "!");
				
				args = new ArrayList<IDLValue>();
				
				String stringValue = "x";

				args.add(IDLValue.create(stringValue));

				args.add(IDLValue.create(new BigInteger("1")));

				idlArgs = IDLArgs.create(args);

				buf = idlArgs.toBytes();

				CompletableFuture<byte[]> queryResponse = agent.queryRaw(Principal.fromString(TestProperties.IC_CANISTER_ID),
						Principal.fromString(TestProperties.IC_CANISTER_ID), "peek", buf, Optional.empty());

				try {
					byte[] queryOutput = queryResponse.get();

					outArgs = IDLArgs.fromBytes(queryOutput);

					LOG.info(outArgs.getArgs().get(0).getValue());
					Assertions.assertEquals("Hello, " + stringValue + "!",outArgs.getArgs().get(0).getValue());
				} catch (Throwable ex) {
					LOG.debug(ex.getLocalizedMessage(), ex);
					Assertions.fail(ex.getLocalizedMessage());
				}
				
				// Record
				Map<String, Object> mapValue = new HashMap<String, Object>();

				mapValue.put("bar", new Boolean(true));

				mapValue.put("foo", BigInteger.valueOf(42));
				
				args = new ArrayList<IDLValue>();

				IDLValue idlValue = IDLValue.create(mapValue);
				
				args.add(idlValue);

				idlArgs = IDLArgs.create(args);

				buf = idlArgs.toBytes();
				
				queryResponse = agent.queryRaw(Principal.fromString(TestProperties.IC_CANISTER_ID),
						Principal.fromString(TestProperties.IC_CANISTER_ID), "echoRecord", buf, Optional.empty());

				try {
					byte[] queryOutput = queryResponse.get();
					
					IDLType[] idlTypes = { idlValue.getIDLType() };

					outArgs = IDLArgs.fromBytes(queryOutput,idlTypes);

					LOG.info(outArgs.getArgs().get(0).getValue().toString());
					Assertions.assertEquals(mapValue,outArgs.getArgs().get(0).getValue());
				} catch (Throwable ex) {
					LOG.debug(ex.getLocalizedMessage(), ex);
					Assertions.fail(ex.getLocalizedMessage());
				}	
				
				args = new ArrayList<IDLValue>();

				args.add(IDLValue.create(new String(stringValue)));

				idlArgs = IDLArgs.create(args);

				buf = idlArgs.toBytes();

				UpdateBuilder updateBuilder = UpdateBuilder
						.create(agent, Principal.fromString(TestProperties.IC_CANISTER_ID), "greet").arg(buf);

				CompletableFuture<byte[]> builderResponse = updateBuilder
						.callAndWait(com.scaleton.dfinity.agent.Waiter.create(60, 5));

				output = builderResponse.get();
				outArgs = IDLArgs.fromBytes(output);

				LOG.info(outArgs.getArgs().get(0).getValue().toString());
				
				HelloProxy hello = ProxyBuilder.create(agent,Principal.fromString(TestProperties.IC_CANISTER_ID)).getProxy(HelloProxy.class);
				
				CompletableFuture<String> proxyResponse = hello.greet(value);			

				LOG.info(proxyResponse.get());
				Assertions.assertEquals(proxyResponse.get(),"Hello, " + value + "!");
				
				BigInteger intValue =new BigInteger("10000");
				
				String result = hello.peek(value, intValue);
				
				LOG.info(result);
				Assertions.assertEquals("Hello, " + value + "!",result);

			} catch (Throwable ex) {
				LOG.error(ex.getLocalizedMessage(), ex);
				Assertions.fail(ex.getMessage());
			}

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

		}

	}

}
