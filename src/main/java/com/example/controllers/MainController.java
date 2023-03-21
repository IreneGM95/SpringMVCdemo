package com.example.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.example.entities.Estudiante;
import com.example.entities.Facultad;
import com.example.services.EstudianteService;
import com.example.services.FacultadService;

@Controller
@RequestMapping("/")
public class MainController {
    /**
     * el controlador delega la peticion en un metodo que tiene en cuenta el
     * verbo(get, put, delate, post...) del protocolo http utilizado para realizar
     * la peticion
     */
    @Autowired
    private FacultadService facultadService;

    @Autowired
    private EstudianteService estudianteService;

    /** Este metodo devuelve un listado de estudiantes: */
    @GetMapping("/listar")
    public ModelAndView listar() {

        List<Estudiante> estudiantes = estudianteService.findAll();

        ModelAndView mav = new ModelAndView("views/listarEstudiantes");
        mav.addObject("estudiantes", estudiantes);

        // Ejemplo para mostrar en la web:
        // mav.addObject("saludo", "Hola y hasta mañana");

        return mav;
    }

    /** Metodo que muestra el formulario de alta de estudiante */
    @GetMapping("/frmAltaEstudiante")
    public String formularioAltaEstudiante(Model model) {

        // Metodo que muestre una lista de facultades
        List<Facultad> facultades = facultadService.findAll(); //esto se le manda al modelo abajo

        // modelo creado antes del formulario
        model.addAttribute("estudiante", new Estudiante());
        model.addAttribute("facultades", facultades);

        return "views/formularioAltaEstudiante";

        

    }
}
