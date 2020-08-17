package com.reedelk.mail.internal.smtp.type;

import com.reedelk.runtime.api.flow.FlowContext;
import com.reedelk.runtime.api.message.Message;
import org.apache.commons.mail.EmailException;

public interface MailTypeStrategy {

    MailTypeStrategyResult create(FlowContext context, Message message) throws EmailException;
}
