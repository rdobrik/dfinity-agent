package com.scaleton.dfinity.agent.hashtree;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = HashTreeDeserializer.class)
public final class HashTree {
	HashTreeNode rootNode;
	
	HashTree(HashTreeNode rootNode)
	{
		this.rootNode = rootNode;
	}
	
	// Recomputes root hash of the full tree that this hash tree was constructed from.
	
	public byte[] digest()
	{
		return this.rootNode.digest();
	}
	
    // Given a (verified) tree, the client can fetch the value at a given path, which is a
    // sequence of labels (blobs).
	public LookupResult lookupPath(List<Label> path)
	{
		return this.rootNode.lookupPath(path);
	}
	
}
