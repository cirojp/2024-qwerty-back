package api;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("https://2024-qwerty-front.vercel.app/") // Permitir origen del frontend
                .allowedOrigins("http://localhost:5173") // Permitir origen del frontend
                .allowedOrigins("http://127.0.0.1:5173") // Permitir origen del frontend
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Incluir OPTIONS
                .allowedHeaders("*")
                .allowCredentials(true); // Permitir credenciales
    }
    
}
