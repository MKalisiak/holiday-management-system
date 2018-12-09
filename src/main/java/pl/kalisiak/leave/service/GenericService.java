package pl.kalisiak.leave.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;

import pl.kalisiak.leave.model.GenericModel;
import pl.kalisiak.leave.repository.GenericRepository;

public class GenericService<T extends GenericModel> {

    @Autowired
    protected GenericRepository<T> repository;

    @Transactional
    public T save(T entity) {
        return repository.save(entity);
    }

    @Transactional
    public T findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Transactional
    public List<T> findAll() {
        return repository.findAll();
    }

    @Transactional
    public void delete(T entity) {
        repository.delete(entity);;
    }
}