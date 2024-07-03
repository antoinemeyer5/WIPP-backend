package gov.nist.itl.ssd.wipp.backend.data.modelcard;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import gov.nist.itl.ssd.wipp.backend.core.CoreConfig;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Model Card controller
 *
 * @author Antoine Meyer <antoine.meyer at nist.gov>
 */
@RestController
@Tag(name="ModelCard Entity")
@RequestMapping(CoreConfig.BASE_URI + "/modelCards/{id}/export")
public class ModelCardController {

    @Autowired
    ModelCardRepository modelCardRepository;

    @RequestMapping(
            value = "tensorflow",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<byte[]> tensorflow(@PathVariable("id") String id) throws IOException
    {
        // Get
        Optional<ModelCard> mc = modelCardRepository.findById(id);
        if(!mc.isPresent()){
            throw new ResourceNotFoundException("ModelCard not found.");
        }

        // Convert ModelCard object
        byte[] bytes = new byte[0];
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
            String json = mapper.writeValueAsString(mc.get());
            bytes = json.getBytes();
        }
        catch (JsonGenerationException | JsonMappingException e) { e.printStackTrace(); }
        
        //
        HttpHeaders head = new HttpHeaders();
        head.add(
                "content-disposition",
                "attachment; filename=\"WIPP_ModelCard_Tensorflow.json\""
        );
        List<String> exposedHead = List.of("content-disposition");
        head.setAccessControlExposeHeaders(exposedHead);

        //
        return ResponseEntity
                .ok()
                .headers(head)
                .contentType(MediaType.APPLICATION_JSON)
                .contentLength(bytes.length)
                .body(bytes);
    }

    @RequestMapping(value = "huggingface", method = RequestMethod.GET)
    public void huggingface(@PathVariable("id") String id) throws IOException {
        /* todo */
    }

    @RequestMapping(value = "bioimageio", method = RequestMethod.GET)
    public void bioimageio(@PathVariable("id") String id) throws IOException {
        /* todo */
    }

}
