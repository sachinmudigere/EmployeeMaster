package com.paypal.bfs.test.employeeserv;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.paypal.bfs.test.employeeserv.api.model.Address;
import com.paypal.bfs.test.employeeserv.api.model.Employee;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = EmployeeservApplication.class)
@AutoConfigureMockMvc
public class EmployeeTest {

	@Autowired
	MockMvc mockMvc;
	
	Employee employee;
	
	Employee employeeDuplicate;

	Employee employeeWithoutAddress;
	
	Employee employeeInvalid;
	
	Employee employeeWithInvalidAddress;
	
	ObjectMapper mapper;

	@Before
	public void setup() {
		mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate birthDate = LocalDate.parse("2001-10-11", formatter);

		Address address = new Address();
		address.setLine1("line1");
		address.setCity("city");
		address.setCountry("country");
		address.setState("state");
		address.setZipcode("zipcode");
		List<Address> addresses = new ArrayList<>();
		addresses.add(address);
		
		Address invalidAddress = new Address();
		invalidAddress.setCity("city");
		invalidAddress.setCountry("country");
		invalidAddress.setState("state");
		invalidAddress.setZipcode("zipcode");
		List<Address> invalidAddresses = new ArrayList<>();
		invalidAddresses.add(invalidAddress);

		employee = new Employee();
		employee.setFirstName("firstName");
		employee.setLastName("lastName");
		employee.setDateOfBirth(birthDate);
		employee.setAddress(addresses);
		
		employeeDuplicate = new Employee();
		employeeDuplicate.setFirstName("firstName3");
		employeeDuplicate.setLastName("lastName3");
		employeeDuplicate.setDateOfBirth(birthDate);
		employeeDuplicate.setAddress(addresses);
		

		employeeWithoutAddress = new Employee();
		employeeWithoutAddress.setFirstName("firstName1");
		employeeWithoutAddress.setLastName("lastName1");
		employeeWithoutAddress.setDateOfBirth(birthDate);

		employeeInvalid = new Employee();
		employeeInvalid.setFirstName("firstName2");
		employeeInvalid.setDateOfBirth(birthDate);
		
		employeeWithInvalidAddress = new Employee();
		employeeWithInvalidAddress.setFirstName("firstName4");
		employeeWithInvalidAddress.setLastName("lastName4");
		employeeWithInvalidAddress.setDateOfBirth(birthDate);
		employeeWithInvalidAddress.setAddress(invalidAddresses);
	}
	
	@Test
	public void testCreateAndGetEmployee()  throws Exception {
			
			String test = mapper.writeValueAsString(employee);
			MvcResult result = mockMvc.perform(
		            MockMvcRequestBuilders.post("/v1/bfs/employees")
		                    .contentType(MediaType.APPLICATION_JSON)
		                    .content(mapper.writeValueAsString(employee)))
		            .andExpect(status().isCreated())
		            .andExpect(MockMvcResultMatchers.jsonPath("$.first_name").value("firstName"))
		            .andReturn();
		    
			employee = mapper.readValue(result.getResponse().getContentAsString(), Employee.class);
			
			this.mockMvc.perform(MockMvcRequestBuilders.get("/v1/bfs/employees/"+employee.getId()))
			.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk());
			
	}
	
	@Test
	public void testCreateAndGetEmployeeWithoutAddress() throws Exception {
			MvcResult result = mockMvc.perform(
		            MockMvcRequestBuilders.post("/v1/bfs/employees")
		                    .contentType(MediaType.APPLICATION_JSON)
		                    .content(mapper.writeValueAsString(employeeWithoutAddress)))
		            .andExpect(status().isCreated())
		            .andExpect(MockMvcResultMatchers.jsonPath("$.first_name").value("firstName1"))
		            .andReturn();
		    
			employeeWithoutAddress = mapper.readValue(result.getResponse().getContentAsString(), Employee.class);
			
			this.mockMvc.perform(MockMvcRequestBuilders.get("/v1/bfs/employees/"+employeeWithoutAddress.getId()))
			.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk());
			
	}
	
	@Test
	public void testCreateInvalidEmployee() throws Exception {
			
			mockMvc.perform(
		            MockMvcRequestBuilders.post("/v1/bfs/employees")
		                    .contentType(MediaType.APPLICATION_JSON)
		                    .content(mapper.writeValueAsString(employeeInvalid)))
		            .andExpect(status().isBadRequest());
		    
	}

	@Test
	public void testCreateEmployeeWithInvalidAddress() throws Exception {

		mockMvc.perform(
		            MockMvcRequestBuilders.post("/v1/bfs/employees")
		                    .contentType(MediaType.APPLICATION_JSON)
		                    .content(mapper.writeValueAsString(employeeWithInvalidAddress)))
		            .andExpect(status().isBadRequest());
		    
	}

	@Test
	public void testEmployeeNotFound() throws Exception {

		this.mockMvc.perform(MockMvcRequestBuilders.get("/v1/bfs/employees/100"))
			.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isNotFound());
			

	}
	
	@Test
	public void testDuplicateEmployeeCreation() throws Exception {
			mockMvc.perform(
		            MockMvcRequestBuilders.post("/v1/bfs/employees")
		                    .contentType(MediaType.APPLICATION_JSON)
		                    .content(mapper.writeValueAsString(employeeDuplicate)))
		            .andExpect(status().isCreated());		    
			
			mockMvc.perform(
		            MockMvcRequestBuilders.post("/v1/bfs/employees")
		                    .contentType(MediaType.APPLICATION_JSON)
		                    .content(mapper.writeValueAsString(employeeDuplicate)))
		            .andExpect(status().isConflict());		    

	}

}
