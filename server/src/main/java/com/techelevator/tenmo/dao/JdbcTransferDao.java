package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao{

    private final JdbcTemplate jdbcTemplate;

    public JdbcTransferDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

//    @Autowired
//    JdbcTemplate jdbcTemplate;

    @Override
    public Transfer createTransfer(Transfer transfer) {
        Transfer createdTransfer;
        String sql = "INSERT INTO transfer(transfer_date, transfer_amount, from_account, to_account, status) VALUES (?,?,?,?,?) RETURNING transfer_id;";
        try{
            int newTransferId = jdbcTemplate.queryForObject(sql, int.class, transfer.getTransferDate(), transfer.getTransferAmount(), transfer.getFromAccount(), transfer.getToAccount(), transfer.getStatus());
            createdTransfer = getTransferByTransferId(newTransferId);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (BadSqlGrammarException e) {
            throw new DaoException("SQL syntax error", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
        return createdTransfer;
    }

    @Override
    public List<Transfer> getAllTransfers() {
        List<Transfer> allTransfers = new ArrayList<>();
        String sql = "SELECT transfer_id, transfer_date, transfer_amount, from_account, to_account, status FROM transfer;";
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
        String sql = "SELECT transfer_id, transfer_date, transfer_amount, from_account, to_account, status FROM transfer WHERE transfer_id = ?;";
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
    public List<Transfer> getTransferByFromAccountId(int fromAccountId) {
        List<Transfer> allTransfers = new ArrayList<>();
        String sql = "SELECT transfer_id, transfer_date, transfer_amount, from_account, to_account, status FROM transfer JOIN account_transfer USING(transfer_id) WHERE account_id = ?;";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, fromAccountId);
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

//    @Override
//    public List<Transfer> getTransferByToAccountId(int toAccountId) {
//        List<Transfer> allTransfers = new ArrayList<>();
//        String sql = "SELECT transfer_id, transfer_date, transfer_amount, from_account, to_account, status FROM transfer JOIN account_transfer USING(transfer_id) WHERE account_id = ?;";
//        try {
//            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, toAccountId);
//            while (results.next()){
//                allTransfers.add(mapRowToTransfer(results));
//            }
//        } catch (CannotGetJdbcConnectionException e) {
//            throw new DaoException("Unable to connect to server or database", e);
//        } catch (BadSqlGrammarException e) {
//            throw new DaoException("SQL syntax error", e);
//        } catch (DataIntegrityViolationException e) {
//            throw new DaoException("Data integrity violation", e);
//        }
//        return allTransfers;
//    }



    //FIX sql statement
    @Override
    public List<Transfer> getTransferByFromUserId(int userId) {
        List<Transfer> allTransfers = new ArrayList<>();
        String sql = "SELECT transfer_id, transfer_date, transfer_amount, from_account, to_account, status FROM transfer JOIN account_transfer USING(transfer_id) JOIN account USING(account_id) WHERE user_id = ?;";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
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

//    @Override
//    public List<Transfer> getTransferByToUserId(int userId) {
//        List<Transfer> allTransfers = new ArrayList<>();
//        String sql = "SELECT transfer_id, transfer_date, transfer_amount, from_account, to_account, status FROM transfer JOIN account_transfer USING(transfer_id) JOIN account USING(account_id) WHERE user_id = ?;";
//        try {
//            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
//            while (results.next()){
//                allTransfers.add(mapRowToTransfer(results));
//            }
//        } catch (CannotGetJdbcConnectionException e) {
//            throw new DaoException("Unable to connect to server or database", e);
//        } catch (BadSqlGrammarException e) {
//            throw new DaoException("SQL syntax error", e);
//        } catch (DataIntegrityViolationException e) {
//            throw new DaoException("Data integrity violation", e);
//        }
//        return allTransfers;
//    }

    @Override
    public Transfer updateTransfer(Transfer transfer, int id) {
        Transfer updatedTransfer = null;
        String sql = "UPDATE transfer SET transfer_date = ?, transfer_amount = ?, from_account = ?, to_account = ?, status = ? WHERE transfer_id = ?;";
        try {
            int numberOfRows = jdbcTemplate.update(sql, transfer.getTransferDate(), transfer.getTransferAmount(), transfer.getFromAccount(), transfer.getToAccount(), transfer.getStatus(), transfer.getTransferId());
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

    //add update to change the balances


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
        transfer.setTransferDate(sqlRowSet.getDate("transfer_date").toLocalDate());
        transfer.setTransferAmount(sqlRowSet.getDouble("transfer_amount"));
        transfer.setFromAccount(sqlRowSet.getInt("from_account"));
        transfer.setToAccount(sqlRowSet.getInt("to_account"));
        transfer.setStatus(sqlRowSet.getString("status"));
        return transfer;
    }


}
