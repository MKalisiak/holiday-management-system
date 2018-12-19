package pl.kalisiak.leave.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import pl.kalisiak.leave.model.Employee;
import pl.kalisiak.leave.model.Leave;

@Repository
public interface LeaveRepository extends GenericRepository<Leave>{
    public List<Leave> findAllByEmployee(Employee employee);
}
