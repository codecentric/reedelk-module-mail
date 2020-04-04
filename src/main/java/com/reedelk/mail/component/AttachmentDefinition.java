package com.reedelk.mail.component;

import com.reedelk.runtime.api.annotation.*;
import com.reedelk.runtime.api.component.Implementor;
import com.reedelk.runtime.api.message.content.MimeType;
import com.reedelk.runtime.api.resource.ResourceBinary;
import com.reedelk.runtime.api.script.dynamicvalue.DynamicByteArray;
import com.reedelk.runtime.api.script.dynamicvalue.DynamicString;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

@Component(service = AttachmentDefinition.class, scope = ServiceScope.PROTOTYPE)
public class AttachmentDefinition implements Implementor {

    @Property("Name")
    @Hint("myAttachmentFile")
    private String name;

    @Property("Transfer Encoding")
    @Combo(editable = true, comboValues = {"Base64", "Quoted-Printable", "8bit", "7bit", "binary"})
    @DefaultValue("7bit")
    @Example("Base64")
    private String contentTransferEncoding;

    @Property("Mime type")
    @MimeTypeCombo
    @Example(MimeType.AsString.APPLICATION_BINARY)
    @DefaultValue(MimeType.AsString.APPLICATION_BINARY)
    @Description("Sets the mime type of the attachment content.")
    private String mimeType;

    @Property("Content Source")
    @InitValue("RESOURCE")
    @DefaultValue("RESOURCE")
    private AttachmentContentType attachmentContentType;

    @Property("Resource file")
    @Example("assets/my-document.pdf")
    @HintBrowseFile("Select Attachment File ...")
    @When(propertyName = "attachmentContentType", propertyValue = "RESOURCE")
    @When(propertyName = "attachmentContentType", propertyValue = When.NULL)
    @Description("The path and name of the attachment file to be read from the project's resources folder.")
    private ResourceBinary resourceFile;

    @Property("File")
    @Hint("/var/documents/my-document.pdf")
    @Example("/var/documents/my-document.pdf")
    @When(propertyName = "attachmentContentType", propertyValue = "FILE")
    private DynamicString file;

    @Property("Expression")
    @Example("<code>message.payload()</code>")
    @When(propertyName = "attachmentContentType", propertyValue = "EXPRESSION")
    @Description("The expression to be evaluated as attachment content.")
    private DynamicByteArray expression;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContentTransferEncoding() {
        return contentTransferEncoding;
    }

    public void setContentTransferEncoding(String contentTransferEncoding) {
        this.contentTransferEncoding = contentTransferEncoding;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public AttachmentContentType getAttachmentContentType() {
        return attachmentContentType;
    }

    public void setAttachmentContentType(AttachmentContentType attachmentContentType) {
        this.attachmentContentType = attachmentContentType;
    }

    public ResourceBinary getResourceFile() {
        return resourceFile;
    }

    public void setResourceFile(ResourceBinary resourceFile) {
        this.resourceFile = resourceFile;
    }

    public DynamicString getFile() {
        return file;
    }

    public void setFile(DynamicString file) {
        this.file = file;
    }

    public DynamicByteArray getExpression() {
        return expression;
    }

    public void setExpression(DynamicByteArray expression) {
        this.expression = expression;
    }
}
