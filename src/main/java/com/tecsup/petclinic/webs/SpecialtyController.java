package com.tecsup.petclinic.webs;

import com.tecsup.petclinic.dtos.SpecialtyDTO;
import com.tecsup.petclinic.exceptions.SpecialtyNotFoundException;
import com.tecsup.petclinic.services.SpecialtyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
public class SpecialtyController {

	private final SpecialtyService service;

	public SpecialtyController(SpecialtyService service) {
		this.service = service;
	}

	/** Lista todos, o filtra por name si viene como parametro. */
	@GetMapping(value = "/specialties")
	public ResponseEntity<List<SpecialtyDTO>> findAll(@RequestParam(required = false) String name) {
		if (name != null) {
			return ResponseEntity.ok(service.findByName(name));
		}
		return ResponseEntity.ok(service.findAll());
	}

	@GetMapping(value = "/specialties/{id}")
	public ResponseEntity<SpecialtyDTO> findById(@PathVariable Integer id) {
		try {
			return ResponseEntity.ok(service.findById(id));
		} catch (SpecialtyNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}

	@PostMapping(value = "/specialties")
	public ResponseEntity<SpecialtyDTO> create(@RequestBody SpecialtyDTO dto) {
		if (dto.getName() == null || dto.getName().isBlank()) {
			return ResponseEntity.badRequest().build();
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
	}

	@PutMapping(value = "/specialties/{id}")
	public ResponseEntity<SpecialtyDTO> update(@RequestBody SpecialtyDTO dto, @PathVariable Integer id) {
		if (dto.getName() == null || dto.getName().isBlank()) {
			return ResponseEntity.badRequest().build();
		}
		dto.setId(id);
		try {
			return ResponseEntity.ok(service.update(dto));
		} catch (SpecialtyNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}

	@DeleteMapping(value = "/specialties/{id}")
	public ResponseEntity<String> delete(@PathVariable Integer id) {
		try {
			service.delete(id);
			return ResponseEntity.ok(" Delete ID :" + id);
		} catch (SpecialtyNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}
}
