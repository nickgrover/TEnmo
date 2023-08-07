package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao{

    private UserDao userDao;
    private JdbcTemplate jdbcTemplate;

    public JdbcTransferDao(JdbcTemplate jdbcTemplate, AccountDao accountDao, UserDao userDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.accountDao = accountDao;
        this.userDao = userDao;
    }

    public AccountDao accountDao;



    @Override
    public Transfer createTransferSend(Transfer transfer, Principal principal) {
        Transfer createdTransfer = null;
        double fromBalance = 0;
        String status = "approved";
        String sqlfrom = "SELECT account_id, user_id, balance FROM account WHERE account_id = ?;";
        SqlRowSet from = jdbcTemplate.queryForRowSet(sqlfrom, transfer.getFromAccount());
        while (from.next()){
            fromBalance = from.getDouble("balance");
        }
        if (fromBalance > 0 && fromBalance >= transfer.getTransferAmount() && !transfer.getFromUser().equals(transfer.getToUser())) {

            String sql = "INSERT INTO transfer (from_user, to_user, from_account, to_account, transfer_amount, transfer_date, status, transfer_type) VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING transfer_id;";
            try {
                int newTransferId = jdbcTemplate.queryForObject(sql, int.class, principal.getName(), transfer.getToUser(), transfer.getFromAccount(), transfer.getToAccount(), transfer.getTransferAmount(), transfer.getTransferDate(), status, transfer.getType());
                createdTransfer = getTransferByTransferId(newTransferId);

                // Do the account updates
                String sqlUpdateFrom = "UPDATE account SET balance = balance - ? WHERE account_id = ?;";
                jdbcTemplate.update(sqlUpdateFrom, transfer.getTransferAmount(), transfer.getFromAccount());

                String sqlUpdateTo = "UPDATE account SET balance = balance + ? WHERE account_id = ?;";
                jdbcTemplate.update(sqlUpdateTo, transfer.getTransferAmount(), transfer.getToAccount());

            } catch (CannotGetJdbcConnectionException e) {
                throw new DaoException("Unable to connect to server or database", e);
            } catch (BadSqlGrammarException e) {
                throw new DaoException("SQL syntax error", e);
            } catch (DataIntegrityViolationException e) {
                throw new DaoException("Data integrity violation", e);
            }
        }
        return createdTransfer;
    }


    @Override
    public Transfer createTransferRequest(Transfer transfer, Principal principal) {
        Transfer createdTransfer = null;
        String status = "pending";
        if (transfer.getTransferAmount() > 0 && !principal.getName().equals(transfer.getFromUser())) {
            String sql = "INSERT INTO transfer (from_user, to_user, from_account, to_account, transfer_amount, transfer_date, status, transfer_type) VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING transfer_id;";
            try {
                int newTransferId = jdbcTemplate.queryForObject(sql, int.class, transfer.getFromUser(), principal.getName(), transfer.getFromAccount(), transfer.getToAccount(), transfer.getTransferAmount(), transfer.getTransferDate(), status, transfer.getType());
                createdTransfer = getTransferByTransferId(newTransferId);
            } catch (CannotGetJdbcConnectionException e) {
                throw new DaoException("Unable to connect to server or database", e);
            } catch (BadSqlGrammarException e) {
                throw new DaoException("SQL syntax error", e);
            } catch (DataIntegrityViolationException e) {
                throw new DaoException("Data integrity violation", e);
            }
        }
        return createdTransfer;
    }



    @Override
    public List<Transfer> getAllTransfers() {
        List<Transfer> allTransfers = new ArrayList<>();
        String sql = "SELECT transfer_id, from_user, to_user, from_account, to_account, transfer_amount, transfer_date, status, transfer_type FROM transfer;";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
            while (results.next()){
                allTransfers.add(mapRowToTransfer(results));
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (BadSqlGrammarException e) {
            throw new DaoException("SQL syntax error", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
        return allTransfers;
    }

    @Override
    public Transfer getTransferByTransferId(int transferId) {
        Transfer transfer = new Transfer();
        String sql = "SELECT transfer_id, from_user, to_user, from_account, to_account, transfer_amount, transfer_date, status, transfer_type FROM transfer WHERE transfer_id = ?;";
        try{
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, transferId);
            while (results.next()){
                transfer = mapRowToTransfer(results);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (BadSqlGrammarException e) {
            throw new DaoException("SQL syntax error", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
        return transfer;
    }


    //FIX sql statement
    @Override
    public List<Transfer> getTransferByUsername(String name) {
        List<Transfer> allTransfers = new ArrayList<>();
        String sql = "SELECT transfer_id, from_user, to_user, from_account, to_account, transfer_amount, transfer_date, status, transfer_type FROM transfer WHERE from_user = ? OR to_user = ?;";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, name, name);
            while (results.next()){
                allTransfers.add(mapRowToTransfer(results));
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (BadSqlGrammarException e) {
            throw new DaoException("SQL syntax error", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
        return allTransfers;
    }

    @Override
    public List<Transfer> getRequestsByUsername(String name) {
        List<Transfer> allTransfers = new ArrayList<>();
        String sql = "SELECT transfer_id, from_user, to_user, from_account, to_account, transfer_amount, transfer_date, status, transfer_type FROM transfer WHERE transfer_type = 'request' AND from_user = ?;";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, name);
            while (results.next()){
                allTransfers.add(mapRowToTransfer(results));
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (BadSqlGrammarException e) {
            throw new DaoException("SQL syntax error", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
        return allTransfers;
    }



    @Override
    public Transfer updateTransfer(Transfer transfer, int id) {
        Transfer updatedTransfer = null;
        String sql = "UPDATE transfer SET from_user = ?, to_user = ?, from_account = ?, to_account = ?, transfer_amount = ?, transfer_date = ?, status = ?, transfer_type = ? WHERE transfer_id = ?;";
        try {
            int numberOfRows = jdbcTemplate.update(sql, transfer.getFromUser(), transfer.getToUser(), transfer.getFromAccount(), transfer.getToAccount(), transfer.getTransferAmount(), transfer.getTransferDate(), transfer.getStatus(), transfer.getType(), transfer.getTransferId());
            if (numberOfRows == 0){
                throw new DaoException("Zero rows affected, expected at least 1");
            } else {
                updatedTransfer = getTransferByTransferId(transfer.getTransferId());
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (BadSqlGrammarException e) {
            throw new DaoException("SQL syntax error", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
        return updatedTransfer;
    }


    @Override
    public Transfer updateRequest(Transfer transfer, int id) {
        Transfer updatedTransfer = null;
        String approved = "approved";
        String denied = "denied";
        String sql = "UPDATE transfer SET status = ? WHERE transfer_id = ?;";
        try {
            if (transfer.getStatus().equalsIgnoreCase("approved")) {
                int numOfRows = jdbcTemplate.update(sql, approved, transfer.getTransferId());
                if (numOfRows == 0) {
                    throw new DaoException("Zero rows affected, expected at least 1");
                } else {
                    updatedTransfer = getTransferByTransferId(transfer.getTransferId());
                }
                String sqlUpdateFrom = "UPDATE account SET balance = balance - ? WHERE account_id = ?;";
                jdbcTemplate.update(sqlUpdateFrom, transfer.getTransferAmount(), transfer.getFromAccount());
                String sqlUpdateTo = "UPDATE account SET balance = balance + ? WHERE account_id = ?";
                jdbcTemplate.update(sqlUpdateTo, transfer.getTransferAmount(), transfer.getToAccount());
            }
            if (transfer.getStatus().equalsIgnoreCase("denied")) {
                int numOfRows = jdbcTemplate.update(sql, denied, transfer.getTransferId());
                if (numOfRows == 0){
                    throw new DaoException("Zero rows affected, expected at least 1");
                } else {
                    updatedTransfer = getTransferByTransferId(transfer.getTransferId());
                }
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (BadSqlGrammarException e) {
            throw new DaoException("SQL syntax error", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
        return updatedTransfer;
    }


    @Override
    public void deleteTransfer(int id) {
        String sql = "DELETE FROM transfer WHERE transfer_id = ?;";
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

    public Transfer mapRowToTransfer(SqlRowSet sqlRowSet){
        Transfer transfer = new Transfer();
        transfer.setTransferId(sqlRowSet.getInt("transfer_id"));
        transfer.setFromUser(sqlRowSet.getString("from_user"));
        transfer.setToUser(sqlRowSet.getString("to_user"));
        transfer.setFromAccount(sqlRowSet.getInt("from_account"));
        transfer.setToAccount(sqlRowSet.getInt("to_account"));
        transfer.setTransferAmount(sqlRowSet.getDouble("transfer_amount"));
        transfer.setTransferDate(sqlRowSet.getDate("transfer_date").toLocalDate());
        transfer.setStatus(sqlRowSet.getString("status"));
        transfer.setType(sqlRowSet.getString("transfer_type"));
        return transfer;
    }


}
