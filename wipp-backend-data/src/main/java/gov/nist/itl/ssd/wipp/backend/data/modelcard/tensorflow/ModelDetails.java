package gov.nist.itl.ssd.wipp.backend.data.modelcard.tensorflow;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document
public class ModelDetails
{
    /***************** ATTRIBUTE(S) *****************/
    private String name;
    private String overview;
    private Version version;
    private Owners owners;
    //private String licences;
    //private String reference;
    private String citation;

    /***************** CONSTRUCTOR(S) *****************/
    public ModelDetails(String na, String ov, Version ve, Owners ow, String ci)
    {
        this.name = na;
        this.overview = ov;
        this.version = ve;
        this.owners = ow;
        this.citation = ci;
    }

    /***************** METHOD(S) *****************/
    public String getName() { return name; }
    public String getOverview() { return overview; }
    public Version getVersion() { return version; }
    public Owners getOwners() { return owners; }
    public String getCitation() { return citation; }
}
