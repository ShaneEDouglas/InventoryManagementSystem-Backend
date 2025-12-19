package com.example.inventorymangamentsystem.config;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info =@Info(
                contact = @Contact(
                        name = "Shane Douglas",
                        email = "shanedouglas346@gmail.com",
                        url = "https://github.com/ShaneEDouglas"
                ),

                description = "Open API documentation for an enterprise inventory management system",
                title = "InvenFlow backend OpenApi specification - Shane Douglas",
                version = "1.0"
        ),
        servers = {
                @Server(
                        description = "Local ENV",
                        url = "http://localhost:8080"

                )
        },

        security = {
            @SecurityRequirement(name = "cookieAuth")
        }
)
@SecurityScheme(
        name = "cookieAuth",
        description = "JWT Authorization that is stored in the cookie",
        type = SecuritySchemeType.APIKEY,
        in = SecuritySchemeIn.COOKIE,
        paramName = "token"
)
public class OpenApiConfig {

}
