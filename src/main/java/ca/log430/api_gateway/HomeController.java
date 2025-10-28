package ca.log430.api_gateway;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HomeController {
    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/")
    public String home() {
        return "API Gateway is running";
    }

    @GetMapping("/status")
    public Map<String, String> getServicesStatus() {
        Map<String, String> result = new HashMap<>();


        result.put("users", checkUrl("http://172.17.0.1:8080"));
        result.put("transactions", checkUrl("http://172.17.0.1:8081"));
        result.put("payments", checkUrl("http://172.17.0.1:8083"));

        return result;
    }

    private String checkUrl(String url) {
        try {
            restTemplate.getForEntity(url, String.class);
            return "OK";
        } catch (Exception e) {
            return "DOWN";
        }
    }


}
