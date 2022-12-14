package com.infoworks.lab.domain.repository;

import com.infoworks.lab.client.jersey.HttpTemplate;
import com.infoworks.lab.rest.repository.RestRepository;
import com.infoworks.lab.domain.entities.Customer;
import com.infoworks.lab.exceptions.HttpInvocationException;
import com.infoworks.lab.rest.models.*;
import com.infoworks.lab.rest.template.Invocation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CustomerRepository extends HttpTemplate<Response, Message> implements RestRepository<Customer, Integer> {

    public CustomerRepository() {
        super(Customer.class, Message.class);
    }

    @Override
    protected String schema() {
        return "http://";
    }

    @Override
    protected String host() {
        String host = System.getenv("app.customer.host");
        return host == null ? "localhost" : host;
    }

    @Override
    protected Integer port() {
        String portStr = System.getenv("app.customer.port");
        return portStr == null ? 8080 : Integer.valueOf(portStr);
    }

    @Override
    protected String api() {
        String api = System.getenv("app.customer.api");
        return  api == null ? "/api/customer/v1/profile" : api;
    }

    @Override
    public String getPrimaryKeyName() {
        return "id";
    }

    @Override
    public Class<Customer> getEntityType() {
        return Customer.class;
    }

    public ItemCount rowCount() {
        try {
            javax.ws.rs.core.Response response = execute(null, Invocation.Method.GET, "rowCount");
            ItemCount iCount = inflate(response, ItemCount.class);
            return iCount;
        } catch (HttpInvocationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
        return new ItemCount();
    }

    public List<Customer> fetch(Integer page, Integer limit){
        try {
            Response items = get(null, new QueryParam("page", page.toString()), new QueryParam("limit", limit.toString()));
            if (items instanceof ResponseList){
                List<Customer> collection = ((ResponseList)items).getCollections();
                return collection;
            }
        } catch (HttpInvocationException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public Customer insert(Customer customer){
        try {
            Customer response = (Customer) post(customer);
            return response;
        } catch (HttpInvocationException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Customer update(Customer customer, Integer userid){
        try {
            customer.setId(userid);
            Customer response = (Customer) put(customer);
            return response;
        } catch (HttpInvocationException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean delete(Integer userId){
        try {
            boolean isDeleted = delete(null, new QueryParam("userid", userId.toString()));
            return isDeleted;
        } catch (HttpInvocationException e) {
            e.printStackTrace();
        }
        return false;
    }
}
