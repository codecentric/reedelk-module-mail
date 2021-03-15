package de.codecentric.reedelk.mail.internal.script;

import de.codecentric.reedelk.runtime.api.script.ScriptGlobalFunctions;

import java.util.HashMap;
import java.util.Map;

public class GlobalFunctions implements ScriptGlobalFunctions {

    private final long moduleId;

    public GlobalFunctions(long moduleId) {
        this.moduleId = moduleId;
    }

    @Override
    public long moduleId() {
        return moduleId;
    }

    @Override
    public Map<String, Object> bindings() {
        Map<String, Object> bindings = new HashMap<>();
        bindings.put("MailAttachmentBuilder", new MailAttachmentBuilder());
        return bindings;
    }
}
