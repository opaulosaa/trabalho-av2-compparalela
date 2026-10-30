package trabalho;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

// Quick Sort paralelo com ForkJoin. Depois de particionar, os dois lados
// viram tarefas separadas. Trechos pequenos voltam para o Quick Sort serial.
public class QuicksortParalelo {

    public static void main(String[] args) {
        Random rand = new Random();
        int[] array = new int[10];
        for (int i = 0; i < array.length; i++) {
            array[i] = rand.nextInt(100);
        }
        System.out.println("Antes: " + Arrays.toString(array));
        ordenar(array, Runtime.getRuntime().availableProcessors());
        System.out.println("Depois: " + Arrays.toString(array));
    }

    public static void ordenar(int[] array, int numThreads) {
        ForkJoinPool pool = new ForkJoinPool(numThreads);
        try {
            pool.invoke(new QuicksortTask(array, 0, array.length - 1));
        } finally {
            pool.shutdown();
        }
    }

    public static class QuicksortTask extends RecursiveAction {
        // Abaixo desse tamanho nao compensa criar tarefa nova
        private static final int LIMITE = 1000;

        private int[] array;
        private int lowIndex;
        private int highIndex;

        QuicksortTask(int[] array, int lowIndex, int highIndex) {
            this.array = array;
            this.lowIndex = lowIndex;
            this.highIndex = highIndex;
        }

        @Override
        protected void compute() {
            if (lowIndex >= highIndex) {
                return;
            }
            if (highIndex - lowIndex + 1 <= LIMITE) {
                Quicksort.quickSort(array, lowIndex, highIndex);
                return;
            }
            int indicePivo = Quicksort.particionar(array, lowIndex, highIndex);
            QuicksortTask quickSort = new QuicksortTask(array, lowIndex, indicePivo - 1);
            QuicksortTask quickSort2 = new QuicksortTask(array, indicePivo + 1, highIndex);
            invokeAll(quickSort, quickSort2);
        }
    }
}
