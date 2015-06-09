package at.ac.tuwien.qse.sepm.dao;

import org.springframework.test.context.jdbc.Sql;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation which populates the test database before a test method is executed
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.METHOD)
@Sql(scripts = "classpath:db/test_data_insert.sql")
public @interface WithData {
}
