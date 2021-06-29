package com.scaleton.dfinity.agent;

import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
* The structure returned by [`ic_agent::Agent::status`], containing the information returned
* by the status endpoint of a replica.
*/
@JsonIgnoreProperties(value = { "impl_hash" })
public class Status {
	
	/**
    * Identifies the interface version supported, i.e. the version of the present document that
    * the internet computer aims to support, e.g. 0.8.1. The implementation may also return
    * unversioned to indicate that it does not comply to a particular version, e.g. in between
    * releases.
    */
	@JsonProperty("ic_api_version")
    public String icAPIVersion;

    /**
    * Optional. Identifies the implementation of the Internet Computer, by convention with the
    * canonical location of the source code.
    */
	@JsonProperty("impl_source")
    public Optional<String> implSource;

    /**
    * Optional. If the user is talking to a released version of an Internet Computer
    * implementation, this is the version number. For non-released versions, output of
    * `git describe` like 0.1.13-13-g2414721 would also be very suitable.
    */
	@JsonProperty("impl_version")
    public Optional<String> implVersion;

    /**
    * Optional. The precise git revision of the Internet Computer implementation.
    */
	@JsonProperty("impl_revision")
    public Optional<String> implRevision;

    /**
    * Optional.  The root (public) key used to verify certificates.
    */
	@JsonProperty("root_key")
    public Optional<byte[]>rootKey;

    /**
    * Contains any additional values that the replica gave as status.
    /*
     * 
     */
    public Map<String, ?> values;
}
