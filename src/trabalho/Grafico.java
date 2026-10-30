package trabalho;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

// Gera graficos estaticos em SVG a partir do resumo, para inserir no relatorio.
// 1) Tempo medio x tamanho do vetor, um grafico por algoritmo e tipo de vetor.
// 2) Speedup por numero de threads no maior tamanho, um grafico por tipo de vetor.
public class Grafico {

    static final String[] CORES = {"#1d4e89", "#d1495b", "#2a9d8f", "#e9a03b"};
    static final int LARGURA = 720;
    static final int ALTURA = 420;
    static final int ESQ = 80, DIR = 170, TOPO = 50, BASE = 60;

    static void gerar(File pasta, List<Main.Resumo> resumos) throws IOException {
        pasta.mkdirs();
        List<String> arquivos = new ArrayList<String>();

        for (String algoritmo : Main.ALGORITMOS) {
            for (String tipo : Main.TIPOS) {
                String nome = slug(algoritmo) + "_" + tipo + ".svg";
                salvar(new File(pasta, nome), graficoTempo(resumos, algoritmo, tipo));
                arquivos.add(nome);
            }
        }
        for (String tipo : Main.TIPOS) {
            String nome = "speedup_" + tipo + ".svg";
            salvar(new File(pasta, nome), graficoSpeedup(resumos, tipo));
            arquivos.add(nome);
        }

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n<html lang=\"pt-BR\"><head><meta charset=\"UTF-8\"><title>Graficos</title>");
        html.append("<style>body{font-family:Arial,sans-serif;margin:24px}img{max-width:100%;border:1px solid #ddd;margin:8px 0 24px}</style>");
        html.append("</head><body>\n<h1>Ordenacao serial x paralela</h1>\n");
        for (String a : arquivos) {
            html.append("<h3>").append(a).append("</h3>\n<img src=\"").append(a).append("\" alt=\"").append(a).append("\">\n");
        }
        html.append("</body></html>\n");
        salvar(new File(pasta, "index.html"), html.toString());
    }

    static String graficoTempo(List<Main.Resumo> resumos, String algoritmo, String tipo) {
        int[] tamanhos = Main.TAMANHOS;
        int[] series = new int[Main.THREADS.length + 1];
        series[0] = 1;
        for (int i = 0; i < Main.THREADS.length; i++) {
            series[i + 1] = Main.THREADS[i];
        }

        double max = 0;
        for (Main.Resumo r : resumos) {
            if (r.algoritmo.equals(algoritmo) && r.tipo.equals(tipo)) {
                max = Math.max(max, r.media);
            }
        }
        max = escala(max);

        int w = LARGURA - ESQ - DIR;
        int h = ALTURA - TOPO - BASE;
        StringBuilder s = inicio(algoritmo + " - vetor " + tipo);
        eixoY(s, max, "Tempo medio (ms)");
        s.append(texto(ESQ + w / 2, ALTURA - 15, "Tamanho do vetor", "middle", 13));

        for (int i = 0; i < tamanhos.length; i++) {
            int x = ESQ + (tamanhos.length == 1 ? w / 2 : i * w / (tamanhos.length - 1));
            s.append(texto(x, TOPO + h + 20, String.valueOf(tamanhos[i]), "middle", 12));
        }

        for (int k = 0; k < series.length; k++) {
            StringBuilder pontos = new StringBuilder();
            for (int i = 0; i < tamanhos.length; i++) {
                Main.Resumo r = buscar(resumos, algoritmo, tipo, tamanhos[i], series[k]);
                if (r == null) continue;
                int x = ESQ + (tamanhos.length == 1 ? w / 2 : i * w / (tamanhos.length - 1));
                int y = TOPO + h - (int) Math.round(h * r.media / max);
                pontos.append(x).append(',').append(y).append(' ');
                s.append("<circle cx=\"").append(x).append("\" cy=\"").append(y).append("\" r=\"4\" fill=\"")
                        .append(CORES[k % CORES.length]).append("\"/>\n");
            }
            s.append("<polyline fill=\"none\" stroke-width=\"2.5\" stroke=\"").append(CORES[k % CORES.length])
                    .append("\" points=\"").append(pontos.toString().trim()).append("\"/>\n");
            legenda(s, k, series[k] == 1 ? "Serial" : series[k] + " threads");
        }
        return s.append("</svg>\n").toString();
    }

    static String graficoSpeedup(List<Main.Resumo> resumos, String tipo) {
        int tamanho = Main.TAMANHOS[Main.TAMANHOS.length - 1];
        double max = 1;
        for (Main.Resumo r : resumos) {
            if (r.tipo.equals(tipo) && r.tamanho == tamanho) {
                max = Math.max(max, r.speedup);
            }
        }
        max = escala(max);

        int w = LARGURA - ESQ - DIR;
        int h = ALTURA - TOPO - BASE;
        StringBuilder s = inicio("Speedup com n = " + tamanho + " - vetor " + tipo);
        eixoY(s, max, "Speedup (serial / paralelo)");

        // Linha de referencia em 1: abaixo dela o paralelo foi mais lento
        int y1 = TOPO + h - (int) Math.round(h * 1.0 / max);
        s.append("<line x1=\"").append(ESQ).append("\" y1=\"").append(y1).append("\" x2=\"").append(ESQ + w)
                .append("\" y2=\"").append(y1).append("\" stroke=\"#444\" stroke-dasharray=\"6 4\"/>\n");

        int grupos = Main.ALGORITMOS.length;
        int larguraGrupo = w / grupos;
        int barras = Main.THREADS.length;
        int larguraBarra = (larguraGrupo - 24) / barras;
        for (int g = 0; g < grupos; g++) {
            int x0 = ESQ + g * larguraGrupo + 12;
            for (int b = 0; b < barras; b++) {
                Main.Resumo r = buscar(resumos, Main.ALGORITMOS[g], tipo, tamanho, Main.THREADS[b]);
                if (r == null) continue;
                int alt = (int) Math.round(h * r.speedup / max);
                int x = x0 + b * larguraBarra;
                s.append("<rect x=\"").append(x).append("\" y=\"").append(TOPO + h - alt).append("\" width=\"")
                        .append(larguraBarra - 3).append("\" height=\"").append(alt).append("\" fill=\"")
                        .append(CORES[(b + 1) % CORES.length]).append("\"/>\n");
            }
            s.append(texto(x0 + (larguraGrupo - 24) / 2, TOPO + h + 20, Main.ALGORITMOS[g], "middle", 12));
        }
        for (int b = 0; b < barras; b++) {
            legenda(s, b, Main.THREADS[b] + " threads", CORES[(b + 1) % CORES.length]);
        }
        return s.append("</svg>\n").toString();
    }

    static StringBuilder inicio(String titulo) {
        StringBuilder s = new StringBuilder();
        s.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        s.append("<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"").append(LARGURA).append("\" height=\"")
                .append(ALTURA).append("\" font-family=\"Arial, sans-serif\">\n");
        s.append("<rect width=\"100%\" height=\"100%\" fill=\"white\"/>\n");
        s.append(texto(LARGURA / 2, 28, titulo, "middle", 16));
        return s;
    }

    static void eixoY(StringBuilder s, double max, String rotulo) {
        int w = LARGURA - ESQ - DIR;
        int h = ALTURA - TOPO - BASE;
        for (int i = 0; i <= 5; i++) {
            double v = max * i / 5;
            int y = TOPO + h - h * i / 5;
            s.append("<line x1=\"").append(ESQ).append("\" y1=\"").append(y).append("\" x2=\"").append(ESQ + w)
                    .append("\" y2=\"").append(y).append("\" stroke=\"#e3e3e3\"/>\n");
            s.append(texto(ESQ - 8, y + 4, numero(v), "end", 11));
        }
        s.append("<line x1=\"").append(ESQ).append("\" y1=\"").append(TOPO).append("\" x2=\"").append(ESQ)
                .append("\" y2=\"").append(TOPO + h).append("\" stroke=\"#333\"/>\n");
        s.append("<line x1=\"").append(ESQ).append("\" y1=\"").append(TOPO + h).append("\" x2=\"").append(ESQ + w)
                .append("\" y2=\"").append(TOPO + h).append("\" stroke=\"#333\"/>\n");
        int yMeio = TOPO + h / 2;
        s.append("<text x=\"20\" y=\"").append(yMeio).append("\" font-size=\"13\" text-anchor=\"middle\" transform=\"rotate(-90 20 ")
                .append(yMeio).append(")\">").append(rotulo).append("</text>\n");
    }

    static void legenda(StringBuilder s, int i, String nome) {
        legenda(s, i, nome, CORES[i % CORES.length]);
    }

    static void legenda(StringBuilder s, int i, String nome, String cor) {
        int x = LARGURA - DIR + 20;
        int y = TOPO + 10 + i * 24;
        s.append("<rect x=\"").append(x).append("\" y=\"").append(y - 9).append("\" width=\"18\" height=\"10\" fill=\"")
                .append(cor).append("\"/>\n");
        s.append(texto(x + 26, y, nome, "start", 12));
    }

    static String texto(int x, int y, String t, String ancora, int tamanho) {
        return "<text x=\"" + x + "\" y=\"" + y + "\" font-size=\"" + tamanho + "\" text-anchor=\"" + ancora + "\">"
                + t.replace("&", "&amp;").replace("<", "&lt;") + "</text>\n";
    }

    static Main.Resumo buscar(List<Main.Resumo> resumos, String algoritmo, String tipo, int tamanho, int threads) {
        for (Main.Resumo r : resumos) {
            if (r.algoritmo.equals(algoritmo) && r.tipo.equals(tipo) && r.tamanho == tamanho && r.threads == threads) {
                return r;
            }
        }
        return null;
    }

    // Arredonda o topo do eixo para um valor "redondo"
    static double escala(double max) {
        if (max <= 0) return 1;
        double alvo = max * 1.1;
        double potencia = Math.pow(10, Math.floor(Math.log10(alvo)));
        double[] passos = {1, 1.2, 1.5, 2, 2.5, 3, 4, 5, 6, 8, 10};
        for (double p : passos) {
            if (p * potencia >= alvo) return p * potencia;
        }
        return 10 * potencia;
    }

    static String numero(double v) {
        if (v >= 100) return String.format(Locale.US, "%.0f", v);
        if (v >= 10) return String.format(Locale.US, "%.1f", v);
        return String.format(Locale.US, "%.2f", v);
    }

    static String slug(String algoritmo) {
        return algoritmo.toLowerCase(Locale.ROOT).replace(" sort", "");
    }

    static void salvar(File arquivo, String conteudo) throws IOException {
        PrintWriter w = new PrintWriter(new OutputStreamWriter(new FileOutputStream(arquivo), StandardCharsets.UTF_8));
        w.print(conteudo);
        w.close();
    }
}
