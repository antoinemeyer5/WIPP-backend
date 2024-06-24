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

import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleAfterSave;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import gov.nist.itl.ssd.wipp.backend.core.CoreConfig;
import gov.nist.itl.ssd.wipp.backend.core.rest.exception.ClientException;
import gov.nist.itl.ssd.wipp.backend.core.rest.exception.NotFoundException;
import gov.nist.itl.ssd.wipp.backend.data.tensorboard.TensorboardLogs;
import gov.nist.itl.ssd.wipp.backend.data.tensorboard.TensorboardLogsRepository;

/**
 * @author Mylene Simon <mylene.simon at nist.gov>
 *
 */
@Component
@RepositoryEventHandler(AiModel.class)
public class AiModelEventHandler {
	
	@Autowired
    AiModelRepository aiModelRepository;
	
	@Autowired
    AiModelLogic aiModelLogic;
	
	@Autowired
	TensorboardLogsRepository tensorboardLogsRepository;
	
	@Autowired
    CoreConfig config;

    @PreAuthorize("isAuthenticated()")
    @HandleBeforeCreate
    public void handleBeforeCreate(AiModel aiModel) {
    	throw new ClientException("Creation of AiModel via REST API is not allowed.");
    }

    @HandleBeforeSave
    @PreAuthorize("isAuthenticated() and (hasRole('admin') or #aiModel.owner == authentication.name)")
    public void handleBeforeSave(AiModel aiModel) {
    	// Assert data exists
        Optional<AiModel> result = aiModelRepository.findById(
                aiModel.getId());
        if (!result.isPresent()) {
            throw new NotFoundException("AiModel with id " + aiModel.getId() + " not found");
        }

        AiModel oldSv = result.get();
        
        // Source job cannot be changed
        if (!Objects.equals(
                aiModel.getSourceJob(),
        		oldSv.getSourceJob())) {
            throw new ClientException("Can not change source job.");
        }

        // Assert data name is unique
        if (!Objects.equals(aiModel.getName(), oldSv.getName())) {
            aiModelLogic.assertAiModelNameUnique(
                    aiModel.getName());
        }
        
        // A public data cannot become private
        if (oldSv.isPubliclyShared() && !aiModel.isPubliclyShared()){
            throw new ClientException("Can not change a public data to private.");
        }
                
        // Owner cannot be changed
        if (!Objects.equals(
                aiModel.getOwner(),
        		oldSv.getOwner())) {
            throw new ClientException("Can not change owner.");
        }
    }
    
    @HandleAfterSave
    public void handleAfterSave(AiModel aiModel) {
    	// If AiModel was made public, check for associated TensorboardLogs
    	if (aiModel.isPubliclyShared() && aiModel.getSourceJob() != null) {
    		TensorboardLogs tensorboardLogs = tensorboardLogsRepository.findOneBySourceJob(
                    aiModel.getSourceJob());
    		if (!tensorboardLogs.isPubliclyShared()) {
    			tensorboardLogs.setPubliclyShared(true);
    			tensorboardLogsRepository.save(tensorboardLogs);
    		}
    	}
    }
}
