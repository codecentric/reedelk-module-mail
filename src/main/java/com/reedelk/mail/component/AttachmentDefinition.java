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
    @Description("Sets the name of the attachment.")
    private String name;

    @Property("Content Type")
    @MimeTypeCombo
    @Example(MimeType.AsString.TEXT_HTML)
    @DefaultValue(MimeType.AsString.TEXT_PLAIN)
    private String contentType;

    @Property("Transfer Encoding")
    @Combo(editable = true, comboValues = {"Base64", "Quoted-Printable", "8bit", "7bit", "binary"})
    @DefaultValue("7bit")
    @Example("Base64")
    private String contentTransferEncoding;

    @Property("Charset")
    @Combo(comboValues = {"US-ASCII", "ISO-8859-1", "UTF-8", "UTF-16BE", "UTF-16LE", "UTF-16"})
    @DefaultValue("UTF-8")
    @Example("ISO-8859-1")
    private String charset;

    @Property("Content Source")
    @InitValue("RESOURCE")
    @DefaultValue("RESOURCE")
    @Description("Determines the source of the attachment. " +
            "If 'RESOURCE', the attachment content is read from a file from the project's resources folder, " +
            "if 'FILE', the content is read from a file on the filesystem, " +
            "if 'EXPRESSION', the result of the evaluated script expression is used as attachment content.")
    private AttachmentSourceType sourceType;

    @Property("Resource file")
    @WidthAuto
    @Example("assets/my-document.pdf")
    @HintBrowseFile("Select Attachment File ...")
    @When(propertyName = "sourceType", propertyValue = "RESOURCE")
    @When(propertyName = "sourceType", propertyValue = When.NULL)
    @Description("The path and name of the attachment file to be read from the project's resources folder.")
    private ResourceBinary resourceFile;

    @Property("File")
    @Hint("/var/documents/my-document.pdf")
    @Example("/var/documents/my-document.pdf")
    @When(propertyName = "sourceType", propertyValue = "FILE")
    private DynamicString file;

    @Property("Expression")
    @Example("<code>message.payload()</code>")
    @When(propertyName = "sourceType", propertyValue = "EXPRESSION")
    @Description("The expression to be evaluated as attachment content.")
    private DynamicByteArray expression;

    @Property("File Name")
    @Example("<code>'my-file-name.txt'</code>")
    @When(propertyName = "sourceType", propertyValue = "EXPRESSION")
    @Description("The file name to be used when expression is used.")
    private DynamicString fileName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContentTransferEncoding() {
        return contentTransferEncoding;
    }

    public void setContentTransferEncoding(String contentTransferEncoding) {
        this.contentTransferEncoding = contentTransferEncoding;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public AttachmentSourceType getSourceType() {
        return sourceType;
    }

    public void setSourceType(AttachmentSourceType sourceType) {
        this.sourceType = sourceType;
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

    public DynamicString getFileName() {
        return fileName;
    }

    public void setFileName(DynamicString fileName) {
        this.fileName = fileName;
    }
}
