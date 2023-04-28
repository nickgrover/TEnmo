package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.JdbcTransferDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
public class TransferController {

    private TransferDao dao;
    private UserDao userDao;

    public TransferController(TransferDao dao, UserDao userDao) {
        this.dao = dao;
        this.userDao = userDao;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "/transfers", method = RequestMethod.POST)
    public Transfer add(@RequestBody Transfer transfer) {
        Transfer transferNew = dao.createTransferSend(transfer);
        if (transferNew == null) {
            throw new ResponseStatusException(HttpStatus.I_AM_A_TEAPOT, "Insufficient funds.");
        } else {
            return transferNew;
        }
    }

    @RequestMapping(path = "/transfers", method = RequestMethod.GET)
    public List<Transfer> listTransfers() {
        return dao.getAllTransfers();
    }

    @RequestMapping(path = "/transfers/{id}", method = RequestMethod.GET)
    public Transfer listTransferByTransferId(@PathVariable int id) {
        Transfer transfer = dao.getTransferByTransferId(id);
        if (transfer == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found.");
        } else {
            return transfer;
        }
    }

//    @RequestMapping(path = "/transfers/accounts/{id}", method = RequestMethod.GET)
//    public List<Transfer> listTransfersByAccountId

    @RequestMapping(path = "/transfers/users", method = RequestMethod.GET)
    public List<Transfer> listTransfersByUsername(@AuthenticationPrincipal User user){
        List<Transfer> transferList = dao.getTransferByUsername(user.getUsername());
        if (transferList == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found.");
        } else {
            return transferList;
        }
    }

    @RequestMapping(path = "/transfers/{id}", method = RequestMethod.PUT)
    public Transfer update(@RequestBody Transfer transfer, @PathVariable int id) {
        Transfer updatedTransfer = dao.updateTransfer(transfer, id);
        if (updatedTransfer == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transfer not found.");
        } else {
            return updatedTransfer;
        }
    }







}
