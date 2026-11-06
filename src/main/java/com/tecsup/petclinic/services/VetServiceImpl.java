package com.tecsup.petclinic.services;

import com.tecsup.petclinic.dtos.VetDTO;
import com.tecsup.petclinic.entities.Vet;
import com.tecsup.petclinic.exceptions.VetNotFoundException;
import com.tecsup.petclinic.repositories.VetRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VetServiceImpl implements VetService {

	private final VetRepository repository;

	public VetServiceImpl(VetRepository repository) {
		this.repository = repository;
	}

	@Override
	public List<VetDTO> findAll() {
		return repository.findAll().stream().map(this::toDto).collect(Collectors.toList());
	}

	@Override
	public VetDTO findById(Integer id) throws VetNotFoundException {
		return repository.findById(id)
				.map(this::toDto)
				.orElseThrow(() -> new VetNotFoundException("Record not found...!"));
	}

	@Override
	public VetDTO create(VetDTO dto)  {
		Vet entity = new Vet();
		fill(entity, dto);
		return toDto(repository.save(entity));
	}

	@Override
	public VetDTO update(VetDTO dto) throws VetNotFoundException {
		Vet entity = repository.findById(dto.getId())
				.orElseThrow(() -> new VetNotFoundException("Record not found...!"));
		fill(entity, dto);
		return toDto(repository.save(entity));
	}

	@Override
	public void delete(Integer id) throws VetNotFoundException {
		Vet entity = repository.findById(id)
				.orElseThrow(() -> new VetNotFoundException("Record not found...!"));
		repository.delete(entity);
	}

	@Override
	public List<VetDTO> findByLastName(String lastName) {
		return repository.findByLastName(lastName).stream().map(this::toDto).collect(Collectors.toList());
	}

	private VetDTO toDto(Vet e) {
		return VetDTO.builder()
				.id(e.getId())
				.firstName(e.getFirstName())
				.lastName(e.getLastName())
				.build();
	}

	private void fill(Vet entity, VetDTO dto) {
		entity.setFirstName(dto.getFirstName());
		entity.setLastName(dto.getLastName());
	}
}
