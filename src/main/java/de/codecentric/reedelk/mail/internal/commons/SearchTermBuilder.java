package de.codecentric.reedelk.mail.internal.commons;

import de.codecentric.reedelk.mail.component.imap.IMAPFlags;

import javax.mail.Flags;
import javax.mail.search.AndTerm;
import javax.mail.search.SearchTerm;

public class SearchTermBuilder {

    private SearchTermBuilder() {
    }

    public static SearchTerm from(IMAPFlags matcher) {
        SearchTerm seenFlag = matcher.getSeen().searchTermOf(Flags.Flag.SEEN);
        SearchTerm answeredFlag = matcher.getAnswered().searchTermOf(Flags.Flag.ANSWERED);
        SearchTerm deletedFlag = matcher.getDeleted().searchTermOf(Flags.Flag.DELETED);
        // They all must match therefore they are in 'AND'.
        return new AndTerm(new SearchTerm[]{ seenFlag, answeredFlag, deletedFlag});
    }
}
