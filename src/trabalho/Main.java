package trabalho;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

// Executa os testes de desempenho, confere se cada resultado saiu ordenado,
// grava os CSV e gera os graficos. Para mudar o experimento, basta alterar
// as constantes abaixo.
public class Main {

    static final int[] TAMANHOS = {10000, 50000, 100000};
    static final int[] THREADS = {2, 4, 8};
    static final int AMOSTRAS = 5;
    static final String[] TIPOS = {GeradorDados.ALEATORIO, GeradorDados.ORDENADO, GeradorDados.DECRESCENTE};
    static final String[] ALGORITMOS = {"Quick Sort", "Merge Sort", "Insertion Sort", "Selection Sort"};

    static final File PASTA = new File("resultados");

    // Uma linha do resumo: um algoritmo, uma versao, um tamanho e um tipo de vetor
    static class Resumo {
        String algoritmo;
        String versao;
        int threads;
        int tamanho;
        String tipo;
        double media;
        double desvio;
        double minimo;
        double maximo;
        double speedup;
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Processadores logicos: " + Runtime.getRuntime().availableProcessors());

        testarCorrecao();
        if (args.length > 0 && args[0].equals("teste")) {
            return;
        }

        aquecerJvm();

        PASTA.mkdirs();
        PrintWriter tempos = abrirCsv(new File(PASTA, "tempos.csv"));
        tempos.println("algoritmo;versao;threads;tamanho;tipo_dados;amostra;tempo_ms");

        List<Resumo> resumos = new ArrayList<Resumo>();

        for (String algoritmo : ALGORITMOS) {
            for (int tamanho : TAMANHOS) {
                for (String tipo : TIPOS) {
                    System.out.println(algoritmo + " | n = " + tamanho + " | " + tipo);

                    // O mesmo vetor base e usado em todas as amostras e versoes
                    int[] base = GeradorDados.gerar(tipo, tamanho, 2026L + tamanho);
                    int[] esperado = base.clone();
                    Arrays.sort(esperado);

                    double[] serial = medir(algoritmo, 1, base, esperado);
                    gravarAmostras(tempos, algoritmo, "serial", 1, tamanho, tipo, serial);
                    Resumo rSerial = resumir(algoritmo, "serial", 1, tamanho, tipo, serial);
                    rSerial.speedup = 1.0;
                    resumos.add(rSerial);
                    System.out.println("   serial     " + fmt(rSerial.media) + " ms");

                    for (int threads : THREADS) {
                        double[] paralelo = medir(algoritmo, threads, base, esperado);
                        gravarAmostras(tempos, algoritmo, "paralelo", threads, tamanho, tipo, paralelo);
                        Resumo r = resumir(algoritmo, "paralelo", threads, tamanho, tipo, paralelo);
                        r.speedup = rSerial.media / r.media;
                        resumos.add(r);
                        System.out.println("   " + threads + " threads  " + fmt(r.media) + " ms  (speedup " + fmt(r.speedup) + ")");
                    }
                    tempos.flush();
                }
            }
        }
        tempos.close();

        gravarResumo(resumos);
        gravarAmbiente();
        Grafico.gerar(new File(PASTA, "graficos"), resumos);
        System.out.println("Pronto. Arquivos na pasta " + PASTA.getAbsolutePath());
    }

    // Roda as AMOSTRAS de uma configuracao. threads = 1 significa versao serial.
    static double[] medir(String algoritmo, int threads, int[] base, int[] esperado) {
        double[] tempos = new double[AMOSTRAS];
        for (int a = 0; a < AMOSTRAS; a++) {
            int[] copia = base.clone();
            long inicio = System.nanoTime();
            ordenar(algoritmo, copia, threads);
            long fim = System.nanoTime();
            tempos[a] = (fim - inicio) / 1000000.0;

            // A conferencia fica fora do tempo medido
            if (!Arrays.equals(copia, esperado)) {
                throw new IllegalStateException(algoritmo + " com " + threads + " thread(s) nao ordenou corretamente");
            }
        }
        return tempos;
    }

    static void ordenar(String algoritmo, int[] vetor, int threads) {
        boolean serial = threads == 1;
        if (algoritmo.equals("Quick Sort")) {
            if (serial) Quicksort.ordenar(vetor); else QuicksortParalelo.ordenar(vetor, threads);
        } else if (algoritmo.equals("Merge Sort")) {
            if (serial) Mergesort.ordenar(vetor); else MergesortParalelo.ordenar(vetor, threads);
        } else if (algoritmo.equals("Insertion Sort")) {
            if (serial) InsertionSort.ordenar(vetor); else InsertionSortParalelo.ordenar(vetor, threads);
        } else if (algoritmo.equals("Selection Sort")) {
            if (serial) SelectionSort.ordenar(vetor); else SelectionSortParalelo.ordenar(vetor, threads);
        } else {
            throw new IllegalArgumentException("Algoritmo desconhecido: " + algoritmo);
        }
    }

