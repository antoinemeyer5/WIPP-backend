package gov.nist.itl.ssd.wipp.backend.data.imagescollection;

import gov.nist.itl.ssd.wipp.backend.core.CoreConfig;
import gov.nist.itl.ssd.wipp.backend.core.rest.exception.ClientException;
import gov.nist.itl.ssd.wipp.backend.data.imagescollection.metadatafiles.MetadataFileHandler;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * Controller for batch import of images collections
 * @author Mylene Simon <mylene.simon at nist.gov>
 */

@RestController
@Tag(name="ImagesCollection Entity")
@RequestMapping(CoreConfig.BASE_URI + "/imagesCollections/batch-import")
public class ImagesCollectionBatchImportController {

    @Autowired
    CoreConfig config;

    @Autowired
    private ImagesCollectionRepository imagesCollectionRepository;

    @Autowired
    private MetadataFileHandler metadataHandler;

    @Autowired
    private ImagesCollectionLocalImporter localImporter;

    @RequestMapping(value = "",
            method = RequestMethod.POST,
            produces = "application/json")
    @PreAuthorize("isAuthenticated()")
    public BatchImportResponse batchImportCollections(
            @RequestBody BatchImportConfiguration configuration) throws IOException {

        // Sanity checks
        if(StringUtils.isEmpty(configuration.getExperimentName())) {
            throw new ClientException("Experiment name cannot be empty.");
        }
        if(StringUtils.isEmpty(configuration.getSourceFolder())) {
            throw new ClientException("Source folder name cannot be empty.");
        }
        String rootLocalImportFolder = config.getLocalImportFolder();
        if (StringUtils.isEmpty(rootLocalImportFolder)) {
            throw new ClientException("Root local import has not been configured, " +
                    "this import option cannot be used.");
        }
        File importFolder = new File(config.getLocalImportFolder(), configuration.getSourceFolder());
        if(!importFolder.exists() || !importFolder.isDirectory()) {
            throw new ClientException("Folder to import at location, " +
                    importFolder.getAbsolutePath() +
                    " does not exist or is not a directory.");
        }

        // List folders to import
        File[] foldersToImport = new File(importFolder.getPath()).listFiles(f -> (
                f.isDirectory() && !f.isHidden() && !f.getName().equals("metadata_files")));
        Arrays.stream(foldersToImport)
            .forEach(f -> {
                ImagesCollection coll = new ImagesCollection(configuration.getExperimentName() + "-" + f.getName(), false,
                        ImagesCollection.ImagesCollectionImportMethod.BACKEND_IMPORT, ImagesCollection.ImagesCollectionFormat.OMETIFF);
                coll.setOwner(SecurityContextHolder.getContext().getAuthentication().getName());
                coll.setSourceBackendImport(configuration.getSourceFolder() + File.separator + f.getName());
                coll.setNotes(configuration.getDescription());
                imagesCollectionRepository.save(coll);
                localImporter.importFromLocalFolder(coll);
            });

        // Handle experiment metadata_files folder separately
        File metadataFilesFolder = new File(importFolder, "metadata_files");
        boolean metadataCollCreated = false;
        if (metadataFilesFolder.exists() && metadataFilesFolder.isDirectory()) {
            ImagesCollection metadataColl = new ImagesCollection(configuration.getExperimentName() + "-metadata", false,
                    ImagesCollection.ImagesCollectionImportMethod.BACKEND_IMPORT, ImagesCollection.ImagesCollectionFormat.OMETIFF);
            metadataColl.setOwner(SecurityContextHolder.getContext().getAuthentication().getName());
            metadataColl.setSourceBackendImport(configuration.getSourceFolder() + File.separator + "metadata_files");
            metadataColl.setNotes(configuration.getDescription());
            imagesCollectionRepository.save(metadataColl);
            metadataHandler.importFolderCopy(metadataColl.getId(), metadataFilesFolder);
            metadataCollCreated = true;
        }

        // Response message
        String responseMessage = " Created " + foldersToImport.length + " images collections";
        if (metadataCollCreated) {
            responseMessage += " and 1 metadata collection";
        }
        responseMessage += " for experiment " + configuration.getExperimentName() + ".";
        return new BatchImportResponse(responseMessage);
    }

    public static class BatchImportConfiguration {
        private String experimentName;
        private String sourceFolder;
        private String description;
        private String regex;

        public String getExperimentName() {
            return experimentName;
        }

        public void setExperimentName(String experimentName) {
            this.experimentName = experimentName;
        }

        public String getSourceFolder() {
            return sourceFolder;
        }

        public void setSourceFolder(String sourceFolder) {
            this.sourceFolder = sourceFolder;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getRegex() {
            return regex;
        }

        public void setRegex(String regex) {
            this.regex = regex;
        }
    }

    public static class BatchImportResponse {
        private String message;

        public BatchImportResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}


