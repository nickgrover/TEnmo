package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcAccountDao implements AccountDao{
//updated

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public Account createAccount(Account account) {
        Account createdAccount;
        double startingBalance = 1000;
        String sql = "INSERT INTO account(user_id, balance) VALUES (?, ?) RETURNING account_id;";
        int newAccountId = jdbcTemplate.queryForObject(sql, int.class, account.getUserId(), startingBalance);
        createdAccount = getAccountByAccountId(newAccountId);
        //add try catch



        return createdAccount;
    }

    @Override
    public List<Account> getAllAccounts() {
        List<Account> allAccounts = new ArrayList<>();
        String sql = "SELECT account_id, user_id, balance FROM account;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while (results.next()){
            allAccounts.add(mapRowToAccount(results));
        }
        return allAccounts;
    }

    @Override
    public Account getAccountByAccountId(int id) {
        Account account = null;
        String sql = "SELECT account_id, user_id, balance FROM account WHERE account_id = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
        while (results.next()){
            account = mapRowToAccount(results);
        }
        return account;
    }

    @Override
    public List<Account> getAccountByUserId(int userId) {
        List<Account> allAccounts = null;
        String sql = "SELECT account_id, user_id, balance FROM account WHERE user_id = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
        while (results.next()){
            allAccounts.add(mapRowToAccount(results));
        }
        return allAccounts;
    }

    @Override
    public Account updateAccount(Account account, int id) {
        String sql = "UPDATE account SET user_id = ?, balance = ? WHERE account_id = ? RETURNING account_id;";
        jdbcTemplate.update(sql, int.class, account.getUserId(), account.getBalance(), account.getAccountId());
        return getAccountByAccountId(id);
    }

    @Override
    public void deleteAccount(int id) {
        String sql = "DELETE FROM account WHERE account_id = ?;";
        jdbcTemplate.update(sql, id);
    }

    public Account mapRowToAccount(SqlRowSet sqlRowSet){
        Account account = new Account();
        account.setAccountId(sqlRowSet.getInt("account_id"));
        account.setUserId(sqlRowSet.getInt("user_id"));
        account.setBalance(sqlRowSet.getDouble("balance"));
        return account;
    }
}
