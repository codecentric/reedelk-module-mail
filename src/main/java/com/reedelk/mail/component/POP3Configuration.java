package com.reedelk.mail.component;

import com.reedelk.runtime.api.annotation.*;
import com.reedelk.runtime.api.component.Implementor;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

@Shared
@Component(service = POP3Configuration.class, scope = ServiceScope.PROTOTYPE)
public class POP3Configuration implements Implementor {

    @Property("Host")
    @Hint("pop3.domain.com")
    @Example("pop3.domain.com")
    @Description("The POP3 server host to be used for retrieving emails.")
    private String host;

    @Property("Port")
    @Hint("110")
    @Example("110")
    @DefaultValue("110")
    @Description("The POP3 server port to be used for retrieving emails.")
    private Integer port;

    @Property("Username")
    @Hint("myUsername")
    @Example("username@domain.com")
    @Description("The username to be used to connect to the POP3 server.")
    private String username;

    @Property("Password")
    @Password
    @Example("myPassword")
    @Description("The password to be used to connect to the POP3 server.")
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
