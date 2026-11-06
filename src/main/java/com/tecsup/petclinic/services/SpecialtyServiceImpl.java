package com.tecsup.petclinic.services;

import com.tecsup.petclinic.dtos.SpecialtyDTO;
import com.tecsup.petclinic.entities.Specialty;
import com.tecsup.petclinic.exceptions.SpecialtyNotFoundException;
import com.tecsup.petclinic.repositories.SpecialtyRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SpecialtyServiceImpl implements SpecialtyService {

	private final SpecialtyRepository repository;

	public SpecialtyServiceImpl(SpecialtyRepository repository) {
		this.repository = repository;
	}

	@Override
	public List<SpecialtyDTO> findAll() {
		return repository.findAll(Sort.by("id")).stream().map(this::toDto).collect(Collectors.toList());
	}

	@Override
	public SpecialtyDTO findById(Integer id) throws SpecialtyNotFoundException {
		return repository.findById(id)
				.map(this::toDto)
				.orElseThrow(() -> new SpecialtyNotFoundException("Record not found...!"));
	}

	@Override
	public SpecialtyDTO create(SpecialtyDTO dto)  {
		Specialty entity = new Specialty();
		fill(entity, dto);
		return toDto(repository.save(entity));
	}

	@Override
	public SpecialtyDTO update(SpecialtyDTO dto) throws SpecialtyNotFoundException {
		Specialty entity = repository.findById(dto.getId())
				.orElseThrow(() -> new SpecialtyNotFoundException("Record not found...!"));
		fill(entity, dto);
		return toDto(repository.save(entity));
	}

	@Override
	public void delete(Integer id) throws SpecialtyNotFoundException {
		Specialty entity = repository.findById(id)
				.orElseThrow(() -> new SpecialtyNotFoundException("Record not found...!"));
		repository.delete(entity);
	}

	@Override
	public List<SpecialtyDTO> findByName(String name) {
		return repository.findByName(name).stream().map(this::toDto).collect(Collectors.toList());
	}

	private SpecialtyDTO toDto(Specialty e) {
		return SpecialtyDTO.builder()
				.id(e.getId())
				.name(e.getName())
				.build();
	}

	private void fill(Specialty entity, SpecialtyDTO dto) {
		entity.setName(dto.getName());
	}
}
