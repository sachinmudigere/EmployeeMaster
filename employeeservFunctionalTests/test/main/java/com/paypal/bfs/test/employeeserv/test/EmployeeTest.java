package com.paypal.bfs.test.employeeserv.test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.paypal.bfs.test.employeeserv.api.model.Address;
import com.paypal.bfs.test.employeeserv.api.model.Employee;
import com.paypal.bfs.test.employeeserv.domain.AddressEntity;
import com.paypal.bfs.test.employeeserv.domain.EmployeeEntity;

@SpringBootTest
@AutoConfigureMockMvc
public class EmployeeTest {

	@Autowired
	MockMvc mockMvc;
	
	@Test
	public void contextLoads() {
		
	}
	
	@Test
	public void testCreateAndGetUser() {
		try {
			Address address = new Address();
			address.setLine1("line1");
			address.setCity("city");
			address.setCountry("country");
			address.setState("state");
			address.setZipcode("zipcode");
			List<Address> addresses = new ArrayList<>();
			addresses.add(address);
			
			Employee employee = new Employee();
			employee.setFirstName("firstName");
			employee.setLastName("lastName");
			employee.setDateOfBirth("22/01/1990");
			employee.setAddress(addresses);
			
			ObjectMapper mapper = new ObjectMapper();
		    mockMvc.perform(
		            MockMvcRequestBuilders.post("/v1/bfs/employees")
		                    .contentType(MediaType.APPLICATION_JSON)
		                    .content(mapper.writeValueAsString(employee)))
		            .andExpect(status().isCreated());
		    
			this.mockMvc.perform(MockMvcRequestBuilders.get("/v1/bfs/employees/1")).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk());
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
