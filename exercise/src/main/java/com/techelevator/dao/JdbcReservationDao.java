package com.techelevator.dao;

import com.techelevator.model.Reservation;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;
import java.time.LocalDate;

public class JdbcReservationDao implements ReservationDao {

    private JdbcTemplate jdbcTemplate;

    public JdbcReservationDao(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public int createReservation(int siteId, String name, LocalDate fromDate, LocalDate toDate) {
        String newReservation = "INSERT INTO reservation(site_id, name, from_date, to_date) " +
                "VALUES(?, ?, ?, ?) RETURNING reservation_id;";
        int reservationId = jdbcTemplate.queryForObject(newReservation, Integer.class, siteId, name,fromDate, toDate);

        return reservationId;
    }

    private Reservation mapRowToReservation(SqlRowSet results) {
        Reservation r = new Reservation();
        r.setReservationId(results.getInt("reservation_id"));
        r.setSiteId(results.getInt("site_id"));
        r.setName(results.getString("name"));
        r.setFromDate(results.getDate("from_date").toLocalDate());
        r.setToDate(results.getDate("to_date").toLocalDate());
        r.setCreateDate(results.getDate("create_date").toLocalDate());
        return r;
    }

/*    @Override
    public Reservation getReservation(int reservationId) {
        Reservation reservation = null;
        String sql = "SELECT reservation_id, site_id, name, from_date, to_date, create_date FROM reservation " +
                "WHERE reservation_id = ? ";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, reservationId);
        while (results.next()) {
            reservation = mapRowToReservation(results);
        }
        return reservation;
    }*/
}
