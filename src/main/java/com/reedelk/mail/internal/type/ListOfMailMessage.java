package com.reedelk.mail.internal.type;

import com.reedelk.runtime.api.annotation.Type;

import java.util.ArrayList;

@Type(listItemType = MailMessage.class)
public class ListOfMailMessage extends ArrayList<MailMessage> {
}
