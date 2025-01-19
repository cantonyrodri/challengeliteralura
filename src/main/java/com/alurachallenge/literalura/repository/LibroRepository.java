package com.alurachallenge.literalura.repository;

import com.alurachallenge.literalura.model.Idioma;
import com.alurachallenge.literalura.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LibroRepository extends JpaRepository<Libro,Long> {

    Optional<Libro> findByTituloContainingIgnoreCase(String titulo);

    List<Libro> findByIdioma(Idioma idioma);

    @Query("SELECT l FROM Libro l ORDER BY l.numeroDeDescargas DESC LIMIT 10")
    List<Libro> findTop10();

    @Query("SELECT l FROM Libro l WHERE l.autor.id = :autorId")
    List<Libro> findLibrosByAutorId(@Param("autorId") Long autorId);
}
