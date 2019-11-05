package com.hub.schoolAid;

public class AdmissionNumberSequence {
    private String prefix;
    private String suffix;
    private int count;

    @Override
    public String toString() {
        // format the admission number and return to the caller
        int newCount = this.count;
        String countSeq;
        if(newCount > 0 && newCount < 100) {
            countSeq = "00" + String.valueOf(newCount);
        } else countSeq = String.valueOf(newCount);


        // check if the suffix is empty
        if(this.suffix.length() < 1)
            this.suffix = "";

        // check if the prefix is empty
        if(this.prefix.length()<1)
            this.suffix = "";

        return prefix+suffix+countSeq;
    }
}
