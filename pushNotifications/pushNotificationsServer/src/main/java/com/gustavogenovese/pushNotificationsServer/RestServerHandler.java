package com.gustavogenovese.pushNotificationsServer;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/")
public class RestServerHandler {

	@GET
	@Produces("application/json")
	@Path("/register")
	public String authAndRegister(String username, String password, String registrationId){
		return "{auth: true}";
	}
}
