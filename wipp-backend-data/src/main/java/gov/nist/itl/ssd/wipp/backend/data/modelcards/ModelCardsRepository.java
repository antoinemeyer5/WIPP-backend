package gov.nist.itl.ssd.wipp.backend.data.modelcards;

import gov.nist.itl.ssd.wipp.backend.core.model.auth.PrincipalFilteredRepository;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@Tag(name="ModelCards Entity")
@RepositoryRestResource
public interface ModelCardsRepository extends PrincipalFilteredRepository<ModelCards, String>
{ }
