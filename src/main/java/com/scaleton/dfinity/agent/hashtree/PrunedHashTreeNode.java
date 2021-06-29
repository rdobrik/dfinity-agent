package com.scaleton.dfinity.agent.hashtree;

public final class PrunedHashTreeNode extends HashTreeNode {
	byte[] digest;
	
	PrunedHashTreeNode(byte[] digest)
	{
		this.type = NodeType.PRUNED;
		this.digest = digest;
	}

}
