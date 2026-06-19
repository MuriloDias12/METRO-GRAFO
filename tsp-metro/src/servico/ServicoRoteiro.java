package servico;

import algoritmos.Dijkstra;
import algoritmos.TSPExato;
import grafo.Grafo;
import grafo.Vertice;

import java.util.ArrayList;
import java.util.List;

/**
 * Camada de Serviço (Aplicação).
 *
 * Orquestra a resolução do problema. O grafo de entrada pode ser esparso;
 * Dijkstra é executado a partir de cada vértice para calcular o tempo mínimo
 * entre todos os pares de pontos, produzindo a matriz de distâncias completa
 * exigida pelo TSP de Held-Karp.
 *
 * Esta camada não conhece detalhes de entrada/saída (arquivo, console);
 * apenas recebe o grafo e devolve o resultado, mantendo a separação de
 * responsabilidades.
 */
public class ServicoRoteiro {

    private final Grafo<String> grafo;

    public ServicoRoteiro(Grafo<String> grafo) {
        this.grafo = grafo;
    }

    /**
     * Resolve o roteiro turístico (TSP) começando e terminando no ponto
     * de partida informado.
     *
     * @param pontoPartida nome do ponto inicial (deve existir no grafo)
     * @return resultado com a rota e o tempo total mínimo
     */
    public TSPExato.Resultado resolver(String pontoPartida) {
        // 1) Coloca o ponto de partida no índice 0 (exigência do Held-Karp).
        List<Vertice<String>> ordenados = ordenarComPartidaPrimeiro(pontoPartida);
        int n = ordenados.size();

        List<String> rotulos = new ArrayList<>();
        for (Vertice<String> v : ordenados) {
            rotulos.add(v.getObjeto());
        }

        // 2) Dijkstra a partir de cada vértice para obter as distâncias mínimas.
        //    O array retornado é indexado pelo índice interno de cada vértice.
        Dijkstra<String> dijkstra = new Dijkstra<>(grafo);
        float[][] dist = new float[n][n];

        for (int i = 0; i < n; i++) {
            float[] distOrigem = dijkstra.menoresDistancias(ordenados.get(i));
            for (int j = 0; j < n; j++) {
                dist[i][j] = distOrigem[ordenados.get(j).getIndice()];
            }
        }

        // 3) Resolve o TSP exato sobre a matriz de tempos mínimos.
        TSPExato tsp = new TSPExato(dist, rotulos);
        return tsp.resolver();
    }

    /** Coloca o ponto de partida no início e os demais na sequência. */
    private List<Vertice<String>> ordenarComPartidaPrimeiro(String pontoPartida) {
        Vertice<String> partida = grafo.getVertice(pontoPartida);
        if (partida == null) {
            throw new IllegalArgumentException(
                    "Ponto de partida não existe no grafo: " + pontoPartida);
        }
        List<Vertice<String>> lista = new ArrayList<>();
        lista.add(partida);
        for (Vertice<String> v : grafo.getVertices()) {
            if (!v.getObjeto().equals(pontoPartida)) {
                lista.add(v);
            }
        }
        return lista;
    }
}
