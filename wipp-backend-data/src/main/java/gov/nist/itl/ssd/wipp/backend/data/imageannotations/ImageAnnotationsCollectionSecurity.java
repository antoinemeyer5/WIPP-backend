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

import gov.nist.itl.ssd.wipp.backend.core.rest.exception.ForbiddenException;
import gov.nist.itl.ssd.wipp.backend.core.rest.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Image Annotations Collection Security service
 * 
 * @author Mylene Simon <mylene.simon at nist.gov>
 *
 */
@Service
public class ImageAnnotationsCollectionSecurity {
	
	@Autowired
    private ImageAnnotationsCollectionRepository imageAnnotationsCollectionRepository;

    public boolean checkAuthorize(String imageAnnotationsCollectionId, Boolean editMode) {
        Optional<ImageAnnotationsCollection> imageAnnotationsCollection = imageAnnotationsCollectionRepository.findById(imageAnnotationsCollectionId);
        if (imageAnnotationsCollection.isPresent()){
            return(checkAuthorize(imageAnnotationsCollection.get(), editMode));
        }
        else {
            throw new NotFoundException("Image annotations collection with id " + imageAnnotationsCollectionId + " not found");
        }
    }

    public static boolean checkAuthorize(ImageAnnotationsCollection imageAnnotationsCollection, Boolean editMode) {
        String imageAnnotationsCollectionOwner = imageAnnotationsCollection.getOwner();
        String connectedUser = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!imageAnnotationsCollection.isPubliclyShared() && (imageAnnotationsCollectionOwner == null || !imageAnnotationsCollectionOwner.equals(connectedUser))) {
            throw new ForbiddenException("You do not have access to this image annotations collection");
        }
        if (imageAnnotationsCollection.isPubliclyShared() && editMode && (imageAnnotationsCollectionOwner == null || !imageAnnotationsCollectionOwner.equals(connectedUser))){
            throw new ForbiddenException("You do not have the right to edit this image annotations collection");
        }
        return(true);
    }

}
