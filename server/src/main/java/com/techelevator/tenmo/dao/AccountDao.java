package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;

import java.security.Principal;
import java.util.List;

public interface AccountDao {

    //create
    Account createAccount(Account account, Principal principal);

    //read
    List<Account> getAllAccounts();
    Account getAccountByAccountId(int id);
    List<Account> getAccountByUserId(int userId);

    //update
    Account updateAccount(Account account, int id);

    //delete
    void deleteAccount(int id);

    boolean checkBalance(Principal principal, Transfer transfer);
}
