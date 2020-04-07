package com.reedelk.mail.internal.script;

import com.reedelk.mail.internal.commons.AttachmentAttribute;
import com.reedelk.runtime.api.annotation.AutocompleteItem;
import com.reedelk.runtime.api.annotation.AutocompleteType;
import com.reedelk.runtime.api.message.content.Attachment;
import com.reedelk.runtime.api.message.content.ByteArrayContent;
import com.reedelk.runtime.api.message.content.MimeType;
import com.reedelk.runtime.api.message.content.StringContent;

@AutocompleteType(description = "A MailAttachment object encapsulates an Mail attachment " +
        "to be used as attachments when sending emails.")
public class MailAttachment {

    private Attachment.Builder current;

    MailAttachment() {
        current = Attachment.builder();
    }

    @AutocompleteItem(cursorOffset = 1,
            signature = "attribute(key: String, value: String)",
            example = "MailAttachmentBuilder.create().attribute('filename','my_image.png')",
            description = "Adds a new attribute with the given key and value to the attachment object.")
    public MailAttachment attribute(String key, String value) {
        current.attribute(key, value);
        return this;
    }

    @AutocompleteItem(cursorOffset = 1,
            signature = "charset(charset: String)",
            example = "MailAttachmentBuilder.create().charset('UTF-8')",
            description = "Sets the charset of the attachment object.")
    public MailAttachment charset(String charset) {
        AttachmentAttribute.CHARSET.set(current, charset);
        return this;
    }

    @AutocompleteItem(cursorOffset = 1,
            signature = "filename(filename: String)",
            example = "MailAttachmentBuilder.create().filename('my-picture.png')",
            description = "Sets the filename of the attachment object.")
    public MailAttachment filename(String filename) {
        AttachmentAttribute.FILENAME.set(current, filename);
        return this;
    }

    @AutocompleteItem(cursorOffset = 1,
            signature = "contentType(contentType: String)",
            example = "MailAttachmentBuilder.create().contentType('image/png')",
            description = "Sets the content type of the attachment object.")
    public MailAttachment contentType(String contentType) {
        AttachmentAttribute.CONTENT_TYPE.set(current, contentType);
        return this;
    }

    @AutocompleteItem(cursorOffset = 1,
            signature = "binary(data: byte[])",
            example = "MailAttachmentBuilder.create().binary(message.payload())",
            description = "Sets binary data to the current attachment object. Default mime type is 'application/octet-stream'.")
    public MailAttachment binary(byte[] data) {
        ByteArrayContent content = new ByteArrayContent(data, MimeType.APPLICATION_BINARY);
        current.content(content);
        AttachmentAttribute.CONTENT_TYPE.set(current, MimeType.APPLICATION_BINARY.toString());
        return this;
    }

    @AutocompleteItem(cursorOffset = 1,
            signature = "text(data: String)",
            example = "MailAttachmentBuilder.create().text(message.payload())",
            description = "Sets text data to the current attachment object. Default mime type is 'text/plain'.")
    public MailAttachment text(String text) {
        StringContent content = new StringContent(text, MimeType.TEXT_PLAIN);
        current.content(content);
        AttachmentAttribute.CONTENT_TYPE.set(current, MimeType.TEXT_PLAIN.toString());
        return this;
    }

    @AutocompleteItem(cursorOffset = 1,
            signature = "binaryWithMimeType(data: byte[], mimeType: String)",
            example = "MailAttachmentBuilder.create().binaryWithMimeType(message.payload(), 'application/octet-stream')",
            description = "Sets binary data to the current attachment object with the given mime type.")
    public MailAttachment binaryWithMimeType(byte[] data, String mimeType) {
        MimeType mimeTypeObject = MimeType.parse(mimeType);
        ByteArrayContent content = new ByteArrayContent(data, mimeTypeObject);
        current.content(content);
        AttachmentAttribute.CONTENT_TYPE.set(current, mimeTypeObject.toString());
        return this;
    }

    @AutocompleteItem(cursorOffset = 1,
            signature = "textWithMimeType(data: String, mimeType: String)",
            example = "MailAttachmentBuilder.create().textWithMimeType(message.payload(), 'text/plain')",
            description = "Sets text data to the current attachment object with the given mime type.")
    public MailAttachment textWithMimeType(String text, String mimeType) {
        MimeType mimeTypeObject = MimeType.parse(mimeType);
        StringContent content = new StringContent(text, mimeTypeObject);
        current.content(content);
        AttachmentAttribute.CONTENT_TYPE.set(current, mimeTypeObject.toString());
        return this;
    }

    @AutocompleteItem(signature = "build()",
            example = "MailAttachmentBuilder.create().text('sample text').build()",
            description = "Creates an MailAttachment object with the configured settings.")
    public Attachment build() {
        return current.build();
    }
}
