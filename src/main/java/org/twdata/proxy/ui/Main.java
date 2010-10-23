package org.twdata.proxy.ui;

import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.sal.api.user.UserManager;
import com.sun.jersey.api.view.Viewable;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 */
@Path("/")
@AnonymousAllowed
public class Main
{
    private final UserManager userManager;

    public Main(UserManager userManager)
    {
        this.userManager = userManager;
    }

    @GET
    @Path("index")
    public Viewable index() {   
        return new Viewable("/templates/index", "It worked");
    }
}
