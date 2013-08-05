package org.motechproject.ananya.referencedata.web.service;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.ananya.referencedata.contactCenter.request.FrontLineWorkerVerificationWebRequest;
import org.motechproject.ananya.referencedata.flw.request.LocationRequest;
import org.motechproject.ananya.referencedata.web.utils.FrontLineWorkerVerificationWebRequestBuilder;

import static org.junit.Assert.assertEquals;

public class DefaultRequestValuesTest {

    DefaultRequestValues defaultRequestValues;
    private String DEFAULT_STATE = "BIHAR";

    @Before
    public void setUp() {
        defaultRequestValues = new DefaultRequestValues(DEFAULT_STATE);
    }

    @Test
    public void shouldUpdateLocationRequestWithDefaultState() {
        LocationRequest locationWithNoState = new LocationRequest();
        defaultRequestValues.update(locationWithNoState);

        assertEquals("BIHAR", locationWithNoState.getState());
    }

    @Test
    public void shouldNotUpdateLocationRequestWithDefaultStateWhenItIsPresent() {
        String validState = "ORISSA";
        LocationRequest locationWithState = new LocationRequest();
        locationWithState.setState(validState);

        defaultRequestValues.update(locationWithState);

        assertEquals(validState, locationWithState.getState());
    }

    @Test
    public void shouldUpdateFLWRequestWithDefaultState() {
        FrontLineWorkerVerificationWebRequest request= new FrontLineWorkerVerificationWebRequestBuilder()
                .withDefaults().withState(null)
                .build();
        FrontLineWorkerVerificationWebRequest requestWithDefaultState = new FrontLineWorkerVerificationWebRequestBuilder()
                .withDefaults().withState(DEFAULT_STATE)
                .build();

        defaultRequestValues.update(request);

        assertEquals(requestWithDefaultState, request);
    }

    @Test
    public void shouldNotUpdateFLWRequestWithDefaultStateWhenItIsPresent() {
        FrontLineWorkerVerificationWebRequest request= new FrontLineWorkerVerificationWebRequestBuilder()
                .withDefaults().withState("ORISSA")
                .build();

        defaultRequestValues.update(request);

        assertEquals("ORISSA", request.getLocation().getState());
    }

}
