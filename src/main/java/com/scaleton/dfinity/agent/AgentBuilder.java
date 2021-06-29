package com.scaleton.dfinity.agent;

import java.time.Duration;
import java.util.Optional;

import com.scaleton.dfinity.agent.identity.Identity;


public class AgentBuilder {
	AgentConfig config = new AgentConfig();
	
	/**
	 * Create an instance of [Agent] with the information from this builder.
	 * @return agent Dfinity agent instance
	 */
	public Agent build()
	{
		Agent agent = new Agent(this);
		
		return agent;
	}

	public AgentBuilder transport(ReplicaTransport transport)
	{
		this.config.transport = Optional.of(transport);
		return this;
	}
	
	/**
    * Provides a _default_ ingress expiry. This is the delta that will be applied
    * at the time an update or query is made. The default expiry cannot be a
    * fixed system time.
    * @param duration default ingress expiry
    */
	
	public AgentBuilder ingresExpiry(Duration duration)
	{
		this.config.ingressExpiryDuration = Optional.of(duration);
		return this;
	}	
	
	/*
	 * Add an identity provider for signing messages. This is required.
	 * @param identity identity provider
	 */
	public AgentBuilder identity(Identity identity)
	{
		this.config.identity = identity;
		return this;
	}	
	
	/*
	* Add a NonceFactory to this Agent. By default, no nonce is produced.
	*/
	
	public AgentBuilder nonceFactory(NonceFactory nonceFactory)
	{
		this.config.nonceFactory = nonceFactory;
		return this;
	}
	
	

}
