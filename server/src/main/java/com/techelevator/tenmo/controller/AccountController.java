package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.JdbcAccountDao;
import com.techelevator.tenmo.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
//import org.springframework.stereotype.Component;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
//@Component
@RequestMapping(path = "/accounts")
public class AccountController {

    @Lazy
    @Autowired
    JdbcAccountDao accountDao;

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST)
    public Account createAccount(@RequestBody Account account) {
        return accountDao.createAccount(account);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<Account> listAllAccounts() {
        return accountDao.getAllAccounts();
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    public Account getAccountAccountId(@PathVariable int id) {
        Account account = accountDao.getAccountByAccountId(id);
        if (account == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found.");
        } else {
            return account;
        }
    }



//    @RequestMapping(path = "/accounts/users/{id}", method = RequestMethod.GET)
//    public List<Account> getAccountUserId(@PathVariable("id") int userId) {
//        List<Account> accounts = accountDao.getAccountByUserId(userId);
//        if (accounts == null) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.");
//        } else {
//            return accounts;
//        }
//    }

    @RequestMapping(path = "/{id}", method = RequestMethod.PUT)
    public Account update(@RequestBody Account account, @PathVariable int id) {
        Account updatedAccount = accountDao.updateAccount(account, id);
        if (updatedAccount == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found.");
        } else {
            return updatedAccount;
        }
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable int id) {
        accountDao.deleteAccount(id);
    }
}
