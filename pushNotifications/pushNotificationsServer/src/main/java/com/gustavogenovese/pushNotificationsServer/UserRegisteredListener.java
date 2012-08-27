package com.gustavogenovese.pushNotificationsServer;

import java.io.Serializable;

public interface UserRegisteredListener extends Serializable{
	void userRegistered(String username, String registrationId);
}
