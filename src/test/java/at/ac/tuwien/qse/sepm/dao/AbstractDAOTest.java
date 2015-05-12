package at.ac.tuwien.qse.sepm.dao;


import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-config.xml")
@Transactional(propagation = Propagation.REQUIRES_NEW)
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
public class AbstractDAOTest {

    private String table;

    protected JdbcTemplate jdbcTemplate;

    public AbstractDAOTest() {
        UsingTable annotation =  getClass().getAnnotation(UsingTable.class);
        if(annotation != null) {
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
}
