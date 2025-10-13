package com.capacitor.plugin.storefront;

import com.getcapacitor.Logger;

public class CapacitorStorefront {

    public String echo(String value) {
        Logger.info("Echo", value);
        return value;
    }
}
