package com.reedelk.mail.component;

import com.reedelk.runtime.api.annotation.*;
import com.reedelk.runtime.api.component.Implementor;
import com.reedelk.runtime.api.message.content.MimeType;
import com.reedelk.runtime.api.script.dynamicvalue.DynamicString;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

@Component(service = BodyDefinition.class, scope = ServiceScope.PROTOTYPE)
public class BodyDefinition implements Implementor {

    @Property("Content")
    @Description("The result of the evaluation of this expression is set as mail body content.")
    private DynamicString content;

    @Property("Content Type")
    @Combo(comboValues = { MimeType.AsString.TEXT_PLAIN, MimeType.AsString.TEXT_HTML }, prototype = "XXXXXXXXXXXX")
    @Example(MimeType.AsString.TEXT_HTML)
    @DefaultValue(MimeType.AsString.TEXT_PLAIN)
    @Description("Sets the content type of the mail body.")
    private String contentType;

    @Property("Charset")
    @Combo(comboValues = {"US-ASCII", "ISO-8859-1", "UTF-8", "UTF-16BE", "UTF-16LE", "UTF-16"})
    @DefaultValue("UTF-8")
    @Example("ISO-8859-1")
    @Description("Sets the charset of the mail body.")
    private String charset;

    public DynamicString getContent() {
        return content;
    }

    public void setContent(DynamicString content) {
        this.content = content;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

}
