package com.alurachallenge.literalura.principal;

import com.alurachallenge.literalura.model.Autor;
import com.alurachallenge.literalura.model.DatosAutor;
import com.alurachallenge.literalura.model.DatosLibro;
import com.alurachallenge.literalura.model.Libro;
import com.alurachallenge.literalura.repository.AutorRepository;
import com.alurachallenge.literalura.repository.LibroRepository;
import com.alurachallenge.literalura.service.ConsumoAPI;
import com.alurachallenge.literalura.service.ConvierteDatos;
import com.alurachallenge.literalura.service.LibroService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {

    private final String URL_BASE = "https://gutendex.com/books/";

    private Scanner teclado = new Scanner(System.in);
    private ConsumoAPI consumoAPI;
    private ConvierteDatos convierteDatos;
    private LibroService libroService;

    //private List<Libro> libros;
    //private List<Autor> autores;

    public Principal(LibroRepository libroRepository, AutorRepository autorRepository){
        this.libroService = new LibroService(libroRepository,autorRepository);
        this.consumoAPI = new ConsumoAPI();
        this.convierteDatos = new ConvierteDatos();
    }

    public void muestraElMenu() {
        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    ---------------MENÚ---------------
                    1 - Buscar libro por título
                    2 - Listar libros registrados
                    3 - Listar autores registrados
                    4 - Listar autores vivos en un determinado año
                    5 - Listar libros por idioma
                    6 - Salir
                    ------------------------------------
                    """;

            System.out.println(menu);
            opcion = teclado.nextInt();
            teclado.nextLine();

            switch (opcion) {
                case 1:
                    buscarLibroPorTitulo();
                    break;
                case 2:
                    listarLibrosRegistrados();
                    break;
                case 3:
                    listarAutoresRegistrados();
                    break;
                case 4:
                    listarAutoresVivosEnDeterminadoAno();
                    break;
                case 5:
                    listarLibrosPorIdioma();
                    break;
                case 6:
                    System.out.println("Cerrando la aplicación...");
                    break;
                default:
                    System.out.println("Opción inválida");
            }
        }
    }

    /*
    private DatosLibro getDatosLibro() {
        System.out.println("Escribe el nombre del libro que deseas buscar");
        var nombreLibro = teclado.nextLine();

        try {
            var json = consumoAPI.obtenerDatos(URL_BASE + "?search=" + nombreLibro.replace(" ","+"));
            System.out.println(json);
            DatosLibro datosLibro = convierteDatos.obtenerDatos(json, DatosLibro.class);
            return datosLibro;
        } catch (Exception e) {
            System.out.println("Error: "+ e.getMessage());
        }
        return null;
    }
     */

    private void buscarLibroPorTitulo() {
        System.out.println("Escribe el nombre del libro que deseas buscar:");
        String titulo = teclado.nextLine();
        try {
            var json = consumoAPI.obtenerDatos(URL_BASE + "?search=" + titulo.replace(" ", "+"));
            DatosLibro datosLibro = convierteDatos.obtenerDatos(json, DatosLibro.class);

            if (datosLibro != null) {
                libroService.guardarLibro(datosLibro);
                System.out.println("Libro guardado exitosamente.");
            } else {
                System.out.println("No se encontró información del libro.");
            }
        } catch (Exception e) {
            System.out.println("Error al buscar el libro: " + e.getMessage());
        }
    }

    private void listarLibrosRegistrados() {
        List<Libro> libros = libroService.listarLibros();
        if (libros.isEmpty()) {
            System.out.println("No hay libros registrados.");
        } else {
            System.out.println("--- Lista de libros registrados ---");
            libros.forEach(System.out::println);
        }
    }

    private void listarAutoresRegistrados() {
        List<DatosAutor> autores = libroService.listarAutores();
        if (autores.isEmpty()) {
            System.out.println("No hay autores registrados.");
        } else {
            System.out.println("--- Lista de autores registrados ---");
            autores.forEach(autor -> {
                System.out.println(autor.toString());
                // Obtener los libros del autor
                List<DatosLibro> librosAutor = libroService.listarLibrosPorAutor(autor.id());
                if (librosAutor.isEmpty()) {
                    System.out.println("Libros: No se encontraron libros para este autor.");
                } else {
                    System.out.println("Libros: [" + librosAutor.stream()
                            .map(DatosLibro::titulo) // Extraer solo los títulos
                            .collect(Collectors.joining(", ")) + "]");
                }
            });
        }
    }

    private void listarAutoresVivosEnDeterminadoAno() {
        System.out.println("Ingrese el año:");
        int anio = teclado.nextInt();
        List<DatosAutor> autoresVivos = libroService.listarAutoresVivosPorAnio(anio);
        if (autoresVivos.isEmpty()) {
            System.out.println("No se encontraron autores vivos en el año " + anio);
        } else {
            System.out.println("--- Autores vivos en " + anio + " ---");
            autoresVivos.forEach(System.out::println);
        }
    }

    private void listarLibrosPorIdioma() {
        System.out.println("Ingrese el idioma (es, en, pt, fr, it, de):");
        String idioma = teclado.nextLine();
        try {
            List<Libro> librosPorIdioma = libroService.listarLibrosPorIdioma(idioma);
            if (librosPorIdioma.isEmpty()) {
                System.out.println("No hay libros registrados en ese idioma.");
            } else {
                System.out.println("--- Libros en idioma " + idioma + " ---");
                librosPorIdioma.forEach(System.out::println);
            }
        } catch (Exception e) {
            System.out.println("Error al listar libros: " + e.getMessage());
        }
    }




}

