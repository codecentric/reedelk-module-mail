package com.reedelk.mail.internal.commons;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Address {

    private Address() {
    }

    public static Serializable asSerializableList(List<? extends javax.mail.Address> addresses) {
        ArrayList<String> addressesList = new ArrayList<>();
        if (addresses == null) return addressesList;
        addresses.forEach(address -> addressesList.add(address.toString()));
        return addressesList;
    }
}
