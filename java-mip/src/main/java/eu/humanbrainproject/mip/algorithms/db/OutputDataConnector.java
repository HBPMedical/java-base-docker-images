package eu.humanbrainproject.mip.algorithms.db;

import eu.humanbrainproject.mip.algorithms.Configuration;
import eu.humanbrainproject.mip.algorithms.ResultsFormat;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OutputDataConnector extends DBConnector {
    private static final Logger LOGGER = Logger.getLogger(OutputDataConnector.class.getName());

    /**
     * @return the output data connector defined by the configuration
     */
    public static OutputDataConnector fromEnv() {
        final Configuration conf = Configuration.INSTANCE;
        return new OutputDataConnector(
                conf.outputResultTable(),
                DBConnectionDescriptor.outputConnectorFromEnv());
    }


    private final String outTable;

    public OutputDataConnector(String outTable, DBConnectionDescriptor dbConnectionDescriptor) {
        super(dbConnectionDescriptor);
        this.outTable = outTable;
    }

    public JobResults getJobResults(String jobId) throws DBException {
        String query = String.format("SELECT node, data, shape FROM %s WHERE job_id ='%s'", outTable, jobId);

        return select(query, resultSet -> {
            try {
                if (resultSet.next()) {
                    return new JobResults(resultSet.getString("node"),
                            resultSet.getString("shape"),
                            resultSet.getString("data"));
                } else {
                    throw new SQLException("Job " + jobId + " not found");
                }
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Cannot query job results", e);
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Save results, using configuration from the environment for jobId, executionMode and function parameters.
     */
    public void saveResults(String results, ResultsFormat resultsFormat)
            throws DBException {

        final Configuration conf = Configuration.INSTANCE;

        saveResults(results, resultsFormat, conf.jobId(), conf.executionNode(), conf.function());
    }

    public void saveResults(String results, ResultsFormat resultsFormat, String jobId, String executionNode, String function)
            throws DBException {

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            String insertRequest = String.format("INSERT INTO %s (job_id, node, data, shape, function) VALUES (?, ?, ?, ?, ?)", outTable);

            try (PreparedStatement stmt = conn.prepareStatement(insertRequest)) {
                stmt.setString(1, jobId);
                stmt.setString(2, executionNode);
                stmt.setString(3, results);
                stmt.setString(4, resultsFormat.getShape());
                stmt.setString(5, function);

                stmt.executeUpdate();
                conn.commit();
            }
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    public static class JobResults {

        private String node;
        private ResultsFormat resultsFormat;
        private String results;

        JobResults(String executionNode, String shape, String results) {
            this.node = executionNode;
            this.resultsFormat = shapeToResultsFormat(shape);
            this.results = results;
        }

        public String getNode() {
            return node;
        }

        public ResultsFormat getResultsFormat() {
            return resultsFormat;
        }

        public String getResults() {
            return results;
        }
    }

    private static ResultsFormat shapeToResultsFormat(String shape) {
        for (ResultsFormat format: ResultsFormat.values()) {
            if (format.getShape().equals(shape)) {
                return format;
            }
        }
        throw new IllegalArgumentException("Unknown shape: " + shape);
    }
}