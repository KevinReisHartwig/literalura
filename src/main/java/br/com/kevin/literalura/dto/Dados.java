package br.com.kevin.literalura.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Dados(
        @JsonAlias("count") Integer total,
        @JsonAlias("results") List<LivroDTO> livros) {
}