package testes;

import algoritmos.TSPExato;
import grafo.Grafo;
import persistencia.LeitorArquivo;
import servico.ServicoRoteiro;

import java.util.List;

/**
 * Testes simples (sem framework externo) para validar a biblioteca e os
 * algoritmos. Executar com:
 *   java -cp bin testes.TestesBiblioteca
 */
public class TestesBiblioteca {

    private static int passou = 0;
    private static int falhou = 0;

    public static void main(String[] args) throws Exception {
        testeAdicaoVerticeUnico();
        testeBFS();
        testeTSPManual();
        testeTSPArquivo();

        System.out.println("\n==============================");
        System.out.println("Passou: " + passou + " | Falhou: " + falhou);
        if (falhou > 0) System.exit(1);
    }

    /** Vértices duplicados não devem ser inseridos duas vezes. */
    private static void testeAdicaoVerticeUnico() {
        Grafo<String> g = new Grafo<>();
        g.adicionarVertice("A");
        g.adicionarVertice("A");
        g.adicionarVertice("B");
        verificar("Unicidade de vértices", g.getNumVertices() == 2);
    }

    /** O BFS deve visitar todos os vértices alcançáveis. */
    private static void testeBFS() {
        Grafo<String> g = new Grafo<>();
        g.adicionarAresta("A", "B", 1);
        g.adicionarAresta("B", "C", 1);
        g.adicionarAresta("A", "C", 1);
        List<String> ordem = g.caminhamentoLargura("A");
        verificar("BFS visita todos os vértices", ordem.size() == 3);
        verificar("BFS começa na origem", ordem.get(0).equals("A"));
    }

    /** TSP em grafo completo pequeno com ótimo conhecido. */
    private static void testeTSPManual() {
        // Triângulo A-B-C com custos 1,1,1 -> ciclo ótimo = 3
        Grafo<String> g = new Grafo<>();
        g.adicionarAresta("A", "B", 1);
        g.adicionarAresta("B", "C", 1);
        g.adicionarAresta("A", "C", 1);
        ServicoRoteiro s = new ServicoRoteiro(g);
        TSPExato.Resultado r = s.resolver("A");
        verificar("TSP triângulo custo = 3", Math.abs(r.custoTotal - 3f) < 1e-4);
        verificar("TSP rota fecha o ciclo",
                r.rota.get(0).equals(r.rota.get(r.rota.size() - 1)));
    }

    /** TSP a partir do arquivo de exemplo (ótimo verificado = 38). */
    private static void testeTSPArquivo() throws Exception {
        LeitorArquivo l = new LeitorArquivo();
        Grafo<String> g = l.carregar("dados/pontos.txt");
        ServicoRoteiro s = new ServicoRoteiro(g);
        TSPExato.Resultado r = s.resolver("Estacao_Central");
        verificar("TSP arquivo custo = 38", Math.abs(r.custoTotal - 38f) < 1e-4);
        verificar("TSP arquivo visita todos os pontos",
                r.rota.size() == g.getNumVertices() + 1); // +1 pelo retorno
    }

    private static void verificar(String nome, boolean condicao) {
        if (condicao) {
            passou++;
            System.out.println("[OK]    " + nome);
        } else {
            falhou++;
            System.out.println("[FALHA] " + nome);
        }
    }
}
