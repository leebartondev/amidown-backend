package com.gtek.handlers;

import java.net.InetAddress;
import java.util.ArrayList;
import com.gtek.objects.NetworkSite;
import com.gtek.util.Console;

public class Pinger extends Thread {
	
	private ArrayList<NetworkSite> NETWORK_SITE_LIST; // Network Site list
	
	
	
	/**
	 * CONSTRUCTOR
	 * Set the list of network sites to be monitored
	 * by the thread.
	 * 
	 * @param list
	 */
	public Pinger(ArrayList<NetworkSite> list) {
		this.setList(list);
	}

	/**
	 * Thread run
	 */
	@Override
	public void run() {
		
		try {
			// Check status by ICMP ECHO
			for(NetworkSite tower : NETWORK_SITE_LIST) {
				
				boolean isUp = this.isReachable(tower.getRouter());
				Console.log(isUp ? tower.getNameId() + " is up!" : tower.getNameId() + " is down!");
				// Set status and time down in minutes
				tower.setStatus(!isUp);
				if(isUp) {
					tower.resetDownTime();
				} else {
					tower.updateDownTime();
				}
				// Update DB with the result
				Database.UpdateDatabase(tower.getCollection(), tower);
			}
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Use inet address to use ICMP 
	 * ECHO Requests to get the boolean
	 * status for an address.
	 * 
	 * @param ip
	 * @return boolean
	 */
	private boolean isReachable(String ip) {
		
		try {
			
			InetAddress address = InetAddress.getByName(ip); // Create inet address
			return address.isReachable(250); // Ping address
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Set the array list of network site
	 * objects.
	 * 
	 * @param list
	 */
	public void setList(ArrayList<NetworkSite> list) {
		this.NETWORK_SITE_LIST = list;
	}
	
}
