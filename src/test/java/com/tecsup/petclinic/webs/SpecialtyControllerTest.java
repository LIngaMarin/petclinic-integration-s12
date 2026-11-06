package com.tecsup.petclinic.webs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.tecsup.petclinic.dtos.SpecialtyDTO;
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
 * Pruebas de integración de /specialties: la petición HTTP atraviesa el controlador,
 * el servicio y el repositorio hasta la base H2 (sin mocks).
 */
@AutoConfigureMockMvc
@SpringBootTest
@Slf4j
public class SpecialtyControllerTest {

	private static final ObjectMapper om = new ObjectMapper();

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void testFindAllSpecialties() throws Exception {
		this.mockMvc.perform(get("/specialties"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(jsonPath("$[0].id", is(1)));
	}

	@Test
	public void testFindSpecialtyOK() throws Exception {
		this.mockMvc.perform(get("/specialties/1"))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(1)))
				.andExpect(jsonPath("$.name", is("radiology")));
	}

	@Test
	public void testFindSpecialtyKO() throws Exception {
		mockMvc.perform(get("/specialties/666"))
				.andExpect(status().isNotFound());
	}

	@Test
	public void testFindByName() throws Exception {
		mockMvc.perform(get("/specialties?name=surgery"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)));
	}

	@Test
	public void testCreateSpecialty() throws Exception {
		SpecialtyDTO newDTO = SpecialtyDTO.builder().name("cardiology").build();

		this.mockMvc.perform(post("/specialties")
						.content(om.writeValueAsString(newDTO))
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.name", is("cardiology")));
	}

	@Test
	public void testCreateSpecialty_BadRequest() throws Exception {
		SpecialtyDTO badDTO = SpecialtyDTO.builder().build();

		this.mockMvc.perform(post("/specialties")
						.content(om.writeValueAsString(badDTO))
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testUpdateSpecialty() throws Exception {
		SpecialtyDTO newDTO = SpecialtyDTO.builder().name("cardiology").build();

		// CREATE
		ResultActions created = mockMvc.perform(post("/specialties")
						.content(om.writeValueAsString(newDTO))
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());
		Integer id = JsonPath.parse(created.andReturn().getResponse().getContentAsString()).read("$.id");

		// UPDATE
		SpecialtyDTO upDTO = SpecialtyDTO.builder().name("cardiology2").build();
		mockMvc.perform(put("/specialties/" + id)
						.content(om.writeValueAsString(upDTO))
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk());

		// FIND: confirma el cambio
		mockMvc.perform(get("/specialties/" + id))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(id)))
				.andExpect(jsonPath("$.name", is("cardiology2")));

		// DELETE
		mockMvc.perform(delete("/specialties/" + id))
				.andExpect(status().isOk());
	}

	@Test
	public void testDeleteSpecialty() throws Exception {
		SpecialtyDTO newDTO = SpecialtyDTO.builder().name("cardiology").build();

		ResultActions created = mockMvc.perform(post("/specialties")
						.content(om.writeValueAsString(newDTO))
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());
		Integer id = JsonPath.parse(created.andReturn().getResponse().getContentAsString()).read("$.id");

		mockMvc.perform(delete("/specialties/" + id))
				.andExpect(status().isOk());

		mockMvc.perform(get("/specialties/" + id))
				.andExpect(status().isNotFound());
	}

	@Test
	public void testDeleteSpecialtyKO() throws Exception {
		mockMvc.perform(delete("/specialties/1000"))
				.andExpect(status().isNotFound());
	}
}
