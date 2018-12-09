package pl.kalisiak.leave.model;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import lombok.Data;

@Data
@MappedSuperclass
public abstract class GenericModel {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
}
