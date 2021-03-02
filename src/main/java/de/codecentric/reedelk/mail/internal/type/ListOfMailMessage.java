package de.codecentric.reedelk.mail.internal.type;

import de.codecentric.reedelk.runtime.api.annotation.Type;

import java.util.ArrayList;

@Type(listItemType = MailMessage.class)
public class ListOfMailMessage extends ArrayList<MailMessage> {
}
