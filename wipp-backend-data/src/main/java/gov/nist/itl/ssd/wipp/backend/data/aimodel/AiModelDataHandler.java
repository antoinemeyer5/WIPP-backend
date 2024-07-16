/*
 * This software was developed at the National Institute of Standards and
 * Technology by employees of the Federal Government in the course of
 * their official duties. Pursuant to title 17 Section 105 of the United
 * States Code this software is not subject to copyright protection and is
 * in the public domain. This software is an experimental system. NIST assumes
 * no responsibility whatsoever for its use by other parties, and makes no
 * guarantees, expressed or implied, about its quality, reliability, or
 * any other characteristic. We would appreciate acknowledgement if the
 * software is used.
 */
package gov.nist.itl.ssd.wipp.backend.data.aimodel;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gov.nist.itl.ssd.wipp.backend.core.model.computation.Plugin;
import gov.nist.itl.ssd.wipp.backend.core.model.computation.PluginRepository;
import gov.nist.itl.ssd.wipp.backend.core.model.data.BaseDataHandler;
import gov.nist.itl.ssd.wipp.backend.data.modelcard.ModelCard;
import gov.nist.itl.ssd.wipp.backend.data.modelcard.ModelCardRepository;
import gov.nist.itl.ssd.wipp.backend.data.tensorboard.TensorBoardLogsController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import gov.nist.itl.ssd.wipp.backend.core.CoreConfig;
import gov.nist.itl.ssd.wipp.backend.core.model.data.DataHandler;
import gov.nist.itl.ssd.wipp.backend.core.model.job.Job;
import gov.nist.itl.ssd.wipp.backend.core.model.job.JobExecutionException;

/**
 * @author Mohamed Ouladi <mohamed.ouladi at nist.gov>
 * @author Mylene Simon <mylene.simon at nist.gov>
 */
@Component("aiModelDataHandler")
public class AiModelDataHandler extends BaseDataHandler implements DataHandler {

	@Autowired
	CoreConfig config;

	@Autowired
	private AiModelRepository aiModelRepository;

    @Autowired
    private PluginRepository wippPluginRepository;

    @Autowired
    private TensorBoardLogsController tensorBoardLogsController;

    @Autowired
    private ModelCardRepository modelCardRepository;

	@Override
	public void importData(Job job, String outputName) throws JobExecutionException {
        AiModel tm = new AiModel(job, outputName, AiModelFramework.TENSORFLOW);
		// Set owner to job owner
        tm.setOwner(job.getOwner());
        // Set TM to private
        tm.setPubliclyShared(false);
        aiModelRepository.save(tm);

		File trainedModelFolder = new File(config.getAiModelsFolder(), tm.getId());
		trainedModelFolder.mkdirs();

		File tempOutputDir = getJobOutputTempFolder(job.getId(), outputName);
		boolean success = tempOutputDir.renameTo(trainedModelFolder);
		if (!success) {
            aiModelRepository.delete(tm);
			throw new JobExecutionException("Cannot move ai model to final destination.");
		}

		setOutputId(job, outputName, tm.getId());

        // Get plugin
        Plugin plugin = wippPluginRepository.findById(job.getWippExecutable()).orElse(null);
        assert plugin != null;

        // Create & save Model Card
        ModelCard mc = new ModelCard(tm, job, plugin);

        // Fill with TensorboardLogs data
        try {
            for(String task : new String[]{"train", "test"}) {
                List<List<String>> data =
                        tensorBoardLogsController.getCSV("6682f3d43149955bd95f59ab", task, "loss"); // todo: use real id
                float startTime = Float.parseFloat(data.get(1).getFirst());
                float endTime = Float.parseFloat(data.getLast().getFirst());
                Integer epoch = Integer.parseInt(data.getLast().get(1));
                switch(task){
                    case "train": mc.setTraining(Math.round(endTime - startTime), epoch); break;
                    case "test": mc.setTesting(Math.round(endTime - startTime), epoch); break;
                }
            }
        } catch (IOException e) { throw new RuntimeException(e); }

        // Save
        modelCardRepository.save(mc);
	}
	
	@Override
    public String exportDataAsParam(String value) {
        String aiModelId = value;
        String aiModelPath;

        // check if the input of the job is the output of another job and if so return the associated path
        String regex = "\\{\\{ (.*)\\.(.*) \\}\\}";
        Pattern pattern = Pattern.compile(regex);
        Matcher m = pattern.matcher(aiModelId);
        if (m.find()) {
            String jobId = m.group(1);
            String outputName = m.group(2);
            aiModelPath = getJobOutputTempFolder(jobId, outputName).getAbsolutePath();
        }
        // else return the path of the AI model
        else {
            File aiModelFolder = new File(config.getAiModelsFolder(), aiModelId);
            aiModelPath = aiModelFolder.getAbsolutePath();

        }
        aiModelPath = aiModelPath.replaceFirst(config.getStorageRootFolder(),config.getContainerInputsMountPath());
        return aiModelPath;

    }
	
	@Override
    public void setDataToPublic(String value) {
    	Optional<AiModel> optAiModel = aiModelRepository.findById(value);
        if(optAiModel.isPresent()) {
            AiModel aiModel = optAiModel.get();
            if (!aiModel.isPubliclyShared()) {
                aiModel.setPubliclyShared(true);
                aiModelRepository.save(aiModel);
            }
        }
    }

}
