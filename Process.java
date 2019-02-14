/*  This class defines the Process.
    It containes arguments such as id, data, flag.
    Each process also has left and right state channels, which is
    set to null, initially.
*/
public class Process{

    private int id;
    private int data;
    private int flag = 0;
    private StateChannel leftSC = null;
    private StateChannel rightSC = null;

    Process(){}

    Process(int data){
        this.data = data;
    }

    public void setId(int id){
        this.id = id;
    }

    public void setData(int data){
        this.data = data;
    }

    public void setFlag(int flag){
        this.flag = flag;
    }

    public void setLeftStateChannel(StateChannel stateChannel){
        this.leftSC = stateChannel;
    }

    public void setRightStateChannel(StateChannel stateChannel){
        this.rightSC = stateChannel;
    }
    
    public StateChannel getLeftStateChannel(){
        return this.leftSC;
    }

    public StateChannel getRightStateChannel(){
        return this.rightSC;
    }

    public int getData(){
        return this.data;
    }

    public int getId(){
        return this.id;
    }

    public int getFlag(){
        return this.flag;
    }

}