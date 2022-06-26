package com.techelevator.dao;

import com.techelevator.model.Reservation;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    public List<Reservation> getReservationNext30Days(int parkId) {
        List<Reservation> reservationList = new ArrayList<>();
        String getReservation30days = "SELECT reservation_id, reservation.site_id, reservation.name, from_date, to_date, create_date \n" +
                "FROM reservation \n" +
                "\tJOIN site ON site.site_id = reservation.site_id\n" +
                "\tJOIN campground ON campground.campground_id = site.campground_id\n" +
                "\tJOIN park ON campground.park_id = park.park_id\n" +
                "WHERE from_date < (CURRENT_DATE + INTERVAL '30 days') AND from_date > CURRENT_DATE AND park.park_id = ?\n" +
                "ORDER BY from_date;";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(getReservation30days, parkId);

        while (sqlRowSet.next()) {
            reservationList.add(mapRowToReservation(sqlRowSet));
        }
        return reservationList;
    }

    public List<Reservation> getAvailableSitesByPark(int parkId) {
        List<Reservation> availableSites = new ArrayList<>();
        String getAvailableSitesByPark = "";

        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(getAvailableSitesByPark, parkId);

        while (sqlRowSet.next()) {
            availableSites.add(mapRowToReservation(sqlRowSet));
        }
        return availableSites;
    }
}
