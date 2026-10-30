# Análise de Desempenho de Algoritmos de Busca em Ambientes Concorrentes e Paralelos: Um Estudo Comparativo em Java

Comparação do tempo de execução de Quick Sort, Merge Sort, Insertion Sort e Selection Sort, cada um com versão serial e versão paralela (ForkJoin). O programa varia o tamanho do vetor, o tipo de entrada e o número de threads, repete cada configuração 5 vezes, grava os tempos em CSV e gera os gráficos.

O relatório está em [RELATORIO.md](RELATORIO.md).

## Estrutura

```
src/trabalho/
  Quicksort.java              Quick Sort serial (pivô aleatório, dois ponteiros)
  QuicksortParalelo.java      Quick Sort paralelo com ForkJoin
  Mergesort.java              Merge Sort serial
  MergesortParalelo.java      Merge Sort paralelo com ForkJoin
  InsertionSort.java          Insertion Sort serial
  InsertionSortParalelo.java  Insertion Sort por blocos (um por thread) + merge
  SelectionSort.java          Selection Sort serial
  SelectionSortParalelo.java  Selection Sort por blocos (um por thread) + merge
  GeradorDados.java           Vetores aleatório, ordenado e decrescente
  Main.java                   Executa os testes, calcula as estatísticas e grava os CSV
  Grafico.java                Gera os gráficos em SVG
resultados/                   Saída do programa
```

Cada classe de algoritmo ainda tem o seu próprio `main` com um vetor de 10 posições, para testar o algoritmo isolado.

## Como rodar

Precisa do JDK 8 ou mais novo.

No Windows, dê dois cliques em `executar.bat` ou rode no terminal:

```bat
javac -encoding UTF-8 -d bin src\trabalho\*.java
java -cp bin trabalho.Main
```

No Linux ou macOS: `./executar.sh`.

Para rodar só a verificação de correção, sem medir tempo:

```bat
java -cp bin trabalho.Main teste
```

O experimento completo leva alguns minutos, principalmente por causa do Insertion Sort e do Selection Sort seriais com 100.000 elementos. Para mudar tamanhos, threads ou número de amostras, altere as constantes no início de `Main.java`.

## Saída

- `resultados/tempos.csv`: todas as amostras
- `resultados/resumo.csv`: média, desvio padrão, mínimo, máximo e speedup de cada configuração
- `resultados/ambiente.txt`: versão do Java, sistema e número de processadores
- `resultados/graficos/`: gráficos SVG (tempo x tamanho e speedup); `index.html` mostra todos

Os CSV usam `;` como separador e vírgula decimal, para abrir direto no Excel.
