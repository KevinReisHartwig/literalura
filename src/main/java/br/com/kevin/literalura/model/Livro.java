package br.com.kevin.literalura.model;

import br.com.kevin.literalura.dto.LivroDTO;
import jakarta.persistence.*;

import java.util.stream.Collectors;

@Entity
@Table(name = "livros")
public class Livro {
    @Id
    private Long id;
    private String titulo;
    @Enumerated(EnumType.STRING)
    private Idioma idioma;
    private String copyright;
    private Integer downloads;
    @ManyToOne
    private Autor autor;

    public Livro() {
    }

    public Livro(LivroDTO livro){
        this.id = livro.id();
        this.titulo = livro.titulo();
        this.idioma = Idioma.fromString(livro.idiomas().stream()
                .limit(1).collect(Collectors.joining()));
        this.copyright = livro.copyright();
        this.downloads = livro.downloads();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Idioma getIdioma() {
        return idioma;
    }

    public void setIdioma(Idioma idioma) {
        this.idioma = idioma;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public Integer getDownloads() {
        return downloads;
    }

    public void setdownloads(Integer downloads) {
        this.downloads = downloads;
    }

    public Autor getAutor() {
        return autor;
    }

    public void setAutor(Autor autor) {
        this.autor = autor;
    }

    @Override
    public String toString() {
        return "\n------------ Livro ------------\n" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", idioma=" + idioma +
                ", copyright='" + copyright + '\'' +
                ", downloads =" + downloads +
                ", autor=" + autor +
                "\n-----------------------------------\n";
    }
}