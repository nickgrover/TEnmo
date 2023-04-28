package com.techelevator.tenmo.model;

import java.time.LocalDate;

public class Transfer {

    private int transferId;
    private LocalDate transferDate;
    private double transferAmount;
    private String toUser;
    private String fromUser;
    private String status;
    private String type;

    public Transfer(){}

    public Transfer(int transferId, LocalDate transferDate, double transferAmount, String toUser, String fromUser, String status, String type) {
        this.transferId = transferId;
        this.transferDate = transferDate;
        this.transferAmount = transferAmount;
        this.toUser = toUser;
        this.fromUser = fromUser;
        this.status = status;
        this.type = type;
    }


    public int getTransferId() {
        return transferId;
    }

    public void setTransferId(int transferId) {
        this.transferId = transferId;
    }

    public LocalDate getTransferDate() {
        return transferDate;
    }

    public void setTransferDate(LocalDate transferDate) {
        this.transferDate = transferDate;
    }

    public double getTransferAmount() {
        return transferAmount;
    }

    public void setTransferAmount(double transferAmount) {
        this.transferAmount = transferAmount;
    }

    public String getToUser() {
        return toUser;
    }

    public void setToUser(String toUser) {
        this.toUser = toUser;
    }

    public String getFromUser() {
        return fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
