package at.ac.tuwien.qse.sepm.dao;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;

import javax.sql.DataSource;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-config.xml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public abstract class AbstractJDBCDAOTest {
    protected JdbcTemplate jdbcTemplate;
    private String table;

    public AbstractJDBCDAOTest() {
        super();

        UsingTable annotation = getClass().getAnnotation(UsingTable.class);
        if (annotation != null) {
            table = annotation.value();
        } else {
            table = "";
        }
    }

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    protected int countRows() {
        return JdbcTestUtils.countRowsInTable(jdbcTemplate, table);
    }

    protected int countRowsWhere(String whereClause) {
        return JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, table, whereClause);
    }
}
