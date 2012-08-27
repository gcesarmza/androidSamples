package com.gustavogenovese.pushNotificationsServer;

import java.util.HashSet;
import java.util.Set;


public class ListenersManager {
	private static ListenersManager instance = null;
	
	private Set<UserRegisteredListener> listeners;
	
	public synchronized static ListenersManager getInstance(){
		if (instance == null){
			instance = new ListenersManager();
		}
		return instance;
	}
	
	private ListenersManager(){
		listeners = new HashSet<UserRegisteredListener>();
	}
	
	public synchronized void addListener(UserRegisteredListener newListener){
		listeners.add(newListener);
	}
	
	public synchronized void removeListener(UserRegisteredListener listener){
		listeners.remove(listener);
	}
	
	public synchronized void fire(String username, String registrationId){
		for (UserRegisteredListener listener : listeners){
			listener.userRegistered(username, registrationId);
		}
	}
}
