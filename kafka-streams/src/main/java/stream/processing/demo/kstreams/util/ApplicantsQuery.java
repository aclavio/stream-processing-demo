package stream.processing.demo.kstreams.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stream.processing.demo.Applicant;

import java.sql.*;
import java.util.Optional;

public class ApplicantsQuery {

    private static final Logger logger = LoggerFactory.getLogger(ApplicantsQuery.class);

    private static String FUZZY_MATCH_SQL = "SELECT * " +
            "FROM applicants " +
            "WHERE UPPER(first_name) = UPPER(?) OR UPPER(last_name) = UPPER(?) OR UPPER(phone) = UPPER(?)";

    Connection conn;

    public void connect(String url, String user, String password) throws SQLException {
        conn = DriverManager.getConnection(url, user, password);
    }

    /**
     * A very naive fuzzy matcher that just compares names and phone, and returns the first match
     * @param firstName
     * @param lastName
     * @param phone
     * @return
     */
    public Applicant fuzzyMatchApplicant(
            String firstName,
            String lastName,
            String phone) {

        Applicant matched = null;

        try (
                PreparedStatement stmt = conn.prepareStatement(FUZZY_MATCH_SQL)
        ) {
            stmt.setString(1, Optional.ofNullable(firstName).orElse(""));
            stmt.setString(2, Optional.ofNullable(lastName).orElse(""));
            stmt.setString(3, Optional.ofNullable(phone).orElse(""));
            ResultSet results = stmt.executeQuery();
            if (results.next()) {
                matched = Applicant.newBuilder()
                        .setId(results.getInt("id"))
                        .setFirstName(results.getString("first_name"))
                        .setLastName(results.getString("last_name"))
                        .setPhone(results.getString("phone"))
                        .setHomeAddress(results.getString("home_address"))
                        .setStatus(results.getString("status"))
                        .setModified(results.getTimestamp("modified").toInstant())
                        .build();
            } else {
                logger.info("no fuzzy matches found!");
            }
        } catch (SQLException e) {
            logger.error("unexpected error during fuzzy match", e);
        }

        return matched;
    }

    public void destroy() throws SQLException {
        if (conn != null) conn.close();
    }

    public static void main(String ...args) throws SQLException {
        ApplicantsQuery applicantsQuery = new ApplicantsQuery();
        applicantsQuery.connect("jdbc:postgresql://localhost:5432/postgres", "postgres", "example");

        Applicant result = applicantsQuery.fuzzyMatchApplicant("frodo", "took", "555-0003");
        logger.info("got result: {}", result);

        applicantsQuery.destroy();
    }

}
