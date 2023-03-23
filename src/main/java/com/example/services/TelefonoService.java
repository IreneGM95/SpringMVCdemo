package com.example.services;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.entities.Estudiante;
import com.example.entities.Telefono;

public interface TelefonoService {
    public List<Telefono> findAll();

    public Telefono findById(int idTelefono);

    public void save(Telefono telefono);

    public void deleteById(int idTelefono);

    public void deleteByEstudiante(Estudiante estudiante);

    public List<Telefono> findByEstudiante(Estudiante estudiante);

}
