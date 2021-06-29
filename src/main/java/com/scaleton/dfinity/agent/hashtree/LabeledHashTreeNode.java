package com.scaleton.dfinity.agent.hashtree;

public final class LabeledHashTreeNode extends HashTreeNode {
	Label label;
	HashTreeNode subtree;
	
	LabeledHashTreeNode(Label label, HashTreeNode subtree)
	{
		this.type = NodeType.LABELED;
		this.label = label;
		this.subtree = subtree;
	}

}
