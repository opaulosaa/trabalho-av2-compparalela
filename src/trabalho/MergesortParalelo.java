package trabalho;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

// Merge Sort paralelo com ForkJoin. As duas metades sao ordenadas ao mesmo
// tempo e a juncao acontece depois que as duas terminam.
public class MergesortParalelo {

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
            pool.invoke(new MergeSortTask(array, 0, array.length - 1));
        } finally {
            pool.shutdown();
        }
    }

    static class MergeSortTask extends RecursiveAction {
        // Abaixo desse tamanho o trecho e ordenado sem criar novas tarefas
        private static final int LIMITE = 1000;

        private int[] array;
        private int left;
        private int right;

        MergeSortTask(int[] array, int left, int right) {
            this.array = array;
            this.left = left;
            this.right = right;
        }

        @Override
        protected void compute() {
            if (left >= right) {
                return;
            }
            if (right - left + 1 <= LIMITE) {
                ordenarTrecho(array, left, right);
                return;
            }
            int mid = (left + right) / 2;

            MergeSortTask leftTask = new MergeSortTask(array, left, mid);
            MergeSortTask rightTask = new MergeSortTask(array, mid + 1, right);

            invokeAll(leftTask, rightTask);

            merge(array, left, mid, right);
        }

        // Mesma logica, mas sem tarefas (usado nos trechos pequenos)
        private void ordenarTrecho(int[] array, int left, int right) {
            if (left >= right) {
                return;
            }
            int mid = (left + right) / 2;
            ordenarTrecho(array, left, mid);
            ordenarTrecho(array, mid + 1, right);
            merge(array, left, mid, right);
        }

        private void merge(int[] array, int left, int mid, int right) {
            int n1 = mid - left + 1;
            int n2 = right - mid;

            int[] L = new int[n1];
            int[] R = new int[n2];

            System.arraycopy(array, left, L, 0, n1);
            System.arraycopy(array, mid + 1, R, 0, n2);

            int i = 0, j = 0, k = left;
            while (i < n1 && j < n2) {
                if (L[i] <= R[j]) {
                    array[k] = L[i];
                    i++;
                } else {
                    array[k] = R[j];
                    j++;
                }
                k++;
            }
            while (i < n1) {
                array[k] = L[i];
                i++;
                k++;
            }
            while (j < n2) {
                array[k] = R[j];
                j++;
                k++;
            }
        }
    }
}
