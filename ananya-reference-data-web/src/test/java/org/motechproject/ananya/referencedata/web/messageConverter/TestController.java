package org.motechproject.ananya.referencedata.web.messageConverter;

import org.motechproject.ananya.referencedata.web.Beneficiary;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
public class TestController {


    @RequestMapping(method = RequestMethod.POST, value = "/test", produces = {"application/json", "application/xml"})
    @ResponseBody
    public TestResponse createOrUpdate(@RequestBody TestRequest testRequest) {
        List<Beneficiary> beneficiaryList = new ArrayList<Beneficiary>() {{
            add(new Beneficiary("ben1"));
            add(new Beneficiary("ben2"));
        }};

        return new TestResponse(1, "success", "foo", beneficiaryList);
    }
}
