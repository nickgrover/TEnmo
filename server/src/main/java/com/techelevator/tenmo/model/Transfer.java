package com.techelevator.tenmo.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;

public class Transfer {

    private int transferId;
    @NotNull(message = "transfer date is required")
    private LocalDate transferDate;
    @Positive(message = "Transfer amount must be greater than 0")
    private double transferAmount;
    @NotBlank(message = "The to-user must not be blank")
    private String toUser;
    @NotBlank(message = "The from-user must not be blank")
    private String fromUser;
    @NotBlank(message = "The status must not be blank")
    private String status;
    @NotBlank(message = "The type must not be blank")
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
