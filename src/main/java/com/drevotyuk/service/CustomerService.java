package com.drevotyuk.service;

import com.drevotyuk.model.Customer;
import com.drevotyuk.model.Order;
import com.drevotyuk.repository.CustomerRepository;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ResolvableType;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class CustomerService {
    @Autowired
    private CustomerRepository repository;
    @Autowired
    private RestTemplate restTemplate;

    public ResponseEntity<Customer> getCustomerById(int customerId) {
        Optional<Customer> optCustomer = repository.findById(customerId);
        if (!optCustomer.isPresent())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(optCustomer.get(), HttpStatus.OK);
    }

    public Iterable<Order> getOrdersById(int id) {
        String orderUrl = "http://localhost:8083/order?customerId=" + id;
        return restTemplate
                .exchange(orderUrl, HttpMethod.GET, null,
                        new ParameterizedTypeReference<Iterable<Order>>() {
                        })
                .getBody();
    }

    public ResponseEntity<Customer> addCustomer(Customer customer) {
        customer.setCreationDate(LocalDate.now());

        // we think of indentical customer's accounts as one person who registered
        // twice, for example using different e-mails
        return new ResponseEntity<>(repository.save(customer), HttpStatus.CREATED);
    }

    public ResponseEntity<Order> addOrderById(int customerId, Order order) {
        String orderUrl = "http://localhost:8083/order/";
        order.setCustomerId(customerId);

        ResponseEntity<Order> orderEntity;
        try {
            orderEntity = restTemplate.postForEntity(orderUrl, order, Order.class);
        } catch (HttpClientErrorException e) {
            return new ResponseEntity<>(e.getStatusCode());
        }

        return orderEntity;
    }

    public ResponseEntity<Customer> updateCustomerById(int customerId, Customer customer) {
        Optional<Customer> optInitialCustomer = repository.findById(customerId);
        if (!optInitialCustomer.isPresent())
            return new ResponseEntity<>(repository.save(customer), HttpStatus.CREATED);

        Customer initialCustomer = optInitialCustomer.get();
        initialCustomer.setFirstname(customer.getFirstname());
        initialCustomer.setSurname(customer.getSurname());
        initialCustomer.setLastname(customer.getLastname());
        initialCustomer.setCreationDate(customer.getCreationDate());
        initialCustomer.setAddress(customer.getAddress());

        return new ResponseEntity<>(repository.save(initialCustomer), HttpStatus.OK);
    }

    public ResponseEntity<Order> updateOrderById(int customerId, int orderId, Order order) {
        String orderUrl = "http://localhost:8083/order/" + orderId;

        try {
            ResponseEntity<Order> initialOrderEntity = restTemplate.getForEntity(orderUrl, Order.class);
            if (!initialOrderEntity.hasBody())
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            Order initialOrder = initialOrderEntity.getBody();
            if (initialOrder.getCustomerId() != customerId)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            order.setCustomerId(customerId);
            restTemplate.put(orderUrl, order);

            ResponseEntity<Order> updatedOrderEntity = restTemplate.getForEntity(orderUrl, Order.class);
            return updatedOrderEntity;
        } catch (HttpClientErrorException e) {
            return new ResponseEntity<>(e.getStatusCode());
        }
    }

    public ResponseEntity<Customer> deleteCustomerById(int customerId) {
        if (!repository.existsById(customerId))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        repository.deleteById(customerId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<Order> deleteOrderById(int customerId, int orderId) {
        String orderUrl = "http://localhost:8083/order/" + orderId;

        try {
            ResponseEntity<Order> initialOrderEntity = restTemplate.getForEntity(orderUrl, Order.class);
            if (!initialOrderEntity.hasBody())
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            Order initialOrder = initialOrderEntity.getBody();
            if (initialOrder.getCustomerId() != customerId)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException e) {
            return new ResponseEntity<>(e.getStatusCode());
        }

        restTemplate.delete(orderUrl);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
