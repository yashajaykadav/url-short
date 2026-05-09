package com.yash.url_shortener.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customeOpenAPI(){
        return  new OpenAPI()
                .info(new Info()
                        .title("Url Shortner API")
                        .version("2.0.0")
                        .description("""
                                Enterprise URL Shortener Service with:
                                                    - Redis Caching
                                                    - Kafka Async Processing
                                                    - Google Gemini AI Analytics
                                                    - Real-time Metrics""")
                        .contact(new Contact()
                                .name("Yash Kadav")
                                .email("yashkadav52@gmail.com")
                                .url("https://github.com/yashajaykadav"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Development  Server")
                ));
    }
}
