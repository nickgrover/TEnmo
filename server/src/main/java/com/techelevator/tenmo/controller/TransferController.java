package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.JdbcTransferDao;
import com.techelevator.tenmo.dao.TransferDao;

public class TransferController {

    private TransferDao dao;

    public TransferController(TransferDao dao) {
        this.dao = dao;
    }

}
