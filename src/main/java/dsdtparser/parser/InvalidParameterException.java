/*
 * Decompiled with CFR 0_115.
 */
package dsdtparser.parser;

public class InvalidParameterException
extends Exception {
    private String reason;

    public InvalidParameterException(String reason) {
        super(reason);
        this.reason = reason;
    }

    public String getReason() {
        return this.reason;
    }
}

