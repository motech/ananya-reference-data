package org.motechproject.ananya.referencedata.admin.security;

import org.junit.Test;
import org.motechproject.ananya.referencedata.admin.security.AuthenticationResponse;

import static junit.framework.Assert.assertTrue;

public class AuthenticationResponseTest {

    @Test
    public void shouldAddRole(){
        AuthenticationResponse response = new AuthenticationResponse();
        response.addRole("admin");
        assertTrue(response.roles().contains("admin"));
    }
}
