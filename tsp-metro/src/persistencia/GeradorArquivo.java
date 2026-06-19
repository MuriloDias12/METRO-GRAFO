package persistencia;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

/**
 * Gerador de arquivos de entrada para o problema do roteiro turístico (TSP).
 *
 * Produz um grafo COMPLETO e SIMÉTRICO no formato lido pela camada de
 * persistência:
 *
 *   PONTOS
 *   A
 *   B
 *   ...
 *   CONEXOES
 *   A;B;12
 *   A;C;7
 *   ...
 *
 * Características da saída:
 *  - Os pontos são nomeados com letras do alfabeto: A..Z e, a partir do 27º,
 *    AA, AB, ... (permite aumentar a quantidade de pontos sem limite prático).
 *  - Os tempos são inteiros, sorteados dentro de um intervalo configurável.
 *  - Como o grafo é completo, é gerada uma conexão para cada par de pontos.
 *
 * COMO USAR: basta alterar as variáveis de configuração abaixo e executar.
 *   java -cp bin util.GeradorArquivo
 */
public class GeradorArquivo {

    // =========================================================
    //  CONFIGURAÇÃO - altere os valores abaixo conforme desejar
    // =========================================================

    // Quantidade de pontos (vértices) do grafo a ser gerado.
    private static final int QTDE_PONTOS = 10;

    // Caminho do arquivo de saída.
    private static final String ARQUIVO_SAIDA = "dados/gerado.txt";

    // Intervalo dos tempos (em minutos) sorteados entre os pontos.
    private static final int TEMPO_MIN = 2;
    private static final int TEMPO_MAX = 30;

    // Semente do gerador aleatório:
    //  - use um valor fixo (ex.: 42) para gerar sempre o mesmo arquivo;
    //  - deixe -1 para usar uma semente aleatória a cada execução.
    private static final long SEMENTE = -1;

    // =========================================================

    public static void main(String[] args) {
        if (QTDE_PONTOS < 2) {
            System.out.println("Informe pelo menos 2 pontos em QTDE_PONTOS.");
            return;
        }
        if (TEMPO_MIN > TEMPO_MAX) {
            System.out.println("TEMPO_MIN não pode ser maior que TEMPO_MAX.");
            return;
        }

        Random rand = (SEMENTE >= 0) ? new Random(SEMENTE) : new Random();

        try {
            gerar(ARQUIVO_SAIDA, QTDE_PONTOS, TEMPO_MIN, TEMPO_MAX, rand);
            long qtdeConexoes = (long) QTDE_PONTOS * (QTDE_PONTOS - 1) / 2;
            System.out.println("Arquivo gerado: " + ARQUIVO_SAIDA);
            System.out.println("Pontos: " + QTDE_PONTOS
                    + " | Conexões: " + qtdeConexoes
                    + " | Tempos em [" + TEMPO_MIN + ", " + TEMPO_MAX + "]");
        } catch (IOException e) {
            System.out.println("Erro ao gerar arquivo: " + e.getMessage());
        }
    }

    /** Gera o arquivo de grafo completo com a quantidade de pontos pedida. */
    public static void gerar(String caminho, int qtdePontos,
                             int tempoMin, int tempoMax, Random rand)
            throws IOException {

        // 1) monta os nomes dos pontos (A, B, ..., Z, AA, AB, ...)
        String[] nomes = new String[qtdePontos];
        for (int i = 0; i < qtdePontos; i++) {
            nomes[i] = gerarNome(i);
        }

        try (PrintWriter out = new PrintWriter(new FileWriter(caminho))) {
            out.println("# Arquivo gerado automaticamente por GeradorArquivo");
            out.println("# Grafo COMPLETO e SIMETRICO - tempos inteiros (minutos)");
            out.println();

            // 2) seção de pontos
            out.println("PONTOS");
            for (String nome : nomes) {
                out.println(nome);
            }
            out.println();

            // 3) seção de conexões: um par para cada combinação i < j
            out.println("CONEXOES");
            int amplitude = tempoMax - tempoMin + 1;
            for (int i = 0; i < qtdePontos; i++) {
                for (int j = i + 1; j < qtdePontos; j++) {
                    int tempo = tempoMin + rand.nextInt(amplitude);
                    out.println(nomes[i] + ";" + nomes[j] + ";" + tempo);
                }
            }
        }
    }

    /**
     * Converte um índice (0-based) em um nome de ponto com letras maiúsculas.
     * 0 -> A, 1 -> B, ..., 25 -> Z, 26 -> AA, 27 -> AB, ...
     * Funciona como numeração em base 26 com o alfabeto, permitindo gerar
     * tantos pontos quantos forem necessários.
     */
    private static String gerarNome(int indice) {
        StringBuilder sb = new StringBuilder();
        int n = indice;
        while (true) {
            sb.insert(0, (char) ('A' + (n % 26)));
            n = n / 26 - 1;
            if (n < 0) break;
        }
        return sb.toString();
    }
}