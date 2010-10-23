package org.twdata.proxy.ui;

import com.sun.jersey.api.view.Viewable;

import javax.ws.rs.Path;

/**
 *
 */
@Path("/plugins/servlet/refappdecorator")
public class DecoratorResources
{
    @Path("general.vmd")
    public Viewable decorateWithGeneral()
    {
        return new Viewable("/templates/general", "");
    }
}
