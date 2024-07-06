package br.com.kevin.literalura.main;

import br.com.kevin.literalura.dto.Dados;
import br.com.kevin.literalura.dto.LivroDTO;
import br.com.kevin.literalura.model.Autor;
import br.com.kevin.literalura.model.Idioma;
import br.com.kevin.literalura.model.Livro;
import br.com.kevin.literalura.repository.AutorRepository;
import br.com.kevin.literalura.services.ConsumoAPI;
import br.com.kevin.literalura.services.ConverteDados;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    private Scanner teclado = new Scanner(System.in);
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private ConverteDados conversor = new ConverteDados();
    private String URL_BASE = "https://gutendex.com/books/";
    private AutorRepository repository;

    public Principal(AutorRepository repository){
        this.repository = repository;
    }

    public void exibirMenu() {
        var opcao = -1;
        var menu = """
            -----   Bem-vindo(a) à Literalura   -----
            --------------------------------------------
                           MENU PRINCIPAL 
            --------------------------------------------
            1 - Buscar Livros por Título
            2 - Buscar Autor por Nome
            3 - Listar Livros Registrados
            4 - Listar Autores Registrados
            5 - Listar Autores Vivos
            6 - Listar Livros por Idioma
            7 - Listar Autores por Ano
            8 - Top 10 Livros mais Buscados
            9 - Gerar Estatísticas
            ----------------------------------------------
            0 - SAIR DO PROGRAMA  
            ----------------------------------------------
            Escolha uma opção:
            """;

        while (opcao != 0) {
            System.out.println(menu);
            try {
                opcao = Integer.valueOf(teclado.nextLine());
                switch (opcao) {
                    case 1:
                        buscarLivroPorTitulo();
                        break;
                    case 2:
                        buscarAutorPorNome();
                        break;
                    case 3:
                        listarLivrosRegistrados();
                        break;
                    case 4:
                        listarAutoresRegistrados();
                        break;
                    case 5:
                        listarAutoresVivos();
                        break;
                    case 6:
                        listarLivrosPorIdioma();
                        break;
                    case 7:
                        listarAutoresPorAno();
                        break;
                    case 8:
                        top10Livros();
                        break;
                    case 9:
                        gerarEstatisticas();
                        break;
                    case 0:
                        System.out.println("Obrigado por utilizar Literalura");
                        System.out.println("Fechando a aplicação Literalura ...");
                        break;
                    default:
                        System.out.println("Opção inválida!");
                        break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Opção inválida: " + e.getMessage());

            }
        }
    }

    public void buscarLivroPorTitulo() {
        System.out.println("""
            --------------------------------
               BUSCAR LIVROS POR TÍTULO 
            --------------------------------
             """);
        System.out.println("Digite o nome do livro que deseja buscar:");
        var nome = teclado.nextLine();
        var json = consumoAPI.obterDados(URL_BASE + "?search=" + nome.replace(" ", "+").toLowerCase());

        // Check if JSON is empty
        if (json.isEmpty() || !json.contains("\"count\":0,\"next\":null,\"previous\":null,\"results\":[]")) {
            var dados = conversor.obterDados(json, Dados.class);

            // Process valid data
            Optional<LivroDTO> livroBuscado = dados.livros().stream()
                    .findFirst();
            if (livroBuscado.isPresent()) {
                System.out.println(
                        "\n------------- LIVRO --------------" +
                                "\nTítulo: " + livroBuscado.get().titulo() +
                                "\nAutor: " + livroBuscado.get().autores().stream()
                                .map(a -> a.nome()).limit(1).collect(Collectors.joining()) +
                                "\nIdioma: " + livroBuscado.get().idiomas().stream().collect(Collectors.joining()) +
                                "\nNúmero de downloads: " + livroBuscado.get().downloads() +
                                "\n--------------------------------------\n"
                );

                try {
                    List<Livro> livroEncontrado = livroBuscado.stream().map(a -> new Livro(a)).collect(Collectors.toList());
                    Autor autorAPI = livroBuscado.stream().
                            flatMap(l -> l.autores().stream()
                                    .map(a -> new Autor(a)))
                            .collect(Collectors.toList()).stream().findFirst().get();
                    Optional<Autor> autorBD = repository.buscarAutorPorNome(livroBuscado.get().autores().stream()
                            .map(a -> a.nome())
                            .collect(Collectors.joining()));
                    Optional<Livro> livroOptional = repository.buscarLivroPorNome(nome);
                    if (livroOptional.isPresent()) {
                        System.out.println("O livro já está salvo no banco de dados.");
                    } else {
                        Autor autor;
                        if (autorBD.isPresent()) {
                            autor = autorBD.get();
                            System.out.println("O autor já está salvo no banco de dados.");
                        } else {
                            autor = autorAPI;
                            repository.save(autor);
                        }
                        autor.setLivros(livroEncontrado);
                        repository.save(autor);
                    }
                } catch (Exception e) {
                    System.out.println("Aviso! " + e.getMessage());
                }
            } else {
                System.out.println("Livro não encontrado!");
            }
        }
    }

    public void buscarAutorPorNome() {
        System.out.println("""
                    -------------------------------
                       BUSCAR AUTOR POR NOME 
                    -------------------------------
                    """);
        System.out.println("Digite o nome do autor que deseja buscar:");
        var nome = teclado.nextLine();
        Optional<Autor> autor = repository.buscarAutorPorNome(nome);
        if (autor.isPresent()) {
            System.out.println(
                    "\nAutor: " + autor.get().getNome() +
                            "\nData de Nascimento: " + autor.get().getNascimento() +
                            "\nData de Falecimento: " + autor.get().getFalecimento() +
                            "\nLivros: " + autor.get().getLivros().stream()
                            .map(l -> l.getTitulo()).collect(Collectors.toList()) + "\n"
            );
        } else {
            System.out.println("O autor não existe no banco de dados");
        }
    }

    public void listarLivrosRegistrados() {
        System.out.println("""
                    ----------------------------------
                        LISTAR LIVROS REGISTRADOS 
                    ----------------------------------
                     """);
        List<Livro> livros = repository.buscarTodosLivros();
        livros.forEach(l -> System.out.println(
                "-------------- LIVRO -----------------" +
                        "\nTítulo: " + l.getTitulo() +
                        "\nAutor: " + l.getAutor().getNome() +
                        "\nIdioma: " + l.getIdioma().getIdioma() +
                        "\nNúmero de downloads: " + l.getDownloads() +
                        "\n----------------------------------------\n"
        ));
    }

    public void listarAutoresRegistrados() {
        System.out.println("""
                    ----------------------------------
                        LISTAR AUTORES REGISTRADOS 
                    ----------------------------------
                     """);
        List<Autor> autores = repository.findAll();
        System.out.println();
        autores.forEach(l -> System.out.println(
                "Autor: " + l.getNome() +
                        "\nData de Nascimento: " + l.getNascimento() +
                        "\nData de Falecimento: " + l.getFalecimento() +
                        "\nLivros: " + l.getLivros().stream()
                        .map(t -> t.getTitulo()).collect(Collectors.toList()) + "\n"
        ));
    }

    public void listarAutoresVivos() {
        System.out.println("""
                    -----------------------------
                        LISTAR AUTORES VIVOS 
                    -----------------------------
                     """);
        System.out.println("Digite um ano para verificar os autores vivos:");
        try {
            var data = Integer.valueOf(teclado.nextLine());
            List<Autor> autores = repository.buscarAutoresVivos(data);
            if (!autores.isEmpty()) {
                System.out.println();
                autores.forEach(a -> System.out.println(
                        "Autor: " + a.getNome() +
                                "\nData de Nascimento: " + a.getNascimento() +
                                "\nData de Falecimento: " + a.getFalecimento() +
                                "\nLivros: " + a.getLivros().stream()
                                .map(l -> l.getTitulo()).collect(Collectors.toList()) + "\n"
                ));
            } else {
                System.out.println("Não há autores vivos no ano registrado");
            }
        } catch (NumberFormatException e) {
            System.out.println("Digite um ano válido " + e.getMessage());
        }
    }

    public void listarLivrosPorIdioma() {
        System.out.println("""
                --------------------------------
                    LISTAR LIVROS POR IDIOMA 
                --------------------------------
                """);
        var menu = """
                    ---------------------------------------------------
                    Selecione o idioma do livro que deseja encontrar:
                    ---------------------------------------------------
                    1 - Espanhol
                    2 - Francês
                    3 - Inglês
                    4 - Português
                    ----------------------------------------------------
                    """;
        System.out.println(menu);

        try {
            var opcao = Integer.parseInt(teclado.nextLine());

            switch (opcao) {
                case 1:
                    buscarLivrosPorIdioma("es");
                    break;
                case 2:
                    buscarLivrosPorIdioma("fr");
                    break;
                case 3:
                    buscarLivrosPorIdioma("en");
                    break;
                case 4:
                    buscarLivrosPorIdioma("pt");
                    break;
                default:
                    System.out.println("Opção inválida!");
                    break;
            }
        } catch (NumberFormatException e) {
            System.out.println("Opção não é válida: " + e.getMessage());
        }
    }

    private void buscarLivrosPorIdioma(String idioma) {
        try {
            Idioma idiomaEnum = Idioma.valueOf(idioma.toUpperCase());
            List<Livro> livros = repository.buscarLivrosPorIdioma(idiomaEnum);
            if (livros.isEmpty()) {
                System.out.println("não tem livros com esse idioma");
            } else {
                System.out.println();
                livros.forEach(l -> System.out.println(
                        "----------- Livro --------------" +
                                "\nTítulo: " + l.getTitulo() +
                                "\nAutor: " + l.getAutor().getNome() +
                                "\nIdioma: " + l.getIdioma().getIdioma() +
                                "\nNúmero de downloads: " + l.getDownloads() +
                                "\n----------------------------------------\n"
                ));
            }
        } catch (IllegalArgumentException e) {
            System.out.println("coloque um idioma válido.");
        }
    }

    public void listarAutoresPorAno() {
        System.out.println("""
                ------------------------------
                   LISTAR AUTORES POR ANO 
                ------------------------------
                """);
        System.out.println("Digite um ano de nascimento dos autores:");
        try {
            var ano = Integer.valueOf(teclado.nextLine());
            List<Autor> autores = repository.listarAutoresPorNascimento(ano);
            if (!autores.isEmpty()) {
                System.out.println();
                autores.forEach(a -> System.out.println(
                        "Autor: " + a.getNome() +
                                "\nData de Nascimento: " + a.getNascimento() +
                                "\nData de Falecimento: " + a.getFalecimento() +
                                "\nLivros: " + a.getLivros().stream()
                                .map(l -> l.getTitulo()).collect(Collectors.toList()) + "\n"
                ));
            } else {
                System.out.println("Não há autores registrados com o ano informado");
            }
        } catch (NumberFormatException e) {
            System.out.println("Digite um ano válido " + e.getMessage());
        }
    }

    public void top10Livros() {
        System.out.println("""
                -----------------------------
                   TOP 10 LIVROS MAIS BUSCADOS 
                -----------------------------
                """);
        List<Livro> livros = repository.top10Livros();
        if (!livros.isEmpty()) {
            livros.forEach(l -> System.out.println(
                    "\n------------- LIVRO --------------" +
                            "\nTítulo: " + l.getTitulo() +
                            "\nAutor: " + l.getAutor().getNome() +
                            "\nIdioma: " + l.getIdioma().getIdioma() +
                            "\nNúmero de downloads: " + l.getDownloads() +
                            "\n--------------------------------------\n"
            ));
        } else {
            System.out.println("Não foram encontrados livros.");
        }
    }

    public void gerarEstatisticas () {
        System.out.println("""
                    ----------------------------
                       Gerar Estastiticas 
                    ----------------------------
                     """);
        var json = consumoAPI.obterDados(URL_BASE);
        var dados = conversor.obterDados(json, Dados.class);
        IntSummaryStatistics est = dados.livros().stream()
                .filter(l -> l.downloads() > 0)
                .collect(Collectors.summarizingInt(LivroDTO::downloads));
        Integer media = (int) est.getAverage();
        System.out.println("\n--------- Estastiticas ------------");
        System.out.println("Media de Downloads: " + media);
        System.out.println("Máxima de Downloads: " + est.getMax());
        System.out.println("Mínima de Downloads: " + est.getMin());
        System.out.println("Total registros para calcular as Estastiticas: " + est.getCount());
        System.out.println("---------------------------------------------------\n");
    }
}
