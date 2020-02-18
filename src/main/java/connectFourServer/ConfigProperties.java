package connectFourServer;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "game") 
public class ConfigProperties { 
 
    private int allowedGoMinutes;
    
    // standard getters and setters 
    public int getAllowedGoMinutes() {
    	return allowedGoMinutes;
    }
   
    public void setAllowedGoMinutes(int goTime) {
    	allowedGoMinutes = goTime;
    }
}
