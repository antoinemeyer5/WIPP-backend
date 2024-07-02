package gov.nist.itl.ssd.wipp.backend.data.modelcard;

import gov.nist.itl.ssd.wipp.backend.core.model.auth.PrincipalFilteredRepository;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

/**
 * @author Antoine Meyer <antoine.meyer at nist.gov>
 */
@Tag(name="ModelCard Entity")
@RepositoryRestResource
public interface ModelCardRepository extends PrincipalFilteredRepository<ModelCard, String> {

    @Override
    @RestResource(exported = false)
    void delete(ModelCard mc);

    @Query("{aiModelId:'?0'}")
    ModelCard findModelCardByAiModelId(String aiModelId);
}
