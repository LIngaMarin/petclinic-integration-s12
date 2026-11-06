package com.tecsup.petclinic.webs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tecsup.petclinic.dtos.OwnerDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import com.tecsup.petclinic.exceptions.OwnerNotFoundException;
import com.tecsup.petclinic.services.OwnerService;
import org.mockito.Mockito;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas del controlador /owners con el servicio simulado (@MockitoBean).
 * Solo se prueba el controlador y el contrato JSON, sin tocar la base de datos.
 */
@AutoConfigureMockMvc
@SpringBootTest
@Slf4j
public class OwnerControllerMockitoTest {

	private static final ObjectMapper om = new ObjectMapper();

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private OwnerService service;

	private OwnerDTO sample() {
		return OwnerDTO.builder().id(1L).firstName("Ana").lastName("Perez").address("Calle 1").city("Lima").telephone("999111222").build();
	}

	@Test
	public void testFindAllOwners() throws Exception {
		Mockito.when(service.findAll()).thenReturn(List.of(sample()));

		this.mockMvc.perform(get("/owners"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(jsonPath("$[0].id", is(1)));
	}

	@Test
	public void testFindOwnerOK() throws Exception {
		OwnerDTO dto = sample();
		Mockito.when(service.findById(1L)).thenReturn(dto);

		this.mockMvc.perform(get("/owners/1"))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(1)))
				.andExpect(jsonPath("$.firstName", is("Ana")))
				.andExpect(jsonPath("$.lastName", is("Perez")))
				.andExpect(jsonPath("$.address", is("Calle 1")))
				.andExpect(jsonPath("$.city", is("Lima")))
				.andExpect(jsonPath("$.telephone", is("999111222")));
	}

	@Test
	public void testFindOwnerKO() throws Exception {
		Mockito.when(service.findById(666L))
				.thenThrow(new OwnerNotFoundException("Record not found...!"));

		mockMvc.perform(get("/owners/666"))
				.andExpect(status().isNotFound());
	}

	@Test
	public void testFindByLastName() throws Exception {
		Mockito.when(service.findByLastName("Davis"))
				.thenReturn(List.of(sample()));

		mockMvc.perform(get("/owners?lastName=Davis"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)));
	}

	@Test
	public void testCreateOwner() throws Exception {
		OwnerDTO newDTO = OwnerDTO.builder().firstName("Ana").lastName("Perez").address("Calle 1").city("Lima").telephone("999111222").build();
		Mockito.when(service.create(any(OwnerDTO.class))).thenReturn(sample());

		this.mockMvc.perform(post("/owners")
						.content(om.writeValueAsString(newDTO))
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.firstName", is("Ana")))
				.andExpect(jsonPath("$.lastName", is("Perez")))
				.andExpect(jsonPath("$.address", is("Calle 1")))
				.andExpect(jsonPath("$.city", is("Lima")))
				.andExpect(jsonPath("$.telephone", is("999111222")));
	}

	@Test
	public void testCreateOwner_BadRequest() throws Exception {
		OwnerDTO badDTO = OwnerDTO.builder().firstName("SinApellido").build();

		this.mockMvc.perform(post("/owners")
						.content(om.writeValueAsString(badDTO))
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testUpdateOwner() throws Exception {
		OwnerDTO upDTO = OwnerDTO.builder().id(1L).firstName("Ana2").lastName("Perez2").address("Calle 2").city("Cusco").telephone("999000111").build();
		Mockito.when(service.update(any(OwnerDTO.class))).thenReturn(upDTO);

		mockMvc.perform(put("/owners/1")
						.content(om.writeValueAsString(upDTO))
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(1)))
				.andExpect(jsonPath("$.firstName", is("Ana2")))
				.andExpect(jsonPath("$.lastName", is("Perez2")))
				.andExpect(jsonPath("$.address", is("Calle 2")))
				.andExpect(jsonPath("$.city", is("Cusco")))
				.andExpect(jsonPath("$.telephone", is("999000111")));
	}

	@Test
	public void testUpdateOwnerKO() throws Exception {
		OwnerDTO upDTO = OwnerDTO.builder().id(1000L).firstName("Ana2").lastName("Perez2").address("Calle 2").city("Cusco").telephone("999000111").build();
		Mockito.when(service.update(any(OwnerDTO.class)))
				.thenThrow(new OwnerNotFoundException("Record not found...!"));

		mockMvc.perform(put("/owners/1000")
						.content(om.writeValueAsString(upDTO))
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

	@Test
	public void testDeleteOwner() throws Exception {
		Mockito.doNothing().when(service).delete(1L);

		mockMvc.perform(delete("/owners/1"))
				.andExpect(status().isOk());
	}

	@Test
	public void testDeleteOwnerKO() throws Exception {
		Mockito.doThrow(new OwnerNotFoundException("Record not found...!"))
				.when(service).delete(1000L);

		mockMvc.perform(delete("/owners/1000"))
				.andExpect(status().isNotFound());
	}
}
