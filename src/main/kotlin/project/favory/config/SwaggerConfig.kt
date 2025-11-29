package project.favory.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {

    @Bean
    fun openAPI(): OpenAPI {
        val jwtSchemeName = "bearerAuth"
        val devBackdoorSchemeName = "X-User-Id"

        return OpenAPI()
            .info(
                Info()
                    .title("Favory API")
                    .description(
                        "Favory 프로젝트 API\n\n" +
                                "## 인증 방법\n" +
                                "1. **JWT Bearer Token**: 프로덕션 환경\n" +
                                "2. **X-User-Id Header**: 개발 환경 백도어 (프로덕션 외의 환경에서만 동작)"
                    )
                    .version("v1.0")
            )
            .components(
                Components()
                    .addSecuritySchemes(
                        jwtSchemeName,
                        SecurityScheme()
                            .type(SecurityScheme.Type.HTTP)
                            .scheme("bearer")
                            .bearerFormat("JWT")
                            .description("JWT 토큰 인증")
                    )
                    .addSecuritySchemes(
                        devBackdoorSchemeName,
                        SecurityScheme()
                            .type(SecurityScheme.Type.APIKEY)
                            .`in`(SecurityScheme.In.HEADER)
                            .name("X-User-Id")
                            .description("개발 환경 백도어 (User ID를 헤더로 전달)")
                    )
            )
            .addSecurityItem(SecurityRequirement().addList(jwtSchemeName))
            .addSecurityItem(SecurityRequirement().addList(devBackdoorSchemeName))
    }
}