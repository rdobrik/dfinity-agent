package com.scaleton.dfinity.agent.hashtree;

public final class ForkHashTreeNode extends HashTreeNode {
	HashTreeNode left;
	HashTreeNode right;
	
	ForkHashTreeNode(HashTreeNode left, HashTreeNode right)
	{
		this.type = NodeType.FORK;
		this.left = left;
		this.right =right;
	}

}
