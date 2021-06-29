package com.scaleton.dfinity.agent;

import java.time.Duration;
import java.util.Optional;

import com.scaleton.dfinity.agent.identity.AnonymousIdentity;
import com.scaleton.dfinity.agent.identity.Identity;

/** 
 * A configuration for an agent.
 */
class AgentConfig
{
	AgentConfig()
	{		
	}
	
	Optional<ReplicaTransport> transport = Optional.empty();
	Optional<Duration> ingressExpiryDuration  = Optional.empty();
	Identity identity = new AnonymousIdentity();
	NonceFactory nonceFactory;
}
