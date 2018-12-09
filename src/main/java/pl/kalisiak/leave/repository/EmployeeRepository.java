package pl.kalisiak.leave.repository;

import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Repository;

import pl.kalisiak.leave.model.Employee;

@Repository
public interface EmployeeRepository extends GenericRepository<Employee>{
    public Optional<Employee> findByEmail(String email);

    public Set<Employee> findAllBySupervisor(Employee supervisor);
}
