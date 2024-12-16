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
package gov.nist.itl.ssd.wipp.backend.data.csvCollection.csv;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import gov.nist.itl.ssd.wipp.backend.core.CoreConfig;
import gov.nist.itl.ssd.wipp.backend.core.rest.exception.ClientException;
import gov.nist.itl.ssd.wipp.backend.core.rest.exception.NotFoundException;
import gov.nist.itl.ssd.wipp.backend.data.aimodelcard.AiModelCard;
import gov.nist.itl.ssd.wipp.backend.data.csvCollection.CsvCollection;
import gov.nist.itl.ssd.wipp.backend.data.csvCollection.CsvCollectionRepository;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.*;
import org.springframework.hateoas.server.EntityLinks;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.apache.commons.io.FileUtils.getFile;

/**
 *
 * @author Samia Benjida <samia.benjida at nist.gov>
 * @author Mylene Simon <mylene.simon at nist.gov>
 */
@RestController
@Tag(name="CsvCollection Entity")
@RequestMapping(CoreConfig.BASE_URI + "/csvCollections/{csvCollectionId}/csv")
@ExposesResourceFor(Csv.class)
public class CsvController {

    @Autowired
    private EntityLinks entityLinks;

    @Autowired
    private CsvRepository csvRepository;

    @Autowired
    private CsvCollectionRepository csvCollectionRepository;

    @Autowired
    private CsvHandler csvHandler;

    @Autowired
    private CoreConfig coreConfig;

    @RequestMapping(value = "", method = RequestMethod.GET)
    @PreAuthorize("hasRole('admin') or @csvCollectionSecurity.checkAuthorize(#csvCollectionId, false)")
    public HttpEntity<PagedModel<EntityModel<Csv>>> getFilesPage(
            @PathVariable("csvCollectionId") String csvCollectionId,
            @ParameterObject @PageableDefault Pageable pageable,
            @Parameter(hidden = true) PagedResourcesAssembler<Csv> assembler) {
        Page<Csv> files = csvRepository.findByCsvCollection(
                csvCollectionId, pageable);
        PagedModel<EntityModel<Csv>> resources
                = assembler.toModel(files);
        resources.forEach(
                resource -> processResource(csvCollectionId, resource));
        return new ResponseEntity<>(resources, HttpStatus.OK);
    }

    @RequestMapping(value = "", method = RequestMethod.DELETE)
    @PreAuthorize("isAuthenticated() and "
            + "(hasRole('admin') or @csvCollectionSecurity.checkAuthorize(#csvCollectionId, true))")
    public void deleteAllFiles(
            @PathVariable("csvCollectionId") String csvCollectionId) {
        Optional<CsvCollection> tc =csvCollectionRepository.findById(
                csvCollectionId);
        if (!tc.isPresent()) {
            throw new NotFoundException("Collection not found");
        }
        if (tc.get().isLocked()) {
            throw new ClientException("Collection locked.");
        }
        csvHandler.deleteAll(csvCollectionId);
    }

    @RequestMapping(value = "/{fileName:.+}", method = RequestMethod.DELETE)
    @PreAuthorize("isAuthenticated() and "
            + "(hasRole('admin') or @csvCollectionSecurity.checkAuthorize(#csvCollectionId, true))")
    public void deleteFile(
            @PathVariable("csvCollectionId") String csvCollectionId,
            @PathVariable("fileName") String fileName) {
        Optional<CsvCollection> tc = csvCollectionRepository.findById(
                csvCollectionId);
        if (!tc.isPresent()) {
            throw new NotFoundException("Collection not found");
        }
        if (tc.get().isLocked()) {
            throw new ClientException("Collection locked.");
        }
        csvHandler.delete(csvCollectionId, fileName);
    }

    protected void processResource(String csvCollectionId,
                                   EntityModel<Csv> resource) {
        Csv file = resource.getContent();
        Link link = entityLinks.linkForItemResource(
                        CsvCollection.class, csvCollectionId)
                .slash("csv")
                .slash(file.getFileName())
                .withSelfRel();
        resource.add(link);
    }

    @RequestMapping(
            value="/{fileName:.+}/content",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    // @PreAuthorize("isAuthenticated() and hasRole('admin')")
    @PreAuthorize("isAuthenticated() and (hasRole('admin') or @csvCollectionSecurity.checkAuthorize(#csvCollectionId, true))")
    public ResponseEntity<byte[]> getContent(
            @PathVariable("csvCollectionId") String csvCollectionId,
            @PathVariable("fileName") String fileName) throws FileNotFoundException {
        // (1) Get Csv
        StringBuilder csv = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(coreConfig.getCsvCollectionsFolder() + "/" + csvCollectionId + "/" + fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                //String[] values = line.split(":");
                //csv.add(Arrays.asList(values));
                csv.append(line);
                csv.append(",");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // (2) Convert Into Bytes
        byte[] bytes = new byte[0];
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
            bytes = mapper.writeValueAsString(csv.toString()).getBytes();
        }
        catch (JsonProcessingException e) { e.printStackTrace(); }

        // (3) Setup Response Head
        HttpHeaders head = new HttpHeaders();
        head.add(
                "content-disposition",
                "attachment; filename=\"" + fileName
        );
        List<String> exposedHead = List.of("content-disposition");
        head.setAccessControlExposeHeaders(exposedHead);

        return ResponseEntity
                .ok()
                .headers(head)
                .contentType(MediaType.APPLICATION_JSON)
                .contentLength(bytes.length)
                .body(bytes);
    }

}
