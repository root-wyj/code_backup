package com.ayou.mobilemsg.test;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;

/**
 * 
 * @author wuyingjie
 * @date 2017年9月25日
 */

public class RemoteClient {
	
	public static void main(String[] args) throws MalformedURLException, RemoteException, NotBoundException {
		Registry registry = LocateRegistry.getRegistry("192.168.2.235", 7890);
		System.out.println(Arrays.toString(registry.list()));
		RemoteService service = (RemoteService)registry.lookup("myrmi");
		String result = service.echo("xixihaha");
		System.out.println(result);
		System.out.println(service.testException());
	}
}
