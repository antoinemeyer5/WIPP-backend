package gov.nist.itl.ssd.wipp.backend.data.modelcard.tensorflow;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Version {
    private String name;
    //private Date date;
    //private String diff;

    public Version(String na) { this.name = na; }
    public String getName() { return name; }
}
