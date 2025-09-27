package picklunch;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(
        title = "Pick Lunch REST API",
        version = "1.0",
        description = "API documentation for Pick Lunch Service"
))
public class GolunchApplication {

    public static void main(String[] args) {
        SpringApplication.run(GolunchApplication.class, args);
    }

}
