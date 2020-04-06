package com.reedelk.mail.internal.commons;

import java.util.ArrayList;
import java.util.Arrays;

public class Address {

    public static ArrayList<String> asSerializableList(javax.mail.Address[] addresses) {
        ArrayList<String> addressesList = new ArrayList<>();
        if (addresses == null) return addressesList;
        Arrays.stream(addresses).forEach(address -> addressesList.add(address.toString()));
        return addressesList;
    }
}
