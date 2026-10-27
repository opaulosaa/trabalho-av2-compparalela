import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class QuicksortParalelo {
    
    public static void main(String[] args) {
        
        
    
    Random rand = new Random();
    int[] array = new int[10];
    for (int i = 0; i < array.length; i++) {
        array[i] = rand.nextInt(100);
    }
    System.out.println("Antes: " + Arrays.toString(array));
    ForkJoinPool.commonPool().invoke(new QuicksortTask(array, 0, array.length - 1));
    System.out.println("Depois: " + Arrays.toString(array));
    
    
    

    }   

public static class QuicksortTask extends RecursiveAction {
    private int[] array;
    private int lowIndex;
    private int highIndex;

    QuicksortTask(int[] array, int lowIndex, int highIndex){
        this.array = array;
        this.lowIndex = lowIndex;
        this.highIndex = highIndex;
    }
@Override 
protected void compute(){
    
    if (lowIndex >= highIndex) {
        return;
    }
    Random rand = new Random();
    int randa = rand.nextInt(highIndex - lowIndex + 1) + lowIndex;
    swap(array, randa, highIndex);
    int pivo = array[highIndex];
    int ponteiroEsq = lowIndex;
    int ponteiroDir = highIndex;
    
    while (ponteiroEsq < ponteiroDir) {
        while (array[ponteiroEsq] <= pivo && ponteiroEsq < ponteiroDir) {
            ponteiroEsq++;
        }
        while (array[ponteiroDir] >= pivo && ponteiroEsq < ponteiroDir) {
            ponteiroDir--;
        }
        swap(array, ponteiroEsq, ponteiroDir);
    }
    swap(array, ponteiroEsq, highIndex);
    QuicksortTask quickSort = new QuicksortTask(array, lowIndex, ponteiroEsq - 1);
    QuicksortTask quickSort2 = new QuicksortTask(array, ponteiroDir + 1, highIndex);
    invokeAll(quickSort, quickSort2);


}
public static void swap(int[] array, int index1, int index2){
    int temp = array[index1];
    array[index1] = array[index2];
    array[index2] = temp;

        }
    }
}