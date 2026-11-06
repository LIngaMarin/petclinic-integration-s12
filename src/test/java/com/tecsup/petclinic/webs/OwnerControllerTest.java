package com.tecsup.petclinic.webs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.tecsup.petclinic.dtos.OwnerDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de integración de /owners: la petición HTTP atraviesa el controlador,
 * el servicio y el repositorio hasta la base H2 (sin mocks).
 */
@AutoConfigureMockMvc
@SpringBootTest
@Slf4j
public class OwnerControllerTest {

	private static final ObjectMapper om = new ObjectMapper();

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void testFindAllOwners() throws Exception {
		this.mockMvc.perform(get("/owners"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(jsonPath("$[0].id", is(1)));
	}

	@Test
	public void testFindOwnerOK() throws Exception {
		this.mockMvc.perform(get("/owners/1"))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(1)))
				.andExpect(jsonPath("$.lastName", is("Franklin")))
				.andExpect(jsonPath("$.firstName", is("George")))
				.andExpect(jsonPath("$.city", is("Madison")));
	}

	@Test
	public void testFindOwnerKO() throws Exception {
		mockMvc.perform(get("/owners/666"))
				.andExpect(status().isNotFound());
	}

	@Test
	public void testFindByLastName() throws Exception {
		mockMvc.perform(get("/owners?lastName=Davis"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2)));
	}

	@Test
	public void testCreateOwner() throws Exception {
		OwnerDTO newDTO = OwnerDTO.builder().firstName("Ana").lastName("Perez").address("Calle 1").city("Lima").telephone("999111222").build();

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
		OwnerDTO newDTO = OwnerDTO.builder().firstName("Ana").lastName("Perez").address("Calle 1").city("Lima").telephone("999111222").build();

		// CREATE
		ResultActions created = mockMvc.perform(post("/owners")
						.content(om.writeValueAsString(newDTO))
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());
		Integer id = JsonPath.parse(created.andReturn().getResponse().getContentAsString()).read("$.id");

		// UPDATE
		OwnerDTO upDTO = OwnerDTO.builder().firstName("Ana2").lastName("Perez2").address("Calle 2").city("Cusco").telephone("999000111").build();
		mockMvc.perform(put("/owners/" + id)
						.content(om.writeValueAsString(upDTO))
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk());

		// FIND: confirma el cambio
		mockMvc.perform(get("/owners/" + id))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(id)))
				.andExpect(jsonPath("$.firstName", is("Ana2")))
				.andExpect(jsonPath("$.lastName", is("Perez2")))
				.andExpect(jsonPath("$.address", is("Calle 2")))
				.andExpect(jsonPath("$.city", is("Cusco")))
				.andExpect(jsonPath("$.telephone", is("999000111")));

		// DELETE
		mockMvc.perform(delete("/owners/" + id))
				.andExpect(status().isOk());
	}

	@Test
	public void testDeleteOwner() throws Exception {
		OwnerDTO newDTO = OwnerDTO.builder().firstName("Ana").lastName("Perez").address("Calle 1").city("Lima").telephone("999111222").build();

		ResultActions created = mockMvc.perform(post("/owners")
						.content(om.writeValueAsString(newDTO))
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());
		Integer id = JsonPath.parse(created.andReturn().getResponse().getContentAsString()).read("$.id");

		mockMvc.perform(delete("/owners/" + id))
				.andExpect(status().isOk());

		mockMvc.perform(get("/owners/" + id))
				.andExpect(status().isNotFound());
	}

	@Test
	public void testDeleteOwnerKO() throws Exception {
		mockMvc.perform(delete("/owners/1000"))
				.andExpect(status().isNotFound());
	}
}
