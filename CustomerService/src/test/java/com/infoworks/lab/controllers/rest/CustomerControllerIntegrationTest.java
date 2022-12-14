package com.infoworks.lab.controllers.rest;

import com.infoworks.lab.domain.entities.Gender;
import com.infoworks.lab.domain.entities.Customer;
import com.infoworks.lab.rest.models.ItemCount;
import com.infoworks.lab.webapp.WebApplicationTest;
import com.infoworks.lab.webapp.config.BeanConfig;
import com.infoworks.lab.webapp.config.TestJPAConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {WebApplicationTest.class
        , CustomerController.class, BeanConfig.class, TestJPAConfig.class})
@TestPropertySource(locations = {"classpath:application-mysql.properties"})
public class CustomerControllerIntegrationTest {

    @Before
    public void before() {
        /**/
    }

    @Autowired
    private CustomerController controller;

    @Test
    public void count(){
        //
        controller.insert(new Customer("Sayed The Coder", Gender.MALE, 24));
        //
        ItemCount count = controller.rowCount();
        System.out.println(count.getCount());
    }

    @Test
    public void query(){
        //
        controller.insert(new Customer("Sayed The Coder", Gender.MALE, 24));
        controller.insert(new Customer("Evan The Pankha Coder", Gender.MALE, 24));
        controller.insert(new Customer("Razib The Pagla", Gender.MALE, 26));
        //
        int size = Long.valueOf(controller.rowCount().getCount()).intValue();
        List<Customer> items = controller.fetch(size, 0);
        items.stream().forEach(customer -> System.out.println(customer.getName()));
    }

}