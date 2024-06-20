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
package gov.nist.itl.ssd.wipp.backend.data.aimodel;

import gov.nist.itl.ssd.wipp.backend.core.CoreConfig;
import gov.nist.itl.ssd.wipp.backend.core.model.data.DataDownloadToken;
import gov.nist.itl.ssd.wipp.backend.core.model.data.DataDownloadTokenRepository;
import gov.nist.itl.ssd.wipp.backend.core.rest.DownloadUrl;
import gov.nist.itl.ssd.wipp.backend.core.rest.exception.ForbiddenException;
import gov.nist.itl.ssd.wipp.backend.core.utils.SecurityUtils;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * AI Models download controller
 *
 * @author Mohamed Ouladi <mohamed.ouladi at nist.gov>
 * @author Mylene Simon <mylene.simon at nist.gov>
 */
@RestController
@Tag(name="AIModel Entity")
@RequestMapping(CoreConfig.BASE_URI + "/AIModels/{AIModelId}/download")
public class AIModelDownloadController {

	@Autowired
	CoreConfig config;

	@Autowired
	AIModelRepository aiModelRepository;
	
	@Autowired
    private DataDownloadTokenRepository dataDownloadTokenRepository;

	@RequestMapping(
            value = "request",
            method = RequestMethod.GET,
            produces = "application/json")
	@PreAuthorize("hasRole('admin') or @aiModelSecurity.checkAuthorize(#AIModelId, false)")
    public DownloadUrl requestDownload(
            @PathVariable("AIModelId") String aiModelId) {
    	
    	// Check existence of images collection
    	Optional<AIModel> tm = aiModelRepository.findById(
				aiModelId);
        if (!tm.isPresent()) {
            throw new ResourceNotFoundException(
                    "AI model " + aiModelId + " not found.");
        }
        
        // Generate download token
        DataDownloadToken downloadToken = new DataDownloadToken(aiModelId);
        dataDownloadTokenRepository.save(downloadToken);
        
        // Generate and send unique download URL
        String tokenParam = "?token=" + downloadToken.getToken();
        String downloadLink = linkTo(AIModelDownloadController.class,
				aiModelId).toString() + tokenParam;
        return new DownloadUrl(downloadLink);
    }
	
	@RequestMapping(
			value = "",
			method = RequestMethod.GET,
			produces = "application/zip")
	public void get(
			@PathVariable("AIModelId") String aiModelId,
			@RequestParam("token") String token,
			HttpServletResponse response) throws IOException {
		
    	// Load security context for system operations
    	SecurityUtils.runAsSystem();
    	
		// Check validity of download token
    	Optional<DataDownloadToken> downloadToken = dataDownloadTokenRepository.findByToken(token);
    	if (!downloadToken.isPresent() || !downloadToken.get().getDataId().equals(aiModelId)) {
    		throw new ForbiddenException("Invalid download token.");
    	}
    	
    	// Check existence of AI Model
		AIModel tm = null;
		Optional<AIModel> optTm = aiModelRepository.findById(aiModelId);
		
		if (!optTm.isPresent()) {
			throw new ResourceNotFoundException(
					"AI model " + aiModelId + " not found.");
		} else { // TrainedModel is present
            tm = optTm.get();
        }

		// get AI model folder
		File aiModelStorageFolder = new File(config.getAIModelsFolder(), tm.getId());
		if (! aiModelStorageFolder.exists()) {
			throw new ResourceNotFoundException(
					"AI model " + aiModelId + " " + tm.getName() + " not found.");
		}

		response.setHeader("Content-disposition",
				"attachment;filename=" + "AIModel-" + tm.getName() + ".zip");

		ZipOutputStream zos = new ZipOutputStream(response.getOutputStream());
		addToZip("", zos, aiModelStorageFolder);
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
