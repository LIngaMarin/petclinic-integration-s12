package com.tecsup.petclinic.webs;

import com.tecsup.petclinic.dtos.OwnerDTO;
import com.tecsup.petclinic.exceptions.OwnerNotFoundException;
import com.tecsup.petclinic.services.OwnerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
public class OwnerController {

	private final OwnerService service;

	public OwnerController(OwnerService service) {
		this.service = service;
	}

	/** Lista todos, o filtra por lastName si viene como parametro. */
	@GetMapping(value = "/owners")
	public ResponseEntity<List<OwnerDTO>> findAll(@RequestParam(required = false) String lastName) {
		if (lastName != null) {
			return ResponseEntity.ok(service.findByLastName(lastName));
		}
		return ResponseEntity.ok(service.findAll());
	}

	@GetMapping(value = "/owners/{id}")
	public ResponseEntity<OwnerDTO> findById(@PathVariable Long id) {
		try {
			return ResponseEntity.ok(service.findById(id));
		} catch (OwnerNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}

	@PostMapping(value = "/owners")
	public ResponseEntity<OwnerDTO> create(@RequestBody OwnerDTO dto) {
		if (dto.getLastName() == null || dto.getLastName().isBlank()) {
			return ResponseEntity.badRequest().build();
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
	}

	@PutMapping(value = "/owners/{id}")
	public ResponseEntity<OwnerDTO> update(@RequestBody OwnerDTO dto, @PathVariable Long id) {
		if (dto.getLastName() == null || dto.getLastName().isBlank()) {
			return ResponseEntity.badRequest().build();
		}
		dto.setId(id);
		try {
			return ResponseEntity.ok(service.update(dto));
		} catch (OwnerNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}

	@DeleteMapping(value = "/owners/{id}")
	public ResponseEntity<String> delete(@PathVariable Long id) {
		try {
			service.delete(id);
			return ResponseEntity.ok(" Delete ID :" + id);
		} catch (OwnerNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}
}
