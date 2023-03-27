package com.example.controllers;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.example.entities.Estudiante;
import com.example.entities.Facultad;
import com.example.entities.Telefono;
import com.example.services.EstudianteService;
import com.example.services.FacultadService;
import com.example.services.TelefonoService;

@Controller
@RequestMapping("/")
public class MainController {
    /**
     * el controlador delega la peticion en un metodo que tiene en cuenta el
     * verbo(get, put, delate, post...) del protocolo http utilizado para realizar
     * la peticion
     */

    /**
     * Logger registra todo lo que pasa en esta clase, MainController, para saber
     * todo lo que pasa y poder "hacer un analisis postmortem" si algo va mal
     */
    private static final Logger LOG = Logger.getLogger("MainController");

    @Autowired
    private FacultadService facultadService;

    @Autowired
    private EstudianteService estudianteService;

    @Autowired
    private TelefonoService telefonoService;

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

    // /** Metodo que muestra el formulario de alta de estudiante */
    // @GetMapping("/frmAltaEstudiante")
    // public String formularioAltaEstudiante(Model model) {

    // // Metodo que muestre una lista de facultades
    // List<Facultad> facultades = facultadService.findAll(); // esto se le manda al
    // modelo abajo

    // Estudiante estudiante = new Estudiante();

    // // modelo creado antes del formulario
    // model.addAttribute("estudiante", estudiante);
    // model.addAttribute("facultades", facultades);

    // //¿Igual para telefonos?
    // List<Telefono> Telefonoes = telefonoService.findAll(); // esto se le manda al
    // modelo abajo
    // model.addAttribute("telefonoes", telefonos);

    // return "views/formularioAltaEstudiante";

    // }

    /**
     * Metodo que recibe los datos procedentes de los controladores del formulario y
     * se muestre el último creado
     */
    @PostMapping("/altaModificacionEstudiante")
    public String altaEstudiante(@ModelAttribute Estudiante estudiante,
            @RequestParam(name = "numerosTelefonos") String telefonosRecibidos,
            @RequestParam(name = "foto") MultipartFile imagen) {
        // El último @RequestParam Recoger el parametro que reconoce el parametro de
        // foto

        // gracias al log nos da un mensaje de comprobación antes de procesar la
        // información. Es una buena práctica de programación hacer esta comprobación
        // previa
        LOG.info("Telefonos recibidos: " + telefonosRecibidos);

        // Preguntar si viene una imagen:
        if (!imagen.isEmpty()) {
            try {
                // ruta relativa de donde voy a almacenar el archivo de imagen
                Path rutaRelativa = Paths.get("src/main/resources/static/images/");

                // ruta absoluta, puede ser de tipo Path o de tipo String:
                String rutaAbsoluta = rutaRelativa.toFile().getAbsolutePath();
                // Array de bytes:
                byte[] imagenEnBytes = imagen.getBytes();

                // guardar imagen en la ruta absoluta, en el file system:
                Files.write(Paths.get(rutaAbsoluta), imagenEnBytes);
                // Asociar la imagen con el objeto estudiante que se va a guardar
                estudiante.setFoto(imagen.getOriginalFilename());

            } catch (Exception e) {
                // TODO: handle exception
            }
        }

        // Se guarda el estudiante para despues poder acceder a él a la hora de
        // meterle los telefonos
        estudianteService.save(estudiante);

        List<String> listadoNumerosTelefono = null; // la declaramos fuera,para poder utilizarla en varios sitios. Y le
                                                    // asignamos null, porque dentro de un método siempre hay que
                                                    // inicializarla (asignarle valor) para que funcione

        // No queremos guardar telefonos si no los hay, por eso ponemos el if
        if (telefonosRecibidos != null) {
            String[] arrayTelefonos = telefonosRecibidos.split(";"); // separa el array cada vez que encuentra un ;,
                                                                     // podría pedirle que separase cada vez que
                                                                     // encuentre un espacio
            // Convertimos este array en una colección para luego pasarlo a flujo y trabajar
            // con ese flujo:
            listadoNumerosTelefono = Arrays.asList(arrayTelefonos);
        }

        // si sí hay telefonos, el flujo lo recorremos e introducimos
        if (listadoNumerosTelefono != null) {
            telefonoService.deleteByEstudiante(estudiante);
            listadoNumerosTelefono.stream().forEach(n -> {
                Telefono telefonoObject = Telefono.builder()
                        .numero(n)
                        .estudiante(estudiante)
                        .build();

                telefonoService.save(telefonoObject);
            });
        }

        return "redirect:/listar";
    }

    /** Método para actualizar un estudiante dado su id */
    @GetMapping("/frmActualizar/{id}")
    // como se hace a través de un link, es un get y es visible para todos, solo es
    // post si se lo especificamos nosotros a la hora de hacer un formulario
    // recogerlo es por get y mostrarlo por post?
    public String fmrActualizarEstudiante(@PathVariable(name = "id") int idEstudiante, Model model) {

        Estudiante estudiante = estudianteService.findById(idEstudiante);

        List<Telefono> todosTelefonos = telefonoService.findAll();

        List<Telefono> telefonosEstudiante = todosTelefonos
                .stream()
                .filter(telefono -> telefono.getEstudiante().getId() == idEstudiante)
                .collect(Collectors.toList());
        // sería más eficiente usar una consulta de mysql, pero en este caso no lo vamos
        // a hacer pporque vamos mal de tiempo y profundizaremos en mysql mas adelante

        String numerosDeTelefono = telefonosEstudiante.stream().map(t -> t.getNumero())
                .collect(Collectors.joining(";"));

        List<Facultad> facultades = facultadService.findAll();

        model.addAttribute("estudiante", estudiante);
        model.addAttribute("telefonos", numerosDeTelefono);

        // Para que en el formulario nos deje modificar/visualizar la facultad de un
        // estudiante ya creado:
        model.addAttribute("facultades", facultades);

        return "views/formularioAltaEstudiante";
    }

    @GetMapping("/borrar/{id}")
    public String borrarEstudiante(@PathVariable(name = "id") int idEstudiante) {
        estudianteService.delete(estudianteService.findById(idEstudiante));
        return "redirect:/listar";
    }

    /**
     * Métodoque encuentre los telefonso de cada estudiante: (hecho por nosotras):
     */
    @GetMapping("/detalles/{id}")
    public String detallesEstudiante(@PathVariable(name = "id") int id, Model model) {

        Estudiante estudiante = estudianteService.findById(id);
        List<Telefono> telefonos = telefonoService.findByEstudiante(estudiante);
        List<String> numerosTelefono = telefonos.stream().map(t -> t.getNumero()).toList();

        model.addAttribute("telefonos", numerosTelefono);
        model.addAttribute("estudiante", estudiante);
        return "views/detalleEstudiante";
    }

}
