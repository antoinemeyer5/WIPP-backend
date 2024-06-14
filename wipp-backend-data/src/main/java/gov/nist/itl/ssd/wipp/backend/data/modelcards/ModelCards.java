package gov.nist.itl.ssd.wipp.backend.data.modelcards;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import gov.nist.itl.ssd.wipp.backend.core.model.data.Data;
import gov.nist.itl.ssd.wipp.backend.core.rest.annotation.IdExposed;

@IdExposed
@Document
public class ModelCards extends Data
{
    @Id
    private String id;
    private String author;
    private String type;

    public ModelCards()
    {
        this.author = "";
        this.type = "";
    }

    public String getId() { return id; }
    public void setAuthor(String author) { this.author = author; }
    public void setType(String type) { this.type = type; }
}
