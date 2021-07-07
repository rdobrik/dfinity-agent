package com.scaleton.dfinity.test;

import java.util.concurrent.CompletableFuture;

import com.scaleton.dfinity.agent.annotations.Agent;
import com.scaleton.dfinity.agent.annotations.Identity;
import com.scaleton.dfinity.agent.annotations.IdentityType;
import com.scaleton.dfinity.agent.annotations.Canister;
import com.scaleton.dfinity.agent.annotations.EffectiveCanister;
import com.scaleton.dfinity.agent.annotations.Transport;
import com.scaleton.dfinity.agent.annotations.Waiter;
import com.scaleton.dfinity.candid.annotations.Argument;
import com.scaleton.dfinity.candid.annotations.Method;
import com.scaleton.dfinity.candid.annotations.MethodType;
import com.scaleton.dfinity.candid.types.Type;

@Agent(identity = @Identity(type = IdentityType.BASIC, pem_file = "./src/test/resources/Ed25519_identity.pem"), transport = @Transport(url = "http://localhost:8001"))
@Canister(id = "rrkah-fqaaa-aaaaa-aaaaq-cai")
@EffectiveCanister(id = "rrkah-fqaaa-aaaaa-aaaaq-cai")
public interface Hello {
	
	@Method(type = MethodType.QUERY, name="peek")
	public String peek(@Argument(type = Type.TEXT)String name, @Argument(type = Type.INT) Integer value);
	
	@Method(type = MethodType.QUERY, name="echoInt")
	public Integer getInt(Integer value);	
	
	@Method(type = MethodType.UPDATE, name="greet")
	@Waiter(timeout = 30)
	public CompletableFuture<String> greet(@Argument(type = Type.TEXT)String name);

}
