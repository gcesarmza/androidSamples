package com.gustavogenovese.pushNotificationsServer;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

@Path("/")
public class RestServerHandler {

	@GET
	@Produces("application/json")
	@Path("/register")
	public String authAndRegister(@QueryParam("username") String username,
								  @QueryParam("password") String password, 
								  @QueryParam("registrationId") String registrationId) {
		// authenticate user first
		if (UsersDAO.getInstance().validUser(username, password)) {
			// update the regId
			UsersDAO.getInstance().updateUserRegId(username, registrationId);
			ListenersManager.getInstance().fire(username, registrationId);
			return "{success: true, error: ''}";
		} else {
			return "{success: false, error:'No such user'}";
		}
	}
}
