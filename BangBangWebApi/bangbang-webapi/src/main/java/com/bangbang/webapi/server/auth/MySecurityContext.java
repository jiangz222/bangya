package com.bangbang.webapi.server.auth;

import com.sun.jndi.url.iiop.iiopURLContext;

import javax.ws.rs.core.SecurityContext;
import java.security.Principal;

/**
 * Created by wisp on 3/17/14.
 */
public class MySecurityContext implements SecurityContext {
    private  Principal _principal;
    private SecurityContext base;

    @Override
    public Principal getUserPrincipal() {
        return _principal;
    }

    @Override
    public boolean isUserInRole(String s) {
        return base.isUserInRole(s);
    }

    @Override
    public boolean isSecure() {
        return base.isSecure();
    }

    @Override
    public String getAuthenticationScheme() {
        return base.getAuthenticationScheme();
    }

    public MySecurityContext(SecurityContext securityContext)
    {
        base = securityContext;
    }
    public void setUserPrincipal(Principal principal)
    {
        _principal = principal;
    }

}
