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
import gov.nist.itl.ssd.wipp.backend.core.model.data.BaseDataHandler;
import gov.nist.itl.ssd.wipp.backend.core.model.data.DataHandler;
import gov.nist.itl.ssd.wipp.backend.core.model.job.Job;
import gov.nist.itl.ssd.wipp.backend.core.model.job.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Mylene Simon <mylene.simon at nist.gov>
 */
@Component("imageAnnotationsDataHandler")
public class ImageAnnotationsCollectionDataHandler extends BaseDataHandler implements DataHandler{
    @Autowired
    CoreConfig config;

	@Autowired
	private ImageAnnotationsCollectionRepository imageAnnotationsCollectionRepository;

	@Override
	public void importData(Job job, String outputName) throws JobExecutionException {
        throw new RuntimeException("Import of image annotations from jobs is not implemented.");
    }

    public String exportDataAsParam(String value) {
        String imageAnnotationCollectionId = value;
        String imageAnnotationsCollectionPath;

        // check if the input of the job is the output of another job and if so return the associated path
        String regex = "\\{\\{ (.*)\\.(.*) \\}\\}";
        Pattern pattern = Pattern.compile(regex);
        Matcher m = pattern.matcher(imageAnnotationCollectionId);
        if (m.find()) {
            String jobId = m.group(1);
            String outputName = m.group(2);
            imageAnnotationsCollectionPath = getJobOutputTempFolder(jobId, outputName).getAbsolutePath();
        }
        // else return the path of the collection
        else {
            Optional<ImageAnnotationsCollection> optImageAnnotationsCollection = imageAnnotationsCollectionRepository.findById(imageAnnotationCollectionId);
            if(optImageAnnotationsCollection.isPresent()) {
                ImageAnnotationsCollection imageAnnotationsCollection = optImageAnnotationsCollection.get();
            }

            File imageAnnotationsCollectionFolder = new File(config.getImageAnnotationsFolder(), imageAnnotationCollectionId);
            imageAnnotationsCollectionPath = imageAnnotationsCollectionFolder.getAbsolutePath();

        }
        imageAnnotationsCollectionPath = imageAnnotationsCollectionPath.replaceFirst(config.getStorageRootFolder(),config.getContainerInputsMountPath());
        return imageAnnotationsCollectionPath;

    }
    
    @Override
    public void setDataToPublic(String value) {
    	Optional<ImageAnnotationsCollection> optImageAnnotationsCollection = imageAnnotationsCollectionRepository.findById(value);
        if(optImageAnnotationsCollection.isPresent()) {
            ImageAnnotationsCollection imageAnnotationsCollection = optImageAnnotationsCollection.get();
            if (!imageAnnotationsCollection.isPubliclyShared()) {
                imageAnnotationsCollection.setPubliclyShared(true);
                imageAnnotationsCollectionRepository.save(imageAnnotationsCollection);
            }
        }
    }

}
