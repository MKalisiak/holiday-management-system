package pl.kalisiak.leave.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pl.kalisiak.leave.DTO.EducationDTO;
import pl.kalisiak.leave.model.Education;
import pl.kalisiak.leave.repository.EducationRepository;

@Service
public class EducationService extends GenericService<Education> {

    @Autowired
    private EducationRepository repository;

    public static Education dtoToModel(EducationDTO educationDTO) {
        if (educationDTO == null)
            return null;
        Education education = new Education();
        education.setId(educationDTO.getId());
        education.setLevel(educationDTO.getLevel());
        education.setSchoolName(educationDTO.getSchoolName());
        education.setStartDate(educationDTO.getStartDate());
        education.setFinishDate(educationDTO.getFinishDate());
        return education;
    }

    public static EducationDTO modelToDTO(Education education) {
        if (education == null)
            return null;
        EducationDTO educationDTO = new EducationDTO();
        educationDTO.setId(education.getId());
        educationDTO.setLevel(education.getLevel());
        educationDTO.setSchoolName(education.getSchoolName());
        educationDTO.setStartDate(education.getStartDate());
        educationDTO.setFinishDate(education.getFinishDate());
        return educationDTO;
    }

}