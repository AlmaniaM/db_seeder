package ch.konnexions.db_seeder.jdbc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import ch.konnexions.db_seeder.AbstractDbmsSeeder;

/**
 * Test Data Generator for a Database - Abstract JDBC Schema.
 * <br>
 * @author  walter@konnexions.ch
 * @since   2020-05-01
 */
abstract class AbstractJdbcSchema extends AbstractDbmsSeeder {

  protected static Map<String, String>                      dmlStatements;

  private static final Logger                               logger      = Logger.getLogger(AbstractJdbcSchema.class);

  protected static Map<String, Integer>                     maxRowSizes;

  protected static final HashMap<String, ArrayList<Object>> pkLists     = new HashMap<>();

  protected static final HashMap<String, Integer>           pkListSizes = new HashMap<>();

  protected static List<String>                             TABLE_NAMES_CREATE;
  protected static List<String>                             TABLE_NAMES_DROP;

  /**
   * Initialises a new abstract JDBC schema object.
   *
   * @param dbmsTickerSymbol DBMS ticker symbol 
   */
  public AbstractJdbcSchema(String dbmsTickerSymbol) {
    super(dbmsTickerSymbol);

    if (isDebug) {
      logger.debug("Start Constructor - dbmsTickerSymbol=" + dbmsTickerSymbol);
    }

    if (isDebug) {
      logger.debug("End   Constructor");
    }
  }

  /**
   * Initialises a new abstract JDBC schema object.
   *
   * @param dbmsTickerSymbol DBMS ticker symbol 
   * @param isClient client database version
   */
  public AbstractJdbcSchema(String dbmsTickerSymbol, boolean isClient) {
    super(dbmsTickerSymbol, isClient);

    if (isDebug) {
      logger.debug("Start Constructor - dbmsTickerSymbol=" + dbmsTickerSymbol + " - isClient=" + isClient);
    }

    if (isDebug) {
      logger.debug("End   Constructor");
    }
  }
}