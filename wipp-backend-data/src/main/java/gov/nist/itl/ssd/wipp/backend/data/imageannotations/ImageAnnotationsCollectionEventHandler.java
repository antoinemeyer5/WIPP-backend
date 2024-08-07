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
package gov.nist.itl.ssd.wipp.backend.data.imageannotations;

import gov.nist.itl.ssd.wipp.backend.core.CoreConfig;
import gov.nist.itl.ssd.wipp.backend.core.rest.exception.ClientException;
import gov.nist.itl.ssd.wipp.backend.core.rest.exception.NotFoundException;
import gov.nist.itl.ssd.wipp.backend.data.imageannotations.annotations.ImageAnnotationHandler;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mylene Simon <mylene.simon at nist.gov>
 */
@Component
@RepositoryEventHandler(ImageAnnotationsCollection.class)
public class ImageAnnotationsCollectionEventHandler {

    private static final Logger LOGGER = Logger.getLogger(ImageAnnotationsCollectionEventHandler.class.getName());

    @Autowired
    private ImageAnnotationsCollectionRepository imageAnnotationsCollectionRepository;
    
    @Autowired
    private ImageAnnotationHandler imageAnnotationRepository;
    
    @Autowired
    private ImageAnnotationsCollectionLogic imageAnnotationsCollectionLogic;

    @Autowired
    CoreConfig config;

    @PreAuthorize("isAuthenticated()")
    @HandleBeforeCreate
    public void handleBeforeCreate(ImageAnnotationsCollection imageAnnotationsCollection) {
    	// Assert collection name is unique
    	imageAnnotationsCollectionLogic.assertCollectionNameUnique(
    			imageAnnotationsCollection.getName());
    	
    	// Set creation date to current date
        imageAnnotationsCollection.setCreationDate(new Date());
        
        // Set the owner to the connected user
        imageAnnotationsCollection.setOwner(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @HandleBeforeSave
    @PreAuthorize("isAuthenticated() and (hasRole('admin') or #imageAnnotationsCollection.owner == authentication.name)")
    public void handleBeforeSave(ImageAnnotationsCollection imageAnnotationsCollection) {
    	// Assert collection exists
        Optional<ImageAnnotationsCollection> result = imageAnnotationsCollectionRepository.findById(
                imageAnnotationsCollection.getId());
        if (!result.isPresent()) {
            throw new NotFoundException("Image annotations collection with id " + imageAnnotationsCollection.getId() + " not found");
        }

        ImageAnnotationsCollection oldTc = result.get();
        
        // A public collection cannot become private
        if (oldTc.isPubliclyShared() && !imageAnnotationsCollection.isPubliclyShared()){
            throw new ClientException("Can not set change a public collection to private.");
        }
        
        // Owner cannot be changed
        if (!Objects.equals(
        		imageAnnotationsCollection.getOwner(),
                oldTc.getOwner())) {
            throw new ClientException("Can not change owner.");
        }
    }
    
    @HandleBeforeDelete
    @PreAuthorize("isAuthenticated() and (hasRole('admin') or #imageAnnotationsCollection.owner == authentication.name)")
    public void handleBeforeDelete(ImageAnnotationsCollection imageAnnotationsCollection) {
    	// Assert collection exists
    	Optional<ImageAnnotationsCollection> result = imageAnnotationsCollectionRepository.findById(
    			imageAnnotationsCollection.getId());
    	if (!result.isPresent()) {
        	throw new NotFoundException("Image annotations collection with id " + imageAnnotationsCollection.getId() + " not found");
        }

        ImageAnnotationsCollection oldTc = result.get();
    }

    @HandleAfterDelete
    public void handleAfterDelete(ImageAnnotationsCollection imageAnnotationsCollection) {
    	// Delete all files from deleted collection
    	imageAnnotationRepository.deleteAll(imageAnnotationsCollection.getId());
    	File imageAnnotationsCollectionFolder = new File (config.getImageAnnotationsFolder(), imageAnnotationsCollection.getId());
    	try {
    		FileUtils.deleteDirectory(imageAnnotationsCollectionFolder);
    	} catch (IOException e) {
    		LOGGER.log(Level.WARNING, "Was not able to delete the annotations folder " + imageAnnotationsCollectionFolder);
    	}	
    }
}

