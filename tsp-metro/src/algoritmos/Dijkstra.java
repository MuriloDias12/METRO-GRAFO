package algoritmos;

import grafo.Aresta;
import grafo.Grafo;
import grafo.Vertice;

import java.util.PriorityQueue;


public class Dijkstra<T> {

    private final Grafo<T> grafo;

    public Dijkstra(Grafo<T> grafo) {
        this.grafo = grafo;
    }


    public float[] menoresDistancias(Vertice<T> origem) {
        int n = grafo.getNumVertices();
        float[] dist = new float[n];
        boolean[] fechado = new boolean[n];
        for (int i = 0; i < n; i++) {
            dist[i] = Float.POSITIVE_INFINITY;
        }
        dist[origem.getIndice()] = 0f;

        // a fila ordena pelos pares (distancia, indice) pela menor distância
        PriorityQueue<float[]> fila =
                new PriorityQueue<>((a, b) -> Float.compare(a[0], b[0]));
        fila.add(new float[]{0f, origem.getIndice()});

        while (!fila.isEmpty()) {
            float[] topo = fila.poll();
            int u = (int) topo[1];

            if (fechado[u]) continue; // já processado com distância menor
            fechado[u] = true;

            Vertice<T> vU = indiceParaVertice(u);
            for (Aresta<T> a : grafo.getArestas(vU)) {
                int v = a.getDestino().getIndice();
                float novo = dist[u] + a.getPeso();
                if (novo < dist[v]) {
                    dist[v] = novo;
                    fila.add(new float[]{novo, v});
                }
            }
        }
        return dist;
    }

    /** Localiza o vértice correspondente a um índice interno. */
    private Vertice<T> indiceParaVertice(int indice) {
        for (Vertice<T> v : grafo.getVertices()) {
            if (v.getIndice() == indice) {
                return v;
            }
        }
        return null;
    }
}
