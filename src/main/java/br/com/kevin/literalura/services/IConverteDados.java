package br.com.kevin.literalura.services;

public interface IConverteDados {

    <T> T obterDados(String json, Class<T> clase);

}
