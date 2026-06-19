package algoritmos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


public class TSPExato {

    private final float[][] dist; // matriz de tempos entre os n pontos
    private final int n;
    private final List<String> rotulos; // índice -> nome do ponto

    public TSPExato(float[][] dist, List<String> rotulos) {
        this.dist = dist;
        this.n = dist.length;
        this.rotulos = rotulos;
    }

    /** Resultado da resolução: rota (sequência de pontos) e custo total. */
    public static class Resultado {
        public final List<String> rota;
        public final float custoTotal;

        public Resultado(List<String> rota, float custoTotal) {
            this.rota = rota;
            this.custoTotal = custoTotal;
        }
    }

    public Resultado resolver() {
        if (n == 0) {
            return new Resultado(new ArrayList<>(), 0f);
        }
        if (n == 1) {
            List<String> rota = new ArrayList<>();
            rota.add(rotulos.get(0));
            return new Resultado(rota, 0f);
        }

        int totalMascaras = 1 << n;
        float[][] dp = new float[totalMascaras][n];
        int[][] pai = new int[totalMascaras][n]; // para reconstruir a rota

        for (float[] linha : dp) {
            Arrays.fill(linha, Float.POSITIVE_INFINITY);
        }
        // estado inicial: partimos do vértice 0, conjunto visitado = {0}
        dp[1][0] = 0f;

        for (int mask = 1; mask < totalMascaras; mask++) {
            for (int u = 0; u < n; u++) {
                // u precisa estar em mask e ter custo finito alcançável
                if ((mask & (1 << u)) == 0) continue;
                if (dp[mask][u] == Float.POSITIVE_INFINITY) continue;

                for (int v = 0; v < n; v++) {
                    if ((mask & (1 << v)) != 0) continue;      // v já visitado
                    if (dist[u][v] == Float.POSITIVE_INFINITY) continue;

                    int proxMask = mask | (1 << v);
                    float novoCusto = dp[mask][u] + dist[u][v];
                    if (novoCusto < dp[proxMask][v]) {
                        dp[proxMask][v] = novoCusto;
                        pai[proxMask][v] = u;
                    }
                }
            }
        }

        // fecha o ciclo: de cada vértice final i volta-se ao vértice 0
        int mascaraCompleta = totalMascaras - 1;
        float melhorCusto = Float.POSITIVE_INFINITY;
        int ultimo = -1;
        for (int i = 1; i < n; i++) {
            if (dp[mascaraCompleta][i] == Float.POSITIVE_INFINITY) continue;
            if (dist[i][0] == Float.POSITIVE_INFINITY) continue;
            float custo = dp[mascaraCompleta][i] + dist[i][0];
            if (custo < melhorCusto) {
                melhorCusto = custo;
                ultimo = i;
            }
        }

        if (ultimo == -1) {
            // não existe ciclo hamiltoniano (grafo desconexo)
            return new Resultado(new ArrayList<>(), Float.POSITIVE_INFINITY);
        }

        List<String> rota = reconstruirRota(pai, mascaraCompleta, ultimo);
        return new Resultado(rota, melhorCusto);
    }

    /** Reconstrói a rota seguindo a tabela de predecessores de trás para frente. */
    private List<String> reconstruirRota(int[][] pai, int mask, int ultimo) {
        LinkedList<String> rota = new LinkedList<>();
        int atual = ultimo;
        int mascaraAtual = mask;

        while (!(atual == 0 && mascaraAtual == 1)) {
            rota.addFirst(rotulos.get(atual));
            int anterior = pai[mascaraAtual][atual];
            mascaraAtual ^= (1 << atual); // remove 'atual' do conjunto
            atual = anterior;
        }
        rota.addFirst(rotulos.get(0));    // ponto de partida no início
        rota.addLast(rotulos.get(0));     // retorno ao ponto de partida (ciclo)
        return rota;
    }
}
