package at.ac.tuwien.qse.sepm.dao;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class JDBCPhotographerDAOTests extends AbstractPhotographerDAOTests {

    private static ClassPathXmlApplicationContext context;

    @BeforeClass
    public static void setUpOnlyOnce(){
        context = new ClassPathXmlApplicationContext("beans.xml");
    }

    @Before
    public void setUp(){
        setPhotographerDAO((PhotographerDAO) context.getBean("photographerDAO"));
    }

    @AfterClass
    public static void tearDownOnlyOnce(){
        context.close();
    }

}
