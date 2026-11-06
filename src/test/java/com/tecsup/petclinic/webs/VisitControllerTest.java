package com.tecsup.petclinic.webs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.tecsup.petclinic.dtos.VisitDTO;
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
 * Pruebas de integración de /visits: la petición HTTP atraviesa el controlador,
 * el servicio y el repositorio hasta la base H2 (sin mocks).
 */
@AutoConfigureMockMvc
@SpringBootTest
@Slf4j
public class VisitControllerTest {

	private static final ObjectMapper om = new ObjectMapper();

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void testFindAllVisits() throws Exception {
		this.mockMvc.perform(get("/visits"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(jsonPath("$[0].id", is(1)));
	}

	@Test
	public void testFindVisitOK() throws Exception {
		this.mockMvc.perform(get("/visits/1"))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(1)))
				.andExpect(jsonPath("$.description", is("rabies shot")))
				.andExpect(jsonPath("$.petId", is(7)))
				.andExpect(jsonPath("$.visitDate", is("2010-03-04")));
	}

	@Test
	public void testFindVisitKO() throws Exception {
		mockMvc.perform(get("/visits/666"))
				.andExpect(status().isNotFound());
	}

	@Test
	public void testFindByPetId() throws Exception {
		mockMvc.perform(get("/visits?petId=7"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2)));
	}

	@Test
	public void testCreateVisit() throws Exception {
		VisitDTO newDTO = VisitDTO.builder().visitDate("2026-10-20").description("vaccination").petId(1).build();

		this.mockMvc.perform(post("/visits")
						.content(om.writeValueAsString(newDTO))
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.visitDate", is("2026-10-20")))
				.andExpect(jsonPath("$.description", is("vaccination")))
				.andExpect(jsonPath("$.petId", is(1)));
	}

	@Test
	public void testCreateVisit_BadRequest() throws Exception {
		VisitDTO badDTO = VisitDTO.builder().description("sin mascota").build();

		this.mockMvc.perform(post("/visits")
						.content(om.writeValueAsString(badDTO))
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testUpdateVisit() throws Exception {
		VisitDTO newDTO = VisitDTO.builder().visitDate("2026-10-20").description("vaccination").petId(1).build();

		// CREATE
		ResultActions created = mockMvc.perform(post("/visits")
						.content(om.writeValueAsString(newDTO))
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());
		Integer id = JsonPath.parse(created.andReturn().getResponse().getContentAsString()).read("$.id");

		// UPDATE
		VisitDTO upDTO = VisitDTO.builder().visitDate("2026-10-21").description("checkup").petId(2).build();
		mockMvc.perform(put("/visits/" + id)
						.content(om.writeValueAsString(upDTO))
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk());

		// FIND: confirma el cambio
		mockMvc.perform(get("/visits/" + id))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(id)))
				.andExpect(jsonPath("$.visitDate", is("2026-10-21")))
				.andExpect(jsonPath("$.description", is("checkup")))
				.andExpect(jsonPath("$.petId", is(2)));

		// DELETE
		mockMvc.perform(delete("/visits/" + id))
				.andExpect(status().isOk());
	}

	@Test
	public void testDeleteVisit() throws Exception {
		VisitDTO newDTO = VisitDTO.builder().visitDate("2026-10-20").description("vaccination").petId(1).build();

		ResultActions created = mockMvc.perform(post("/visits")
						.content(om.writeValueAsString(newDTO))
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());
		Integer id = JsonPath.parse(created.andReturn().getResponse().getContentAsString()).read("$.id");

		mockMvc.perform(delete("/visits/" + id))
				.andExpect(status().isOk());

		mockMvc.perform(get("/visits/" + id))
				.andExpect(status().isNotFound());
	}

	@Test
	public void testDeleteVisitKO() throws Exception {
		mockMvc.perform(delete("/visits/1000"))
				.andExpect(status().isNotFound());
	}
}
