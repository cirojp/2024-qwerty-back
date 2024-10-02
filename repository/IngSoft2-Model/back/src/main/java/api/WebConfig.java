package api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("https://2024-qwerty-front-2.vercel.app/") // Permitir origen del frontend
                .allowedOrigins("http://localhost:5173") // Permitir origen del frontend
                .allowedOrigins("http://127.0.0.1:5173") // Permitir origen del frontend
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Incluir OPTIONS
                .allowedHeaders("*")
                .allowCredentials(true); // Permitir credenciales
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
