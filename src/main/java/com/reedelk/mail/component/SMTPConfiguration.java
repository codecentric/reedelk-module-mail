package com.reedelk.mail.component;

import com.reedelk.runtime.api.annotation.*;
import com.reedelk.runtime.api.component.Implementor;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

@Shared
@Component(service = SMTPConfiguration.class, scope = ServiceScope.PROTOTYPE)
public class SMTPConfiguration implements Implementor {

    @Property("Host")
    @Hint("smtp.my-domain.com")
    @Example("smtp.my-domain.com")
    @Description("Sets the SMTP server to be used for sending emails.")
    private String host;

    @Property("Port")
    @Hint("587")
    @Example("587")
    @Description("The SMTP server port to be used for sending emails.")
    private Integer port;

    @Property("Username")
    @Hint("myUsername")
    @Example("username@my-domain.com")
    private String username;

    @Property("Password")
    @Password
    @Example("myPassword")
    private String password;

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
}
