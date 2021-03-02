package de.codecentric.reedelk.mail.component.smtp;

import de.codecentric.reedelk.runtime.api.annotation.*;
import de.codecentric.reedelk.runtime.api.component.Implementor;
import de.codecentric.reedelk.runtime.api.message.content.MimeType;
import de.codecentric.reedelk.runtime.api.resource.ResourceBinary;
import de.codecentric.reedelk.runtime.api.script.dynamicvalue.DynamicByteArray;
import de.codecentric.reedelk.runtime.api.script.dynamicvalue.DynamicString;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

@Component(service = AttachmentDefinition.class, scope = ServiceScope.PROTOTYPE)
public class AttachmentDefinition implements Implementor {

    @Property("Name")
    @Hint("PDF Attachment")
    @Description("A display name of the attachment used in the Flow Designer and as email attachment description.")
    private String name;

    @Property("Content Type")
    @MimeTypeCombo
    @Example(MimeType.AsString.TEXT_HTML)
    @DefaultValue(MimeType.AsString.TEXT_PLAIN)
    @Description("Sets the mime type of the attachment to be sent in the email.")
    private String contentType;

    @Property("Charset")
    @DefaultValue("UTF-8")
    @Example("ISO-8859-1")
    @Combo(comboValues = {"US-ASCII", "ISO-8859-1", "UTF-8", "UTF-16BE", "UTF-16LE", "UTF-16"})
    @Description("Sets the charset of the attachment.")
    private String charset;

    @Property("Attachment Source")
    @InitValue("RESOURCE")
    @DefaultValue("RESOURCE")
    @Description("Determines the source of the attachment. " +
            "If 'RESOURCE', the attachment content is read from a file from the project's resources folder, " +
            "if 'FILE', the content is read from a file on the filesystem, " +
            "if 'EXPRESSION', the result of the evaluated script expression is used as attachment content.")
    @Example("EXPRESSION")
    private AttachmentSourceType sourceType;

    @Property("Resource file")
    // This is because the properties in this object are displayed in a dialog.
    // Therefore the browse file input field must stretch / shrink according to the dialog size.
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
    @Description("The path and name of the file to be read from the filesystem.")
    private DynamicString file;

    @Property("Expression")
    @InitValue("#[]")
    @Example("<code>message.payload()</code>")
    @When(propertyName = "sourceType", propertyValue = "EXPRESSION")
    @Description("The expression to be evaluated as attachment content.")
    private DynamicByteArray expression;

    @Property("File Name")
    @Hint("my-picture.png")
    @Example("<code>'my-file-name.txt'</code>")
    @Description("The file name of the attachment to be used is used. A static or dynamic expression can be used in this property." +
            "If attachment source is 'Resource' and this field is empty, the resource file name will be used instead. " +
            "If attachment source is 'File' and this field is empty, the file name will be used instead. " +
            "If attachment source is 'Expression' and this field is empty, an exception will be thrown.")
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
