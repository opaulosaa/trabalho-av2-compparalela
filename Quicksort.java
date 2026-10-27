import java.util.Arrays;
import java.util.Random;

public class Quicksort{



public static void main(String[] args) {
 
    Random rand = new Random();
    int[] numeros = new int[10];
    for (int i = 0; i < numeros.length; i++) {
        numeros[i] = rand.nextInt(100);
    }
    System.out.println(Arrays.toString(numeros));
    quickSort(numeros, 0, numeros.length - 1);
    System.out.println(Arrays.toString(numeros));



}
public static void quickSort(int[] array, int lowIndex, int highIndex){
    if ( lowIndex >= highIndex){
        return;
    }
    Random rand = new Random();
    int randa = rand.nextInt(highIndex - lowIndex + 1) + lowIndex;
    swap(array, randa, highIndex);
    int pivo = array[highIndex];
    int leftPointer = lowIndex;
    int rightPointer = highIndex;

    while (leftPointer < rightPointer){

        while (leftPointer < rightPointer && array[leftPointer] <= pivo) {
            leftPointer++;
        }
        while (leftPointer < rightPointer && array[rightPointer] >= pivo){
            rightPointer--;
        }
        swap(array, leftPointer, rightPointer);

    }
    swap(array, leftPointer, highIndex);
    quickSort(array, lowIndex, leftPointer - 1);
    quickSort(array, rightPointer + 1, highIndex);


}
private static void swap(int[] array, int index1, int index2){
    int temp = array[index1];
    array[index1] = array[index2];
    array[index2] = temp;
}


}