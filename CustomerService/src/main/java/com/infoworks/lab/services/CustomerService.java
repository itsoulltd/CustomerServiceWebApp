package com.infoworks.lab.services;

import com.infoworks.lab.domain.entities.Customer;
import com.infoworks.lab.domain.repositories.CustomerRepository;
import com.it.soul.lab.data.simple.SimpleDataSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("customerService")
public class CustomerService extends SimpleDataSource<String, Customer> {

    private CustomerRepository repository;

    public CustomerService(CustomerRepository repository) {
        this.repository = repository;
    }

    @Override
    public Customer read(String key) {
        List<Customer> res = repository.findByName(key);
        return res != null && res.size() > 0 ? res.get(0) : null;
    }

    @Override
    public Customer[] readSync(int offset, int pageSize) {
        Page<Customer> finds = repository.findAll(PageRequest.of(offset, pageSize));
        return finds.getContent().toArray(new Customer[0]);
    }

    @Override
    public int size() {
        return Long.valueOf(repository.count()).intValue();
    }

    @Override
    public void put(String key, Customer customer) {
        repository.save(customer);
    }

    @Override
    public Customer replace(String key, Customer customer) {
        Customer existing = read(key);
        if (existing != null && customer != null) {
            customer.setId(existing.getId());
            existing.unmarshallingFromMap(customer.marshallingToMap(true), true);
            repository.save(existing);
        }
        return existing;
    }

    @Override
    public Customer remove(String key) {
        Customer existing = read(key);
        if (existing != null) {
            repository.deleteById(existing.getId());
        }
        return existing;
    }
}
