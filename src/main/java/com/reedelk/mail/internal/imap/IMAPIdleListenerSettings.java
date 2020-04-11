package com.reedelk.mail.internal.imap;

import com.reedelk.mail.component.IMAPConfiguration;
import com.reedelk.mail.internal.commons.Defaults;

import java.util.Optional;

public class IMAPIdleListenerSettings {

    private IMAPConfiguration configuration;
    private String folder;
    private boolean deleteOnSuccess;
    private boolean peek;
    private boolean batch;

    public IMAPConfiguration getConfiguration() {
        return configuration;
    }

    public String getFolder() {
        return folder;
    }

    public boolean isPeek() {
        return peek;
    }

    public boolean isDeleteOnSuccess() {
        return deleteOnSuccess;
    }

    public boolean isBatch() {
        return batch;
    }

    public static Builder create() {
        return new Builder();
    }

    public static class Builder {

        private IMAPConfiguration configuration;
        private String folder;
        private Boolean peek;
        private Boolean batch;
        private Boolean deleteOnSuccess;

        public Builder configuration(IMAPConfiguration configuration) {
            this.configuration = configuration;
            return this;
        }

        public Builder folder(String folder) {
            this.folder = folder;
            return this;
        }

        public Builder peek(Boolean peek) {
            this.peek = peek;
            return this;
        }

        public Builder batch(Boolean batch) {
            this.batch = batch;
            return this;
        }

        public Builder deleteOnSuccess(Boolean deleteOnSuccess) {
            this.deleteOnSuccess = deleteOnSuccess;
            return this;
        }

        public IMAPIdleListenerSettings build() {
            IMAPIdleListenerSettings settings = new IMAPIdleListenerSettings();
            settings.configuration = configuration;
            settings.peek = Optional.ofNullable(peek).orElse(Defaults.PEEK);
            settings.folder = Optional.ofNullable(folder).orElse(Defaults.IMAP_FOLDER_NAME);
            settings.batch = Optional.ofNullable(batch).orElse(Defaults.Poller.BATCH_EMAILS);
            settings.deleteOnSuccess = Optional.ofNullable(deleteOnSuccess).orElse(Defaults.Poller.DELETE_ON_SUCCESS);
            return settings;
        }
    }
}
