package ru.merkulyevsasha.movies;

import org.junit.Test;

import ru.merkulyevsasha.movies.http.MovieService;
import ru.merkulyevsasha.movies.models.Details;
import ru.merkulyevsasha.movies.models.Movies;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class MovieServiceUnitTest {

    private final int PAGE = 20; // на странице их всегда 20 (на 1й точно 20)

    @Test
    public void popular_movies_always_20() throws Exception {

        MovieService service = MovieService.getInstance();

        //Movies result = service.popular("ru", 1);

        //assertEquals(result.results.size(), PAGE);
    }

    @Test
    public void latest_movies_always_20() throws Exception {

        MovieService service = MovieService.getInstance();

        //Movies result = service.latest("en", 1);

        //assertEquals(result.results.size(), PAGE);
    }

    @Test
    public void search_movies_always_12() throws Exception {

        MovieService service = MovieService.getInstance();

//        Movies result = service.search("стар трек", "ru", 1);
//
//        assertEquals(result.results.size(), 12);
    }

    @Test
    public void details_188927() throws Exception {

        MovieService service = MovieService.getInstance();

        //Details result = service.details(188927, "ru", 1);

        //assertEquals(result.originalTitle, "Star Trek Beyond");
    }

}