package org.motechproject.ananya.referencedata.contactCenter.service;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext-contact-center.xml")
public class FrontLineWorkerServiceIT {

    @Autowired
    private FrontLineWorkerService frontLineWorkerService;

    @Test
    public void should(){
        System.out.println("");
    }
}
