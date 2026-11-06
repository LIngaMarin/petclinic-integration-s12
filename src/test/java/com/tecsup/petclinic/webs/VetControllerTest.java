package com.tecsup.petclinic.webs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.tecsup.petclinic.dtos.VetDTO;
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
 * Pruebas de integración de /vets: la petición HTTP atraviesa el controlador,
 * el servicio y el repositorio hasta la base H2 (sin mocks).
 */
@AutoConfigureMockMvc
@SpringBootTest
@Slf4j
public class VetControllerTest {

	private static final ObjectMapper om = new ObjectMapper();

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void testFindAllVets() throws Exception {
		this.mockMvc.perform(get("/vets"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(jsonPath("$[0].id", is(1)));
	}

	@Test
	public void testFindVetOK() throws Exception {
		this.mockMvc.perform(get("/vets/1"))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(1)))
				.andExpect(jsonPath("$.lastName", is("Carter")))
				.andExpect(jsonPath("$.firstName", is("James")));
	}

	@Test
	public void testFindVetKO() throws Exception {
		mockMvc.perform(get("/vets/666"))
				.andExpect(status().isNotFound());
	}

	@Test
	public void testFindByLastName() throws Exception {
		mockMvc.perform(get("/vets?lastName=Carter"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)));
	}

	@Test
	public void testCreateVet() throws Exception {
		VetDTO newDTO = VetDTO.builder().firstName("Lucia").lastName("Torres").build();

		this.mockMvc.perform(post("/vets")
						.content(om.writeValueAsString(newDTO))
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.firstName", is("Lucia")))
				.andExpect(jsonPath("$.lastName", is("Torres")));
	}

	@Test
	public void testCreateVet_BadRequest() throws Exception {
		VetDTO badDTO = VetDTO.builder().firstName("SinApellido").build();

		this.mockMvc.perform(post("/vets")
						.content(om.writeValueAsString(badDTO))
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testUpdateVet() throws Exception {
		VetDTO newDTO = VetDTO.builder().firstName("Lucia").lastName("Torres").build();

		// CREATE
		ResultActions created = mockMvc.perform(post("/vets")
						.content(om.writeValueAsString(newDTO))
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());
		Integer id = JsonPath.parse(created.andReturn().getResponse().getContentAsString()).read("$.id");

		// UPDATE
		VetDTO upDTO = VetDTO.builder().firstName("Lucia2").lastName("Torres2").build();
		mockMvc.perform(put("/vets/" + id)
						.content(om.writeValueAsString(upDTO))
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk());

		// FIND: confirma el cambio
		mockMvc.perform(get("/vets/" + id))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(id)))
				.andExpect(jsonPath("$.firstName", is("Lucia2")))
				.andExpect(jsonPath("$.lastName", is("Torres2")));

		// DELETE
		mockMvc.perform(delete("/vets/" + id))
				.andExpect(status().isOk());
	}

	@Test
	public void testDeleteVet() throws Exception {
		VetDTO newDTO = VetDTO.builder().firstName("Lucia").lastName("Torres").build();

		ResultActions created = mockMvc.perform(post("/vets")
						.content(om.writeValueAsString(newDTO))
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());
		Integer id = JsonPath.parse(created.andReturn().getResponse().getContentAsString()).read("$.id");

		mockMvc.perform(delete("/vets/" + id))
				.andExpect(status().isOk());

		mockMvc.perform(get("/vets/" + id))
				.andExpect(status().isNotFound());
	}

	@Test
	public void testDeleteVetKO() throws Exception {
		mockMvc.perform(delete("/vets/1000"))
				.andExpect(status().isNotFound());
	}
}
