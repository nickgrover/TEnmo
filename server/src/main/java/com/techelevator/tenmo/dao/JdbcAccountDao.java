package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcAccountDao implements AccountDao{

    private UserDao userDao;

    @Autowired
    JdbcTemplate jdbcTemplate;

    public JdbcAccountDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public Account createAccount(Account account, Principal principal) {
        Account createdAccount;
        double startingBalance = 1000;
        String sql = "INSERT INTO account(user_id, balance) VALUES (?, ?) RETURNING account_id;";
        try {
            int newAccountId = jdbcTemplate.queryForObject(sql, int.class, userDao.findIdByUsername(principal.getName()), startingBalance);
            createdAccount = getAccountByAccountId(newAccountId);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (BadSqlGrammarException e) {
            throw new DaoException("SQL syntax error", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
        return createdAccount;
    }


//    @Override
//    public Account createAccount(Account account) {
//        Account createdAccount;
//        double startingBalance = 1000;
//        String sql = "INSERT INTO account(user_id, balance) VALUES (?, ?) RETURNING account_id;";
//        try {
//            int newAccountId = jdbcTemplate.queryForObject(sql, int.class, account.getUserId(), startingBalance);
//            createdAccount = getAccountByAccountId(newAccountId);
//        } catch (CannotGetJdbcConnectionException e) {
//            throw new DaoException("Unable to connect to server or database", e);
//        } catch (BadSqlGrammarException e) {
//            throw new DaoException("SQL syntax error", e);
//        } catch (DataIntegrityViolationException e) {
//            throw new DaoException("Data integrity violation", e);
//        }
//        return createdAccount;
//    }



    @Override
    public List<Account> getAllAccounts() {
        List<Account> allAccounts = new ArrayList<>();
        String sql = "SELECT tenmo_user.username, account_id, user_id, balance FROM account JOIN tenmo_user USING (user_id);";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
            while (results.next()) {
                allAccounts.add(mapRowToAccount(results));
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (BadSqlGrammarException e) {
            throw new DaoException("SQL syntax error", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
        return allAccounts;
    }

    @Override
    public Account getAccountByAccountId(int id) {
        Account account = null;
        String sql = "SELECT account_id, user_id, balance FROM account WHERE account_id = ?;";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
            while (results.next()) {

                account = mapRowToAccount(results);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (BadSqlGrammarException e) {
            throw new DaoException("SQL syntax error", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
        return account;
    }

    @Override
    public List<Account> getAccountByUserId(int userId) {
        List<Account> allAccounts = null;
        String sql = "SELECT account_id, user_id, balance FROM account WHERE user_id = ?;";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
            while (results.next()) {
                allAccounts.add(mapRowToAccount(results));
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (BadSqlGrammarException e) {
            throw new DaoException("SQL syntax error", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
        return allAccounts;
    }


    @Override
    public Account updateAccount(Account account, int id) {
        Account updatedAccount = null;
        String sql = "UPDATE account SET user_id = ?, balance = ? WHERE account_id = ?;";
        try {
            int numberOfRows = jdbcTemplate.update(sql, account.getUserId(), account.getBalance(), account.getAccountId());
            if (numberOfRows == 0){
                throw new DaoException("Zero rows affected, expected at least 1");
            } else {
                updatedAccount = getAccountByAccountId(account.getAccountId());
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (BadSqlGrammarException e) {
            throw new DaoException("SQL syntax error", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
        return updatedAccount;
    }


    @Override
    public void deleteAccount(int id) {
        String sql = "DELETE FROM account WHERE account_id = ?;";
        try {
            int numberOfRows = jdbcTemplate.update(sql, id);
            if (numberOfRows == 0){
                throw new DaoException("Zero rows affected, expected at least 1");
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (BadSqlGrammarException e) {
            throw new DaoException("SQL syntax error", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
    }

    @Override
    public boolean checkBalance(Principal principal, Transfer transfer) {
        double fromBalance = 0;
        String sql = "SELECT balance FROM account JOIN tenmo_user USING (user_id) WHERE username = ?;";
        SqlRowSet from = jdbcTemplate.queryForRowSet(sql, principal.getName());
        while (from.next()){
            fromBalance = from.getDouble("balance");
        }
        return fromBalance >= transfer.getTransferAmount();
    }

    public Account mapRowToAccount(SqlRowSet sqlRowSet){
        Account account = new Account();
        account.setAccountId(sqlRowSet.getInt("account_id"));
        account.setUserId(sqlRowSet.getInt("user_id"));
        account.setBalance(sqlRowSet.getDouble("balance"));
        return account;
    }


}
