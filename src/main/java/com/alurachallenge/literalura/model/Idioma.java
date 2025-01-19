package com.alurachallenge.literalura.model;

public enum Idioma {
    ESPANIOL("es", "Español"),
    INGLES("en", "Inglés"),
    PORTUGUES("pt","Portugués"),
    FRANCES("fr","Francés"),
    ITALIANO("it", "Italiano"),
    ALEMAN("de", "Alemán");

    private String idiomaAPI;
    private String idiomaEspanol;

    Idioma (String idiomaAPI, String idiomaEspanol){
        this.idiomaAPI = idiomaAPI;
        this.idiomaEspanol = idiomaEspanol;
    }

    public static Idioma fromString(String text){
        for (Idioma idioma : Idioma.values()){
            if (idioma.idiomaAPI.equalsIgnoreCase(text)){
                return idioma;
            }
        }
        throw new IllegalArgumentException("Ningun idioma encontrado: " + text);
    }

    public static Idioma fromEspanol(String text){
        for (Idioma idioma : Idioma.values()){
            if (idioma.idiomaEspanol.equalsIgnoreCase(text)){
                return idioma;
            }
        }
        throw new IllegalArgumentException("Ningun idioma encontrado: " + text);
    }
}
