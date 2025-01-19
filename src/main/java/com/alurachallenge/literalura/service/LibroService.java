package com.alurachallenge.literalura.service;

import com.alurachallenge.literalura.model.*;
import com.alurachallenge.literalura.repository.AutorRepository;
import com.alurachallenge.literalura.repository.LibroRepository;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LibroService {

    private LibroRepository libroRepository;
    private AutorRepository autorRepository;

    public LibroService(LibroRepository libroRepository, AutorRepository autorRepository) {
        this.libroRepository = libroRepository;
        this.autorRepository = autorRepository;
    }

    public void guardarLibro(DatosLibro datosLibro) {
        Optional<Autor> autorExistente = autorRepository.findByNombre(datosLibro.autor().get(0).nombre());

        // Verifica si el libro ya existe en la base de datos
        Optional<Libro> libroExistente = libroRepository.findByTituloContainingIgnoreCase(datosLibro.titulo());
        if (libroExistente.isPresent()) {
            System.out.println("El libro ya ha sido registrado");
            return;
        }

        // Crea el nuevo libro
        Libro libro = new Libro();
        libro.setTitulo(datosLibro.titulo());
        libro.setIdioma(Idioma.fromString(datosLibro.idioma().get(0)));
        libro.setNumeroDeDescargas(datosLibro.numeroDeDescargas());

        // Asocia el autor existente o crea uno nuevo
        if (autorExistente.isPresent()) {
            libro.setAutor(autorExistente.get());
        } else {
            Autor nuevoAutor = new Autor(datosLibro.autor().get(0));
            nuevoAutor = autorRepository.save(nuevoAutor);
            libro.setAutor(nuevoAutor);
        }
        libroRepository.save(libro);
    }

    public List<Libro> listarLibros() {
        return libroRepository.findAll();
    }

    public List<DatosAutor> listarAutores() {
        List<Autor> autores = autorRepository.findAll();
        if (autores == null || autores.isEmpty()){
            throw new IllegalArgumentException("No se han encontrado registros");
        }
        return autores.stream()
                .map(a -> new DatosAutor(a.getId(),a.getNombre(),a.getFechaDeNacimiento(),a.getFechaDeFallecimiento()))
                .collect(Collectors.toList());
    }

    public List<DatosLibro> listarLibrosPorAutor(Long autorId) {
        List<Libro> libros = libroRepository.findLibrosByAutorId(autorId);
        if (libros.isEmpty()) {
            throw new IllegalArgumentException("No se encontraron libros para el autor con ID: " + autorId);
        }
        return libros.stream()
                .map(libro -> new DatosLibro(
                        libro.getTitulo(),
                        List.of(new DatosAutor(
                                libro.getAutor().getId(),
                                libro.getAutor().getNombre(),
                                libro.getAutor().getFechaDeNacimiento(),
                                libro.getAutor().getFechaDeFallecimiento()
                        )),
                        List.of(libro.getIdioma().toString()),
                        libro.getNumeroDeDescargas()
                ))
                .collect(Collectors.toList());
    }

    public List<DatosAutor> listarAutoresVivosPorAnio(int anio) {
        Year year = Year.of(anio);

        return autorRepository.findAll().stream()
                .filter(autor -> {
                    try {
                        // Verifica que la fecha de nacimiento no sea nula
                        if (autor.getFechaDeNacimiento() == null) {
                            return false;
                        }
                        // Convierte las cadenas de fecha en Year
                        Year nacimiento = Year.parse(autor.getFechaDeNacimiento());
                        Year fallecimiento = autor.getFechaDeFallecimiento() != null
                                ? Year.parse(autor.getFechaDeFallecimiento())
                                : null;

                        // Filtra si el autor estaba vivo durante el año dado
                        return nacimiento.isBefore(year) &&
                                (fallecimiento == null || fallecimiento.isAfter(year));
                    } catch (Exception e) {
                        // Manejo de excepciones si el formato de fecha no es válido
                        System.err.println("Error procesando fechas para el autor: " + autor.getNombre());
                        return false;
                    }
                })
                .map(autor -> new DatosAutor(
                        autor.getId(),
                        autor.getNombre(),
                        autor.getFechaDeNacimiento(),
                        autor.getFechaDeFallecimiento()))
                .collect(Collectors.toList());
    }

    public List<Libro> listarLibrosPorIdioma(String idioma) {
        try {
            return libroRepository.findByIdioma(Idioma.fromString(idioma));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("El idioma ingresado no es válido.");
        }
    }

    public List<Libro> obtenerTopLibrosMasDescargados() {
        return libroRepository.findTop10();
    }

    public String calcularEstadisticas() {
        List<Libro> libros = listarLibros();

        if (libros.isEmpty()) {
            return "No hay libros registrados.";
        }

        DoubleSummaryStatistics estadisticas = libros.stream()
                .filter(l -> l.getNumeroDeDescargas() > 0)
                .collect(Collectors.summarizingDouble(Libro::getNumeroDeDescargas));

        return String.format("""
                Máximo de descargas: %.2f
                Mínimo de descargas: %.2f
                Cantidad de libros registrados: %d
                Total de descargas de todos los libros registrados: %.2f
                Promedio de descargas: %.2f
                """, estadisticas.getMax(), estadisticas.getMin(), estadisticas.getCount(),
                estadisticas.getSum(), estadisticas.getAverage());
    }
}

