package com.bangbang.webapi.server;

import org.glassfish.jersey.examples.helloworld.webapp.App;
import org.glassfish.jersey.examples.helloworld.webapp.MyApplication;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;

import static org.junit.Assert.assertEquals;

/**
 * Created by wisp on 3/15/14.
 */
public class UserTest  extends JerseyTest {
    @Override
    protected Application configure() {
        return new MyApplication();
    }
    @Override
    protected URI getBaseUri() {
        return UriBuilder.fromUri(super.getBaseUri()).path("webapi").build();
    }
}
