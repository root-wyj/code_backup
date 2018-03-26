package com.ayou.mobilemsg.test;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;


/**
 * 
 * @author wuyingjie
 * @date 2017年9月25日
 */

public class RemoteServiceImp extends UnicastRemoteObject implements Serializable, RemoteService{

	
	private static final long serialVersionUID = 1L;

	protected RemoteServiceImp() throws RemoteException{
		super();
	}
	
	@Override
	public String echo(String hello) throws RemoteException {
		return hello+"world";
	}
	
	@Override
	public String testException() throws RemoteException {
//		try {
			return "1/0="+(1/0);
//		} catch (Exception e) {
//			System.out.println(e);
//			e.printStackTrace();
//			throw new RemoteException(e.getMessage());
//		}
	}
	
	public static void main(String[] args) throws RemoteException, MalformedURLException {
			RemoteService remoteService = new RemoteServiceImp();
			Registry registry = LocateRegistry.createRegistry(7890);
			registry.rebind("myrmi", remoteService);
			System.out.println("service rmi://192.168.2.229:7890/myrmi bind success!!");
	}

}
