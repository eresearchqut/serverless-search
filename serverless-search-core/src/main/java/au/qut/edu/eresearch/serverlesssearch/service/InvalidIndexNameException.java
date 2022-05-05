package au.qut.edu.eresearch.serverlesssearch.service;

public class InvalidIndexNameException extends RuntimeException {


    public InvalidIndexNameException() {
        super("Invalid index name. " +
                "Index names should be less than 128 characters. " +
                "All letters must be lowercase. " +
                "Index names can’t begin with underscores (_) or hyphens (-)." +
                "Index names can’t contain spaces, commas, or the following characters: " +
                ":, \", *, +, /, \\, |, ?, #, >, or <");
    }


}
