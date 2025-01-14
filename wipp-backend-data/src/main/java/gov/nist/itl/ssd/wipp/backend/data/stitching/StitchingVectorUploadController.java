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
package gov.nist.itl.ssd.wipp.backend.data.stitching;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import gov.nist.itl.ssd.wipp.backend.data.stitching.timeslices.StitchingVectorTimeSlice;
import gov.nist.itl.ssd.wipp.backend.core.CoreConfig;
import gov.nist.itl.ssd.wipp.backend.core.rest.exception.ClientException;

/**
 *
 * @author Antoine Vandecreme <antoine.vandecreme at nist.gov>
 * @author Mylene Simon <mylene.simon at nist.gov>
 */
@RestController
@Tag(name="StitchingVector Entity")
@RequestMapping(CoreConfig.BASE_URI + "/stitchingVectors/upload")
public class StitchingVectorUploadController {

    @Autowired
    private CoreConfig config;

    @Autowired
    private StitchingVectorRepository stitchingVectorRepository;

    @Autowired
    private StitchingVectorLogic stitchingVectorLogic;

    @RequestMapping(value = "", method = RequestMethod.POST)
    @PreAuthorize("isAuthenticated()")
    public StitchingVector upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("name") String name,
            @RequestParam(value = "tilesPattern",
                    required = false,
                    defaultValue = "") String tilesPattern,
            @RequestParam(
                    value = "message",
                    required = false,
                    defaultValue = "") String message)
            throws IOException {
        if (name == null || name.isEmpty()) {
            throw new ClientException(
                    "A stitching vector name must be specified.");
        }

        stitchingVectorLogic.assertStitchingVectorNameUnique(name);

        List<StitchingVectorTimeSlice> timeSlices = Arrays.asList(
                new StitchingVectorTimeSlice(1, message));
        StitchingVector stitchingVector = new StitchingVector(name,
                tilesPattern, timeSlices);
        // Set the owner to the connected user
        stitchingVector.setOwner(SecurityContextHolder.getContext().getAuthentication().getName());
        stitchingVector = stitchingVectorRepository.save(stitchingVector);
        File stitchingVectorFolder = new File(
                config.getStitchingFolder(),
                stitchingVector.getId());
        stitchingVectorFolder.mkdirs();
        file.transferTo(new File(stitchingVectorFolder,
        		StitchingVectorConfig.STITCHING_VECTOR_GLOBAL_POSITION_PREFIX + "1"
                + StitchingVectorConfig.STITCHING_VECTOR_FILENAME_SUFFIX));
        return stitchingVector;
    }

}