package at.ac.tuwien.qse.sepm.service.impl;

import at.ac.tuwien.qse.sepm.entities.Photo;
import org.junit.Test;

import java.nio.file.Paths;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class PhotoPathFilterTest {

    @Test
    public void test_pathMatches() {
        PhotoPathFilter filter = new PhotoPathFilter();
        filter.setIncludedPath(Paths.get("/home/user/images"));

        assertThat(filter.test(new Photo(Paths.get("/home/user/images/"))), is(true));
        assertThat(filter.test(new Photo(Paths.get("/home/user/images/foobar.jpg"))), is(true));
        assertThat(filter.test(new Photo(Paths.get("/home/user/images/subdir/test.jpg"))), is(true));
        assertThat(filter.test(new Photo(Paths.get("/home/user/images/1/2/3/4/5/test.jpg"))), is(true));
    }

    @Test
    public void test_pathDoesNotMatch() {
        PhotoPathFilter filter = new PhotoPathFilter();
        filter.setIncludedPath(Paths.get("/home/user/images"));

        assertThat(filter.test(new Photo(Paths.get("/home/user/"))), is(false));
        assertThat(filter.test(new Photo(Paths.get("/home/user/image/foobar.jpg"))), is(false));
    }
}
