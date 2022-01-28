package io.achilles.moviecatalogservice.services;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.netflix.hystrix.contrib.javanica.conf.HystrixPropertiesManager;
import io.achilles.moviecatalogservice.models.Rating;
import io.achilles.moviecatalogservice.models.UserRating;
import io.achilles.moviecatalogservice.resources.MovieCatalogResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.logging.Logger;

@Service
public class RatingDataService {

    public static final Logger LOGGER = Logger.getLogger(RatingDataService.class.getName());

    @Autowired
    private RestTemplate restTemplate;

    // Creating bulkhead
    @HystrixCommand(fallbackMethod = "getFallbackUserRatings",
            threadPoolKey = "ratingDataService",
            threadPoolProperties = {
                    @HystrixProperty(name = "coreSize", value = "20"),
                    @HystrixProperty(name = "maxQueueSize", value = "10")
            },
            commandProperties = {
                @HystrixProperty(name = HystrixPropertiesManager.EXECUTION_ISOLATION_THREAD_TIMEOUT_IN_MILLISECONDS, value = "3000"),
                @HystrixProperty(name = HystrixPropertiesManager.CIRCUIT_BREAKER_REQUEST_VOLUME_THRESHOLD, value = "10"),
                @HystrixProperty(name = HystrixPropertiesManager.CIRCUIT_BREAKER_ERROR_THRESHOLD_PERCENTAGE, value = "50"),
                @HystrixProperty(name = HystrixPropertiesManager.CIRCUIT_BREAKER_SLEEP_WINDOW_IN_MILLISECONDS, value = "20000")
            })
    public UserRating getUserRatings(String userId) {
        LOGGER.info("--getUserRatings(): calling actual method");
        return restTemplate.getForObject("http://ratings-data-service/rating-data/users/" + userId, UserRating.class);
    }

    public UserRating getFallbackUserRatings(String userId) {
        LOGGER.info("--getFallbackUserRatings(): calling fallback method");
        UserRating userRating = new UserRating();
        userRating.setUserId(userId);
        userRating.setUserRating(Arrays.asList(new Rating("0", 0)));
        return userRating;
    }
}
