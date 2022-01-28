package io.achilles.moviecatalogservice.resources;

import io.achilles.moviecatalogservice.models.CatalogItem;
import io.achilles.moviecatalogservice.services.MovieInfoService;
import io.achilles.moviecatalogservice.services.RatingDataService;
import io.achilles.moviecatalogservice.models.UserRating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/catalog")
public class MovieCatalogResource {

    public static final Logger LOGGER = Logger.getLogger(MovieCatalogResource.class.getName());

    @Autowired
    private RatingDataService ratingDataService;

    @Autowired
    private MovieInfoService movieInfoService;

    @Autowired
    private WebClient.Builder webClientBuilder;

    // Available in IoC container - created by spring
    @Autowired
    private DiscoveryClient discovery;

    @GetMapping("/{userId}")
    public List<CatalogItem> getCatalogItems(@PathVariable("userId") String userId) {

        // print all the instances for a serviceId available in Eureka
        LOGGER.info("--getCatalogItems(): ratings-data-service: " + Arrays.toString(discovery.getInstances("ratings-data-service").toArray()));
        LOGGER.info("--getCatalogItems(): movie-info-service: " + Arrays.toString(discovery.getInstances("movie-info-service").toArray()));

        // get all rated movie Ids/ ratings for a user
        UserRating ratings = ratingDataService.getUserRatings(userId);
        LOGGER.info("--getCatalogItems(): ratings: " + ratings.toString());

        // for each movie get movie info, add it to the catalog object
        return ratings.getUserRating().stream()
                .map(rating -> movieInfoService.getCatalogItem(rating))
                .collect(Collectors.toList());
        // put them all together and return
    }

    /*
    public List<CatalogItem> getFallbackCatalog(@PathVariable("userId") String userId) {
        return Arrays.asList(new CatalogItem("NA", "No Movie", "NA", 0));
    }
    */
}

/* Alternative webclient way::>

    Movie movie =
    webClientBuilder.build()
    .get()
    .uri("http://localhost:8082/movies/" + rating.getMovieId())
    .retrieve()
    .bodyToMono(Movie.class
    block();
*/
