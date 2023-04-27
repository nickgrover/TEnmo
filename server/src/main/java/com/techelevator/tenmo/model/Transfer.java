package com.techelevator.tenmo.model;

import java.time.LocalDate;

public class Transfer {

    private int transferId;
    private LocalDate transferDate;
    private double transferAmount;
    private int toAccount;
    private int fromAccount;
    private String status;

    public Transfer(){}

    public Transfer(int transferId, LocalDate transferDate, double transferAmount, int toAccount, int fromAccount, String status) {
        this.transferId = transferId;
        this.transferDate = transferDate;
        this.transferAmount = transferAmount;
        this.toAccount = toAccount;
        this.fromAccount = fromAccount;
        this.status = status;
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

    public int getToAccount() {
        return toAccount;
    }

    public void setToAccount(int toAccount) {
        this.toAccount = toAccount;
    }

    public int getFromAccount() {
        return fromAccount;
    }

    public void setFromAccount(int fromAccount) {
        this.fromAccount = fromAccount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
