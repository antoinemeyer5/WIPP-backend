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
package gov.nist.itl.ssd.wipp.backend.data.imagescollection.metadatafiles;

import gov.nist.itl.ssd.wipp.backend.core.CoreConfig;
import gov.nist.itl.ssd.wipp.backend.core.model.data.DataDownloadToken;
import gov.nist.itl.ssd.wipp.backend.core.model.data.DataDownloadTokenRepository;
import gov.nist.itl.ssd.wipp.backend.core.rest.DownloadUrl;
import gov.nist.itl.ssd.wipp.backend.core.rest.exception.ClientException;
import gov.nist.itl.ssd.wipp.backend.core.rest.exception.ForbiddenException;
import gov.nist.itl.ssd.wipp.backend.core.rest.exception.NotFoundException;
import gov.nist.itl.ssd.wipp.backend.data.imagescollection.ImagesCollection;
import gov.nist.itl.ssd.wipp.backend.data.imagescollection.ImagesCollectionRepository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Optional;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.server.EntityLinks;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

/**
 *
 * @author Antoine Vandecreme <antoine.vandecreme at nist.gov>
 * @author Mylene Simon <mylene.simon at nist.gov>
 */
@RestController
@Tag(name="ImagesCollection Entity")
@RequestMapping(CoreConfig.BASE_URI + "/imagesCollections/{imagesCollectionId}/metadataFiles")
@ExposesResourceFor(MetadataFile.class)
public class MetadataFileController {

    @Autowired
    private MetadataFileHandler metadataFileHandler;

    @Autowired
    private MetadataFileRepository metadataFileRepository;

    @Autowired
    private ImagesCollectionRepository imagesCollectionRepository;

    @Autowired
    private DataDownloadTokenRepository dataDownloadTokenRepository;
    @Autowired
    private EntityLinks entityLinks;

    @RequestMapping(value = "", method = RequestMethod.GET)
    @PreAuthorize("hasRole('admin') or @imagesCollectionSecurity.checkAuthorize(#imagesCollectionId, false)")
    public HttpEntity<PagedModel<EntityModel<MetadataFile>>> getFilesPage(
            @PathVariable("imagesCollectionId") String imagesCollectionId,
            @ParameterObject @PageableDefault Pageable pageable,
            @Parameter(hidden = true) PagedResourcesAssembler<MetadataFile> assembler) {
        Page<MetadataFile> files = metadataFileRepository.findByImagesCollection(
                imagesCollectionId, pageable);
        PagedModel<EntityModel<MetadataFile>> resources
                = assembler.toModel(files);
        resources.forEach(
                resource -> processResource(imagesCollectionId, resource));
        return new ResponseEntity<>(resources, HttpStatus.OK);
    }

    @RequestMapping(value = "", method = RequestMethod.DELETE)
    @PreAuthorize("isAuthenticated() and "
    		+ "(hasRole('admin') or @imagesCollectionSecurity.checkAuthorize(#imagesCollectionId, true))")
    public void deleteAllFiles(
            @PathVariable("imagesCollectionId") String imagesCollectionId) {
    	Optional<ImagesCollection> tc = imagesCollectionRepository.findById(
                imagesCollectionId);
    	if (!tc.isPresent()) {
        	throw new NotFoundException("Image collection does not exist.");
        }
        if (tc.get().isLocked()) {
            throw new ClientException("Collection locked.");
        }
        metadataFileHandler.deleteAll(imagesCollectionId);
    }

    @RequestMapping(
            value = "/{fileName:.+}/request",
            method = RequestMethod.GET,
            produces = "application/json")
    @PreAuthorize("hasRole('admin') or @imagesCollectionSecurity.checkAuthorize(#imagesCollectionId, false)")
    public DownloadUrl requestFileDownload(
            @PathVariable("imagesCollectionId") String imagesCollectionId,
            @PathVariable("fileName") String fileName) {
        // Generate and send unique download URL
        String tokenParam = generateDownloadTokenParam(imagesCollectionId);
        String imagePath = "/" + fileName;
        String downloadLink = linkTo(MetadataFileController.class,
                imagesCollectionId).toString() + imagePath + tokenParam;
        return new DownloadUrl(downloadLink);
    }

