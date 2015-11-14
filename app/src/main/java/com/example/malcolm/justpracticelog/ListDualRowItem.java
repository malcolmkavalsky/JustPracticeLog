package com.example.malcolm.justpracticelog;

/**
 * Created by malcolm on 6/26/15.
 */
public class ListDualRowItem {
    private String top;
    private String bottom;
    private long reference; // Extra for binding to a database index

    public long getReference() {
        return reference;
    }

    public void setReference(long reference) {
        this.reference = reference;
    }

    public String getTop() {
        return top;
    }

    public void setTop(String top) {
        this.top = top;
    }

    public String getBottom() {
        return bottom;
    }

    public void setBottom(String bottom) {
        this.bottom = bottom;
    }

}
