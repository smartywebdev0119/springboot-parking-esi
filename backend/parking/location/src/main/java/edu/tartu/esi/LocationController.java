package edu.tartu.esi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping("/location")
    public ResponseEntity<String> processAddress(@RequestParam String address) {
        try {
            log.debug("HELLO");
            locationService.processAddress(address);
            return new ResponseEntity<>("Address processed successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error processing address: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}