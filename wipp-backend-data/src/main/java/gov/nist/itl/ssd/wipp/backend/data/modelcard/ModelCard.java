package gov.nist.itl.ssd.wipp.backend.data.modelcard;

import gov.nist.itl.ssd.wipp.backend.core.model.computation.Plugin;
import gov.nist.itl.ssd.wipp.backend.core.model.computation.PluginRepository;
import gov.nist.itl.ssd.wipp.backend.core.model.job.Job;
import gov.nist.itl.ssd.wipp.backend.core.model.job.JobRepository;
import gov.nist.itl.ssd.wipp.backend.data.aimodel.AiModel;
import gov.nist.itl.ssd.wipp.backend.data.aimodel.AiModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import gov.nist.itl.ssd.wipp.backend.core.model.data.Data;
import gov.nist.itl.ssd.wipp.backend.core.rest.annotation.IdExposed;

/**
 * @author Antoine Meyer <antoine.meyer at nist.gov>
 */
@IdExposed
@Document
public class ModelCard extends Data
{
    // An ModelCard is linked to a AiModel
    private AiModel aiModel;

    // Common framework parameters
    @Id private String id;
    private String author;
    private String wippVersion; // CUSTOM ANTOINE
    private String owner; // CUSTOM ANTOINE
    /*
    private String modelType;
    private String license;
    private String contact;
    private Integer version;
    private String primaryIntendedUses;
    private String description;
    */

    // Inspired by BioImageIo requirements
    /*
    private String citation;
    private String documentation;
    private String modelName;
    private String[] inputs;
    private String[] outputsAxes;
    private String[] outputsTestTensor;
    */

    // Inspired by HuggingFace requirements
    /*
    private String language;
    private String outOfScopeUse;
    private String biasRisksLimitations;
    private String biasRecommendations;
    private String getStartedCode;
    private String[] trainingData;
    private String trainingRegime;
    private String[] testingData;
    private String[] testingFactors;
    private String[] testingMetrics;
    private String results;
    private String hardwareType;
    private Integer hoursUsed;
    private String cloudProvider;
    private String cloudRegion;
    private Integer co2Emitted;
    */

    // Constructor(s)
    public ModelCard(AiModel aiModel) {
        this.aiModel = aiModel;
        this.fill();
    }

    // Get methods
    public String getId() { return id; }
    public String getAuthor() { return author; }

    //------------------------------------------------------------------

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private PluginRepository pluginRepository;

    public void fill() {
        // from model card
        this.owner = aiModel.getOwner();
        // ...

        // get job
        //assert jobRepository != null;
        //Job job = jobRepository.findByNameContainingIgnoreCase(aiModel.getSourceJob());
        // from job
        //this.wippVersion = job.getWippVersion();
        // ...

        // get plugin
        //assert pluginRepository != null;
        //Plugin plugin = pluginRepository.findById(job.getWippExecutable()).orElse(null);
        //assert plugin != null;
        // from plugin
        //this.author = plugin.getAuthor();
        // ...
    }

}
