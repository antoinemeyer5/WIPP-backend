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
package gov.nist.itl.ssd.wipp.backend.data.imageannotations.annotations;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

/**
 * @author Mylene Simon <mylene.simon at nist.gov>
 */
@RepositoryRestResource(exported = false)
public interface ImageAnnotationRepository extends MongoRepository<ImageAnnotation, String>, ImageAnnotationRepositoryCustom {

	List<ImageAnnotation> findByImageAnnotationsCollection(String imageAnnotationsCollection);

    Page<ImageAnnotation> findByImageAnnotationsCollection(String imageAnnotationsCollection, Pageable p);

    List<ImageAnnotation> findByImageAnnotationsCollectionAndImageFileNameRegex(String imageAnnotationsCollection, String imageFileName);

    Page<ImageAnnotation> findByImageAnnotationsCollectionAndImageFileNameRegex(String imageAnnotationsCollection, String imageFileName, Pageable p);

}