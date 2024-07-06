package br.com.kevin.literalura.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AutorDTO(
        @JsonAlias("name") String nome,
        @JsonAlias("birth_year") Integer nascimento,
        @JsonAlias("death_year") Integer falecimento
) {
}