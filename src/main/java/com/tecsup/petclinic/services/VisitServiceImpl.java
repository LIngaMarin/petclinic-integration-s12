package com.tecsup.petclinic.services;

import com.tecsup.petclinic.dtos.VisitDTO;
import com.tecsup.petclinic.entities.Pet;
import com.tecsup.petclinic.entities.Visit;
import com.tecsup.petclinic.exceptions.PetNotFoundException;
import com.tecsup.petclinic.exceptions.VisitNotFoundException;
import com.tecsup.petclinic.repositories.PetRepository;
import com.tecsup.petclinic.repositories.VisitRepository;
import java.time.LocalDate;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VisitServiceImpl implements VisitService {

	private final VisitRepository repository;
	private final PetRepository petRepository;

	public VisitServiceImpl(VisitRepository repository, PetRepository petRepository) {
		this.repository = repository;
		this.petRepository = petRepository;
	}

	@Override
	public List<VisitDTO> findAll() {
		return repository.findAll(Sort.by("id")).stream().map(this::toDto).collect(Collectors.toList());
	}

	@Override
	public VisitDTO findById(Long id) throws VisitNotFoundException {
		return repository.findById(id)
				.map(this::toDto)
				.orElseThrow(() -> new VisitNotFoundException("Record not found...!"));
	}

	@Override
	public VisitDTO create(VisitDTO dto) throws PetNotFoundException {
		Visit entity = new Visit();
		fill(entity, dto);
		return toDto(repository.save(entity));
	}

	@Override
	public VisitDTO update(VisitDTO dto) throws VisitNotFoundException, PetNotFoundException {
		Visit entity = repository.findById(dto.getId())
				.orElseThrow(() -> new VisitNotFoundException("Record not found...!"));
		fill(entity, dto);
		return toDto(repository.save(entity));
	}

	@Override
	public void delete(Long id) throws VisitNotFoundException {
		Visit entity = repository.findById(id)
				.orElseThrow(() -> new VisitNotFoundException("Record not found...!"));
		repository.delete(entity);
	}

	@Override
	public List<VisitDTO> findByPetId(Integer petId) {
		return repository.findByPetId(petId).stream().map(this::toDto).collect(Collectors.toList());
	}

	private VisitDTO toDto(Visit v) {
		return VisitDTO.builder()
				.id(v.getId())
				.visitDate(v.getVisitDate() == null ? null : v.getVisitDate().toString())
				.description(v.getDescription())
				.petId(v.getPet() == null ? null : v.getPet().getId())
				.build();
	}

	private void fill(Visit v, VisitDTO dto) throws PetNotFoundException {
		v.setVisitDate(dto.getVisitDate() == null || dto.getVisitDate().isEmpty()
				? null : LocalDate.parse(dto.getVisitDate()));
		v.setDescription(dto.getDescription());
		Pet pet = petRepository.findById(dto.getPetId())
				.orElseThrow(() -> new PetNotFoundException("Pet not found...!"));
		v.setPet(pet);
	}
}
