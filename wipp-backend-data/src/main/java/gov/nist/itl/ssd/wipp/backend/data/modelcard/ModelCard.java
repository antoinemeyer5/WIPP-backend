/**
 * NIST-developed software is provided by NIST as a public service. You may
 * use, copy, and distribute copies of the software in any medium, provided
 * that you keep intact this entire notice. You may improve, modify, and create
 * derivative works of the software or any portion of the software, and you may
 * copy and distribute such modifications or works. Modified works should carry
 * a notice stating that you changed the software and should note the date and
 * nature of any such change. Please explicitly acknowledge the National
 * Institute of Standards and Technology as the source of the software.
 *
 * NIST-developed software is expressly provided "AS IS." NIST MAKES NO
 * WARRANTY OF ANY KIND, EXPRESS, IMPLIED, IN FACT, OR ARISING BY OPERATION OF
 * LAW, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTY OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, NON-INFRINGEMENT, AND DATA ACCURACY. NIST
 * NEITHER REPRESENTS NOR WARRANTS THAT THE OPERATION OF THE SOFTWARE WILL BE
 * UNINTERRUPTED OR ERROR-FREE, OR THAT ANY DEFECTS WILL BE CORRECTED. NIST
 * DOES NOT WARRANT OR MAKE ANY REPRESENTATIONS REGARDING THE USE OF THE
 * SOFTWARE OR THE RESULTS THEREOF, INCLUDING BUT NOT LIMITED TO THE
 * CORRECTNESS, ACCURACY, RELIABILITY, OR USEFULNESS OF THE SOFTWARE.
 *
 * You are solely responsible for determining the appropriateness of using and
 * distributing the software and you assume all risks associated with its use,
 * including but not limited to the risks and costs of program errors,
 * compliance with applicable laws, damage to or loss of data, programs or
 * equipment, and the unavailability or interruption of operation. This
 * software is not intended to be used in any situation where a failure could
 * cause risk of injury or damage to property. The software developed by NIST
 * employees is not subject to copyright protection within the United States.
 */
package gov.nist.itl.ssd.wipp.backend.data.modelcard;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import gov.nist.itl.ssd.wipp.backend.core.model.data.Data;
import gov.nist.itl.ssd.wipp.backend.core.rest.annotation.IdExposed;
import gov.nist.itl.ssd.wipp.backend.core.model.computation.Plugin;
import gov.nist.itl.ssd.wipp.backend.core.model.job.Job;
import gov.nist.itl.ssd.wipp.backend.data.aimodel.AiModel;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * Web Image Processing Pipeline (WIPP) model card for Artificial Intelligence (AI) models.
 * @author Antoine Meyer <antoine.meyer at nist.gov>
 */
@IdExposed
@Document
public class ModelCard extends Data
{
    /***************** ATTRIBUTE(S) *****************/
    @Id
    private String id;

    /**
     * AI model id corresponding to this ModelCard object.
     */
    private String aiModelId;

    /**
     * Human-readable name of this model card.
     * E.g. ['My Model Card', 'ModelCardName']
     * Filling source: aiModel.getName() or job.getName() or plugin.getName()
     */
    private String name;

    /**
     * The authors are the creators of the model and the primary points of
     *  contact.
     * E.g. ['[firstname] [lastname]', '[url]']
     * Filling source: plugin.getAuthor()
     */
    private String author;

    /**
     * The version of the resource following SemVer 2.0.
     * E.g. ['0.0.1', '2.0.0']
     * Filling source: job.getWippVersion() or plugin.getVersion()
     */
    private String version;

    /**
     * Platform where the model will be published.
     * E.g. ['tensorflow', 'pytorch']
     * Filling source: aiModel.getFramework()
     */
    private String framework;

    /**
     * Specialized resource type 'model'.
     * E.g. ['Naive Bayes classifier', 'Convolutional Neural Network',
     *  'Supervision/Learning Method', 'Machine Learning Type']
     * Filling source: Java enums
     */
    private String type;

    // todo: comments
    private String architecture;

    /**
     * Creation date.
     * E.g. [2020-01-15T10:47:54Z, 2024-06-26T01:01:01Z]
     * Filling source: aiModel.getCreationDate() or job.getCreationDate()
     */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Date date;

    // todo: comments
    private Integer[] training;
    private Integer[] testing;

    /**
     * Provide basic details about the model.
     * E.g. ['Some text..', '[url]']
     * Filling source: plugin.getDescription()
     */
    private String description;

    /**
     * url or relative path to a markdown file with additional documentation.
     * E.g. ['Some text..', '[url]']
     * Filling source: [SOURCE?]
     */
    //private String documentation;

    /**
     * Name and link to the license being used.
     * E.g. ['CC0-1.0', 'MIT', 'BSD-2-Clause']
     * Filling source: Java enums
     */
    private String license;

    /**
     * Provides a way for people to contact the Model Card authors.
     * E.g. ['[firstname].[lastname]@nist.gov', '[username]@[domain]']
     * Filling source: [SOURCE?]
     */
    // private String contact;

    /**
     * The developers’ preferred citation for this model.
     * E.g. ['https://dl.acm.org/doi/10.1145/3287560.3287596', '[url]']
     * Filling source: plugin.getCitation()
     */
    private String citation;

    /**
     * Describes the input type expected by this model.
     * E.g. ['images', '[type]']
     * Filling source: plugin.getInputs() or Java enums
     */
    // private List<> inputs;

    /**
     * Describes the output type expected by this model.
     * E.g. ['images', 'text', '[type]']
     * Filling source: plugin.getOutputs() or Java enums
     */
    // private List<> outputs;

    /**
     * Pointer to input collection in WIPP.
     * E.g. ['[url]']
     * Filling source: [SOURCE?]
     */
    // private String trainingData;

    /***************** CONSTRUCTOR(S) *****************/
    public ModelCard(){}

    public ModelCard(AiModel aiModel, Job job, Plugin plugin)
    {
        this.aiModelId = aiModel.getId();           // AiModel
        this.name = aiModel.getName();              // AiModel
        this.author = plugin.getAuthor();           // Plugin
        this.version = plugin.getVersion();         // Plugin
        this.framework = aiModel.getFramework();    // AiModel
        this.type = "Untype";                       // Can be selected -> todo: add in PM
        this.architecture = "Unknown";              // Can be selected -> todo: add in PM
        this.date = aiModel.getCreationDate();      // AiModel
        this.training = new Integer[]{-1, -1};      // Tensorboard Logs
        this.testing = new Integer[]{-1, -1};       // Tensorboard Logs
        this.description = plugin.getDescription(); // Plugin
        this.license = "Unlicense";                 // Can be selected -> todo: add in PM
        this.citation = plugin.getCitation();       // Plugin

        // input type -> ?? PM?
        // output type -> ?? PM?
    }

    /***************** METHOD(S) *****************/
    public String getId() { return id; }
    public String getAiModelId() { return aiModelId; }
    public String getName() { return name; }
    public String getAuthor() { return author; }
    public String getVersion() { return version; }
    public String getFramework() { return framework; }
    public String getType() { return type; }
    public String getArchitecture() { return architecture; }
    public Date getDate() { return date; }
    public Integer[] getTraining() { return training; }
    public Integer[] getTesting() { return testing; }
    public String getDescription() { return description; }
    public String getCitation() { return citation; }
    public String getLicense() {return license;}

    public void setTraining(Integer time, Integer epoch) {
        this.training[0] = time;
        this.training[1] = epoch;
    }
    public void setTesting(Integer time, Integer epoch) {
        this.testing[0] = time;
        this.testing[1] = epoch;
    }
}
