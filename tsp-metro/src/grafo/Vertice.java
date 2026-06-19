package grafo;

/**
 * Representa um vértice do grafo.
 * Armazena um objeto genérico do tipo T (ex.: nome de uma estação de metrô)
 * e um índice interno inteiro, usado pelos algoritmos que precisam indexar
 * vértices em vetores/matrizes (Dijkstra) ou em máscaras de bits (Held-Karp).
 *
 * @param <T> tipo do objeto armazenado no vértice
 */
public class Vertice<T> {

    private final T objeto;   // dado armazenado (genérico)
    private final int indice; // posição interna 0..n-1, atribuída pelo grafo

    public Vertice(T objeto, int indice) {
        this.objeto = objeto;
        this.indice = indice;
    }

    public T getObjeto() {
        return objeto;
    }

    public int getIndice() {
        return indice;
    }

    @Override
    public String toString() {
        return String.valueOf(objeto);
    }
}
