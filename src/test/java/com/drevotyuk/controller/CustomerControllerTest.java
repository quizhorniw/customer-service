package com.drevotyuk.controller;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import com.drevotyuk.model.Customer;
import com.drevotyuk.model.Order;
import com.drevotyuk.service.CustomerService;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RunWith(MockitoJUnitRunner.class)
public class CustomerControllerTest {

    @InjectMocks
    private CustomerController controller;

    @Mock
    private CustomerService service;

    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetAllCustomers() {
        List<Customer> customerList = new ArrayList<>();
        customerList.add(new Customer("Surname1", "Firstname1", "Patronymic1", "Address1"));
        customerList.add(new Customer("Surname2", "Firstname2", "Patronymic2", "Address2"));

        when(service.getAllCustomers()).thenReturn(customerList);

        Iterable<Customer> customers = controller.getAllCustomers();
        Assert.assertNotNull(customers);
        Assert.assertEquals(2, ((List<Customer>) customers).size());
    }

    @Test
    public void testGetCustomerById() {
        int customerId = 1;
        String surname = "Ivanov";
        Customer customer = new Customer(surname, "Ivan", "Ivanovich", "Address");

        when(service.getCustomer(customerId))
                .thenReturn(new ResponseEntity<>(customer, HttpStatus.OK));

        ResponseEntity<Customer> responseEntity = controller.getCustomer(customerId);
        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assert.assertEquals(surname, responseEntity.getBody().getSurname());
    }

    @Test
    public void testGetOrders() {
        int customerId = 1;
        List<Order> orderList = new ArrayList<>();
        orderList.add(new Order(customerId, "TestProduct", 2));
        orderList.add(new Order(customerId, "TestProduct2", 1));

        when(service.getOrders(customerId)).thenReturn(orderList);

        Iterable<Order> orders = controller.getOrders(customerId);
        Assert.assertNotNull(orders);
        Assert.assertEquals(2, ((List<Order>) orders).size());
    }

    @Test
    public void testAddCustomer() {
        Customer customerToAdd = new Customer("Surname", "Firstname", "Patronymic", "Address");
        when(service.addCustomer(customerToAdd))
                .thenReturn(new ResponseEntity<>(customerToAdd, HttpStatus.CREATED));

        ResponseEntity<Customer> responseEntity = controller.addCustomer(customerToAdd);
        Assert.assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        Assert.assertEquals(customerToAdd, responseEntity.getBody());
    }

    @Test
    public void testAddOrder() {
        int customerId = 1;
        Order orderToAdd = new Order(customerId, "TestProduct", 2);
        when(service.addOrder(customerId, orderToAdd))
                .thenReturn(new ResponseEntity<>(orderToAdd, HttpStatus.CREATED));

        ResponseEntity<Order> responseEntity = controller.addOrder(customerId, orderToAdd);
        Assert.assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        Assert.assertEquals(orderToAdd, responseEntity.getBody());
    }

    @Test
    public void testUpdateCustomer() {
        int customerId = 1;
        Customer customerToUpdate = new Customer("Surname", "UpdatedFirstname", "UpdatedPatronymic", "UpdatedAddress");
        when(service.updateCustomer(customerId, customerToUpdate))
                .thenReturn(new ResponseEntity<>(customerToUpdate, HttpStatus.OK));

        ResponseEntity<Customer> responseEntity = controller.updateCustomer(customerId, customerToUpdate);
        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assert.assertEquals(customerToUpdate, responseEntity.getBody());
    }

    @Test
    public void testUpdateOrder() {
        int customerId = 1;
        int orderId = 1;
        Order orderToUpdate = new Order(customerId, "UpdatedProduct", 3);
        when(service.updateOrder(customerId, orderId, orderToUpdate))
                .thenReturn(new ResponseEntity<>(orderToUpdate, HttpStatus.OK));

        ResponseEntity<Order> responseEntity = controller.updateOrder(customerId, orderId, orderToUpdate);
        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assert.assertEquals(orderToUpdate, responseEntity.getBody());
    }

    @Test
    public void testDeleteCustomer() {
        int customerId = 1;
        when(service.deleteCustomer(customerId)).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        ResponseEntity<Customer> responseEntity = controller.deleteCustomer(customerId);
        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void testDeleteOrder() {
        int customerId = 1;
        int orderId = 1;
        when(service.deleteOrder(customerId, orderId))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        ResponseEntity<Order> responseEntity = controller.deleteOrder(customerId, orderId);
        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }
}
