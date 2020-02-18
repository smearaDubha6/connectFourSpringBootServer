package connectFourServer;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@EnableAutoConfiguration
@EnableConfigurationProperties(ConfigProperties.class)
@ComponentScan
public class Application {

    public static void main(String[] args) {	
        SpringApplication.run(Application.class, args);
    }

}