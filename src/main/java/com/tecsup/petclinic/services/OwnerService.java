package com.tecsup.petclinic.services;

import com.tecsup.petclinic.dtos.OwnerDTO;
import com.tecsup.petclinic.exceptions.OwnerNotFoundException;

import java.util.List;

public interface OwnerService {

	List<OwnerDTO> findAll();

	OwnerDTO findById(Long id) throws OwnerNotFoundException;

	OwnerDTO create(OwnerDTO ownerDTO);

	OwnerDTO update(OwnerDTO ownerDTO) throws OwnerNotFoundException;

	void delete(Long id) throws OwnerNotFoundException;

	List<OwnerDTO> findByLastName(String lastName);
}
