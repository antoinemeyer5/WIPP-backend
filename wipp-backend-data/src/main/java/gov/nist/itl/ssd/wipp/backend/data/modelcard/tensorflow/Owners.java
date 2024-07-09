package gov.nist.itl.ssd.wipp.backend.data.modelcard.tensorflow;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Owners {
    private String name;
    //private String contact;

    public Owners(String na) { this.name = na; }
    public String getName() { return name; }
}
