package com.reedelk.mail.component;

import com.reedelk.runtime.api.annotation.*;
import com.reedelk.runtime.api.component.Implementor;
import com.reedelk.runtime.api.message.content.MimeType;
import com.reedelk.runtime.api.script.dynamicvalue.DynamicString;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

@Component(service = BodyConfiguration.class, scope = ServiceScope.PROTOTYPE)
public class BodyConfiguration implements Implementor {

    @Property("Body")
    private DynamicString content;

    @Property("Content Type")
    @MimeTypeCombo
    @Example(MimeType.AsString.TEXT_HTML)
    @DefaultValue(MimeType.AsString.TEXT_PLAIN)
    private String contentType;

    @Property("Charset")
    @Combo(comboValues = {"US-ASCII", "ISO-8859-1", "UTF-8", "UTF-16BE", "UTF-16LE", "UTF-16"})
    @DefaultValue("UTF-8")
    @Example("ISO-8859-1")
    private String charset;

    @Property("Transfer Encoding")
    @Combo(editable = true, comboValues = {"Base64", "Quoted-Printable", "8bit", "7bit", "binary"})
    @DefaultValue("7bit")
    @Example("Base64")
    private String contentTransferEncoding;

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

    public String getContentTransferEncoding() {
        return contentTransferEncoding;
    }

    public void setContentTransferEncoding(String contentTransferEncoding) {
        this.contentTransferEncoding = contentTransferEncoding;
    }
}
