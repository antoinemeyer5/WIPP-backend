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
package gov.nist.itl.ssd.wipp.backend.data.aimodelcard;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
 * Web Image Processing Pipeline (WIPP) AI model card for Artificial Intelligence (AI) models.
 * @author Antoine Meyer <antoine.meyer at nist.gov>
 */
@IdExposed
@Document
public class AiModelCard extends Data
{
    /***************** ATTRIBUTE(S) *****************/
    @Id
    private String id;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Date version;

    private String aiModelId;
    private String name;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Date date;
    private String framework;

    private Map<String, String> trainingData;

    private String author;
    private String description;
    private String citation;
    private String operationType;
    private String architecture;

    private Map<String, Float> training;
    private Map<String, Float> testing;

    private String license;

    /***************** CONSTRUCTOR(S) *****************/
    public AiModelCard(){}

    public AiModelCard(AiModel aiModel, Job job, Plugin plugin)
    {
        this.version = new Date();

        this.aiModelId = aiModel.getId();
        this.name = aiModel.getName();
        this.date = aiModel.getCreationDate();
        this.framework = aiModel.getFramework();

        this.trainingData = job.getParameters();

        this.author = plugin.getAuthor();
        this.description = plugin.getDescription();
        this.citation = plugin.getCitation();
        this.operationType = plugin.getOperationType();
        // WARNING!: first output architecture used to set AiModel architecture
        Map<String, Object> outputs_options = plugin.getOutputs().getFirst().getOptions();
        if(outputs_options!=null && !outputs_options.isEmpty()) {
            this.architecture = outputs_options.get("architecture").toString();
        } else {
            this.architecture = "N/A";
        }

        this.training = new HashMap<>();
        this.testing = new HashMap<>();

        this.license = "Unlicense";
    }

    /***************** GET *****************/
    public String getId() { return id; }
    public Date getVersion() { return version; }

    public String getAiModelId() { return aiModelId; }
    public String getName() { return name; }
    public Date getDate() { return date; }
    public String getFramework() { return framework; }

    public Map<String, String> getTrainingData() { return trainingData; }

    public String getAuthor() { return author; }
    public String getDescription() { return description; }
    public String getCitation() { return citation; }
    public String getOperationType() { return operationType; }
    public String getArchitecture() { return architecture; }

    public Map<String, Float> getTraining() { return training; }
    public Map<String, Float> getTesting() { return testing; }

    public String getLicense() {return license;}

    /***************** SET *****************/
    public void addTrainingEntries(String field, Float value) { this.training.put(field, value); }
    public void addTestingEntries(String field, Float value) { this.testing.put(field, value); }
}
