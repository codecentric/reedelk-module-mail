package com.reedelk.mail.component.imap;

import com.reedelk.runtime.api.annotation.DisplayName;

import javax.mail.Flags;
import javax.mail.search.FlagTerm;
import javax.mail.search.OrTerm;
import javax.mail.search.SearchTerm;

public enum IMAPFlag {

    @DisplayName("Yes")
    YES {
        @Override
        public SearchTerm searchTermOf(Flags.Flag flag) {
            return new FlagTerm(new Flags(flag), true);
        }
    },

    @DisplayName("No")
    NO {
        @Override
        public SearchTerm searchTermOf(Flags.Flag flag) {
            return new FlagTerm(new Flags(flag), false);
        }
    },

    @DisplayName("Both")
    BOTH {
        @Override
        public SearchTerm searchTermOf(Flags.Flag flag) {
            return new OrTerm(
                    new FlagTerm(new Flags(flag), true),
                    new FlagTerm(new Flags(flag), false));
        }
    };

    public abstract SearchTerm searchTermOf(Flags.Flag flag);
}
