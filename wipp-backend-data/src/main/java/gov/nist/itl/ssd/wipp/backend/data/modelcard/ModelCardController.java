package gov.nist.itl.ssd.wipp.backend.data.modelcard;

import gov.nist.itl.ssd.wipp.backend.core.CoreConfig;
import gov.nist.itl.ssd.wipp.backend.data.aimodel.AiModel;
import gov.nist.itl.ssd.wipp.backend.data.genericdatacollection.genericfiles.GenericFile;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Optional;

/**
 * Model Card controller
 *
 * @author Antoine Meyer <antoine.meyer at nist.gov>
 */

/*@RestController
@Tag(name="ModelCard Entity")
@RequestMapping(CoreConfig.BASE_URI + "/modelCards/{aiModelId}/aiModel")
public class ModelCardController {
    @Autowired
    private MongoTemplate mongoTemplate;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ModelCard get(@PathVariable("aiModelId") String aiModelId) throws IOException
    {
        // todo

        // cherche model card ou :
        //          modelcard.aiModel.id == aiModelId


    }

}*/
