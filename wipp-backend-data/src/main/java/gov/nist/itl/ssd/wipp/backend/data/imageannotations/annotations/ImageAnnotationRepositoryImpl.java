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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

/**
 *
 * @author Mylene Simon <mylene.simon at nist.gov>
 */
public class ImageAnnotationRepositoryImpl implements ImageAnnotationRepositoryCustom {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void deleteByImageAnnotationsCollection(String imageAnnotationsCollection) {
        mongoTemplate.remove(Query.query(
                Criteria.where("imageAnnotationsCollection").is(imageAnnotationsCollection)),
                ImageAnnotation.class);
    }

    @Override
    public void deleteByImageAnnotationsCollectionAndImageFileName(String imageAnnotationsCollection,
            String imageFileName) {
        mongoTemplate.remove(Query.query(
                Criteria.where("imageAnnotationsCollection").is(imageAnnotationsCollection)
                .and("imageFileName").is(imageFileName)),
                ImageAnnotation.class);
    }

}
