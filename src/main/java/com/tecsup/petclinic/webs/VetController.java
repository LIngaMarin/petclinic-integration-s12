package com.tecsup.petclinic.webs;

import com.tecsup.petclinic.dtos.VetDTO;
import com.tecsup.petclinic.exceptions.VetNotFoundException;
import com.tecsup.petclinic.services.VetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
public class VetController {

	private final VetService service;

	public VetController(VetService service) {
		this.service = service;
	}

	/** Lista todos, o filtra por lastName si viene como parametro. */
	@GetMapping(value = "/vets")
	public ResponseEntity<List<VetDTO>> findAll(@RequestParam(required = false) String lastName) {
		if (lastName != null) {
			return ResponseEntity.ok(service.findByLastName(lastName));
		}
		return ResponseEntity.ok(service.findAll());
	}

	@GetMapping(value = "/vets/{id}")
	public ResponseEntity<VetDTO> findById(@PathVariable Integer id) {
		try {
			return ResponseEntity.ok(service.findById(id));
		} catch (VetNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}

	@PostMapping(value = "/vets")
	public ResponseEntity<VetDTO> create(@RequestBody VetDTO dto) {
		if (dto.getLastName() == null || dto.getLastName().isBlank()) {
			return ResponseEntity.badRequest().build();
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
	}

	@PutMapping(value = "/vets/{id}")
	public ResponseEntity<VetDTO> update(@RequestBody VetDTO dto, @PathVariable Integer id) {
		if (dto.getLastName() == null || dto.getLastName().isBlank()) {
			return ResponseEntity.badRequest().build();
		}
		dto.setId(id);
		try {
			return ResponseEntity.ok(service.update(dto));
		} catch (VetNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}

	@DeleteMapping(value = "/vets/{id}")
	public ResponseEntity<String> delete(@PathVariable Integer id) {
		try {
			service.delete(id);
			return ResponseEntity.ok(" Delete ID :" + id);
		} catch (VetNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}
}
