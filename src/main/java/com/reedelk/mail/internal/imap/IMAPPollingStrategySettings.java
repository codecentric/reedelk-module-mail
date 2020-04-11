package com.reedelk.mail.internal.imap;

import com.reedelk.mail.component.IMAPConfiguration;
import com.reedelk.mail.component.imap.IMAPFlags;
import com.reedelk.mail.internal.commons.Defaults;

import java.util.Optional;

public class IMAPPollingStrategySettings {

    private IMAPFlags matcher;
    private IMAPConfiguration configuration;
    private String folder;
    private int limit;
    private boolean peek;
    private boolean batch;
    private boolean deleteOnSuccess;
    private boolean markDeleteOnSuccess;

    private IMAPPollingStrategySettings() {
    }

    public IMAPFlags getMatcher() {
        return matcher;
    }

    public IMAPConfiguration getConfiguration() {
        return configuration;
    }

    public String getFolder() {
        return folder;
    }

    public int getLimit() {
        return limit;
    }

    public boolean isPeek() {
        return peek;
    }

    public boolean isBatch() {
        return batch;
    }

    public boolean isDeleteOnSuccess() {
        return deleteOnSuccess;
    }

    public boolean isMarkDeleteOnSuccess() {
        return markDeleteOnSuccess;
    }

    public static Builder create() {
        return new Builder();
    }

    public static class Builder {

        private IMAPFlags matcher;
        private IMAPConfiguration configuration;

        private String folder;
        private Integer limit;
        private Boolean peek;
        private Boolean batch;
        private Boolean deleteOnSuccess;
        private Boolean markDeleteOnSuccess;

        public Builder peek(Boolean peek) {
            this.peek = peek;
            return this;
        }

        public Builder folder(String folder) {
            this.folder = folder;
            return this;
        }

        public Builder batch(Boolean batch) {
            this.batch = batch;
            return this;
        }

        public Builder limit(Integer limit) {
            this.limit = limit;
            return this;
        }

        public Builder matcher(IMAPFlags matcher) {
            this.matcher = matcher;
            return this;
        }

        public Builder deleteOnSuccess(Boolean deleteOnSuccess) {
            this.deleteOnSuccess = deleteOnSuccess;
            return this;
        }

        public Builder configuration(IMAPConfiguration configuration) {
            this.configuration = configuration;
            return this;
        }

        public Builder markDeleteOnSuccess(Boolean markDeleteOnSuccess) {
            this.markDeleteOnSuccess = markDeleteOnSuccess;
            return this;
        }

        public IMAPPollingStrategySettings build() {
            IMAPPollingStrategySettings settings = new IMAPPollingStrategySettings();
            settings.configuration = configuration;
            settings.peek = Optional.ofNullable(peek).orElse(Defaults.PEEK);
            settings.matcher = Optional.ofNullable(matcher).orElse(new IMAPFlags());
            settings.limit = Optional.ofNullable(limit).orElse(Defaults.Poller.LIMIT);
            settings.folder = Optional.ofNullable(folder).orElse(Defaults.IMAP_FOLDER_NAME);
            settings.batch = Optional.ofNullable(batch).orElse(Defaults.Poller.BATCH_EMAILS);
            settings.deleteOnSuccess = Optional.ofNullable(deleteOnSuccess).orElse(Defaults.Poller.DELETE_ON_SUCCESS);
            settings.markDeleteOnSuccess = Optional.ofNullable(markDeleteOnSuccess).orElse(Defaults.Poller.MARK_DELETE_ON_SUCCESS);
            return settings;
        }
    }
}
