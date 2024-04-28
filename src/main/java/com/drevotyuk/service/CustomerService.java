package com.drevotyuk.service;

import com.drevotyuk.model.Customer;
import com.drevotyuk.model.Order;
import com.drevotyuk.repository.CustomerRepository;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    @Value("${order.url}")
    private String orderServiceUrl;

    public ResponseEntity<Customer> getCustomer(int customerId) {
        Optional<Customer> optCustomer = repository.findById(customerId);
        if (!optCustomer.isPresent())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(optCustomer.get(), HttpStatus.OK);
    }

    public Iterable<Order> getOrders(int customerId) {
        String url = String.format("http://%s?customerId=" + customerId, orderServiceUrl);
        return restTemplate
                .exchange(
                        url, HttpMethod.GET, null, new ParameterizedTypeReference<Iterable<Order>>() {
                        })
                .getBody();
    }

    public ResponseEntity<Order> getOrderById(int customerId, int orderId) {
        String url = String.format("http://%s/" + orderId, orderServiceUrl);
        try {
            ResponseEntity<Order> orderEntity = restTemplate.getForEntity(url, Order.class);
            if (!orderEntity.hasBody())
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            Order order = orderEntity.getBody();
            if (order.getCustomerId() != customerId)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            return new ResponseEntity<>(order, HttpStatus.OK);
        } catch (HttpClientErrorException e) {
            return new ResponseEntity<>(e.getStatusCode());
        }
    }

    public ResponseEntity<Customer> addCustomer(Customer customer) {
        customer.setCreationDate(LocalDate.now());

        // we think of indentical customer's accounts as one person who registered
        // twice, for example using different e-mails
        return new ResponseEntity<>(repository.save(customer), HttpStatus.CREATED);
    }

    public ResponseEntity<Order> addOrder(int customerId, Order order) {
        String url = String.format("http://%s/", orderServiceUrl);
        order.setCustomerId(customerId);

        try {
            ResponseEntity<Order> postOrderEntity = restTemplate.postForEntity(url, order, Order.class);

            return postOrderEntity.hasBody()
                    ? new ResponseEntity<>(postOrderEntity.getBody(), HttpStatus.CREATED)
                    : new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException e) {
            return new ResponseEntity<>(e.getStatusCode());
        }
    }

    public ResponseEntity<Customer> updateCustomer(int customerId, Customer customer) {
        Optional<Customer> optInitialCustomer = repository.findById(customerId);
        if (!optInitialCustomer.isPresent())
            return new ResponseEntity<>(repository.save(customer), HttpStatus.CREATED);

        Customer initialCustomer = optInitialCustomer.get();
        initialCustomer.setFirstname(customer.getFirstname());
        initialCustomer.setSurname(customer.getSurname());
        initialCustomer.setLastname(customer.getLastname());
        initialCustomer.setAddress(customer.getAddress());

        return new ResponseEntity<>(repository.save(initialCustomer), HttpStatus.OK);
    }

    public ResponseEntity<Order> updateOrder(int customerId, int orderId, Order order) {
        String url = String.format("http://%s/" + orderId, orderServiceUrl);

        try {
            ResponseEntity<Order> initialOrderEntity = restTemplate.getForEntity(url, Order.class);
            if (!initialOrderEntity.hasBody())
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            Order initialOrder = initialOrderEntity.getBody();
            if (initialOrder.getCustomerId() != customerId)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            order.setCustomerId(customerId);
            restTemplate.put(url, order);

            ResponseEntity<Order> updatedOrderEntity = restTemplate.getForEntity(url, Order.class);
            if (!updatedOrderEntity.hasBody())
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            return new ResponseEntity<>(updatedOrderEntity.getBody(), HttpStatus.OK);
        } catch (HttpClientErrorException e) {
            return new ResponseEntity<>(e.getStatusCode());
        }
    }

    public ResponseEntity<Customer> deleteCustomer(int customerId) {
        if (!repository.existsById(customerId))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        String url = String.format("http://%s/", orderServiceUrl);

        Iterable<Order> orders = getOrders(customerId);
        orders.forEach(order -> restTemplate.delete(url + order.getId()));

        repository.deleteById(customerId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    public ResponseEntity<Order> deleteOrder(int customerId, int orderId) {
        String url = String.format("http://%s/" + orderId, orderServiceUrl);

        try {
            ResponseEntity<Order> initialOrderEntity = restTemplate.getForEntity(url, Order.class);
            if (!initialOrderEntity.hasBody())
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            Order initialOrder = initialOrderEntity.getBody();
            if (initialOrder.getCustomerId() != customerId)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            restTemplate.delete(url);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (HttpClientErrorException e) {
            return new ResponseEntity<>(e.getStatusCode());
        }
    }
}
