package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

import java.util.List;

public interface TransferDao {

    //create
    Transfer createTransfer(Transfer transfer);

    //read -potentially use the commented out methods to differentiate
    List<Transfer> getAllTransfers();

    Transfer getTransferByTransferId(int transferId);

    List<Transfer> getTransferByFromAccountId(int fromAccountId);

//    List<Transfer> getTransferByToAccountId(int toAccountId);


    List<Transfer> getTransferByFromUserId(int userId);

//    List<Transfer> getTransferByToUserId(int userId);


    //update
    Transfer updateTransfer(Transfer transfer, int id);


    //delete
    void deleteTransfer(int id);
}
