package com.scaleton.dfinity.test;

import java.math.BigInteger;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import com.scaleton.dfinity.agent.annotations.Agent;
import com.scaleton.dfinity.agent.annotations.Identity;
import com.scaleton.dfinity.agent.annotations.IdentityType;
import com.scaleton.dfinity.agent.annotations.Canister;
import com.scaleton.dfinity.agent.annotations.EffectiveCanister;
import com.scaleton.dfinity.agent.annotations.Transport;
import com.scaleton.dfinity.agent.annotations.Waiter;
import com.scaleton.dfinity.candid.annotations.Argument;
import com.scaleton.dfinity.candid.annotations.Name;
import com.scaleton.dfinity.candid.annotations.QUERY;
import com.scaleton.dfinity.candid.annotations.UPDATE;
import com.scaleton.dfinity.candid.types.Type;

@Agent(identity = @Identity(type = IdentityType.BASIC, pem_file = "./src/test/resources/Ed25519_identity.pem"), transport = @Transport(url = "http://localhost:8001"))
@Canister("rrkah-fqaaa-aaaaa-aaaaq-cai")
@EffectiveCanister("rrkah-fqaaa-aaaaa-aaaaq-cai")
public interface HelloProxy {
	
	@QUERY
	public String peek(@Argument(Type.TEXT)String name, @Argument(Type.INT) BigInteger value);
	
	@QUERY
	@Name("echoInt")
	public BigInteger getInt(BigInteger value);	
	
	@QUERY
	@Name("echoPojo")
	public Pojo getPojo(Pojo value);
	
	@QUERY
	@Name("echoOptionPojo")
	public Pojo echoOptionPojo(Optional<Pojo> value);	
	
	@QUERY
	public ComplexPojo echoComplexPojo(ComplexPojo value);
	
	@QUERY
	@Name("subComplexPojo")
	public Pojo subComplexPojo(ComplexPojo value);
	
	@QUERY
	@Name("echoPojoVec")
	public ComplexPojo[] echoComplexPojo(ComplexPojo[] value);	
	
	@QUERY
	public CompletableFuture<Double> getFloat(Double value);
	
	@UPDATE
	@Name("greet")
	@Waiter(timeout = 30)
	public CompletableFuture<String> greet(@Argument(Type.TEXT)String name);

}
