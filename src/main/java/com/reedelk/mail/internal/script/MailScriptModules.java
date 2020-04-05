package com.reedelk.mail.internal.script;

import com.reedelk.runtime.api.script.ScriptSource;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.unmodifiableList;

public class MailScriptModules implements ScriptSource {

    private final long moduleId;

    public MailScriptModules(long moduleId) {
        this.moduleId = moduleId;
    }

    @Override
    public Map<String, Object> bindings() {
        Map<String, Object> bindings = new HashMap<>();
        bindings.put("MailAttachmentBuilder", new MailAttachmentBuilder());
        return bindings;
    }

    @Override
    public Collection<String> scriptModuleNames() {
        return unmodifiableList(Collections.singletonList("MailAttachmentBuilder"));
    }

    @Override
    public long moduleId() {
        return moduleId;
    }

    @Override
    public String resource() {
        return "/function/javascript-functions.js";
    }
}
