import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class AlternativeSort{

    private int numOfProcess;
    private Process[] processes;


    /*  This function creates input number of processes with random numbers.
        Each process has been assigned with an id which is its position + 1.
    */
    public void createProcess(){

       this.processes = new Process[this.numOfProcess];

       Random rand = new Random();

        for(int i=0;i<this.numOfProcess;i++){
            processes[i] = new Process();
            processes[i].setId(i+1);
            processes[i].setData(rand.nextInt(this.numOfProcess * this.numOfProcess)+1);
            System.out.print(processes[i].getData() + " ");
        }
        System.out.println();
    }

    /*  This function is simulating a send event with arguments sendProcess
        and receiveProcess of type Process, with data and flag.
        Flag tells whether data is send is to the right or left, corresponding to the value 1 or 2,
        of the process, since the processes are on a lined network.
        Here, flag is just a parameter to differentiate and should not be confused with
        flag term in the algorithm. 
    */
    public void send(Process sendProcess, Process receiveProcess, int data, int flag){

        if(flag == 1){
            receiveProcess.getLeftStateChannel().setData(data);
            sendProcess.getRightStateChannel().setSendState(false);
        }else{
            receiveProcess.getRightStateChannel().setData(data);
            sendProcess.getLeftStateChannel().setSendState(false);
        }
    }

    /*  This function is simulating a receive event with arguments Processand flag.
        Flag tells whether data is received is to the left or right, corresponding to the value 1 or 2,
        of the process, since the processes are on a lined network.
        Here, flag is just a parameter to differentiate and should not be confused with
        flag term in the algorithm. 
    */
    public void receive(Process process, int flag){
        if(flag == 1){
            process.setData(process.getLeftStateChannel().getData());
        }else{
            process.setData(process.getRightStateChannel().getData());
        }
    }

    /*  This function simulates a local computation.
        Here, the process takes left and right value if sent to it,
        and find the min and max by comparing both the values with its value.
        It sets the middle value to itself.
        It returns a list with two values min and max.
    */
    public ArrayList<Integer> localComputation(Process process, int leftValue, int rightValue){

        ArrayList<Integer> arrayList = new ArrayList<>();

        if(leftValue != -1 && rightValue != -1){
            arrayList.add(leftValue);
            arrayList.add(rightValue);
            arrayList.add(process.getData());
            Collections.sort(arrayList);
            process.setData(arrayList.get(1));
            arrayList.remove(1);
        }else if(leftValue != -1){
            if(leftValue > process.getData()){
                arrayList.add(process.getData());
                process.setData(leftValue);
            }else{
                arrayList.add(leftValue);
            }
        }else{
            if(rightValue < process.getData()){
                arrayList.add(rightValue);
                arrayList.add(process.getData());
                process.setData(rightValue);
            }else{
                arrayList.add(process.getData());
                arrayList.add(rightValue);
            }
        }
        return arrayList;

    }

    /*  This function simulates the receive event done by process with
        flag 0 or 2. It takes the data from left channel and right channel
        and calls the local computation function.
    */
    public ArrayList<Integer> receiveData(Process process){

        int leftTmp = -1;
        int rightTmp = -1;
        if(process.getLeftStateChannel() != null){
            leftTmp = process.getLeftStateChannel().getData();
        }
        if(process.getRightStateChannel() != null){
            rightTmp = process.getRightStateChannel().getData();
        }

        return localComputation(process, leftTmp, rightTmp);

    }

    /*  This process creates a thread for each process.
        If the flag of a process is 0 or one only one thread is created,
        else a thread with a left and right thread is created to receive
        and send from and to left and right process.
        Threads are join so that main thread waits for all the threads to finish for
        a round and then goes for next round.
    */
    public void createThreads(){

        System.out.println("\nPrinting states of each round...");

        for(int i=1; i<this.numOfProcess; i++){

            ArrayList<Thread> threads = new ArrayList<>();

            for(int j=0; j<this.numOfProcess;j++){

                final int index = j;
            
                processes[index].setFlag((((i-1)*2)+j)%3);
                
                if(processes[index].getFlag() == 0){
                    if(index < this.numOfProcess-1){
                        processes[index].setRightStateChannel(new StateChannel());
                    }
                }else if(processes[index].getFlag() == 1){
                    if(index > 0){
                        processes[index].setLeftStateChannel(new StateChannel());
                    }
                    if(index < this.numOfProcess-1){
                        processes[index].setRightStateChannel(new StateChannel());
                    }
                }else{
                    if(index > 0){
                        processes[index].setLeftStateChannel(new StateChannel());
                    }
                }

                Process processes[] = this.processes;
                int numOfProcess = this.numOfProcess;

                Thread thread = new Thread(new CustomRunnable(processes, numOfProcess){
                
                    @Override
                    public void run() {

                        if(processes[index].getFlag() == 0){
                            if(processes[index].getRightStateChannel() != null){
                                
                                send(processes[index], processes[index+1], processes[index].getData(), 1);

                                synchronized(processes[index+1].getLeftStateChannel()){
                                    processes[index+1].getLeftStateChannel().notify();
                                }

                                synchronized(processes[index].getRightStateChannel()){
                                    if(processes[index+1].getLeftStateChannel().isSendFree()){
                                        try {
                                            processes[index].getRightStateChannel().wait();
                                        } catch (Exception e) {
                                            
                                        }
                                    }
                                    receive(processes[index], 2);
                                }

                            }
                        }else if(processes[index].getFlag() == 2){
                            if(processes[index].getLeftStateChannel() != null){
                                
                                send(processes[index], processes[index-1], processes[index].getData(), 2);

                                synchronized(processes[index-1].getRightStateChannel()){
                                    processes[index-1].getRightStateChannel().notify();
                                }

                                synchronized(processes[index].getLeftStateChannel()){
                                    if(processes[index-1].getRightStateChannel().isSendFree()){
                                        try {
                                            processes[index].getLeftStateChannel().wait();
                                        } catch (Exception e) {
                                            
                                        }
                                    }
                                    receive(processes[index], 1);
                                }
                                

                            }
                        }else{

                            Thread leftChannelThread = null;
                            Thread rightChannelThread = null;

                            if(processes[index].getLeftStateChannel() != null){

                                leftChannelThread = new Thread(new CustomRunnable(processes, numOfProcess){
                                
                                    @Override
                                    public void run() {
                                        synchronized(processes[index].getLeftStateChannel()){
                                            if(processes[index-1].getRightStateChannel().isSendFree()){
                                                try {
                                                    processes[index].getLeftStateChannel().wait();
                                                } catch (Exception e) {
                                                }
                                            }
                                        }
                                    }
                                });

                                leftChannelThread.start();
                                
                            }
                            if(processes[index].getRightStateChannel() != null){

                                rightChannelThread = new Thread(new CustomRunnable(processes, numOfProcess){
                                
                                    @Override
                                    public void run() {
                                        synchronized(processes[index].getRightStateChannel()){
                                            if(processes[index+1].getLeftStateChannel().isSendFree()){
                                                try {
                                                    processes[index].getRightStateChannel().wait();
                                                } catch (Exception e) {
                                                
                                                }
                                            }
                                        }
                                    }
                                });

                                rightChannelThread.start();
                                
                            }
                            
                            if(leftChannelThread != null){
                                try {
                                    leftChannelThread.join();
                                } catch (Exception e) {
                                }
                            }
                            if(rightChannelThread != null){
                                try {
                                    rightChannelThread.join();
                                } catch (Exception e) {
                                }
                            }

                            ArrayList<Integer> array = receiveData(processes[index]);
                            if(leftChannelThread != null){
                                send(processes[index], processes[index-1], array.get(0), 2);
                                synchronized(processes[index-1].getRightStateChannel()){
                                    processes[index-1].getRightStateChannel().notify();
                                }
                            }
                            if(rightChannelThread != null){
                                send(processes[index], processes[index+1], array.get(1), 1);
                                synchronized(processes[index+1].getLeftStateChannel()){
                                    processes[index+1].getLeftStateChannel().notify();
                                }
                            }

                            leftChannelThread = null;
                            rightChannelThread = null;

                        }
                        
                    }
                });
                threads.add(thread);
            }

            for(Thread thread: threads){
                thread.start();
            }

            for(Thread thread: threads){
                try {
                    thread.join();
                } catch (Exception e) {
                }
            }

            threads = null;

            printState();

            if(i+1 == this.numOfProcess){
                break;
            }

        }
    }

    /*  This function just prints the corresponding values of every process.*/
    public void printState(){

        for(int i=0;i<this.numOfProcess;i++){
            System.out.print(processes[i].getData() + " ");
        }
        System.out.println();
    }

    /*  This is the main function where the execution of program starts.
        It takes input number of processes.
        It first creates the object of type AlternativeSort and sets the number
        of processes.
        Then functions createProcess, createThreads and printState are called
        one by one.
    */

    public static void main(String args[])throws IOException{

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        AlternativeSort aSort = new AlternativeSort();

        System.out.println("Enter the number of process.");
        try {
            aSort.numOfProcess = Integer.parseInt(br.readLine());
        } catch (Exception e) {
            System.out.println("Not a number");
        }

        System.out.println();

        System.out.printf("Generating %d processes with random numbers...", aSort.numOfProcess);
        System.out.println();

        long startTime = System.nanoTime();

        aSort.createProcess();

        System.out.println("\nCreating threads for each process...");

        aSort.createThreads();

        System.out.println("\nFinal sorted list of numbers...");

        aSort.printState();

        aSort = null;

        long endTime = System.nanoTime();

        System.out.printf("\nSorted in %.3f milliseconds", (float)((endTime - startTime)/1000000.0));
        System.out.println();

        return;

    }
}