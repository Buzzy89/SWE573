package com.yusuf.mysticalObject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MysticalObjectApplication {

    public static void main(String[] args) {
        String credentialsPath = System.getProperty("user.dir")
                + "/src/main/resources/mysticalobjectemporium-444521-88fda94ddf96.json";

        System.setProperty("GOOGLE_APPLICATION_CREDENTIALS", credentialsPath);
        System.out.println("GCP Credentials path: " + credentialsPath);  // Debug için

        SpringApplication.run(MysticalObjectApplication.class, args);
    }

}
