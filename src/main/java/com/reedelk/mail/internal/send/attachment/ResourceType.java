package com.reedelk.mail.internal.send.attachment;

import com.reedelk.mail.component.AttachmentDefinition;
import com.reedelk.mail.internal.commons.ContentType;
import com.reedelk.mail.internal.commons.Headers;
import com.reedelk.runtime.api.commons.StreamUtils;
import com.reedelk.runtime.api.flow.FlowContext;
import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.resource.ResourceBinary;
import com.reedelk.runtime.api.script.ScriptEngineService;

import javax.activation.DataHandler;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.util.ByteArrayDataSource;
import java.nio.file.Paths;

public class ResourceType implements Strategy {

    @Override
    public MimeBodyPart attach(ScriptEngineService scriptEngine,
                               AttachmentDefinition definition,
                               FlowContext context,
                               Message message) throws MessagingException {

        final String charset = definition.getCharset();
        final String attachmentName = definition.getName();
        final String contentType = definition.getContentType();
        final String attachmentContentType = ContentType.from(contentType, charset);
        final String contentTransferEncoding = definition.getContentTransferEncoding();

        ResourceBinary resourceFile = definition.getResourceFile();
        byte[] data = StreamUtils.FromByteArray.consume(resourceFile.data());
        ByteArrayDataSource dataSource = new ByteArrayDataSource(data, attachmentContentType);
        dataSource.setName(attachmentName);

        MimeBodyPart part = new MimeBodyPart();
        part.setDataHandler(new DataHandler(dataSource));
        part.addHeader(Headers.CONTENT_TRANSFER_ENCODING, contentTransferEncoding);
        part.setFileName(Paths.get(resourceFile.path()).getFileName().toString());
        return part;
    }
}
