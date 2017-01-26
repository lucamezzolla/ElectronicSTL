package aero.urbe.electronicstl.MyClasses;

/**
 *
 * @author Luca Mezzolla
 */
public class SimulatorStatusItem {
    
    private int id = -1;
    private int simulatorId;
    private String simulator;
    private int simulatorStatusId;
    private String simulatorStatus;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSimulatorId() {
        return simulatorId;
    }

    public void setSimulatorId(int simulatorId) {
        this.simulatorId = simulatorId;
    }

    public String getSimulator() {
        return simulator;
    }

    public void setSimulator(String simulator) {
        this.simulator = simulator;
    }

    public int getSimulatorStatusId() {
        return simulatorStatusId;
    }

    public void setSimulatorStatusId(int simulatorStatusId) {
        this.simulatorStatusId = simulatorStatusId;
    }

    public String getSimulatorStatus() {
        return simulatorStatus;
    }

    public void setSimulatorStatus(String simulatorStatus) {
        this.simulatorStatus = simulatorStatus;
    }
    
    
    
}