    // Vetores pequenos e com casos de borda (vazio, um elemento, repetidos, negativos)
    static void testarCorrecao() {
        List<int[]> casos = new ArrayList<int[]>();
        casos.add(new int[] {});
        casos.add(new int[] {7});
        casos.add(new int[] {5, 5, 5, 5});
        casos.add(new int[] {3, -1, 8, 0, -7, 3, 12, 1, 1, 9, -2});
        for (String tipo : TIPOS) {
            casos.add(GeradorDados.gerar(tipo, 5003, 99));
        }

        int[] threadsTeste = {1, 2, 3, 4, 8};
        for (String algoritmo : ALGORITMOS) {
            for (int[] caso : casos) {
                int[] esperado = caso.clone();
                Arrays.sort(esperado);
                for (int threads : threadsTeste) {
                    int[] copia = caso.clone();
                    ordenar(algoritmo, copia, threads);
                    if (!Arrays.equals(copia, esperado)) {
                        throw new IllegalStateException("Teste de correcao falhou: " + algoritmo
                                + ", " + threads + " thread(s), tamanho " + caso.length);
                    }
                }
            }
        }
        System.out.println("Teste de correcao ok.");
    }

    // A primeira execucao na JVM e mais lenta (compilacao JIT), entao e descartada
    static void aquecerJvm() {
        int[] base = GeradorDados.gerar(GeradorDados.ALEATORIO, 20000, 1);
        for (int rodada = 0; rodada < 3; rodada++) {
            for (String algoritmo : ALGORITMOS) {
                ordenar(algoritmo, base.clone(), 1);
                ordenar(algoritmo, base.clone(), 4);
            }
        }
        System.out.println("Aquecimento concluido (nao entra nos resultados).");
    }

    static Resumo resumir(String algoritmo, String versao, int threads, int tamanho, String tipo, double[] t) {
        Resumo r = new Resumo();
        r.algoritmo = algoritmo;
        r.versao = versao;
        r.threads = threads;
        r.tamanho = tamanho;
        r.tipo = tipo;

        double soma = 0;
        r.minimo = t[0];
        r.maximo = t[0];
        for (double v : t) {
            soma += v;
            r.minimo = Math.min(r.minimo, v);
            r.maximo = Math.max(r.maximo, v);
        }
        r.media = soma / t.length;

        // Desvio padrao amostral (divide por n - 1)
        double somaQuadrados = 0;
        for (double v : t) {
            somaQuadrados += (v - r.media) * (v - r.media);
        }
        r.desvio = t.length > 1 ? Math.sqrt(somaQuadrados / (t.length - 1)) : 0;
        return r;
    }

    static void gravarAmostras(PrintWriter csv, String algoritmo, String versao, int threads,
                               int tamanho, String tipo, double[] t) {
        for (int i = 0; i < t.length; i++) {
            csv.println(algoritmo + ";" + versao + ";" + threads + ";" + tamanho + ";" + tipo + ";"
                    + (i + 1) + ";" + fmt(t[i]));
        }
    }

    static void gravarResumo(List<Resumo> resumos) throws IOException {
        PrintWriter csv = abrirCsv(new File(PASTA, "resumo.csv"));
        csv.println("algoritmo;versao;threads;tamanho;tipo_dados;media_ms;desvio_padrao_ms;minimo_ms;maximo_ms;speedup");
        for (Resumo r : resumos) {
            csv.println(r.algoritmo + ";" + r.versao + ";" + r.threads + ";" + r.tamanho + ";" + r.tipo + ";"
                    + fmt(r.media) + ";" + fmt(r.desvio) + ";" + fmt(r.minimo) + ";" + fmt(r.maximo) + ";"
                    + fmt(r.speedup));
        }
        csv.close();
    }

    static void gravarAmbiente() throws IOException {
        PrintWriter w = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream(new File(PASTA, "ambiente.txt")), StandardCharsets.UTF_8));
        w.println("java.version=" + System.getProperty("java.version"));
        w.println("java.vendor=" + System.getProperty("java.vendor"));
        w.println("os.name=" + System.getProperty("os.name"));
        w.println("os.arch=" + System.getProperty("os.arch"));
        w.println("processadores_logicos=" + Runtime.getRuntime().availableProcessors());
        w.println("memoria_max_jvm_mb=" + Runtime.getRuntime().maxMemory() / (1024 * 1024));
        w.println("tamanhos=" + Arrays.toString(TAMANHOS));
        w.println("threads=" + Arrays.toString(THREADS));
        w.println("amostras=" + AMOSTRAS);
        w.close();
    }

    // CSV com BOM e separador ";" para o Excel em portugues abrir direto
    static PrintWriter abrirCsv(File arquivo) throws IOException {
        PrintWriter w = new PrintWriter(new OutputStreamWriter(new FileOutputStream(arquivo), StandardCharsets.UTF_8));
        w.print('﻿');
        return w;
    }

    static String fmt(double valor) {
        return String.format(Locale.forLanguageTag("pt-BR"), "%.3f", valor);
    }
}
