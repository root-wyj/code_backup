package com.ayou.mobilemsg.test;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * 
 * @author wuyingjie
 * @date 2017年9月25日
 */

public interface RemoteService extends Remote{
	String echo(String hello) throws RemoteException;
	
	String testException() throws RemoteException;
}
