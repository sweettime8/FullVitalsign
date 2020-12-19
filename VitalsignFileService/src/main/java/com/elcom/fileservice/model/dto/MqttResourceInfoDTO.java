package com.elcom.fileservice.model.dto;

/**
 *
 * @author anhdv
 */
public class MqttResourceInfoDTO {
    
    private String name;
    private String protocol;
    private String ipAddress;
    private String portNumber;
    private String account;
    private String password;

    public MqttResourceInfoDTO() {
    }
    
    public MqttResourceInfoDTO(String name, String protocol, String ipAddress, String portNumber, String account, String password) {
        this.name = name;
        this.protocol = protocol;
        this.ipAddress = ipAddress;
        this.portNumber = portNumber;
        this.account = account;
        this.password = password;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the protocol
     */
    public String getProtocol() {
        return protocol;
    }

    /**
     * @param protocol the protocol to set
     */
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    /**
     * @return the ipAddress
     */
    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * @param ipAddress the ipAddress to set
     */
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    /**
     * @return the portNumber
     */
    public String getPortNumber() {
        return portNumber;
    }

    /**
     * @param portNumber the portNumber to set
     */
    public void setPortNumber(String portNumber) {
        this.portNumber = portNumber;
    }

    /**
     * @return the account
     */
    public String getAccount() {
        return account;
    }

    /**
     * @param account the account to set
     */
    public void setAccount(String account) {
        this.account = account;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }
    
}
