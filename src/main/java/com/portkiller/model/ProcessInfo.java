package com.portkiller.model;

public class ProcessInfo {
    private String protocol;
    private String localAddress;
    private String foreignAddress;
    private String state;
    private String pid;
    private String processName;

    public ProcessInfo(String protocol, String localAddress, String foreignAddress,
                       String state, String pid) {
        this.protocol = protocol;
        this.localAddress = localAddress;
        this.foreignAddress = foreignAddress;
        this.state = state;
        this.pid = pid;
        this.processName = "Loading...";
    }

    // Getters and Setters
    public String getProtocol() { return protocol; }
    public void setProtocol(String protocol) { this.protocol = protocol; }

    public String getLocalAddress() { return localAddress; }
    public void setLocalAddress(String localAddress) { this.localAddress = localAddress; }

    public String getForeignAddress() { return foreignAddress; }
    public void setForeignAddress(String foreignAddress) { this.foreignAddress = foreignAddress; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getPid() { return pid; }
    public void setPid(String pid) { this.pid = pid; }

    public String getProcessName() { return processName; }
    public void setProcessName(String processName) { this.processName = processName; }

    public String getPort() {
        String[] parts = localAddress.split(":");
        return parts[parts.length - 1];
    }

    public Object[] toTableRow() {
        return new Object[]{protocol, localAddress, foreignAddress, state, pid, processName, "Kill"};
    }

    @Override
    public String toString() {
        return String.format("ProcessInfo[pid=%s, process=%s, port=%s, state=%s]",
                pid, processName, getPort(), state);
    }
}