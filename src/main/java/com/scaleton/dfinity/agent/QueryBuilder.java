package com.scaleton.dfinity.agent;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.lang3.ArrayUtils;

import com.scaleton.dfinity.types.Principal;

/*
 * A Query Request Builder.
 *	This makes it easier to do query calls without actually passing all arguments.
 */

public final class QueryBuilder {
	Agent agent;
	Principal effectiveCanisterId;
	Principal canisterId;
	String methodName;
	byte[] arg;
	Optional<Long> ingressExpiryDatetime;
	
	QueryBuilder(Agent agent, Principal canisterId,String methodName )
	{
		this.agent = agent;
		this.canisterId = canisterId;
		this.methodName = methodName;
		this.effectiveCanisterId = canisterId.clone();
		this.ingressExpiryDatetime = Optional.empty();
		this.arg = ArrayUtils.EMPTY_BYTE_ARRAY;
	}
	
	public static QueryBuilder create(Agent agent, Principal canisterId,String methodName )
	{
		return new QueryBuilder(agent, canisterId, methodName);
	}
	
	public QueryBuilder effectiveCanisterId(Principal effectiveCanisterId)
	{
		this.effectiveCanisterId = effectiveCanisterId;
		return this;	
	}
	
	public QueryBuilder arg(byte[] arg)
	{
		this.arg = arg;
		return this;	
	}
	
	/**
	Takes a SystemTime converts it to a Duration by calling
    duration_since(UNIX_EPOCH) to learn about where in time this SystemTime lies.
    The Duration is converted to nanoseconds and stored in ingressExpiryDatetime
  	*/
	public QueryBuilder expireAt(LocalDateTime time) 
	{	
		this.ingressExpiryDatetime = Optional.of(time.toEpochSecond(ZoneOffset.UTC));
				
		return this;		
	}
    /**
	Takes a Duration (i.e. 30 sec/5 min 30 sec/1 h 30 min, etc.) and adds it to the
    Duration of the current SystemTime since the UNIX_EPOCH
    Subtracts a permitted drift from the sum to account for using system time and not block time.
    Converts the difference to nanoseconds and stores in ingressExpiryDatetime	
	*/
	
	public QueryBuilder expireAfter(Duration duration) 
	{
		Duration permittedDrift = Duration.ofSeconds(Agent.DEFAULT_PERMITTED_DRIFT);

		this.ingressExpiryDatetime =  Optional.of((Duration.ofMillis(System.currentTimeMillis()).plus(duration).minus(permittedDrift)).toNanos());
		
		return this;
	}
	
	/*
	 * Make a query call. This will return a byte vector.
	 */
	 
	public CompletableFuture<byte[]> call() throws AgentError
	{
		return agent.queryRaw(this.canisterId, this.effectiveCanisterId, this.methodName, this.arg, this.ingressExpiryDatetime);
	}
}
