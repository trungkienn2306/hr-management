package com.kiennt.hrManagement.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()// Thêm server URL
                .servers(List.of(
                        new Server().url("http://localhost:" + serverPort).description("Local Server"),
                        new Server().url("https://api.example.com").description("Test Example Server")
                ))
                // Thông tin API
                .info(new Info()
                        .title("HR Management System API")
                        .version("1.0.0")
                        .description("""
                                ## Hệ thống Quản lý Nhân sự
                                
                                **API Documentation** cho hệ thống quản lý phòng ban và nhân sự.
                                
                                ### Tính năng chính:
                                1. **Quản lý Phòng ban** - CRUD phòng ban
                                2. **Quản lý Nhân sự** - CRUD nhân viên
                                3. **Điều chuyển Nhân sự** - Chuyển đổi phòng ban
                                4. **Lịch sử Điều chuyển** - Theo dõi lịch sử
                                
                                ### HTTP Status Codes:
                                - 200: Success
                                - 201: Created
                                - 400: Bad Request
                                - 401: Unauthorized
                                - 403: Forbidden
                                - 404: Not Found
                                - 409: Conflict
                                - 500: Internal Server Error
                                """));
    }
}
