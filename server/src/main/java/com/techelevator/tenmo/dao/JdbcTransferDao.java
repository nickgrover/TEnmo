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

    private final JdbcTemplate jdbcTemplate;

    public JdbcTransferDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public JdbcAccountDao accountDao;

    public JdbcUserDao userDao;

//    public JdbcAccountDao(JdbcTemplate jdbcTemplate) {
//
//    }

//    @Autowired
//    JdbcTemplate jdbcTemplate;


    @Override
    public Transfer createTransferSend(Transfer transfer) {
        Transfer createdTransfer = null;
        String status = "approved";
        String sqlfrom = "SELECT account_id, user_id, balance FROM account WHERE user_id = ?;";
        SqlRowSet from = jdbcTemplate.queryForRowSet(sqlfrom, userDao.findIdByUsername(transfer.getFromUser()));
        double fromBalance = from.getDouble("balance");
        if (fromBalance > 0 && fromBalance >= transfer.getTransferAmount() && !transfer.getFromUser().equals(transfer.getToUser())) {

            String sql = "INSERT INTO transfer (from_user, to_user, transfer_amount, transfer_date, status, transfer_type) VALUES (?, ?, ?, ?, ?, ?) RETURNING transfer_id;";
            try {
                int newTransferId = jdbcTemplate.queryForObject(sql, int.class, transfer.getFromUser(), transfer.getToUser(), transfer.getTransferAmount(), transfer.getTransferDate(), status, transfer.getType());
                createdTransfer = getTransferByTransferId(newTransferId);

                String sqlTo = "SELECT account_id, user_id, balance FROM account JOIN tenmo_user USING (user_id) WHERE username = ?;";
                SqlRowSet toUser = jdbcTemplate.queryForRowSet(sqlTo,transfer.getToUser());
                double toBalance = toUser.getDouble("balance");
                double updatedToBalance = toBalance + transfer.getTransferAmount();

                // Do the account updates
                double fromUpdatedBalance = fromBalance - transfer.getTransferAmount();
                String sqlUpdateFrom = "UPDATE account SET balance = ? WHERE user_id = ?;";
                jdbcTemplate.update(sql, fromUpdatedBalance, userDao.findIdByUsername(transfer.getFromUser()));

                String sqlUpdateTo = "UPDATE account SET balance = ? JOIN tenmo_user USING (user_id) WHERE username = ?;";
                jdbcTemplate.update(sql, updatedToBalance, transfer.getToUser());

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


//    @Override
//    public Transfer createTransferSend(Transfer transfer, Principal principal) {
//        Transfer createdTransfer = null;
//        String status = "approved";
//        String sqlfrom = "SELECT balance FROM account WHERE user_id = ?;";
//        SqlRowSet from = jdbcTemplate.queryForRowSet(sqlfrom, userDao.findIdByUsername(principal.getName()));
//        double fromBalance = from.getInt("balance");
//        if (fromBalance > 0 && fromBalance >= transfer.getTransferAmount() && !principal.getName().equals(transfer.getToUser())) {
//
//            String sql = "INSERT INTO transfer (from_user, to_user, transfer_amount, transfer_date, status, transfer_type) VALUES (?, ?, ?, ?, ?, ?) RETURNING transfer_id;";
//            try {
//                int newTransferId = jdbcTemplate.queryForObject(sql, int.class, transfer.getFromUser(), transfer.getToUser(), transfer.getTransferAmount(), transfer.getTransferDate(), status, transfer.getType());
//                createdTransfer = getTransferByTransferId(newTransferId);
//
//                String sqlTo = "SELECT balance FROM account JOIN tenmo_user USING (user_id) WHERE username = ?;";
//                double toBalance = jdbcTemplate.queryForObject(sqlTo, double.class, transfer.getToUser());
//                double updatedToBalance = toBalance + transfer.getTransferAmount();
//
//                // Do the account updates
//                double fromUpdatedBalance = fromBalance - transfer.getTransferAmount();
//                String sqlUpdateFrom = "UPDATE account SET balance = ? WHERE user_id = ?;";
//                jdbcTemplate.update(sql, fromUpdatedBalance, userDao.findIdByUsername(principal.getName()));
//
//                String sqlUpdateTo = "UPDATE account SET balance = ? JOIN tenmo_user USING (user_id) WHERE username = ?;";
//                jdbcTemplate.update(sql, updatedToBalance, transfer.getToUser());
//
//            } catch (CannotGetJdbcConnectionException e) {
//                throw new DaoException("Unable to connect to server or database", e);
//            } catch (BadSqlGrammarException e) {
//                throw new DaoException("SQL syntax error", e);
//            } catch (DataIntegrityViolationException e) {
//                throw new DaoException("Data integrity violation", e);
//            }
//        }
//        return createdTransfer;
//    }

//    @Override
//    public Transfer createTransferRequest(Transfer transfer) {
//        Transfer createdTransfer;
//        String sql = "INSERT INTO transfer(transfer_date, transfer_amount, from_account, to_account, status, type) VALUES (?,?,?,?,?,?) WHERE type = 'request' RETURNING transfer_id;";
//        try{
//            int newTransferId = jdbcTemplate.queryForObject(sql, int.class, transfer.getTransferDate(), transfer.getTransferAmount(), transfer.getFromUser(), transfer.getToUser(), transfer.getStatus(), transfer.getType());
//            createdTransfer = getTransferByTransferId(newTransferId);
//        } catch (CannotGetJdbcConnectionException e) {
//            throw new DaoException("Unable to connect to server or database", e);
//        } catch (BadSqlGrammarException e) {
//            throw new DaoException("SQL syntax error", e);
//        } catch (DataIntegrityViolationException e) {
//            throw new DaoException("Data integrity violation", e);
//        }
//        return createdTransfer;
//    }

    @Override
    public List<Transfer> getAllTransfers() {
        List<Transfer> allTransfers = new ArrayList<>();
        String sql = "SELECT transfer_id, from_user, to_user, transfer_amount, transfer_date, status, transfer_type FROM transfer;";
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
        String sql = "SELECT transfer_id, from_user, to_user, transfer_amount, transfer_date, status, transfer_type FROM transfer WHERE transfer_id = ?;";
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
        String sql = "UPDATE transfer SET from_user = ?, to_user = ?, transfer_amount = ?, transfer_date = ?, status = ?, transfer_type = ? WHERE transfer_id = ?;";
        try {
            int numberOfRows = jdbcTemplate.update(sql, transfer.getFromUser(), transfer.getToUser(), transfer.getTransferAmount(), transfer.getTransferDate(), transfer.getStatus(), transfer.getType(), transfer.getTransferId());
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
        transfer.setFromUser(sqlRowSet.getString("from_user"));
        transfer.setToUser(sqlRowSet.getString("to_user"));
        transfer.setTransferAmount(sqlRowSet.getDouble("transfer_amount"));
        transfer.setTransferDate(sqlRowSet.getDate("transfer_date").toLocalDate());
        transfer.setStatus(sqlRowSet.getString("status"));
        transfer.setType(sqlRowSet.getString("transfer_type"));
        return transfer;
    }


}