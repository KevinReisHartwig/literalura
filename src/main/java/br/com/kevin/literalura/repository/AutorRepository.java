package br.com.kevin.literalura.repository;

import br.com.kevin.literalura.model.Autor;
import br.com.kevin.literalura.model.Idioma;
import br.com.kevin.literalura.model.Livro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AutorRepository extends JpaRepository<Autor, Long> {

    @Query("SELECT a FROM Livro l JOIN l.autor a WHERE a.nome LIKE %:nome%")
    Optional<Autor> buscarAutorPorNome(@Param("nome") String nome);

    @Query("SELECT l FROM Livro l JOIN l.autor a WHERE l.titulo LIKE %:nome%")
    Optional<Livro> buscarLivroPorNome(@Param("nome") String nome);

    @Query("SELECT a FROM Autor a WHERE a.falecimento > :data")
    List<Autor> buscarAutoresVivos(@Param("data") Integer data);

    @Query("SELECT l FROM Autor a JOIN a.livros l WHERE l.idioma = :idioma")
    List<Livro> buscarLivrosPorIdioma(@Param("idioma") Idioma idioma);

    @Query("SELECT l FROM Autor a JOIN a.livros l ORDER BY l.downloads DESC LIMIT 10")
    List<Livro> top10Livros();

    @Query("SELECT a FROM Autor a WHERE a.nascimento = :data")
    List<Autor> listarAutoresPorNascimento(@Param("data") Integer data);

    @Query("SELECT a FROM Autor a WHERE a.falecimento = :data")
    List<Autor> listarAutoresPorFalecimento(@Param("data") Integer data);

    @Query("SELECT l FROM Autor a JOIN a.livros l")
    List<Livro> buscarTodosLivros();

}