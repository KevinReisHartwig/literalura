package br.com.kevin.literalura.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;


@Service
public class ConverteDados implements IConverteDados {
    private ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public <T> T obterDados(String json, Class<T> classe) {
        try {
            return objectMapper.readValue(json, classe);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}