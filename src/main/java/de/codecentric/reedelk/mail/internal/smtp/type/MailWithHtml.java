package de.codecentric.reedelk.mail.internal.smtp.type;

import de.codecentric.reedelk.mail.component.SMTPMailSend;
import de.codecentric.reedelk.runtime.api.converter.ConverterService;
import de.codecentric.reedelk.runtime.api.flow.FlowContext;
import de.codecentric.reedelk.runtime.api.message.Message;
import de.codecentric.reedelk.runtime.api.message.content.MimeType;
import de.codecentric.reedelk.runtime.api.message.content.Pair;
import org.apache.commons.mail.DataSourceResolver;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.ImageHtmlEmail;

import javax.activation.DataSource;
import javax.activation.URLDataSource;
import java.io.IOException;
import java.net.URL;

public class MailWithHtml extends AbstractMailType {

    public MailWithHtml(SMTPMailSend component, ConverterService converterService) {
        super(component, converterService);
    }

    @Override
    public MailTypeStrategyResult create(FlowContext context, Message message) throws EmailException {

        ImageHtmlEmail email = new ImageHtmlEmail();

        configureConnection(email);
        configureBaseMessage(context, message, email);
        configureAttachments(context, message, email);

        Pair<String, String> charsetAndBody = buildCharsetAndMailBody(context, message);
        email.setCharset(charsetAndBody.left());
        email.setHtmlMsg(charsetAndBody.right());
        email.setDataSourceResolver(new AbsoluteURLDataSourceResolver()); // resolve image sources inline.

        return MailTypeStrategyResult.create(email, charsetAndBody.right(), MimeType.TEXT_HTML);
    }

    // Resolves img src URLs when Email contains image URLs
    private static class AbsoluteURLDataSourceResolver implements DataSourceResolver {

        @Override
        public DataSource resolve(String resourceLocation) throws IOException {
            return new URLDataSource(new URL(resourceLocation));
        }

        @Override
        public DataSource resolve(String resourceLocation, boolean isLenient) throws IOException {
            return new URLDataSource(new URL(resourceLocation));
        }
    }
}
