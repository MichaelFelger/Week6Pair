package com.techelevator.dao;

import com.techelevator.model.Site;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

public class JdbcSiteDao implements SiteDao {

    private JdbcTemplate jdbcTemplate;

    public JdbcSiteDao(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<Site> getSitesThatAllowRVs(int parkId) {
        List<Site> sites = new ArrayList<>();
        String selectSites =
                "SELECT site_id, s.campground_id, site_number, max_occupancy, accessible, max_rv_length, utilities\n" +
                        "FROM site AS s" +
                        " JOIN campground AS c ON s.campground_id = c.campground_id " +
                        " JOIN park AS p ON p.park_id = c.park_id " +
                        " WHERE " +
                        " max_rv_length > ? AND c.park_id = ?";
        SqlRowSet selectRowSet = jdbcTemplate.queryForRowSet(selectSites, 0, parkId);
        while (selectRowSet.next()) {
            sites.add(mapRowToSite(selectRowSet));
        }

        return sites;
    }

    private Site mapRowToSite(SqlRowSet results) {
        Site site = new Site();
        site.setSiteId(results.getInt("site_id"));
        site.setCampgroundId(results.getInt("campground_id"));
        site.setSiteNumber(results.getInt("site_number"));
        site.setMaxOccupancy(results.getInt("max_occupancy"));
        site.setAccessible(results.getBoolean("accessible"));
        site.setMaxRvLength(results.getInt("max_rv_length"));
        site.setUtilities(results.getBoolean("utilities"));
        return site;
    }
}
