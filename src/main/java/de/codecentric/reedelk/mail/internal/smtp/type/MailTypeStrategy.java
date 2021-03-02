package de.codecentric.reedelk.mail.internal.smtp.type;

import de.codecentric.reedelk.runtime.api.flow.FlowContext;
import de.codecentric.reedelk.runtime.api.message.Message;
import org.apache.commons.mail.EmailException;

public interface MailTypeStrategy {

    MailTypeStrategyResult create(FlowContext context, Message message) throws EmailException;
}
