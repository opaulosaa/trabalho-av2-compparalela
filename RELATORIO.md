# Análise de Desempenho de Algoritmos de Busca em Ambientes Concorrentes e Paralelos: Um Estudo Comparativo em Java

## Resumo

Este trabalho compara o tempo de quatro algoritmos de ordenação em Java: Quick Sort, Merge Sort, Insertion Sort e Selection Sort. Cada um tem uma versão serial e uma versão paralela. Os testes usaram vetores de 10.000, 50.000 e 100.000 elementos, em ordem aleatória, crescente e decrescente, com 2, 4 e 8 threads e 5 repetições por teste. A versão paralela ajudou pouco no Quick Sort e no Merge Sort, e ajudou bastante no Insertion Sort e no Selection Sort.

## Introdução

O objetivo é ver se dividir a ordenação entre várias threads deixa o programa mais rápido, e em quais situações.

- **Quick Sort:** escolhe um pivô, separa os menores e os maiores e repete nos dois lados.
- **Merge Sort:** divide o vetor ao meio, ordena cada metade e junta as duas.
- **Insertion Sort:** encaixa cada elemento na parte do vetor que já está ordenada.
- **Selection Sort:** procura o menor elemento e coloca na posição atual, repetindo até o fim.

Nas versões paralelas foi usado o `ForkJoinPool` do Java. No Quick Sort e no Merge Sort, as duas partes do vetor são ordenadas ao mesmo tempo. No Insertion Sort e no Selection Sort, o vetor é dividido em blocos, um para cada thread. Cada bloco é ordenado separadamente e depois os blocos são juntados.

## Metodologia

- **Tamanhos:** 10.000, 50.000 e 100.000 elementos.
- **Tipos de vetor:** aleatório, ordenado e decrescente.
- **Versões:** serial e paralela com 2, 4 e 8 threads.
- **Repetições:** 5 vezes cada teste, usando a média.
- **Medição:** `System.nanoTime()`, contando só o tempo da ordenação.
- **Ambiente:** máquina Linux com 2 processadores lógicos e Java 21.

Antes das medições o programa roda os algoritmos algumas vezes sem contar o tempo, para aquecer a JVM. Depois de cada ordenação, ele confere se o vetor ficou certo.

Os resultados ficam em `resultados/tempos.csv` (todas as medições) e `resultados/resumo.csv` (média, desvio padrão e speedup). O speedup é o tempo serial dividido pelo tempo paralelo. Acima de 1, o paralelo foi mais rápido.

## Resultados

Tempos médios em milissegundos, com vetor aleatório de 100.000 elementos:

| Algoritmo | Serial | 2 threads | 4 threads | 8 threads |
| --- | ---: | ---: | ---: | ---: |
| Quick Sort | 11,55 | 6,52 | 6,64 | 8,49 |
| Merge Sort | 13,96 | 18,18 | 16,44 | 10,84 |
| Insertion Sort | 2569,24 | 644,77 | 332,86 | 163,53 |
| Selection Sort | 5761,12 | 1570,59 | 754,92 | 366,48 |

Os gráficos de todos os casos estão em `resultados/graficos/`.

**Quick Sort e Merge Sort.** Esses algoritmos já são rápidos, levando poucos milissegundos. Com 10.000 elementos a versão paralela foi mais lenta, porque criar as threads custa mais do que o ganho. Com vetores maiores o Quick Sort paralelo ficou quase 2 vezes mais rápido. O Merge Sort variou bastante entre as repetições e não teve um ganho claro.

**Insertion Sort e Selection Sort.** Aqui o paralelo foi muito mais rápido: cerca de 4 vezes com 2 threads, 8 vezes com 4 e 16 vezes com 8. Esse ganho é maior que o número de núcleos porque esses algoritmos ficam muito mais lentos à medida que o vetor cresce. Ordenar vários blocos pequenos dá menos trabalho do que ordenar o vetor inteiro de uma vez.

**Tipo de vetor.** O Insertion Sort com vetor já ordenado é quase instantâneo no serial (0,13 ms). Nesse caso a versão paralela ficou mais lenta (1,84 ms com 8 threads), porque só acrescentou o custo de dividir e juntar. O Selection Sort demorou parecido nos três tipos de vetor, porque ele sempre percorre o vetor todo.

**Número de threads.** Como a máquina tinha 2 núcleos, usar 4 ou 8 threads não ajudou o Quick Sort e o Merge Sort.

## Conclusão

Usar threads nem sempre deixa a ordenação mais rápida. Em vetores pequenos, ou quando o algoritmo já é rápido, o custo de criar e coordenar as threads pode ser maior que o ganho. A versão paralela compensou mais nos algoritmos lentos, o Insertion Sort e o Selection Sort, com vetores grandes. Mesmo assim, o Quick Sort serial continuou muito mais rápido que todos eles, o que mostra que a escolha do algoritmo pesa mais que o paralelismo.

## Referências

CORMEN, Thomas H. et al. **Algoritmos: teoria e prática**. 3. ed. Rio de Janeiro: Elsevier, 2012.

ORACLE. **Fork/Join**. The Java Tutorials. Disponível em: https://docs.oracle.com/javase/tutorial/essential/concurrency/forkjoin.html. Acesso em: 30 out. 2026.

## Anexos

Códigos em `src/trabalho`:

- `Quicksort.java` e `QuicksortParalelo.java`
- `Mergesort.java` e `MergesortParalelo.java`
- `InsertionSort.java` e `InsertionSortParalelo.java`
- `SelectionSort.java` e `SelectionSortParalelo.java`
- `GeradorDados.java`: gera os vetores
- `Main.java`: roda os testes e grava os CSV
- `Grafico.java`: gera os gráficos
