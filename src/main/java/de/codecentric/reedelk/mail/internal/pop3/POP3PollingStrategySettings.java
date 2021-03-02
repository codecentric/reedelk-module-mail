package de.codecentric.reedelk.mail.internal.pop3;

import de.codecentric.reedelk.mail.component.POP3Configuration;
import de.codecentric.reedelk.mail.internal.commons.Defaults;

import java.util.Optional;

public class POP3PollingStrategySettings {

    private POP3Configuration configuration;
    private boolean deleteOnSuccess;
    private boolean batch;
    private int limit;

    private POP3PollingStrategySettings() {
    }

    public POP3Configuration getConfiguration() {
        return configuration;
    }

    public boolean isDeleteOnSuccess() {
        return deleteOnSuccess;
    }

    public boolean isBatch() {
        return batch;
    }

    public int getLimit() {
        return limit;
    }

    public static Builder create() {
        return new Builder();
    }

    public static class Builder {
        private POP3Configuration configuration;
        private Boolean deleteOnSuccess;
        private Boolean batch;
        private Integer limit;

        public Builder configuration(POP3Configuration configuration) {
            this.configuration = configuration;
            return this;
        }

        public Builder deleteOnSuccess(Boolean deleteOnSuccess) {
            this.deleteOnSuccess = deleteOnSuccess;
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

        public POP3PollingStrategySettings build() {
            POP3PollingStrategySettings settings = new POP3PollingStrategySettings();
            settings.configuration = configuration;
            settings.limit = Optional.ofNullable(limit).orElse(Defaults.Poller.LIMIT);
            settings.batch = Optional.ofNullable(batch).orElse(Defaults.Poller.BATCH_EMAILS);
            settings.deleteOnSuccess = Optional.ofNullable(deleteOnSuccess).orElse(Defaults.Poller.DELETE_ON_SUCCESS);
            return settings;
        }
    }
}
