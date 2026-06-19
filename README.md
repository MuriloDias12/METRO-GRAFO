# Roteiro Turístico em Rede de Metrô — TSP Exato

Aplicativo que resolve o **Problema do Caixeiro Viajante (TSP)** aplicado ao
roteiro de um turista que deseja visitar um conjunto de pontos de interesse de
uma cidade exatamente uma vez e retornar ao ponto de partida, minimizando o
tempo total de deslocamento.

A resolução é **exata**, pelo algoritmo de **Held-Karp** (programação dinâmica
com máscara de bits).

## Estrutura do projeto

```
tsp-metro/
├── src/
│   ├── grafo/          # Biblioteca genérica de grafos (Vertice, Aresta, Grafo)
│   ├── algoritmos/     # Dijkstra e TSPExato (Held-Karp)
│   ├── persistencia/   # Leitura de dados de arquivo (LeitorArquivo)
│   ├── util/           # Gerador de arquivos de entrada (GeradorArquivo)
│   ├── servico/        # Orquestração da resolução (camada de aplicação)
│   └── apresentacao/   # Interface de linha de comando (menu)
├── dados/              # Arquivos de entrada gerados (criados em tempo de execução)
└── README.md
```

A organização segue uma **arquitetura em camadas**: apresentação → serviço →
persistência / biblioteca de grafos. Cada camada depende apenas das camadas
inferiores.

## Como compilar

A partir da raiz do projeto:

```bash
javac -d bin src/grafo/*.java src/algoritmos/*.java src/persistencia/*.java src/util/*.java src/servico/*.java src/apresentacao/*.java
```

## Como executar

### 1. Gerar o arquivo de entrada

```bash
java -cp bin util.GeradorArquivo <qtdePontos> [arquivoSaida] [tempoMin] [tempoMax] [semente]
```

Exemplos:

```bash
# 6 pontos, arquivo padrão (dados/gerado.txt), tempos aleatórios entre 2 e 30
java -cp bin util.GeradorArquivo 6

# 12 pontos, arquivo customizado
java -cp bin util.GeradorArquivo 12 dados/teste.txt

# 8 pontos, tempos entre 2 e 30, semente fixa (resultado reproduzível)
java -cp bin util.GeradorArquivo 8 dados/teste.txt 2 30 42
```

O gerador produz um grafo **completo e simétrico**: todos os pares de pontos
recebem uma conexão com tempo sorteado no intervalo informado.
Os pontos são nomeados automaticamente: A, B, ..., Z, AA, AB, ...

### 2. Executar o menu principal

```bash
java -cp bin apresentacao.Main
```

O Main carrega automaticamente `dados/gerado.txt` na inicialização. Para usar
outro arquivo, escolha a opção 1 do menu.

## Formato do arquivo de entrada

```
# linhas iniciadas por # são comentários

PONTOS
A
B
C
...

CONEXOES
A;B;12
A;C;7
...
```

- Seção `PONTOS`: um nome de ponto por linha.
- Seção `CONEXOES`: `origem;destino;tempo`. Como o grafo é não-direcionado,
  cada conexão vale nos dois sentidos.

## Menu do aplicativo

```

1 - Adicionar ponto
2 - Adicionar conexão (aresta)
3 - Imprimir grafo
4 - Caminhamento em largura (BFS)
5 - Resolver roteiro turístico (TSP)
0 - Sair
```

## Observação sobre desempenho

O TSP é NP-difícil. O algoritmo de Held-Karp tem complexidade O(2^n · n²),
viável até aproximadamente 18 pontos. Acima disso, o tempo cresce
exponencialmente e o programa emite um aviso.
