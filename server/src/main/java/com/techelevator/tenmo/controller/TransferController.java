package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.JdbcTransferDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

@RestController
public class TransferController {

    private TransferDao dao;

    public TransferController(TransferDao dao) {
        this.dao = dao;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "/transfers", method = RequestMethod.POST)
    public Transfer add(@RequestBody Transfer transfer) {
        return dao.createTransferSend(transfer);
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

//    @RequestMapping(path = "/transfers/users/{id}", method = RequestMethod.GET)
//    public List<Transfer> listTransfersByUserId()

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
