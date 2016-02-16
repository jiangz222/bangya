package com.bangbang.webapi.server.auth;

import org.springframework.security.crypto.codec.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;

/**
 * Created by wisp on 3/16/14.
 */
@Provider
@BangBangAuth
public class AuthorizationRequestFilter implements ContainerRequestFilter {
    @Override
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {

        System.out.println("Enter request filter.");
        SecurityContext securityContext =
                containerRequestContext.getSecurityContext();
        final String auth = containerRequestContext.getHeaders().getFirst("X-Bangbang-Auth");
        MyRSA myRSA = new MyRSA();
        try {
            final String id = myRSA.decrypt(Base64.decode(auth.getBytes()));
            // Set the credential
            MySecurityContext mySecurityContext = new MySecurityContext(securityContext);
            Principal principal = new Principal() {
                    @Override
                    public String getName() {
                        return id;
                    }
                };
            mySecurityContext.setUserPrincipal(principal);
            containerRequestContext.setSecurityContext(mySecurityContext);
        } catch (Exception e) {
            containerRequestContext.abortWith(Response
                    .status(Response.Status.UNAUTHORIZED)
                    .entity("Invalid user credential.")
                    .build());
        }
    }
}
