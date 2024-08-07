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
import gov.nist.itl.ssd.wipp.backend.core.model.data.DataDownloadToken;
import gov.nist.itl.ssd.wipp.backend.core.model.data.DataDownloadTokenRepository;
import gov.nist.itl.ssd.wipp.backend.core.rest.DownloadUrl;
import gov.nist.itl.ssd.wipp.backend.core.rest.exception.ForbiddenException;
import gov.nist.itl.ssd.wipp.backend.core.utils.SecurityUtils;
import gov.nist.itl.ssd.wipp.backend.data.csvCollection.CsvCollection;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

/**
* Image Annotations Collection download controller
*
* @author Mylene Simon <mylene.simon at nist.gov>
*/
@RestController
@Tag(name="ImageAnnotations Entity")
@RequestMapping(CoreConfig.BASE_URI + "/imageAnnotationsCollections/{imageAnnotationsCollectionId}/download")
public class ImageAnnotationsCollectionDownloadController {

	@Autowired
	CoreConfig config;

	@Autowired
    ImageAnnotationsCollectionRepository imageAnnotationsCollectionRepository;
	
	@Autowired
	DataDownloadTokenRepository dataDownloadTokenRepository;
	
	@RequestMapping(
            value = "request",
            method = RequestMethod.GET,
            produces = "application/json")
	@PreAuthorize("hasRole('admin') or @imageAnnotationsCollectionSecurity.checkAuthorize(#imageAnnotationsCollectionId, false)")
	public DownloadUrl requestDownload(
            @PathVariable("imageAnnotationsCollectionId") String imageAnnotationsCollectionId) {
    	
    	// Check existence of CSV collection
    	Optional<ImageAnnotationsCollection> imageAnnotationsCollection = imageAnnotationsCollectionRepository.findById(
				imageAnnotationsCollectionId);
        if (!imageAnnotationsCollection.isPresent()) {
            throw new ResourceNotFoundException(
                    "Image annotations collection " + imageAnnotationsCollectionId + " not found.");
        }
        
        // Generate download token
        DataDownloadToken downloadToken = new DataDownloadToken(imageAnnotationsCollectionId);
        dataDownloadTokenRepository.save(downloadToken);
        
        // Generate and send unique download URL
        String tokenParam = "?token=" + downloadToken.getToken();
        String downloadLink = linkTo(ImageAnnotationsCollectionDownloadController.class,
				imageAnnotationsCollectionId).toString() + tokenParam;
        return new DownloadUrl(downloadLink);
    }

	@RequestMapping(
			value = "",
			method = RequestMethod.GET,
			produces = "application/zip")
	public void get(
			@PathVariable("imageAnnotationsCollectionId") String imageAnnotationsCollectionId,
			@RequestParam("token")String token,
			HttpServletResponse response) throws IOException {
		
    	// Load security context for system operations
    	SecurityUtils.runAsSystem();
    	
    	// Check validity of download token
    	Optional<DataDownloadToken> downloadToken = dataDownloadTokenRepository.findByToken(token);
    	if (!downloadToken.isPresent() || !downloadToken.get().getDataId().equals(imageAnnotationsCollectionId)) {
    		throw new ForbiddenException("Invalid download token.");
    	}
    	
    	// Check existence of collection
        ImageAnnotationsCollection imageAnnotationsCollection = null;
		Optional<ImageAnnotationsCollection> optImageAnnotationsCollection = imageAnnotationsCollectionRepository.findById(imageAnnotationsCollectionId);
		
		if (!optImageAnnotationsCollection.isPresent()) {
			throw new ResourceNotFoundException(
					"Image annotations Collection " + imageAnnotationsCollectionId + " not found.");
		} else { // collection is present
			imageAnnotationsCollection = optImageAnnotationsCollection.get();
        }

		// get collection folder
		File imageAnnotationsCollectionStorageFolder = new File(config.getImageAnnotationsFolder(), imageAnnotationsCollection.getId());
		if (! imageAnnotationsCollectionStorageFolder.exists()) {
			throw new ResourceNotFoundException(
					"Image annotations Collection " + imageAnnotationsCollectionId + " " + imageAnnotationsCollection.getName() + " not found.");
		}

		response.setHeader("Content-disposition",
				"attachment;filename=" + "ImageAnnotations-" + imageAnnotationsCollection.getName() + ".zip");

		ZipOutputStream zos = new ZipOutputStream(response.getOutputStream());
		addToZip("", zos, imageAnnotationsCollectionStorageFolder);
		zos.finish();
		
		// Clear security context after system operations
		SecurityContextHolder.clearContext();
	}

	//Recursive method to handle sub-folders
	public static void addToZip(String path, ZipOutputStream myZip, File f) throws FileNotFoundException, IOException{
		if(f.isDirectory()){
			for(File subF : f.listFiles()){
				addToZip(path + File.separator + f.getName() , myZip, subF);
			}
		}
		else {
			ZipEntry e = new ZipEntry(path + File.separator + f.getName());
			myZip.putNextEntry(e);
			try (InputStream is = new FileInputStream(f.getAbsolutePath())) {
				IOUtils.copyLarge(is, myZip);
			}
		}
	}
}
