package ca.log430.api_gateway;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class OpenApiAggregatorComponent {

    private final RestTemplate restTemplate = new RestTemplate();
    private final List<String> serviceDocs = List.of(
            "http://localhost:8080/v3/api-docs",
            "http://localhost:8081/v3/api-docs"
    );

    @GetMapping("/v3/api-docs")
    public Map<String, Object> aggregate(HttpServletRequest request) {
        Map<String, Object> merged = new HashMap<>();

        merged.put("openapi", "3.0.1");
        merged.put("info", Map.of("title", "API Gateway", "version", "1.0"));

        Map<String, Object> allPaths = new HashMap<>();
        Map<String, Object> allComponents = new HashMap<>();

        boolean viaGateway = request.getServerPort() == 5050; // ou autre condition

        for (String url : serviceDocs) {
            Map<String, Object> doc = restTemplate.getForObject(url, Map.class);
            if (doc == null) continue;

            // fusionner paths
            Map<String, Object> paths = (Map<String, Object>) doc.get("paths");
            if (paths != null) allPaths.putAll(paths);

            // fusionner components
            Map<String, Object> components = (Map<String, Object>) doc.get("components");
            if (components != null) {
                components.forEach((key, value) -> allComponents.putIfAbsent(key, value));
            }

            // dynamique : changer servers si Swagger UI passe par la Gateway
            if (viaGateway) {
                merged.put("servers", List.of(Map.of("url", "http://localhost:5050")));
            } else {
                // garder le serveur original du microservice
                merged.put("servers", doc.getOrDefault("servers", List.of(Map.of("url", url.replace("/v3/api-docs","")))));
            }
        }

        merged.put("paths", allPaths);
        merged.put("components", allComponents);

        return merged;
    }
}
