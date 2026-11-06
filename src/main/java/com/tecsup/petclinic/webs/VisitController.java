package com.tecsup.petclinic.webs;

import com.tecsup.petclinic.dtos.VisitDTO;
import com.tecsup.petclinic.exceptions.VisitNotFoundException;
import com.tecsup.petclinic.exceptions.PetNotFoundException;
import com.tecsup.petclinic.services.VisitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
public class VisitController {

	private final VisitService service;

	public VisitController(VisitService service) {
		this.service = service;
	}

	/** Lista todos, o filtra por petId si viene como parametro. */
	@GetMapping(value = "/visits")
	public ResponseEntity<List<VisitDTO>> findAll(@RequestParam(required = false) Integer petId) {
		if (petId != null) {
			return ResponseEntity.ok(service.findByPetId(petId));
		}
		return ResponseEntity.ok(service.findAll());
	}

	@GetMapping(value = "/visits/{id}")
	public ResponseEntity<VisitDTO> findById(@PathVariable Long id) {
		try {
			return ResponseEntity.ok(service.findById(id));
		} catch (VisitNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}

	@PostMapping(value = "/visits")
	public ResponseEntity<VisitDTO> create(@RequestBody VisitDTO dto) {
		if (dto.getPetId() == null) {
			return ResponseEntity.badRequest().build();
		}
		try {
			return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
		} catch (PetNotFoundException e) {
			return ResponseEntity.badRequest().build();
		}
	}

	@PutMapping(value = "/visits/{id}")
	public ResponseEntity<VisitDTO> update(@RequestBody VisitDTO dto, @PathVariable Long id) {
		if (dto.getPetId() == null) {
			return ResponseEntity.badRequest().build();
		}
		dto.setId(id);
		try {
			return ResponseEntity.ok(service.update(dto));
		} catch (VisitNotFoundException e) {
			return ResponseEntity.notFound().build();
		} catch (PetNotFoundException e) {
			return ResponseEntity.badRequest().build();
		}
	}

	@DeleteMapping(value = "/visits/{id}")
	public ResponseEntity<String> delete(@PathVariable Long id) {
		try {
			service.delete(id);
			return ResponseEntity.ok(" Delete ID :" + id);
		} catch (VisitNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}
}
