package com.infoworks.lab.domain.executor;

import com.infoworks.lab.components.rest.RestRepositoryExecutor;
import com.infoworks.lab.domain.repository.CustomerRepository;
import com.infoworks.lab.rest.template.Interactor;

public class CustomerExecutor extends RestRepositoryExecutor {

    private CustomerRepository repository;

    public CustomerExecutor() {
        super(null);
    }

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

}
