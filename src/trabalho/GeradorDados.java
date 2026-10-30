package trabalho;

import java.util.Random;

// Monta os vetores de entrada usados nos testes.
public class GeradorDados {

    public static final String ALEATORIO = "aleatorio";
    public static final String ORDENADO = "ordenado";
    public static final String DECRESCENTE = "decrescente";

    public static int[] gerar(String tipo, int tamanho, long semente) {
        int[] vetor = new int[tamanho];
        if (ORDENADO.equals(tipo)) {
            for (int i = 0; i < tamanho; i++) {
                vetor[i] = i + 1;
            }
        } else if (DECRESCENTE.equals(tipo)) {
            for (int i = 0; i < tamanho; i++) {
                vetor[i] = tamanho - i;
            }
        } else {
            Random rand = new Random(semente);
            for (int i = 0; i < tamanho; i++) {
                vetor[i] = rand.nextInt(1000000);
            }
        }
        return vetor;
    }
}
