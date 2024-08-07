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

import gov.nist.itl.ssd.wipp.backend.core.rest.PaginationParameterTemplatesHelper;
import gov.nist.itl.ssd.wipp.backend.data.imageannotations.annotations.ImageAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.EntityLinks;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

/**
*
* @author Mylene Simon <mylene.simon at nist.gov>
*/
@Component
public class ImageAnnotationsCollectionResourceProcessor implements RepresentationModelProcessor<EntityModel<ImageAnnotationsCollection>>{

	@Autowired
	private PaginationParameterTemplatesHelper assembler;

	@Autowired
	private EntityLinks entityLinks;

	@Override
	public EntityModel<ImageAnnotationsCollection> process(EntityModel<ImageAnnotationsCollection> resource) {
		ImageAnnotationsCollection imageAnnotationsCollection = resource.getContent();
		
        Link downloadLink = linkTo(ImageAnnotationsCollectionDownloadController.class,
				imageAnnotationsCollection.getId())
        		.slash("request")
                .withRel("download");
        resource.add(downloadLink);

		Link annotationsLink = entityLinks.linkForItemResource(
				ImageAnnotation.class, imageAnnotationsCollection.getId())
				.slash("annotations")
				.withRel("annotations");
		resource.add(assembler.appendPaginationParameterTemplates(annotationsLink));
        
		return resource;
	}
}
	