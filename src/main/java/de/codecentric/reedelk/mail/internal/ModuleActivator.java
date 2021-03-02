package de.codecentric.reedelk.mail.internal;

import de.codecentric.reedelk.mail.internal.script.GlobalFunctions;
import de.codecentric.reedelk.runtime.api.script.ScriptEngineService;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import static org.osgi.service.component.annotations.ServiceScope.SINGLETON;

@Component(service = ModuleActivator.class, scope = SINGLETON, immediate = true)
public class ModuleActivator {

    @Reference
    private ScriptEngineService scriptEngine;
    @Reference
    private CloseableService closeableService;

    @Activate
    public void start(BundleContext context) {
        long moduleId = context.getBundle().getBundleId();
        GlobalFunctions globalFunctions = new GlobalFunctions(moduleId);
        scriptEngine.register(globalFunctions);
    }

    @Deactivate
    public void stop() {
        // If this module is being stopped, we must close all the listeners.
        // This is needed because other modules using this component might be shutdown
        // only later.
        closeableService.closeAll();
    }
}
