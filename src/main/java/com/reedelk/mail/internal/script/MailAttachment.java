package com.reedelk.mail.internal.script;

import com.reedelk.mail.internal.commons.AttachmentAttribute;
import com.reedelk.runtime.api.annotation.Type;
import com.reedelk.runtime.api.annotation.TypeFunction;
import com.reedelk.runtime.api.message.content.Attachment;
import com.reedelk.runtime.api.message.content.MimeType;

@Type(description = "A MailAttachment object encapsulates an Mail attachment " +
        "to be used as attachments when sending emails.")
public class MailAttachment {

    private Attachment.Builder current;

    MailAttachment() {
        current = Attachment.builder();
    }

    @TypeFunction(cursorOffset = 1,
            signature = "attribute(String key, String value)",
            example = "MailAttachmentBuilder.create().attribute('filename','my_image.png')",
            description = "Adds a new attribute with the given key and value to the attachment object.")
    public MailAttachment attribute(String key, String value) {
        current.attribute(key, value);
        return this;
    }

    @TypeFunction(cursorOffset = 1,
            signature = "charset(String charset)",
            example = "MailAttachmentBuilder.create().charset('UTF-8')",
            description = "Sets the charset of the attachment object.")
    public MailAttachment charset(String charset) {
        AttachmentAttribute.CHARSET.set(current, charset);
        return this;
    }

    @TypeFunction(cursorOffset = 1,
            signature = "filename(String filename)",
            example = "MailAttachmentBuilder.create().filename('my-picture.png')",
            description = "Sets the filename of the attachment object.")
    public MailAttachment filename(String filename) {
        AttachmentAttribute.FILENAME.set(current, filename);
        return this;
    }

    @TypeFunction(cursorOffset = 1,
            signature = "contentType(String contentType)",
            example = "MailAttachmentBuilder.create().contentType('image/png')",
            description = "Sets the content type of the attachment object.")
    public MailAttachment contentType(String contentType) {
        AttachmentAttribute.CONTENT_TYPE.set(current, contentType);
        return this;
    }

    @TypeFunction(cursorOffset = 1,
            signature = "binary(byte[] data)",
            example = "MailAttachmentBuilder.create().binary(message.payload())",
            description = "Sets binary data to the current attachment object. Default mime type is 'application/octet-stream'.")
    public MailAttachment binary(byte[] data) {
        current.data(data);
        current.mimeType(MimeType.APPLICATION_BINARY);
        AttachmentAttribute.CONTENT_TYPE.set(current, MimeType.APPLICATION_BINARY.toString());
        return this;
    }

    @TypeFunction(cursorOffset = 1,
            signature = "binary(byte[] data, String mimeType)",
            example = "MailAttachmentBuilder.create().binary(message.payload(), 'application/octet-stream')",
            description = "Sets binary data to the current attachment object with the given mime type.")
    public MailAttachment binary(byte[] data, String mimeType) {
        MimeType mimeTypeObject = MimeType.parse(mimeType);
        current.data(data);
        current.mimeType(mimeTypeObject);
        AttachmentAttribute.CONTENT_TYPE.set(current, mimeTypeObject.toString());
        return this;
    }

    @TypeFunction(cursorOffset = 1,
            signature = "text(String data)",
            example = "MailAttachmentBuilder.create().text(message.payload())",
            description = "Sets text data to the current attachment object. Default mime type is 'text/plain'.")
    public MailAttachment text(String text) {
        current.data(text.getBytes());
        current.mimeType(MimeType.TEXT_PLAIN);
        AttachmentAttribute.CONTENT_TYPE.set(current, MimeType.TEXT_PLAIN.toString());
        return this;
    }

    @TypeFunction(cursorOffset = 1,
            signature = "text(String data, String mimeType)",
            example = "MailAttachmentBuilder.create().text(message.payload(), 'text/plain')",
            description = "Sets text data to the current attachment object with the given mime type.")
    public MailAttachment text(String text, String mimeType) {
        MimeType mimeTypeObject = MimeType.parse(mimeType);
        current.data(text.getBytes());
        current.mimeType(mimeTypeObject);
        AttachmentAttribute.CONTENT_TYPE.set(current, mimeTypeObject.toString());
        return this;
    }

    @TypeFunction(signature = "build()",
            example = "MailAttachmentBuilder.create().text('sample text').build()",
            description = "Creates an MailAttachment object with the configured settings.")
    public Attachment build() {
        return current.build();
    }
}
