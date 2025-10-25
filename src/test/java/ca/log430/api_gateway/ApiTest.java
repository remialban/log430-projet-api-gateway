package ca.log430.api_gateway;


import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ApiTest {

    // test home controller
    @Test
    public void testHomeController() {
        HomeController homeController = new HomeController();
        String response = homeController.home();
        assert(response.equals("API Gateway is running"));
    }
}
