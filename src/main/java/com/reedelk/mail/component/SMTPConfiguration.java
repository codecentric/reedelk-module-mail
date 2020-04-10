package com.reedelk.mail.component;

import com.reedelk.mail.component.smtp.SMTPProtocol;
import com.reedelk.runtime.api.annotation.*;
import com.reedelk.runtime.api.component.Implementor;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

@Shared
@Component(service = SMTPConfiguration.class, scope = ServiceScope.PROTOTYPE)
public class SMTPConfiguration implements Implementor {

    @Property("Protocol")
    @Example("SMTP")
    @DefaultValue("SMTP")
    private SMTPProtocol protocol;

    @Property("Host")
    @Hint("smtp.domain.com")
    @Example("smtp.domain.com")
    @Description("The SMTP server host to be used for sending emails.")
    private String host;

    @Property("Port")
    @Hint("587")
    @Example("587")
    @DefaultValue("587")
    @Description("The SMTP server port to be used for sending emails.")
    private Integer port;

    @Property("Username")
    @Hint("myUsername")
    @Example("username@domain.com")
    @Description("The username to be used to connect to the SMTP server.")
    private String username;

    @Property("Password")
    @Password
    @Example("myPassword")
    @Description("The password to be used to connect to the SMTP server.")
    private String password;

    @Property("Socket Timeout")
    @Hint("10000")
    @Example("10000")
    @DefaultValue("30000")
    @Description("Socket I/O timeout value in milliseconds")
    private Integer socketTimeout;

    @Property("Connection Timeout")
    @Hint("30000")
    @Example("30000")
    @DefaultValue("60000")
    @Description("Socket connection timeout value in milliseconds.")
    private Integer connectTimeout;

    @Property("Start TLS Enabled")
    @DefaultValue("false")
    @Example("true")
    @Description("If true, enables the use of the STARTTLS command (if supported by the server) to switch the connection " +
            "to a TLS-protected connection before issuing any login commands. " +
            "If the server does not support STARTTLS, the connection continues without the use of TLS; " +
            "Note that an appropriate trust store must configured so that the client will trust the server's certificate.")
    @When(propertyName = "protocol", propertyValue = When.NULL)
    @When(propertyName = "protocol", propertyValue = "SMTP")
    private Boolean startTlsEnabled;

    public SMTPProtocol getProtocol() {
        return protocol;
    }

    public void setProtocol(SMTPProtocol protocol) {
        this.protocol = protocol;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(Integer connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public Integer getSocketTimeout() {
        return socketTimeout;
    }

    public void setSocketTimeout(Integer socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public Boolean getStartTlsEnabled() {
        return startTlsEnabled;
    }

    public void setStartTlsEnabled(Boolean startTlsEnabled) {
        this.startTlsEnabled = startTlsEnabled;
    }
}
