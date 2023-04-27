package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.JdbcAccountDao;
import com.techelevator.tenmo.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
//import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
//@Component
@RequestMapping(path = "/accounts")
public class AccountController {

    @Lazy
    @Autowired
    JdbcAccountDao accountDao;

    @RequestMapping(method = RequestMethod.POST)
    public Account createAccount(@RequestBody Account account) {
        return accountDao.createAccount(account);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<Account> listAllAccounts() {
        return accountDao.getAllAccounts();
    }


}
