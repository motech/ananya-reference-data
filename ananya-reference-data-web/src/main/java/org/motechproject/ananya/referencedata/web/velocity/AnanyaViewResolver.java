package org.motechproject.ananya.referencedata.web.velocity;

import org.springframework.web.servlet.view.velocity.VelocityLayoutViewResolver;

public class AnanyaViewResolver extends VelocityLayoutViewResolver {
    @Override
    protected Class requiredViewClass() {
        return AnanyaView.class;
    }

}
