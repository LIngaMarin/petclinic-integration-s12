package com.tecsup.petclinic.services;

import com.tecsup.petclinic.dtos.VisitDTO;
import com.tecsup.petclinic.exceptions.VisitNotFoundException;
import com.tecsup.petclinic.exceptions.PetNotFoundException;

import java.util.List;

public interface VisitService {

	List<VisitDTO> findAll();

	VisitDTO findById(Long id) throws VisitNotFoundException;

	VisitDTO create(VisitDTO visitDTO) throws PetNotFoundException;

	VisitDTO update(VisitDTO visitDTO) throws VisitNotFoundException, PetNotFoundException;

	void delete(Long id) throws VisitNotFoundException;

	List<VisitDTO> findByPetId(Integer petId);
}
