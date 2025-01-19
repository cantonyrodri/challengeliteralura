package com.alurachallenge.literalura.repository;

import com.alurachallenge.literalura.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AutorRepository extends JpaRepository<Autor,Long> {

    //@Query("SELECT a FROM Autor a WHERE nombre ILIKE %:nombre%")
    Optional<Autor> findByNombre(String nombre);

    @Query("SELECT a FROM Autor a WHERE fechaDeNacimiento < :anio AND fechaDeFallecimiento > :anio")
    List<Autor> buscarAutoresPorAnio(int anio);

    //boolean existsByNombre(String nombre);

}
