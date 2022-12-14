package com.infoworks.lab.domain.repository;

import com.infoworks.lab.domain.entities.Gender;
import com.infoworks.lab.domain.entities.Customer;
import com.infoworks.lab.exceptions.HttpInvocationException;
import com.infoworks.lab.rest.models.ItemCount;
import com.infoworks.lab.rest.template.Interactor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;

import java.util.List;

public class CustomerRepositoryTest {

    private CustomerRepository repository;

    public CustomerRepository getRepository() {
        if (repository == null){
            try {
                repository = Interactor.create(CustomerRepository.class);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
        return repository;
    }

    @Rule
    public final EnvironmentVariables env = new EnvironmentVariables();

    @Before
    public void before() {
        env.set("app.customer.host", "localhost");
        env.set("app.customer.port", "8080");
        env.set("app.customer.api", "/api/customer");
    }

    @Test
    public void envTest(){
        Assert.assertTrue(System.getenv("app.customer.host").equalsIgnoreCase("localhost"));
        Assert.assertTrue(System.getenv("app.customer.port").equalsIgnoreCase("8080"));
        Assert.assertTrue(System.getenv("app.customer.api").equalsIgnoreCase("customer"));
    }

    @Test
    public void rowCount() {
        ItemCount count = getRepository().rowCount();
        System.out.println(count.getCount());
    }

    @Test
    public void fetch() {
        ItemCount count = getRepository().rowCount();
        int max = count.getCount().intValue();
        int limit = 5;
        int page = 0;
        int numOfPage = (max / limit) + 1;
        while (page < numOfPage){
            List<Customer> riders = getRepository().fetch(page, limit);
            riders.forEach(rider -> System.out.println(rider.getName()));
            page++;
        }
    }

    @Test
    public void doa() throws HttpInvocationException {
        //Create & Insert:
        Customer created = getRepository()
                .insert(new Customer("Tictoc", Gender.NONE, 18));
        if(created != null) {
            System.out.println("Created: " + created.getName());
            //Update:
            created.setName("Tictoc-up");
            Customer update = getRepository().update(created, created.getId());
            if (update != null){
                System.out.println("Updated: " + update.getName());
                //Delete:
                boolean isDeleted = getRepository().delete(update.getId());
                System.out.println("Is Deleted : " + isDeleted);
            }
        }
    }
}