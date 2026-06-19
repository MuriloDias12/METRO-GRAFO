package persistencia;

import grafo.Grafo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Camada de Persistência.
 *
 * Responsável por ler os dados do problema a partir de um arquivo-texto e
 * construir o grafo correspondente. Mantém a leitura de dados isolada das
 * demais camadas (serviço e apresentação).
 *
 * Formato do arquivo esperado:
 *
 *   PONTOS
 *   Estacao_Central
 *   Museu
 *   Parque
 *   ...
 *   CONEXOES
 *   Estacao_Central;Museu;12.5
 *   Museu;Parque;8.0
 *   ...
 *
 * - Linhas em branco e linhas iniciadas por '#' (comentários) são ignoradas.
 * - Em CONEXOES, os campos são separados por ';' : origem;destino;tempo.
 */
public class LeitorArquivo {

    /** Lê o arquivo informado e devolve um grafo de Strings já construído. */
    public Grafo<String> carregar(String caminho) throws IOException {
        Grafo<String> grafo = new Grafo<>();
        String secaoAtual = "";

        try (BufferedReader br = new BufferedReader(new FileReader(caminho))) {
            String linha;
            int numLinha = 0;
            while ((linha = br.readLine()) != null) {
                numLinha++;
                linha = linha.trim();

                if (linha.isEmpty() || linha.startsWith("#")) {
                    continue;
                }

                String maiuscula = linha.toUpperCase();
                if (maiuscula.equals("PONTOS")) {
                    secaoAtual = "PONTOS";
                    continue;
                }
                if (maiuscula.equals("CONEXOES")) {
                    secaoAtual = "CONEXOES";
                    continue;
                }

                if (secaoAtual.equals("PONTOS")) {
                    grafo.adicionarVertice(linha);

                } else if (secaoAtual.equals("CONEXOES")) {
                    String[] partes = linha.split(";");
                    if (partes.length != 3) {
                        throw new IOException("Linha " + numLinha
                                + " inválida em CONEXOES: \"" + linha + "\"");
                    }
                    String origem = partes[0].trim();
                    String destino = partes[1].trim();
                    float tempo;
                    try {
                        tempo = Float.parseFloat(partes[2].trim());
                    } catch (NumberFormatException e) {
                        throw new IOException("Tempo inválido na linha "
                                + numLinha + ": \"" + partes[2] + "\"");
                    }
                    grafo.adicionarAresta(origem, destino, tempo);

                } else {
                    throw new IOException("Dado fora de seção na linha "
                            + numLinha + ": \"" + linha + "\". "
                            + "Use cabeçalhos PONTOS e CONEXOES.");
                }
            }
        }
        return grafo;
    }
}
