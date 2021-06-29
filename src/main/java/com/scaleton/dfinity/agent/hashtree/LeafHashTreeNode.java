package com.scaleton.dfinity.agent.hashtree;

public final class LeafHashTreeNode extends HashTreeNode {
	byte[] value;
	
	LeafHashTreeNode(byte[] value)
	{
		this.type = NodeType.LEAF;
		this.value = value;
	}
	
	public byte[] getValue()
	{
		return this.value;
	}

}
