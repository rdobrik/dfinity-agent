package com.scaleton.dfinity.agent.hashtree;

enum NodeType{
	EMPTY(0),
	FORK(1),
	LABELED(2),
	LEAF(3),
	PRUNED(4);	
	
	public int value;
	
	NodeType(int value) {
		this.value = value;
	}		
}
