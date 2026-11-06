package com.tecsup.petclinic.webs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tecsup.petclinic.dtos.SpecialtyDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import com.tecsup.petclinic.exceptions.SpecialtyNotFoundException;
import com.tecsup.petclinic.services.SpecialtyService;
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
 * Pruebas del controlador /specialties con el servicio simulado (@MockitoBean).
 * Solo se prueba el controlador y el contrato JSON, sin tocar la base de datos.
 */
@AutoConfigureMockMvc
@SpringBootTest
@Slf4j
public class SpecialtyControllerMockitoTest {

	private static final ObjectMapper om = new ObjectMapper();

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private SpecialtyService service;

	private SpecialtyDTO sample() {
		return SpecialtyDTO.builder().id(1).name("cardiology").build();
	}

	@Test
	public void testFindAllSpecialties() throws Exception {
		Mockito.when(service.findAll()).thenReturn(List.of(sample()));

		this.mockMvc.perform(get("/specialties"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(jsonPath("$[0].id", is(1)));
	}

	@Test
	public void testFindSpecialtyOK() throws Exception {
		SpecialtyDTO dto = sample();
		Mockito.when(service.findById(1)).thenReturn(dto);

		this.mockMvc.perform(get("/specialties/1"))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(1)))
				.andExpect(jsonPath("$.name", is("cardiology")));
	}

	@Test
	public void testFindSpecialtyKO() throws Exception {
		Mockito.when(service.findById(666))
				.thenThrow(new SpecialtyNotFoundException("Record not found...!"));

		mockMvc.perform(get("/specialties/666"))
				.andExpect(status().isNotFound());
	}

	@Test
	public void testFindByName() throws Exception {
		Mockito.when(service.findByName("surgery"))
				.thenReturn(List.of(sample()));

		mockMvc.perform(get("/specialties?name=surgery"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)));
	}

	@Test
	public void testCreateSpecialty() throws Exception {
		SpecialtyDTO newDTO = SpecialtyDTO.builder().name("cardiology").build();
		Mockito.when(service.create(any(SpecialtyDTO.class))).thenReturn(sample());

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
		SpecialtyDTO upDTO = SpecialtyDTO.builder().id(1).name("cardiology2").build();
		Mockito.when(service.update(any(SpecialtyDTO.class))).thenReturn(upDTO);

		mockMvc.perform(put("/specialties/1")
						.content(om.writeValueAsString(upDTO))
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(1)))
				.andExpect(jsonPath("$.name", is("cardiology2")));
	}

	@Test
	public void testUpdateSpecialtyKO() throws Exception {
		SpecialtyDTO upDTO = SpecialtyDTO.builder().id(1000).name("cardiology2").build();
		Mockito.when(service.update(any(SpecialtyDTO.class)))
				.thenThrow(new SpecialtyNotFoundException("Record not found...!"));

		mockMvc.perform(put("/specialties/1000")
						.content(om.writeValueAsString(upDTO))
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

	@Test
	public void testDeleteSpecialty() throws Exception {
		Mockito.doNothing().when(service).delete(1);

		mockMvc.perform(delete("/specialties/1"))
				.andExpect(status().isOk());
	}

	@Test
	public void testDeleteSpecialtyKO() throws Exception {
		Mockito.doThrow(new SpecialtyNotFoundException("Record not found...!"))
				.when(service).delete(1000);

		mockMvc.perform(delete("/specialties/1000"))
				.andExpect(status().isNotFound());
	}
}
