package edu.tartu.esi;

import com.google.maps.GeoApiContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "edu.tartu.esi")
public class Config {

    @Bean
    public GeoApiContext geoApiContext() {
        return new GeoApiContext.Builder()
                .apiKey("AIzaSyDpDx-V5s6lH1W0HLqmE9MvkX4zMFP3TfY")
                .build();
    }
}
