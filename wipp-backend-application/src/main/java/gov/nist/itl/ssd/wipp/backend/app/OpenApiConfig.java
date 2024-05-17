package gov.nist.itl.ssd.wipp.backend.app;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.*;

/**
 * OpenAPI docs configuration
 *
 * @author Mylene Simon <mylene.simon at nist.gov>
 */
@OpenAPIDefinition(info = @Info(title = "WIPP",
        description = "WIPP REST API", version = "v3.2.0"),
        security = @SecurityRequirement(name = "security_auth"))
@SecurityScheme(name = "security_auth", type = SecuritySchemeType.OAUTH2,
        flows = @OAuthFlows(authorizationCode = @OAuthFlow(
                authorizationUrl = "${springdoc.oAuthFlow.authorizationUrl}"
                , tokenUrl = "${springdoc.oAuthFlow.tokenUrl}", scopes = {
                @OAuthScope(name = "openid", description = "Keycloak") })))
public class OpenApiConfig {
}

