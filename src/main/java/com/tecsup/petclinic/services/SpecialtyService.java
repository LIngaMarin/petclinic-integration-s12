package com.tecsup.petclinic.services;

import com.tecsup.petclinic.dtos.SpecialtyDTO;
import com.tecsup.petclinic.exceptions.SpecialtyNotFoundException;

import java.util.List;

public interface SpecialtyService {

	List<SpecialtyDTO> findAll();

	SpecialtyDTO findById(Integer id) throws SpecialtyNotFoundException;

	SpecialtyDTO create(SpecialtyDTO specialtyDTO);

	SpecialtyDTO update(SpecialtyDTO specialtyDTO) throws SpecialtyNotFoundException;

	void delete(Integer id) throws SpecialtyNotFoundException;

	List<SpecialtyDTO> findByName(String name);
}
