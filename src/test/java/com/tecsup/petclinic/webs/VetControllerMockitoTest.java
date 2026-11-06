package com.tecsup.petclinic.webs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tecsup.petclinic.dtos.VetDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import com.tecsup.petclinic.exceptions.VetNotFoundException;
import com.tecsup.petclinic.services.VetService;
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
 * Pruebas del controlador /vets con el servicio simulado (@MockitoBean).
 * Solo se prueba el controlador y el contrato JSON, sin tocar la base de datos.
 */
@AutoConfigureMockMvc
@SpringBootTest
@Slf4j
public class VetControllerMockitoTest {

	private static final ObjectMapper om = new ObjectMapper();

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private VetService service;

	private VetDTO sample() {
		return VetDTO.builder().id(1).firstName("Lucia").lastName("Torres").build();
	}

	@Test
	public void testFindAllVets() throws Exception {
		Mockito.when(service.findAll()).thenReturn(List.of(sample()));

		this.mockMvc.perform(get("/vets"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(jsonPath("$[0].id", is(1)));
	}

	@Test
	public void testFindVetOK() throws Exception {
		VetDTO dto = sample();
		Mockito.when(service.findById(1)).thenReturn(dto);

		this.mockMvc.perform(get("/vets/1"))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(1)))
				.andExpect(jsonPath("$.firstName", is("Lucia")))
				.andExpect(jsonPath("$.lastName", is("Torres")));
	}

	@Test
	public void testFindVetKO() throws Exception {
		Mockito.when(service.findById(666))
				.thenThrow(new VetNotFoundException("Record not found...!"));

		mockMvc.perform(get("/vets/666"))
				.andExpect(status().isNotFound());
	}

	@Test
	public void testFindByLastName() throws Exception {
		Mockito.when(service.findByLastName("Carter"))
				.thenReturn(List.of(sample()));

		mockMvc.perform(get("/vets?lastName=Carter"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)));
	}

	@Test
	public void testCreateVet() throws Exception {
		VetDTO newDTO = VetDTO.builder().firstName("Lucia").lastName("Torres").build();
		Mockito.when(service.create(any(VetDTO.class))).thenReturn(sample());

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
		VetDTO upDTO = VetDTO.builder().id(1).firstName("Lucia2").lastName("Torres2").build();
		Mockito.when(service.update(any(VetDTO.class))).thenReturn(upDTO);

		mockMvc.perform(put("/vets/1")
						.content(om.writeValueAsString(upDTO))
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(1)))
				.andExpect(jsonPath("$.firstName", is("Lucia2")))
				.andExpect(jsonPath("$.lastName", is("Torres2")));
	}

	@Test
	public void testUpdateVetKO() throws Exception {
		VetDTO upDTO = VetDTO.builder().id(1000).firstName("Lucia2").lastName("Torres2").build();
		Mockito.when(service.update(any(VetDTO.class)))
				.thenThrow(new VetNotFoundException("Record not found...!"));

		mockMvc.perform(put("/vets/1000")
						.content(om.writeValueAsString(upDTO))
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

	@Test
	public void testDeleteVet() throws Exception {
		Mockito.doNothing().when(service).delete(1);

		mockMvc.perform(delete("/vets/1"))
				.andExpect(status().isOk());
	}

	@Test
	public void testDeleteVetKO() throws Exception {
		Mockito.doThrow(new VetNotFoundException("Record not found...!"))
				.when(service).delete(1000);

		mockMvc.perform(delete("/vets/1000"))
				.andExpect(status().isNotFound());
	}
}
