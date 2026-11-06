package com.tecsup.petclinic.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class OwnerDTO {

	private Long id;
	private String firstName;
	private String lastName;
	private String address;
	private String city;
	private String telephone;
}
