package com.tecsup.petclinic.services;

import com.tecsup.petclinic.dtos.OwnerDTO;
import com.tecsup.petclinic.entities.Owner;
import com.tecsup.petclinic.exceptions.OwnerNotFoundException;
import com.tecsup.petclinic.repositories.OwnerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OwnerServiceImpl implements OwnerService {

	private final OwnerRepository repository;

	public OwnerServiceImpl(OwnerRepository repository) {
		this.repository = repository;
	}

	@Override
	public List<OwnerDTO> findAll() {
		return repository.findAll().stream().map(this::toDto).collect(Collectors.toList());
	}

	@Override
	public OwnerDTO findById(Long id) throws OwnerNotFoundException {
		return repository.findById(id)
				.map(this::toDto)
				.orElseThrow(() -> new OwnerNotFoundException("Record not found...!"));
	}

	@Override
	public OwnerDTO create(OwnerDTO dto)  {
		Owner entity = new Owner();
		fill(entity, dto);
		return toDto(repository.save(entity));
	}

	@Override
	public OwnerDTO update(OwnerDTO dto) throws OwnerNotFoundException {
		Owner entity = repository.findById(dto.getId())
				.orElseThrow(() -> new OwnerNotFoundException("Record not found...!"));
		fill(entity, dto);
		return toDto(repository.save(entity));
	}

	@Override
	public void delete(Long id) throws OwnerNotFoundException {
		Owner entity = repository.findById(id)
				.orElseThrow(() -> new OwnerNotFoundException("Record not found...!"));
		repository.delete(entity);
	}

	@Override
	public List<OwnerDTO> findByLastName(String lastName) {
		return repository.findByLastName(lastName).stream().map(this::toDto).collect(Collectors.toList());
	}

	private OwnerDTO toDto(Owner e) {
		return OwnerDTO.builder()
				.id(e.getId())
				.firstName(e.getFirstName())
				.lastName(e.getLastName())
				.address(e.getAddress())
				.city(e.getCity())
				.telephone(e.getTelephone())
				.build();
	}

	private void fill(Owner entity, OwnerDTO dto) {
		entity.setFirstName(dto.getFirstName());
		entity.setLastName(dto.getLastName());
		entity.setAddress(dto.getAddress());
		entity.setCity(dto.getCity());
		entity.setTelephone(dto.getTelephone());
	}
}
