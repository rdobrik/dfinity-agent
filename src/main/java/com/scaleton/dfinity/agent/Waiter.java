package com.scaleton.dfinity.agent;


public final class Waiter{
	int timeout;
	int sleep;
	int waited = 0;
	
	Waiter(int timeout,int sleep)
	{
		this.timeout = timeout;
		this.sleep = sleep;
	}
	
	public static Waiter create(int timeout, int sleep)
	{
		return new Waiter(timeout,sleep);
	}
	
	boolean waitUntil() 
	{
		if(timeout > 0 && waited >= timeout)
			return false;
		
		try
		{
			Thread.sleep(sleep*1000);
		}
		catch(InterruptedException e)
		{
			
		}
		
		waited += sleep;
		
		return true;
	}
}
