package com.reedelk.mail.internal;

import com.reedelk.runtime.api.component.Inbound;
import org.osgi.service.component.annotations.Component;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.osgi.service.component.annotations.ServiceScope.SINGLETON;

@Component(service = CloseableService.class, scope = SINGLETON, immediate = true)
public class CloseableService {

    private final Map<Inbound, Closeable> closeables = new HashMap<>();

    public void register(Inbound component, Closeable closeable) {
        if (closeable != null) {
            this.closeables.put(component, closeable);
        }
    }

    public void unregister(Inbound component) {
        if (closeables.containsKey(component)) {
            close(closeables.get(component));
        }
    }

    public void closeAll() {
        this.closeables.values().forEach(this::close);
    }

    private void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                // nothing we can do.
            }
        }
    }
}
