package org.motechproject.ananya.referencedata.web.functional.framework;

import com.eclipsesource.restfuse.Destination;
import org.junit.Rule;

public class FunctionalTestCase {
    @Rule
    public Destination destination = getDestination();

    protected Destination getDestination() {
        Destination destination = new Destination(this, "http://localhost:9979");
        destination.getRequestContext().
                addPathSegment("channel", "contact_center").
                addPathSegment("hostname", "localhost").
                addPathSegment("port", "9979").
                addPathSegment("appPath", "ananya-reference-data").
                addPathSegment("APIKeyName", "APIKey").
                addPathSegment("APIKeyValue", "1234");
        return destination;
    }
}