    @RequestMapping(value = "/{fileName:.+}", method = RequestMethod.HEAD)
    @PreAuthorize("hasRole('admin') or @imagesCollectionSecurity.checkAuthorize(#imagesCollectionId, false)")
    public void headFile(
            @PathVariable("imagesCollectionId") String imagesCollectionId,
            @PathVariable("fileName") String fileName,
            @RequestParam("token") String token,
            HttpServletResponse response) throws IOException {
        // Check validity of download token
        checkDownloadTokenValidity(token, imagesCollectionId);
        // Check existence of file and send length
        File file = metadataFileHandler.getFile(imagesCollectionId, fileName);
        if (!file.exists()) {
            throw new NotFoundException("File does not exist.");
        }
        response.setContentLengthLong(file.length());
    }

    @RequestMapping(value = "/{fileName:.+}", method = RequestMethod.GET)
    public void getFile(
            @PathVariable("imagesCollectionId") String imagesCollectionId,
            @PathVariable("fileName") String fileName,
            @RequestParam("token") String token,
            HttpServletResponse response) throws IOException {
        // Check validity of download token
        checkDownloadTokenValidity(token, imagesCollectionId);
        // Send file
        File file = metadataFileHandler.getFile(imagesCollectionId, fileName);
        response.setContentLengthLong(file.length());
        response.setContentType(Files.probeContentType(file.toPath()));
        try (InputStream fis = new FileInputStream(file)) {
            IOUtils.copyLarge(fis, response.getOutputStream());
            response.flushBuffer();
        } catch (FileNotFoundException ex) {
            throw new NotFoundException("File does not exist.", ex);
        }
    }

    @RequestMapping(value = "/{fileName:.+}", method = RequestMethod.DELETE)
    @PreAuthorize("isAuthenticated() and "
    		+ "(hasRole('admin') or @imagesCollectionSecurity.checkAuthorize(#imagesCollectionId, true))")
    public void deleteFile(
            @PathVariable("imagesCollectionId") String imagesCollectionId,
            @PathVariable("fileName") String fileName) {
    	Optional<ImagesCollection> tc = imagesCollectionRepository.findById(
                imagesCollectionId);
    	if (!tc.isPresent()) {
        	throw new NotFoundException("Image collection does not exist.");
        }
        if (tc.get().isLocked()) {
            throw new ClientException("Collection locked.");
        }
        metadataFileHandler.delete(imagesCollectionId, fileName);
    }

    protected void processResource(String imagesCollectionId,
            EntityModel<MetadataFile> resource) {
        MetadataFile file = resource.getContent();

        Link link = entityLinks.linkForItemResource(
                ImagesCollection.class, imagesCollectionId)
                .slash("metadataFiles")
                .slash(file.getFileName())
                .slash("request")
                .withSelfRel();
        resource.add(link);
    }

    private void checkDownloadTokenValidity(String token, String imagesCollectionId) {
        Optional<DataDownloadToken> downloadToken = dataDownloadTokenRepository.findByToken(token);
        if (!downloadToken.isPresent() || !downloadToken.get().getDataId().equals(imagesCollectionId)) {
            throw new ForbiddenException("Invalid download token.");
        }
    }

    private String generateDownloadTokenParam(String imagesCollectionId) {
        // Check existence of images collection
        Optional<ImagesCollection> tc = imagesCollectionRepository.findById(
                imagesCollectionId);
        if (!tc.isPresent()) {
            throw new ResourceNotFoundException(
                    "Images collection " + imagesCollectionId + " not found.");
        }

        // Generate download token
        DataDownloadToken downloadToken = new DataDownloadToken(imagesCollectionId);
        dataDownloadTokenRepository.save(downloadToken);

        // Generate token param
        String tokenParam = "?token=" + downloadToken.getToken();

        return tokenParam;
    }
}
