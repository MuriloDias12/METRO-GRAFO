package grafo;

/**
 * Representa uma aresta ponderada do grafo, ligando dois vértices.
 * No contexto do problema, o peso corresponde ao tempo de deslocamento
 * (em minutos) entre dois pontos.
 *
 * @param <T> tipo do objeto armazenado nos vértices ligados pela aresta
 */
public class Aresta<T> {

    private final Vertice<T> origem;
    private final Vertice<T> destino;
    private final float peso; // tempo de deslocamento entre origem e destino

    public Aresta(Vertice<T> origem, Vertice<T> destino, float peso) {
        this.origem = origem;
        this.destino = destino;
        this.peso = peso;
    }

    public Vertice<T> getOrigem() {
        return origem;
    }

    public Vertice<T> getDestino() {
        return destino;
    }

    public float getPeso() {
        return peso;
    }

    @Override
    public String toString() {
        return origem + " --(" + peso + ")--> " + destino;
    }
}
