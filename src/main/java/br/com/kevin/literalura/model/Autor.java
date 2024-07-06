package br.com.kevin.literalura.model;

import br.com.kevin.literalura.dto.AutorDTO;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "autores")
public class Autor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String nome;
    private Integer nascimento;
    private Integer falecimento;
    @OneToMany(mappedBy = "autor", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Livro> livros;

    public Autor(){

    }

    public Autor(AutorDTO autorDTO){
        this.nome = autorDTO.nome();
        this.nascimento = autorDTO.nascimento();
        this.falecimento = autorDTO.falecimento();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getNascimento() {
        return nascimento;
    }

    public void setNascimento(Integer nascimento) {
        this.nascimento = nascimento;
    }

    public Integer getFalecimento() {
        return falecimento;
    }

    public void setFalecimento(Integer falecimento) {
        this.falecimento = falecimento;
    }

    public List<Livro> getLivros() {
        return livros;
    }

    public void setLivros(List<Livro> livros) {
        livros.forEach(l -> l.setAutor(this));
        this.livros = livros;
    }

    @Override
    public String toString() {
        return "\n------------ Autor ------------\n" +
                "id=" + id +
                "nome='" + nome + '\n' +
                "Ano de nascimento =" + nascimento +
                "Ano de falecimento =" + falecimento +
                "\n-----------------------------------\n";
    }
}