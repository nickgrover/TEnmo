package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.JdbcTransferDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
public class TransferController {

    private TransferDao dao;
    private UserDao userDao;
    private AccountDao accountDao;

    public TransferController(TransferDao dao, UserDao userDao, AccountDao accountDao) {
        this.dao = dao;
        this.userDao = userDao;
        this.accountDao = accountDao;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "/transfers", method = RequestMethod.POST)
    public Transfer addSend(@Valid @RequestBody Transfer transfer, Principal principal) {
        if (transfer.getType().equalsIgnoreCase("send")) {
            Transfer transferNew = dao.createTransferSend(transfer, principal);
            if (transferNew == null) {
                throw new ResponseStatusException(HttpStatus.I_AM_A_TEAPOT, "Insufficient funds.");
            } else {
                return transferNew;
            }
        } else if (transfer.getType().equalsIgnoreCase("request")) {
            Transfer newRequest = dao.createTransferRequest(transfer, principal);
            if (newRequest == null){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot request money from self");
            }
            return newRequest;
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid transfer type.");
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


    @RequestMapping(path = "/transfers/users", method = RequestMethod.GET)
    public List<Transfer> listTransfersByUsername(Principal principal){
        List<Transfer> transferList = dao.getTransferByUsername(principal.getName());
        if (transferList == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found.");
        } else {
            return transferList;
        }
    }

    @RequestMapping(path = "/requests", method = RequestMethod.GET)
    public List<Transfer> listRequestsByUsername(Principal principal){
        List<Transfer> transferList = dao.getRequestsByUsername(principal.getName());
        if (transferList == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found.");
        } else {
            return transferList;
        }
    }

    @RequestMapping(path = "/transfers/{id}", method = RequestMethod.PUT)
    public Transfer updateTransfer(@Valid @RequestBody Transfer transfer, @PathVariable int id) {
        Transfer updatedTransfer = dao.updateTransfer(transfer, id);
        if (updatedTransfer == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transfer not found.");
        } else {
            return updatedTransfer;
        }
    }


    @RequestMapping(path = "/requests/{id}", method = RequestMethod.PUT)
    public Transfer updateRequest(@Valid @RequestBody Transfer transfer, @PathVariable int id, Principal principal) {
        if (!transfer.getStatus().equalsIgnoreCase("approved") && !transfer.getStatus().equalsIgnoreCase("denied")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status selection.");
        }
        if (!accountDao.checkBalance(principal, transfer) && transfer.getStatus().equalsIgnoreCase("approved")) {
            throw new ResponseStatusException(HttpStatus.I_AM_A_TEAPOT, "Insufficient funds for transfer");
        }
        Transfer updatedTransfer = dao.updateRequest(transfer, id);
        if (updatedTransfer == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transfer not found");
        }
        return updatedTransfer;
    }

}
