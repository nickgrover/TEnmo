package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

import java.security.Principal;
import java.util.List;

public interface TransferDao {

    //create
    Transfer createTransferSend(Transfer transfer, Principal principal);
    Transfer createTransferRequest(Transfer transfer, Principal principal);
//    Transfer createTransferRequest(Transfer transfer);

    //read -potentially use the commented out methods to differentiate
    List<Transfer> getAllTransfers();
    Transfer getTransferByTransferId(int transferId);

//    List<Transfer> getTransferByFromAccountId(int fromAccountId);

//    List<Transfer> getTransferByToAccountId(int toAccountId);


    List<Transfer> getTransferByUsername(String name);
    List<Transfer> getRequestsByUsername(String name);

//    List<Transfer> getTransferByToUserId(int userId);


    //update
    Transfer updateTransfer(Transfer transfer, int id);
    Transfer updateRequest(Transfer transfer, int id);


    //delete
    void deleteTransfer(int id);
}
