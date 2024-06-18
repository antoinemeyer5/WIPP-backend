package gov.nist.itl.ssd.wipp.backend.data.modelcards;

import gov.nist.itl.ssd.wipp.backend.data.aimodel.AIModel;
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

    private AIModel aiModel;

    private String author;
    private String type;
    private String overview;
    private String license;
    private String contact;

    public ModelCards(AIModel aiModel)
    {
        this.aiModel = aiModel;
    }

    public String getId() { return id; }
    public void setAuthor(String author) { this.author = author; }
    public void setType(String type) { this.type = type; }
}
