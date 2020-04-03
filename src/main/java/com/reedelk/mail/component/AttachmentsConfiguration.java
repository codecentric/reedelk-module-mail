package com.reedelk.mail.component;

import com.reedelk.runtime.api.component.Implementor;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

@Component(service = AttachmentsConfiguration.class, scope = ServiceScope.PROTOTYPE)
public class AttachmentsConfiguration implements Implementor {
}
