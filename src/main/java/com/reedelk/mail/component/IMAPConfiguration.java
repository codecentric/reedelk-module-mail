package com.reedelk.mail.component;

import com.reedelk.runtime.api.annotation.*;
import com.reedelk.runtime.api.component.Implementor;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

@Shared
@Component(service = IMAPConfiguration.class, scope = ServiceScope.PROTOTYPE)
public class IMAPConfiguration implements Implementor {

    @Property("Host")
    @Hint("imap.domain.com")
    @Example("imap.gmail.com")
    @Description("The IMAP server host to be used for listening on new emails.")
    private String host;

    @Property("Port")
    @Hint("993")
    @Example("993")
    @Description("The IMAP server port to be used for listening on new emails.")
    private Integer port;

    @Property("Username")
    @Hint("myUsername")
    @Example("username@domain.com")
    private String username;

    @Property("Password")
    @Password
    @Example("myPassword")
    private String password;

    @Property("Timeout")
    @Example("10000")
    private Integer timeout;

    @Property("IMAP Folder")
    @Example("INBOX")
    @InitValue("INBOX")
    @DefaultValue("INBOX")
    @Description("The IMAP folder from which the listener should be listening from.")
    private String folder;

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

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }
}
