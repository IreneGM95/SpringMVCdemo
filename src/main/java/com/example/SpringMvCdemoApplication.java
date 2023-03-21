package com.example;

import java.time.LocalDate;
import java.time.Month;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.entities.Estudiante;
import com.example.entities.Facultad;
import com.example.entities.Telefono;
import com.example.entities.Estudiante.Genero;
import com.example.services.EstudianteService;
import com.example.services.FacultadService;
import com.example.services.TelefonoService;

@SpringBootApplication
public class SpringMvCdemoApplication implements CommandLineRunner {

	@Autowired
	private FacultadService facultadService;

	@Autowired
	private EstudianteService estudianteService;

	@Autowired
	private TelefonoService telefonoService;

	public static void main(String[] args) {
		SpringApplication.run(SpringMvCdemoApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		/**
		 * Método para agregar registros de muestra para Facultad (crear y añadir
		 * facultades),
		 * Estudiante (crear y añadir estudiantes) y Telefono (crear y añadir
		 * telefonos):
		 */

		facultadService.save(Facultad.builder().nombre("Informatica").build());
		facultadService.save(Facultad.builder().nombre("Biologia").build());

		estudianteService.save(Estudiante.builder()
				.id(1) // hay que meterle el id o no funciona
				.nombre("Elisabet")
				.primerApellido("Agulló")
				.segundoApellido("García")
				.fechaAlta(LocalDate.of(2008, Month.APRIL, 2))
				.fechaNacimiento(LocalDate.of(2008, Month.APRIL, 2))
				.genero(Genero.MUJER)
				.beca(98765432.00)
				.facultad(facultadService.findById(1))
				.build());

		telefonoService.save(Telefono.builder()
				.id(1)
				.numero("8633456")
				.estudiante(estudianteService.findById(1))
				.build());

		telefonoService.save(Telefono.builder()
				.id(2)
				.numero("8")
				.estudiante(estudianteService.findById(1))
				.build());
	}
}
