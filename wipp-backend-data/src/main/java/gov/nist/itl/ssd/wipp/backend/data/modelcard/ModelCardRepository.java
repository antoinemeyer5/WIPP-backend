package gov.nist.itl.ssd.wipp.backend.data.modelcard;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * @author Antoine Meyer <antoine.meyer at nist.gov>
 */
@Tag(name="ModelCard Entity")
@RepositoryRestResource
public interface ModelCardRepository extends CrudRepository<ModelCard, String> {

    @Query("SELECT mc FROM ModelCard mc WHERE mc.aiModel.id = ?1")
    ModelCard findByAiModelId(String aiModelId);

}
