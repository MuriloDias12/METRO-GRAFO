package apresentacao;

import algoritmos.TSPExato;
import grafo.Grafo;
import persistencia.LeitorArquivo;
import servico.ServicoRoteiro;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * Camada de Apresentação.
 *
 * Interface de linha de comando (menu) que permite ao usuário:
 *  1. Carregar o grafo a partir de um arquivo;
 *  2. Adicionar novos pontos e conexões;
 *  3. Imprimir o grafo e executar o caminhamento em largura (BFS);
 *  4. Resolver o roteiro turístico (TSP exato).
 *
 * Na inicialização, o {@link GeradorArquivo} produz automaticamente o arquivo
 * de entrada padrão, que é então carregado como grafo inicial.
 *
 * Esta camada lida apenas com a interação (entrada/saída no console) e delega
 * o processamento às camadas de persistência e serviço.
 */
public class Main {

    private static final String ARQUIVO_PADRAO = "dados/gerado.txt";

    private static Grafo<String> grafo = new Grafo<>();
    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=== Roteiro Turístico em Rede de Metrô (TSP exato) ===");

        carregarArquivo(ARQUIVO_PADRAO);

        int opcao;
        do {
            exibirMenu();
            opcao = lerInteiro();
            switch (opcao) {
                case 1 -> adicionarPonto();
                case 2 -> adicionarConexao();
                case 3 -> imprimirGrafo();
                case 4 -> executarBFS();
                case 5 -> resolverRoteiro();
                case 0 -> System.out.println("Encerrando.");
                default -> System.out.println("Opção inválida.");
            }
        } while (opcao != 0);
    }

    private static void exibirMenu() {
        System.out.println("\n---------------------------------------");
    
        System.out.println("1 - Adicionar ponto");
        System.out.println("2 - Adicionar conexão (aresta)");
        System.out.println("3 - Imprimir grafo");
        System.out.println("4 - Caminhamento em largura (BFS)");
        System.out.println("5 - Resolver roteiro turístico (TSP)");
        System.out.println("0 - Sair");
        System.out.print("Opção: ");
    }

    private static void carregarArquivo(String caminho) {
        try {
            LeitorArquivo leitor = new LeitorArquivo();
            grafo = leitor.carregar(caminho);
            System.out.println("Grafo carregado: " + grafo.getNumVertices()
                    + " ponto(s).");
        } catch (IOException e) {
            System.out.println("Erro ao carregar arquivo: " + e.getMessage());
        }
    }

    private static void adicionarPonto() {
        System.out.print("Nome do ponto: ");
        String nome = sc.nextLine().trim();
        if (nome.isEmpty()) {
            System.out.println("Nome vazio, operação cancelada.");
            return;
        }
        grafo.adicionarVertice(nome);
        System.out.println("Ponto adicionado (ou já existente): " + nome);
    }

    private static void adicionarConexao() {
        System.out.print("Origem: ");
        String origem = sc.nextLine().trim();
        System.out.print("Destino: ");
        String destino = sc.nextLine().trim();
        System.out.print("Tempo (minutos): ");
        float tempo;
        try {
            tempo = Float.parseFloat(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Tempo inválido.");
            return;
        }
        grafo.adicionarAresta(origem, destino, tempo);
        System.out.println("Conexão adicionada: " + origem + " <-> "
                + destino + " (" + tempo + ")");
    }

    private static void imprimirGrafo() {
        if (grafo.getNumVertices() == 0) {
            System.out.println("Grafo vazio.");
            return;
        }
        grafo.imprimir();
    }

    private static void executarBFS() {
        if (grafo.getNumVertices() == 0) {
            System.out.println("Grafo vazio.");
            return;
        }
        System.out.print("Ponto inicial do BFS: ");
        String inicio = sc.nextLine().trim();
        if (!grafo.existeVertice(inicio)) {
            System.out.println("Ponto não existe no grafo.");
            return;
        }
        List<String> ordem = grafo.caminhamentoLargura(inicio);
        System.out.println("Ordem de visita (BFS): " + String.join(" -> ", ordem));
    }

    private static void resolverRoteiro() {
        if (grafo.getNumVertices() == 0) {
            System.out.println("Grafo vazio.");
            return;
        }
        System.out.print("Ponto de partida/retorno: ");
        String partida = sc.nextLine().trim();
        if (!grafo.existeVertice(partida)) {
            System.out.println("Ponto não existe no grafo.");
            return;
        }

        int n = grafo.getNumVertices();
        if (n > 18) {
            System.out.println("Aviso: " + n + " pontos. O TSP exato é "
                    + "exponencial e pode demorar muito acima de ~18 pontos.");
        }

        long inicio = System.currentTimeMillis();
        ServicoRoteiro servico = new ServicoRoteiro(grafo);
        TSPExato.Resultado r = servico.resolver(partida);
        long fim = System.currentTimeMillis();

        if (r.custoTotal == Float.POSITIVE_INFINITY || r.rota.isEmpty()) {
            System.out.println("Não foi possível encontrar um ciclo que visite "
                    + "todos os pontos (grafo possivelmente desconexo).");
            return;
        }

        System.out.println("\n=== Melhor roteiro encontrado ===");
        System.out.println("Rota: " + String.join(" -> ", r.rota));
        System.out.printf("Tempo total: %.2f minutos%n", r.custoTotal);
        System.out.println("Tempo de processamento: " + (fim - inicio) + " ms");
    }

    /** Lê um inteiro com tratamento de erro, consumindo a linha. */
    private static int lerInteiro() {
        try {
            return Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
