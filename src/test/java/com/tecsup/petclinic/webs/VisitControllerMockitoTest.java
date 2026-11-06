package com.tecsup.petclinic.webs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tecsup.petclinic.dtos.VisitDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import com.tecsup.petclinic.exceptions.VisitNotFoundException;
import com.tecsup.petclinic.services.VisitService;
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
 * Pruebas del controlador /visits con el servicio simulado (@MockitoBean).
 * Solo se prueba el controlador y el contrato JSON, sin tocar la base de datos.
 */
@AutoConfigureMockMvc
@SpringBootTest
@Slf4j
public class VisitControllerMockitoTest {

	private static final ObjectMapper om = new ObjectMapper();

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private VisitService service;

	private VisitDTO sample() {
		return VisitDTO.builder().id(1L).visitDate("2026-10-20").description("vaccination").petId(1).build();
	}

	@Test
	public void testFindAllVisits() throws Exception {
		Mockito.when(service.findAll()).thenReturn(List.of(sample()));

		this.mockMvc.perform(get("/visits"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(jsonPath("$[0].id", is(1)));
	}

	@Test
	public void testFindVisitOK() throws Exception {
		VisitDTO dto = sample();
		Mockito.when(service.findById(1L)).thenReturn(dto);

		this.mockMvc.perform(get("/visits/1"))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(1)))
				.andExpect(jsonPath("$.visitDate", is("2026-10-20")))
				.andExpect(jsonPath("$.description", is("vaccination")))
				.andExpect(jsonPath("$.petId", is(1)));
	}

	@Test
	public void testFindVisitKO() throws Exception {
		Mockito.when(service.findById(666L))
				.thenThrow(new VisitNotFoundException("Record not found...!"));

		mockMvc.perform(get("/visits/666"))
				.andExpect(status().isNotFound());
	}

	@Test
	public void testFindByPetId() throws Exception {
		Mockito.when(service.findByPetId(7))
				.thenReturn(List.of(sample()));

		mockMvc.perform(get("/visits?petId=7"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)));
	}

	@Test
	public void testCreateVisit() throws Exception {
		VisitDTO newDTO = VisitDTO.builder().visitDate("2026-10-20").description("vaccination").petId(1).build();
		Mockito.when(service.create(any(VisitDTO.class))).thenReturn(sample());

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
		VisitDTO upDTO = VisitDTO.builder().id(1L).visitDate("2026-10-21").description("checkup").petId(2).build();
		Mockito.when(service.update(any(VisitDTO.class))).thenReturn(upDTO);

		mockMvc.perform(put("/visits/1")
						.content(om.writeValueAsString(upDTO))
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(1)))
				.andExpect(jsonPath("$.visitDate", is("2026-10-21")))
				.andExpect(jsonPath("$.description", is("checkup")))
				.andExpect(jsonPath("$.petId", is(2)));
	}

	@Test
	public void testUpdateVisitKO() throws Exception {
		VisitDTO upDTO = VisitDTO.builder().id(1000L).visitDate("2026-10-21").description("checkup").petId(2).build();
		Mockito.when(service.update(any(VisitDTO.class)))
				.thenThrow(new VisitNotFoundException("Record not found...!"));

		mockMvc.perform(put("/visits/1000")
						.content(om.writeValueAsString(upDTO))
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

	@Test
	public void testDeleteVisit() throws Exception {
		Mockito.doNothing().when(service).delete(1L);

		mockMvc.perform(delete("/visits/1"))
				.andExpect(status().isOk());
	}

	@Test
	public void testDeleteVisitKO() throws Exception {
		Mockito.doThrow(new VisitNotFoundException("Record not found...!"))
				.when(service).delete(1000L);

		mockMvc.perform(delete("/visits/1000"))
				.andExpect(status().isNotFound());
	}
}
