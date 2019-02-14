/*  This is a class which defines state channel.
    It containes parameter such as data and a boolean
    value which tells whether send can be done or not.
    Initially send is true and when a send is made it is
    set to false.
*/

public class StateChannel{

    private int data;
    private boolean sendState = true;

    StateChannel(){}


    public void setData(int data){
        this.data = data;
    }

    public void setSendState(boolean state){
        this.sendState = state;
    }

    public int getData(){
        return this.data;
    }

    public boolean isSendFree(){
        return this.sendState;
    }
}