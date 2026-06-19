package grafo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * Biblioteca genérica de grafos.
 *
 * Estratégia de representação: LISTA DE ADJACÊNCIAS.
 *  - 'vertices'    : Map<T, Vertice<T>> -> garante unicidade dos vértices e
 *                    permite localizar o vértice de um objeto em O(1) amortizado.
 *  - 'adjacencias' : Map<Vertice<T>, List<Aresta<T>>> -> para cada vértice,
 *                    a lista de arestas que partem dele.
 *
 * A lista de adjacências é eficiente em memória para grafos esparsos (como a
 * rede real de um metrô) e permite percorrer apenas os vizinhos de fato
 * existentes, o que beneficia BFS e Dijkstra.
 *
 * Por padrão o grafo é tratado como NÃO-DIRECIONADO (simétrico): ao adicionar
 * uma aresta, ela é inserida nos dois sentidos. Isso reflete o enunciado, em
 * que o tempo de deslocamento independe do sentido.
 *
 * @param <T> tipo do objeto armazenado em cada vértice
 */
public class Grafo<T> {

    private final Map<T, Vertice<T>> vertices;
    private final Map<Vertice<T>, List<Aresta<T>>> adjacencias;
    private int proximoIndice; // contador para atribuir índices internos únicos

    public Grafo() {
        this.vertices = new HashMap<>();
        this.adjacencias = new HashMap<>();
        this.proximoIndice = 0;
    }

    /**
     * (i) Adiciona um vértice ao grafo.
     * Caso já exista um vértice com o objeto informado, nada é feito,
     * garantindo a unicidade. Complexidade: O(1) amortizado.
     */
    public void adicionarVertice(T objeto) {
        if (!vertices.containsKey(objeto)) {
            Vertice<T> v = new Vertice<>(objeto, proximoIndice++);
            vertices.put(objeto, v);
            adjacencias.put(v, new ArrayList<>());
        }
    }

    /**
     * (ii) Adiciona uma aresta entre dois objetos, com o peso informado.
     * Se algum dos vértices ainda não existir, ele é criado automaticamente.
     * Como o grafo é não-direcionado, a aresta é inserida nos dois sentidos.
     * Complexidade: O(1) amortizado.
     */
    public void adicionarAresta(T origem, T destino, float peso) {
        adicionarVertice(origem);
        adicionarVertice(destino);

        Vertice<T> vOrigem = vertices.get(origem);
        Vertice<T> vDestino = vertices.get(destino);

        adjacencias.get(vOrigem).add(new Aresta<>(vOrigem, vDestino, peso));
        adjacencias.get(vDestino).add(new Aresta<>(vDestino, vOrigem, peso));
    }

    /**
     * (iii) Caminhamento em largura (BFS) a partir de um vértice inicial.
     * Retorna a ordem em que os vértices foram visitados.
     * Útil para imprimir/testar a conectividade do grafo.
     * Complexidade: O(V + E).
     */
    public List<T> caminhamentoLargura(T inicio) {
        List<T> ordemVisita = new ArrayList<>();
        Vertice<T> vInicio = vertices.get(inicio);
        if (vInicio == null) {
            return ordemVisita; // vértice inicial inexistente
        }

        Set<Vertice<T>> visitados = new HashSet<>();
        Queue<Vertice<T>> fila = new LinkedList<>();

        fila.add(vInicio);
        visitados.add(vInicio);

        while (!fila.isEmpty()) {
            Vertice<T> atual = fila.poll();
            ordemVisita.add(atual.getObjeto());

            for (Aresta<T> a : adjacencias.get(atual)) {
                Vertice<T> vizinho = a.getDestino();
                if (!visitados.contains(vizinho)) {
                    visitados.add(vizinho);
                    fila.add(vizinho);
                }
            }
        }
        return ordemVisita;
    }

    /** Imprime o grafo como lista de adjacências (para depuração/testes). */
    public void imprimir() {
        for (Vertice<T> v : vertices.values()) {
            StringBuilder sb = new StringBuilder();
            sb.append(v).append(" -> ");
            List<Aresta<T>> arestas = adjacencias.get(v);
            for (int i = 0; i < arestas.size(); i++) {
                Aresta<T> a = arestas.get(i);
                sb.append(a.getDestino()).append("(").append(a.getPeso()).append(")");
                if (i < arestas.size() - 1) sb.append(", ");
            }
            System.out.println(sb);
        }
    }

    // ---- Métodos auxiliares usados pelos algoritmos ----

    public Vertice<T> getVertice(T objeto) {
        return vertices.get(objeto);
    }

    public Collection<Vertice<T>> getVertices() {
        return vertices.values();
    }

    public List<Aresta<T>> getArestas(Vertice<T> v) {
        return adjacencias.get(v);
    }

    public int getNumVertices() {
        return vertices.size();
    }

    public boolean existeVertice(T objeto) {
        return vertices.containsKey(objeto);
    }
}
