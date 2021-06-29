package com.scaleton.dfinity.agent;

import java.util.concurrent.CompletableFuture;

import com.scaleton.dfinity.agent.requestid.RequestId;
import com.scaleton.dfinity.types.Principal;


public interface ReplicaTransport {
	
	public CompletableFuture<byte[]> status();
	
	public CompletableFuture<byte[]> query(Principal canisterId, byte[] envelope);
	
	public CompletableFuture<byte[]> call(Principal canisterId, byte[] envelope, RequestId requestId);
	
	public CompletableFuture<byte[]> readState(Principal canisterId, byte[] envelope);

}
