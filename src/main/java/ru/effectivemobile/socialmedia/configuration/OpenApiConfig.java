package ru.effectivemobile.socialmedia.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
        info = @Info(
                title = "Social media API",
                description = "Social media", version = "1.0.0",
                contact = @Contact(
                        name = "Ramazan Mansurov",
                        email = "mansurov.r13@gmail.com",
                        url = "https://github.com/Hanibalai/social-media"
                )
        )
)
public class OpenApiConfig {

}
