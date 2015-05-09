package at.ac.tuwien.qse.sepm.dao;

import org.junit.After;
import org.junit.Before;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class JDBCPhotographerDAOTests extends AbstractPhotographerDAOTests {
    @Before
    public void setUp(){
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        setPhotographerDAO((PhotographerDAO) context.getBean("photographerDAO"));
    }

    @After
    public void tearDown() {

    }
}
