package trabalho;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

// Selection Sort paralelo. O vetor e dividido ao meio com ForkJoin ate os blocos
// ficarem do tamanho n / numThreads. Cada bloco e ordenado com o Selection Sort
// serial e depois os blocos sao juntados com merge, do mesmo jeito do Merge Sort.
public class SelectionSortParalelo {

    public static void main(String[] args) {
        Random rand = new Random();
        int[] array = new int[10];
        for (int i = 0; i < array.length; i++) {
            array[i] = rand.nextInt(100);
        }
        System.out.println("Antes: " + Arrays.toString(array));
        ordenar(array, 2);
        System.out.println("Depois: " + Arrays.toString(array));
    }

    public static void ordenar(int[] array, int numThreads) {
        if (array.length < 2) {
            return;
        }
        // Um bloco por thread
        int tamanhoBloco = (array.length + numThreads - 1) / numThreads;
        ForkJoinPool pool = new ForkJoinPool(numThreads);
        try {
            pool.invoke(new SelectionSortTask(array, 0, array.length - 1, tamanhoBloco));
        } finally {
            pool.shutdown();
        }
    }

    public static class SelectionSortTask extends RecursiveAction {
        private int[] array;
        private int lowIndex;
        private int highIndex;
        private int tamanhoBloco;

        SelectionSortTask(int[] array, int lowIndex, int highIndex, int tamanhoBloco) {
            this.array = array;
            this.lowIndex = lowIndex;
            this.highIndex = highIndex;
            this.tamanhoBloco = tamanhoBloco;
        }

        @Override
        protected void compute() {
            if (lowIndex >= highIndex) {
                return;
            }

            if (highIndex - lowIndex + 1 <= tamanhoBloco) {
                SelectionSort.selectionSort(array, lowIndex, highIndex);
                return;
            }

            int mid = (lowIndex + highIndex) / 2;

            SelectionSortTask leftTask = new SelectionSortTask(array, lowIndex, mid, tamanhoBloco);
            SelectionSortTask rightTask = new SelectionSortTask(array, mid + 1, highIndex, tamanhoBloco);

            invokeAll(leftTask, rightTask);

            merge(array, lowIndex, mid, highIndex);
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
