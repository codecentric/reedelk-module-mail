package com.reedelk.mail.component;

import com.reedelk.runtime.api.annotation.Description;
import com.reedelk.runtime.api.annotation.ModuleComponent;
import com.reedelk.runtime.api.component.AbstractInbound;
import org.osgi.service.component.annotations.Component;

import static org.osgi.service.component.annotations.ServiceScope.PROTOTYPE;

@ModuleComponent("Mail Listener")
@Description("The Email listener can be used to trigger events whenever new emails " +
        "are received on the server.")
@Component(service = MailListener.class, scope = PROTOTYPE)
public class MailListener extends AbstractInbound {

    @Override
    public void onStart() {

    }

    @Override
    public void onShutdown() {

    }
}
