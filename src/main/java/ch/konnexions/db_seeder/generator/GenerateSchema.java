package ch.konnexions.db_seeder.generator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import ch.konnexions.db_seeder.AbstractDbmsSeeder;
import ch.konnexions.db_seeder.schema.SchemaPojo;
import ch.konnexions.db_seeder.schema.SchemaPojo.Table;
import ch.konnexions.db_seeder.schema.SchemaPojo.Table.Column;
import ch.konnexions.db_seeder.schema.SchemaPojo.Table.Column.ColumnConstraint;
import ch.konnexions.db_seeder.schema.SchemaPojo.Table.TableConstraint;
import ch.konnexions.db_seeder.utils.MessageHandling;

/**
 * Test Data Generator for a Database - Transform JSON to POJO.
 * <br>
 * @author  walter@konnexions.ch
 * @since   2020-07-15
 */
public final class GenerateSchema extends AbstractDbmsSeeder {

  private static final Logger                               logger                    = Logger.getLogger(GenerateSchema.class);

  private int                                               constraintNumber          = 0;

  private int                                               errors                    = 0;

  private final ArrayList<String>                           genTableHierarchy         = new ArrayList<>();
  private final Map<String, ArrayList<String>>              genTableNameColumnNames   = new HashMap<>();
  private final ArrayList<String>                           genTableNames             = new ArrayList<>();
  private final Map<String, Integer>                        genTableNumberOfRows      = new HashMap<>();
  private final HashMap<String, ArrayList<Column>>          genTablesColumns          = new HashMap<>();
  private final HashMap<String, ArrayList<TableConstraint>> genTablesTableConstraints = new HashMap<>();
  private final Set<String>                                 genVarcharColumnNames     = new HashSet<>();

  private final boolean                                     isDebug                   = logger.isDebugEnabled();
  private boolean                                           isNotNull;

  private final String                                      printDate;

  private Set<Table>                                        valTables;
  private HashMap<String, HashSet<String>>                  valTablesColumns;
  private final HashMap<String, HashSet<String>>            valTableNameForeignKeys   = new HashMap<>();

  /**
   * Instantiates a new GenerateSchema object.
   */
  public GenerateSchema() {
    super();

    String           printDatePattern = "yyyy-MM-dd";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(printDatePattern);

    printDate = simpleDateFormat.format(new Date());
  }

  private ArrayList<String> editColumnConstraints(String tickerSymbolLower, String identifierDelimiter, String tableName, Column column) {
    isNotNull = false;

    ArrayList<ColumnConstraint> columnConstraints   = column.getColumnConstraints();

    Integer                     defaultValueInteger = column.getDefaultValueInteger();
    String                      defaultValueString  = column.getDefaultValueString();

    ArrayList<String>           editedConstraints   = new ArrayList<>();

    if (columnConstraints == null && defaultValueInteger == null && defaultValueString == null) {
      return editedConstraints;
    }

    if (defaultValueInteger != null) {
      editedConstraints.add("DEFAULT " + defaultValueInteger.toString());
    }

    if (defaultValueString != null) {
      editedConstraints.add("DEFAULT \"" + defaultValueString + "\"");
    }

    if (columnConstraints != null) {
      String constraintType;

      for (ColumnConstraint columnConstraint : columnConstraints) {
        constraintType = columnConstraint.getConstraintType().toUpperCase();

        if ("NOT NULL".equals(constraintType)) {
          isNotNull = true;
          continue;
        }

        if ("cratedb".equals(tickerSymbolLower)) {
          continue;
        }

        switch (constraintType) {
        case "FOREIGN" -> editedConstraints.add(("derby".equals(tickerSymbolLower)
            || "firebird".equals(tickerSymbolLower)
            || "h2".equals(tickerSymbolLower)
            || "ibmdb2".equals(tickerSymbolLower)
            || "informix".equals(tickerSymbolLower)
            || "mariadb".equals(tickerSymbolLower)
            || "mimer".equals(tickerSymbolLower)
            || "mysql".equals(tickerSymbolLower)
            || "oracle".equals(tickerSymbolLower)
            || "postgresql".equals(tickerSymbolLower)
            || "sqlite".equals(tickerSymbolLower)
                ? ""
                : "FOREIGN KEY ") + "REFERENCES " + String.format("%-33s",
                                                                  identifierDelimiter + columnConstraint.getReferenceTable().toUpperCase()
                                                                      + identifierDelimiter) + " (" + identifierDelimiter + columnConstraint
                                                                          .getReferenceColumn().toUpperCase() + identifierDelimiter + ")");
        case "PRIMARY" -> {
          if ("ibmdb2".equals(tickerSymbolLower)) {
            isNotNull = true;
          }
          editedConstraints.add("PRIMARY KEY");
        }
        case "UNIQUE" -> editedConstraints.add("UNIQUE");
        default -> MessageHandling.abortProgram(logger,
                                                "Database table: '" + tableName + "' - Unknown constraint type '" + constraintType + "'");
        }
      }
    }

    Collections.sort(editedConstraints);

    return editedConstraints;
  }

  private String editDataType(String tickerSymbolLower, Column column) {

    return switch (column.getDataType().toUpperCase()) {
    case "BIGINT" -> switch (tickerSymbolLower) {
      case "cubrid" -> "INT";
      case "firebird", "sqlite" -> "INTEGER";
      case "oracle" -> "NUMBER";
      default -> "BIGINT";
      };
    case "BLOB" -> switch (tickerSymbolLower) {
      case "cratedb" -> "OBJECT";
      case "mariadb", "mysql" -> "LONGBLOB";
      case "mssqlserver" -> "VARBINARY(MAX)";
      case "postgresql" -> "BYTEA";
      default -> "BLOB";
      };
    case "CLOB" -> switch (tickerSymbolLower) {
      case "cratedb", "postgresql" -> "TEXT";
      case "firebird" -> "BLOB SUB_TYPE 1";
      case "mariadb", "mysql" -> "LONGTEXT";
      case "mssqlserver" -> "VARCHAR(MAX)";
      default -> "CLOB";
      };
    case "TIMESTAMP" -> switch (tickerSymbolLower) {
      case "informix" -> "DATETIME YEAR TO FRACTION";
      case "mariadb", "mysql", "sqlite" -> "DATETIME";
      case "mssqlserver" -> "DATETIME2";
      default -> "TIMESTAMP";
      };
    case "VARCHAR" -> switch (tickerSymbolLower) {
      case "cratedb" -> "TEXT";
      case "informix" -> column.getSize() > 254
          ? "LVARCHAR(" + column.getSize() + ")"
          : "VARCHAR(" + column.getSize() + ")";
      case "mimer" -> "NVARCHAR(" + column.getSize() + ")";
      case "oracle", "sqlite" -> "VARCHAR2(" + column.getSize() + ")";
      default -> "VARCHAR(" + column.getSize() + ")";
      };
    default -> "";
    };
  }

  private ArrayList<String> editTableConstraints(String tickerSymbolLower,
                                                 String identifierDelimiter,
                                                 String tableName,
                                                 ArrayList<TableConstraint> tableConstraints) {
    ArrayList<String> editedConstraints = new ArrayList<>();

    if ("cratedb".equals(tickerSymbolLower) || tableConstraints == null) {
      return editedConstraints;
    }

    String        constraintType;

    StringBuilder workArea;

    for (int i = 0; i < tableConstraints.size(); i++) {
      TableConstraint tableConstraint = tableConstraints.get(i);

      workArea = new StringBuilder(" ".repeat(23));
      if (!"informix".equals(tickerSymbolLower)) {
        workArea.append(String.format("%-31s",
                                      "CONSTRAINT CONSTRAINT_" + ++constraintNumber));
      }

      constraintType = tableConstraint.getConstraintType().toUpperCase();

      switch (constraintType) {
      case "FOREIGN" -> workArea.append("FOREIGN KEY (").append(identifierDelimiter);
      case "PRIMARY" -> workArea.append("PRIMARY KEY (").append(identifierDelimiter);
      case "UNIQUE" -> workArea.append("UNIQUE      (").append(identifierDelimiter);
      default -> MessageHandling.abortProgram(logger,
                                              "Database table: '" + tableName + "' - Unknown constraint type '" + constraintType + "'");
      }

      workArea.append(String.join(identifierDelimiter + ", " + identifierDelimiter,
                                  tableConstraint.getColumns()).toUpperCase()).append(identifierDelimiter).append(")");

      if ("FOREIGN".equals(constraintType)) {
        editedConstraints.add(workArea.toString());
        workArea = new StringBuilder(" ".repeat(46));
        workArea.append("REFERENCES ").append(String.format("%-33s",
                                                            identifierDelimiter + tableConstraint.getReferenceTable().toUpperCase() + identifierDelimiter));
        editedConstraints.add(workArea.toString());
        workArea = new StringBuilder(" ".repeat(46));
        workArea.append("(").append(identifierDelimiter).append(String.join(identifierDelimiter + ", " + identifierDelimiter,
                                                                            tableConstraint.getReferenceColumns()).toUpperCase()).append(identifierDelimiter)
            .append(")");
      }

      if (i < tableConstraints.size() - 1) {
        workArea.append(",");
      }

      editedConstraints.add(workArea.toString());
    }

    return editedConstraints;
  }

  /**
   * Generate the code of the Java class AbstractGen[db_ticker]Schema.
   *
   * @param release            the current release number
   * @param schemaPojo         the schema POJO
   * @param tickerSymbolLower  the lower case ticker symbol
   * @param tickerSymbolPascal the Pascal case ticker symbol
   */
  private void generateClassDbmsSchema(String release, SchemaPojo schemaPojo, String tickerSymbolLower, String tickerSymbolPascal) {
    if (isDebug) {
      logger.debug("Start");
    }

    BufferedWriter bufferedWriter = null;
    try {
      bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File("src/main/java/ch/konnexions/db_seeder/generated/AbstractGen"
          + tickerSymbolPascal + "Schema.java"), false), StandardCharsets.UTF_8));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      System.exit(1);
    }

    generateCodeDbmsSchema(bufferedWriter,
                           release,
                           schemaPojo,
                           tickerSymbolLower,
                           tickerSymbolPascal);

    try {
      bufferedWriter.close();
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
    }

    if (isDebug) {
      logger.debug("End");
    }
  }

  /**
   * Generate the Java classes.
   *
   * @param release    the current release number
   * @param schemaPojo the schema POJO
   */
  private void generateClasses(String release, SchemaPojo schemaPojo) {
    if (isDebug)

    {
      logger.debug("Start");
    }

    MessageHandling.startProgress(logger,
                                  "Start generating Java classes");

    generateClassSchema(release);

    logger.info("===> Generated class: AbstractGenSchema");

    generateClassSeeder(release,
                        schemaPojo);

    logger.info("===> Generated class: AbstractGenSeeder");

    for (DbmsEnum tickerSymbol : DbmsEnum.values()) {
      String tickerSymbolLower  = tickerSymbol.getTickerSymbol();
      String tickerSymbolPascal = tickerSymbolLower.substring(0,
                                                              1).toUpperCase() + tickerSymbolLower.substring(1);

      generateClassDbmsSchema(release,
                              schemaPojo,
                              tickerSymbolLower,
                              tickerSymbolPascal);

      logger.info("===> Generated class: Abstract" + tickerSymbolPascal + "GenSchema");
    }

    logger.info("");
    logger.info("===> The Java class generation was successful.");
    logger.info("-".repeat(74));

    if (isDebug) {
      logger.debug("End");
    }
  }

  /**
   * Generate the Java class AbstractGenSchema.
   *  @param release    the current release number
   *
   */
  private void generateClassSchema(String release) {
    if (isDebug) {
      logger.debug("Start");
    }

    BufferedWriter bufferedWriter = null;
    try {
      bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(
          "src/main/java/ch/konnexions/db_seeder/generated/AbstractGenSchema.java"), false), StandardCharsets.UTF_8));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      System.exit(1);
    }

    generateCodeSchema(bufferedWriter,
                       release);

    try {
      bufferedWriter.close();
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
    }

    if (isDebug) {
      logger.debug("End");
    }
  }

  /**
   * Generate the Java class AbstractGenSeeder.
   *
   * @param release    the current release number
   * @param schemaPojo the schema POJO
   */
  private void generateClassSeeder(String release, SchemaPojo schemaPojo) {
    if (isDebug) {
      logger.debug("Start");
    }

    BufferedWriter bufferedWriter = null;
    try {
      bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(
          "src/main/java/ch/konnexions/db_seeder/generated/AbstractGenSeeder.java"), false), StandardCharsets.UTF_8));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      System.exit(1);
    }

    generateCodeSeeder(bufferedWriter,
                       release);

    try {
      bufferedWriter.close();
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
    }

    if (isDebug) {
      logger.debug("End");
    }
  }

  /**
   * Generate the code of the Java class AbstractGen[db_ticker]Schema.
   *
   * @param release            the current release number
   * @param schemaPojo         the schema POJO
   * @param tickerSymbolLower  the lower case ticker symbol
   * @param tickerSymbolPascal the Pascal case ticker symbol
   */
  @SuppressWarnings("unused")
  private void generateCodeDbmsSchema(BufferedWriter bw, String release, SchemaPojo schemaPojo, String tickerSymbolLower, String tickerSymbolPascal) {
    if (isDebug) {
      logger.debug("Start");
    }

    String            columnConstraint;
    String            columnNameLast      = "";
    ArrayList<Column> columns;

    String            dataType;
    String            dbmsName            = getDbmsName(tickerSymbolLower);

    ArrayList<String> editedColumnConstraints;
    String            editedColumnName;
    String            editedDataType;
    ArrayList<String> editedTableConstraints;

    String            identifierDelimiter = getIdentifierDelimiter(tickerSymbolLower);
    boolean           isColumnConstraints;
    boolean           isTableConstraints;

    StringBuffer      workArea;

    try {
      bw.append("package ch.konnexions.db_seeder.generated;");
      bw.newLine();
      bw.newLine();
      bw.append("import java.util.HashMap;");
      bw.newLine();
      bw.newLine();
      bw.append("import org.apache.log4j.Logger;");
      bw.newLine();
      bw.newLine();
      bw.append("/**");
      bw.newLine();
      bw.append(" * CREATE TABLE statements for a ").append(dbmsName).append(" DBMS. <br>");
      bw.newLine();
      bw.append(" * ");
      bw.newLine();
      bw.append(" * @author  GenerateSchema.class");
      bw.newLine();
      bw.append(" * @version ").append(release);
      bw.newLine();
      bw.append(" * @since   ").append(printDate);
      bw.newLine();
      bw.append(" */");
      bw.newLine();
      bw.append("public abstract class AbstractGen").append(tickerSymbolPascal).append("Schema extends AbstractGenSeeder {");
      bw.newLine();
      bw.newLine();
      bw.append("  public static final HashMap<String, String> createTableStmnts = createTableStmnts();");
      bw.newLine();
      bw.newLine();
      bw.append("  private static final Logger                 logger            = Logger.getLogger(AbstractGen").append(tickerSymbolPascal).append(
                                                                                                                                                    "Schema.class);");
      bw.newLine();
      bw.newLine();
      bw.append("  /**");
      bw.newLine();
      bw.append("   * Creates the CREATE TABLE statements.");
      bw.newLine();
      bw.append("   */");
      bw.newLine();
      bw.append("  @SuppressWarnings(\"preview\")");
      bw.newLine();
      bw.append("  private static HashMap<String, String> createTableStmnts() {");
      bw.newLine();
      bw.append("    HashMap<String, String> statements = new HashMap<>();");
      bw.newLine();
      bw.newLine();

      for (String tableName : genTableNames) {
        columns                = genTablesColumns.get(tableName);

        editedTableConstraints = editTableConstraints(tickerSymbolLower,
                                                      identifierDelimiter,
                                                      tableName,
                                                      genTablesTableConstraints.get(tableName));

        bw.append("    statements.put(TABLE_NAME_").append(tableName).append(",");
        bw.newLine();
        bw.append("                   \"\"\"");
        bw.newLine();
        bw.append("                   CREATE TABLE ").append(identifierDelimiter).append(tableName).append(identifierDelimiter).append(" (");
        bw.newLine();

        for (Column column : columns) {
          columnNameLast = column.getColumnName().toUpperCase();
        }

        for (Column column : columns) {
          editedColumnName        = column.getColumnName().toUpperCase();

          editedColumnConstraints = editColumnConstraints(tickerSymbolLower,
                                                          identifierDelimiter,
                                                          tableName,
                                                          column);

          editedDataType          = editDataType(tickerSymbolLower,
                                                 column);

          ArrayList<ColumnConstraint> columnConstraints = column.getColumnConstraints();

          workArea = new StringBuffer(" ".repeat(23));
          workArea.append(String.format("%-33s",
                                        identifierDelimiter + editedColumnName + identifierDelimiter));
          workArea.append(String.format("%-26s",
                                        editedDataType));

          if (isNotNull) {
            workArea.append("NOT NULL ");
          } else {
            workArea.append(" ".repeat(9));
          }

          if (editedColumnConstraints.size() > 0) {
            workArea.append(editedColumnConstraints.get(0));
          }

          if (editedTableConstraints.size() > 0 || editedColumnConstraints.size() > 1 || !columnNameLast.equals(editedColumnName)) {
            bw.append(StringUtils.stripEnd(workArea.toString(),
                                           null)).append(",");
          } else {
            bw.append(workArea.toString());
          }

          bw.newLine();

          if (editedColumnConstraints.size() > 1) {
            for (int i = 1; i < editedColumnConstraints.size(); i++) {
              bw.append(" ".repeat(79)).append(editedColumnConstraints.get(i));

              if (editedTableConstraints.size() > 0 || i < editedColumnConstraints.size() - 1 || !columnNameLast.equals(editedColumnName)) {
                bw.append(",");
              }

              bw.newLine();
            }
          }
        }

        for (String string : editedTableConstraints) {
          bw.append(string);
          bw.newLine();
        }

        bw.append("                   )");
        bw.newLine();
        bw.append("                   \"\"\");");
        bw.newLine();
        bw.newLine();
      }

      bw.append("    return statements;");
      bw.newLine();
      bw.append("  }");
      bw.newLine();
      bw.newLine();
      bw.append("  /**");
      bw.newLine();
      bw.append("   * Initialises a new abstract ").append(dbmsName).append(" schema object.");
      bw.newLine();
      bw.append("   *");
      bw.newLine();
      bw.append("   * @param dbmsTickerSymbol");
      bw.newLine();
      bw.append("   *            DBMS ticker symbol");
      bw.newLine();
      bw.append("   */");
      bw.newLine();
      bw.append("  public AbstractGen").append(tickerSymbolPascal).append("Schema(String dbmsTickerSymbol) {");
      bw.newLine();
      bw.append("    super(dbmsTickerSymbol);");
      bw.newLine();
      bw.newLine();
      bw.append("    if (isDebug) {");
      bw.newLine();
      bw.append("      logger.debug(\"Start Constructor - dbmsTickerSymbol=\" + dbmsTickerSymbol);");
      bw.newLine();
      bw.append("    }");
      bw.newLine();
      bw.newLine();
      bw.append("    if (isDebug) {");
      bw.newLine();
      bw.append("      logger.debug(\"End   Constructor\");");
      bw.newLine();
      bw.append("    }");
      bw.newLine();
      bw.append("  }");
      bw.newLine();

      if ("derby".equals(tickerSymbolLower) || "hsqldb".equals(tickerSymbolLower) || "h2".equals(tickerSymbolLower)) {
        bw.newLine();
        bw.append("  /**");
        bw.newLine();
        bw.append("   * Initialises a new abstract ").append(dbmsName).append(" schema object.");
        bw.newLine();
        bw.append("   *");
        bw.newLine();
        bw.append("   * @param dbmsTickerSymbol");
        bw.newLine();
        bw.append("   *            DBMS ticker symbol");
        bw.newLine();
        bw.append("   * @param isClient");
        bw.newLine();
        bw.append("   *            client database version");
        bw.newLine();
        bw.append("   */");
        bw.newLine();
        bw.append("  public AbstractGen").append(tickerSymbolPascal).append("Schema(String dbmsTickerSymbol, boolean isClient) {");
        bw.newLine();
        bw.append("    super(dbmsTickerSymbol, isClient);");
        bw.newLine();
        bw.newLine();
        bw.append("    if (isDebug) {");
        bw.newLine();
        bw.append("      logger.debug(\"Start Constructor - dbmsTickerSymbol=\" + dbmsTickerSymbol + \" - isClient=\" + isClient);");
        bw.newLine();
        bw.append("    }");
        bw.newLine();
        bw.newLine();
        bw.append("    if (isDebug) {");
        bw.newLine();
        bw.append("      logger.debug(\"End   Constructor\");");
        bw.newLine();
        bw.append("    }");
        bw.newLine();
        bw.append("  }");
        bw.newLine();
      }

      bw.append("}");
      bw.newLine();
      bw.newLine();
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
    }

    if (isDebug) {
      logger.debug("End");
    }
  }

  /**
   * Generate the code of the Java class AbstractGenSchema.
   *
   * @param bw         the buffered writer
   * @param release    the current release number
   */
  private void generateCodeSchema(BufferedWriter bw, String release) {
    if (isDebug) {
      logger.debug("Start");
    }

    try {
      bw.append("package ch.konnexions.db_seeder.generated;");
      bw.newLine();
      bw.newLine();
      bw.append("import java.util.Arrays;");
      bw.newLine();
      bw.append("import java.util.HashMap;");
      bw.newLine();
      bw.append("import java.util.Properties;");
      bw.newLine();
      bw.newLine();
      bw.append("import org.apache.log4j.Logger;");
      bw.newLine();
      bw.newLine();
      bw.append("import ch.konnexions.db_seeder.jdbc.AbstractJdbcSeeder;");
      bw.newLine();
      bw.newLine();
      bw.append("/**");
      bw.newLine();
      bw.append(" * Test Data Generator for a Database - Abstract Generated Schema.");
      bw.newLine();
      bw.append(" * <br>");
      bw.newLine();
      bw.append(" * @author  GenerateSchema.class");
      bw.newLine();
      bw.append(" * @version ").append(release);
      bw.newLine();
      bw.append(" * @since   ").append(printDate);
      bw.newLine();
      bw.append(" */");
      bw.newLine();
      bw.append("abstract class AbstractGenSchema extends AbstractJdbcSeeder {");
      bw.newLine();
      bw.newLine();

      for (String tableName : genTableNames) {
        bw.append("  protected static final String TABLE_NAME_").append(String.format("%-30s",
                                                                                      tableName)).append(" = \"").append(tableName).append("\";");
        bw.newLine();
      }

      bw.newLine();
      bw.append("  private static final Logger   logger                   = Logger.getLogger(AbstractGenSchema.class);");
      bw.newLine();
      bw.newLine();
      bw.append("  /**");
      bw.newLine();
      bw.append("   * Initialises a new abstract generated schema object.");
      bw.newLine();
      bw.append("   *");
      bw.newLine();
      bw.append("   * @param dbmsTickerSymbol DBMS ticker symbol ");
      bw.newLine();
      bw.append("   */");
      bw.newLine();
      bw.append("  public AbstractGenSchema(String dbmsTickerSymbol) {");
      bw.newLine();
      bw.append("    super(dbmsTickerSymbol);");
      bw.newLine();
      bw.newLine();
      bw.append("    if (isDebug) {");
      bw.newLine();
      bw.append("      logger.debug(\"Start Constructor - dbmsTickerSymbol=\" + dbmsTickerSymbol);");
      bw.newLine();
      bw.append("    }");
      bw.newLine();
      bw.newLine();
      bw.append("    initConstants();");
      bw.newLine();
      bw.newLine();
      bw.append("    if (isDebug) {");
      bw.newLine();
      bw.append("      logger.debug(\"End   Constructor\");");
      bw.newLine();
      bw.append("    }");
      bw.newLine();
      bw.append("  }");
      bw.newLine();
      bw.newLine();
      bw.append("  /**");
      bw.newLine();
      bw.append("   * Initialises a new abstract generated schema object.");
      bw.newLine();
      bw.append("   *");
      bw.newLine();
      bw.append("   * @param dbmsTickerSymbol DBMS ticker symbol ");
      bw.newLine();
      bw.append("   * @param isClient client database version");
      bw.newLine();
      bw.append("   */");
      bw.newLine();
      bw.append("  public AbstractGenSchema(String dbmsTickerSymbol, boolean isClient) {");
      bw.newLine();
      bw.append("    super(dbmsTickerSymbol, isClient);");
      bw.newLine();
      bw.newLine();
      bw.append("    if (isDebug) {");
      bw.newLine();
      bw.append("      logger.debug(\"Start Constructor - dbmsTickerSymbol=\" + dbmsTickerSymbol + \" - isClient=\" + isClient);");
      bw.newLine();
      bw.append("    }");
      bw.newLine();
      bw.newLine();
      bw.append("    initConstants();");
      bw.newLine();
      bw.newLine();
      bw.append("    if (isDebug) {");
      bw.newLine();
      bw.append("      logger.debug(\"End   Constructor\");");
      bw.newLine();
      bw.append("    }");
      bw.newLine();
      bw.append("  }");
      bw.newLine();
      bw.newLine();
      bw.append("  protected final Properties createColumnNames() {");
      bw.newLine();
      bw.append("    if (isDebug) {");
      bw.newLine();
      bw.append("      logger.debug(\"Start\");");
      bw.newLine();
      bw.append("    }");
      bw.newLine();
      bw.newLine();
      bw.append("    Properties columnName = new Properties();");
      bw.newLine();
      bw.newLine();
      bw.append("    // Encoding ASCII");
      bw.newLine();

      Set<String> genVarcharColumnNamesSorted = new TreeSet<>(genVarcharColumnNames);

      for (String columnName : genVarcharColumnNamesSorted) {
        bw.append("    columnName.setProperty(\"").append(columnName).append("_0\",");
        bw.newLine();
        bw.append("                           \"\");");
        bw.newLine();
      }

      bw.newLine();
      bw.append("    // Encoding ISO_8859_1");
      bw.newLine();
      bw.append("    boolean isIso_8859_1 = config.getEncodingIso_8859_1();");
      bw.newLine();
      bw.newLine();

      for (String columnName : genVarcharColumnNamesSorted) {
        bw.append("    columnName.setProperty(\"").append(columnName).append("_1\",");
        bw.newLine();
        bw.append("                           isIso_8859_1 ? \"ÁÇÉÍÑÓ_\" : \"NO_ISO_8859_1_\");");
        bw.newLine();
      }

      bw.newLine();
      bw.append("    // Encoding UTF_8");
      bw.newLine();
      bw.append("    boolean isUtf_8 = config.getEncodingUtf_8();");
      bw.newLine();
      bw.newLine();

      for (String columnName : genVarcharColumnNamesSorted) {
        bw.append("    columnName.setProperty(\"").append(columnName).append("_2\",");
        bw.newLine();
        bw.append("                           isUtf_8 ? \"缩略语地址电子邮件传真_\" : \"NO_UTF_8_\");");
        bw.newLine();
      }

      bw.newLine();
      bw.append("    if (isDebug) {");
      bw.newLine();
      bw.append("      logger.debug(\"End\");");
      bw.newLine();
      bw.append("    }");
      bw.newLine();
      bw.newLine();
      bw.append("    return columnName;");
      bw.newLine();
      bw.append("  }");
      bw.newLine();
      bw.newLine();
      bw.append("  /**");
      bw.newLine();
      bw.append("   * Initialising constants.");
      bw.newLine();
      bw.append("   */");
      bw.newLine();
      bw.append("  @SuppressWarnings(\"serial\")");
      bw.newLine();
      bw.append("  private void initConstants() {");
      bw.newLine();
      bw.append("    if (isDebug) {");
      bw.newLine();
      bw.append("      logger.debug(\"Start\");");
      bw.newLine();
      bw.append("    }");
      bw.newLine();
      bw.newLine();
      bw.append("    dmlStatements      = new HashMap<>() {");
      bw.newLine();
      bw.append("                         {");
      bw.newLine();

      for (String tableName : genTableNames) {
        ArrayList<String> columnNames          = genTableNameColumnNames.get(tableName);

        String            columnNamesWithComma = String.join(",",
                                                             columnNames);
        StringBuilder     questionMarks        = new StringBuilder();

        for (int i = 0; i < columnNames.size(); i++) {
          questionMarks.append(i == 0
              ? "?"
              : ",?");
        }

        bw.append("                           put(TABLE_NAME_").append(tableName).append(",");
        bw.newLine();
        bw.append("                               \"").append(columnNamesWithComma).append(") VALUES (").append(questionMarks.toString()).append("\");");
        bw.newLine();
      }

      bw.append("                         }");
      bw.newLine();
      bw.append("                       };");
      bw.newLine();
      bw.newLine();
      bw.append("    maxRowSizes        = new HashMap<>() {");
      bw.newLine();
      bw.append("                         {");
      bw.newLine();

      for (String tableName : genTableNames) {
        bw.append("                           put(TABLE_NAME_").append(tableName).append(",");
        bw.newLine();
        bw.append("                               ").append(genTableNumberOfRows.get(tableName).toString()).append(");");
        bw.newLine();
      }

      bw.append("                         }");
      bw.newLine();
      bw.append("                       };");
      bw.newLine();
      bw.newLine();

      int count = 0;

      for (String tableName : genTableHierarchy) {
        if (count == 0) {
          bw.append("    TABLE_NAMES_CREATE = Arrays.asList(TABLE_NAME_");
        } else {
          bw.append("                                       TABLE_NAME_");
        }

        bw.append(tableName);

        count++;

        if (genTableHierarchy.size() == count) {
          bw.append(");");
        } else {
          bw.append(",");
        }

        bw.newLine();
      }

      bw.newLine();

      Collections.reverse(genTableHierarchy);

      count = 0;

      for (String tableName : genTableHierarchy) {
        if (count == 0) {
          bw.append("    TABLE_NAMES_DROP   = Arrays.asList(TABLE_NAME_");
        } else {
          bw.append("                                       TABLE_NAME_");
        }

        bw.append(tableName);

        count++;

        if (genTableHierarchy.size() == count) {
          bw.append(");");
        } else {
          bw.append(",");
        }

        bw.newLine();
      }

      bw.newLine();
      bw.append("    if (isDebug) {");
      bw.newLine();
      bw.append("      logger.debug(\"End\");");
      bw.newLine();
      bw.append("    }");
      bw.newLine();
      bw.append("  }");
      bw.newLine();
      bw.append("}");
      bw.newLine();
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
    }

    if (isDebug) {
      logger.debug("End");
    }
  }

  /**
   * Generate the code of the Java class AbstractGenSeeder.
   *
   * @param bw         the buffered writer
   * @param release    the current release number
   */
  private void generateCodeSeeder(BufferedWriter bw, String release) {
    if (isDebug) {
      logger.debug("Start");
    }

    try {
      bw.append("package ch.konnexions.db_seeder.generated;");
      bw.newLine();
      bw.newLine();
      bw.append("import java.sql.PreparedStatement;");
      bw.newLine();
      bw.append("import java.sql.Timestamp;");
      bw.newLine();
      bw.append("import java.util.ArrayList;");
      bw.newLine();
      bw.append("import java.util.Arrays;");
      bw.newLine();
      bw.append("import java.util.List;");
      bw.newLine();
      bw.newLine();
      bw.append("import org.apache.log4j.Logger;");
      bw.newLine();
      bw.newLine();
      bw.append("/**");
      bw.newLine();
      bw.append(" * Test Data Generator for a Database - Abstract Generated Seeder.");
      bw.newLine();
      bw.append(" * <br>");
      bw.newLine();
      bw.append(" * @author  GenerateSchema.class");
      bw.newLine();
      bw.append(" * @version ").append(release);
      bw.newLine();
      bw.append(" * @since   ").append(printDate);
      bw.newLine();
      bw.append(" */");
      bw.newLine();
      bw.append("abstract class AbstractGenSeeder extends AbstractGenSchema {");
      bw.newLine();
      bw.newLine();
      bw.append("  private static final Logger logger = Logger.getLogger(AbstractGenSeeder.class);");
      bw.newLine();
      bw.newLine();
      bw.append("  /**");
      bw.newLine();
      bw.append("   * Initialises a new abstract generated seeder object.");
      bw.newLine();
      bw.append("   *");
      bw.newLine();
      bw.append("   * @param dbmsTickerSymbol DBMS ticker symbol ");
      bw.newLine();
      bw.append("   */");
      bw.newLine();
      bw.append("  public AbstractGenSeeder(String dbmsTickerSymbol) {");
      bw.newLine();
      bw.append("    super(dbmsTickerSymbol);");
      bw.newLine();
      bw.newLine();
      bw.append("    if (isDebug) {");
      bw.newLine();
      bw.append("      logger.debug(\"Start Constructor - dbmsTickerSymbol=\" + dbmsTickerSymbol);");
      bw.newLine();
      bw.append("    }");
      bw.newLine();
      bw.newLine();
      bw.append("    if (isDebug) {");
      bw.newLine();
      bw.append("      logger.debug(\"End   Constructor\");");
      bw.newLine();
      bw.append("    }");
      bw.newLine();
      bw.append("  }");
      bw.newLine();
      bw.newLine();
      bw.append("  /**");
      bw.newLine();
      bw.append("   * Initialises a new abstract generated seeder object.");
      bw.newLine();
      bw.append("   *");
      bw.newLine();
      bw.append("   * @param dbmsTickerSymbol DBMS ticker symbol ");
      bw.newLine();
      bw.append("   * @param isClient client database version");
      bw.newLine();
      bw.append("   */");
      bw.newLine();
      bw.append("  public AbstractGenSeeder(String dbmsTickerSymbol, boolean isClient) {");
      bw.newLine();
      bw.append("    super(dbmsTickerSymbol, isClient);");
      bw.newLine();
      bw.newLine();
      bw.append("    if (isDebug) {");
      bw.newLine();
      bw.append("      logger.debug(\"Start Constructor - dbmsTickerSymbol=\" + dbmsTickerSymbol + \" - isClient=\" + isClient);");
      bw.newLine();
      bw.append("    }");
      bw.newLine();
      bw.newLine();
      bw.append("    if (isDebug) {");
      bw.newLine();
      bw.append("      logger.debug(\"End   Constructor\");");
      bw.newLine();
      bw.append("    }");
      bw.newLine();
      bw.append("  }");
      bw.newLine();
      bw.newLine();
      bw.append("  /**");
      bw.newLine();
      bw.append("   * Creates a content value of type BIGINT.");
      bw.newLine();
      bw.append("   *");
      bw.newLine();
      bw.append("   * @param tableName         the table name");
      bw.newLine();
      bw.append("   * @param columnName        the column name");
      bw.newLine();
      bw.append("   * @param rowNo             the current row number");
      bw.newLine();
      bw.append("   */");
      bw.newLine();
      bw.append("  @Override");
      bw.newLine();
      bw.append("  protected final long getContentBigint(String tableName, String columnName, long rowNo) {");
      bw.newLine();
      bw.append("    long result = super.getContentBigint(tableName,");
      bw.newLine();
      bw.append("                                         columnName,");
      bw.newLine();
      bw.append("                                         rowNo);");
      bw.newLine();
      bw.newLine();
      bw.append("    return result;");
      bw.newLine();
      bw.append("  }");
      bw.newLine();
      bw.newLine();
      bw.append("  /**");
      bw.newLine();
      bw.append("   * Creates a content value of type BLOB.");
      bw.newLine();
      bw.append("   *");
      bw.newLine();
      bw.append("   * @param tableName         the table name");
      bw.newLine();
      bw.append("   * @param columnName        the column name");
      bw.newLine();
      bw.append("   * @param rowNo             the current row number");
      bw.newLine();
      bw.append("   */");
      bw.newLine();
      bw.append("  @Override");
      bw.newLine();
      bw.append("  protected final byte[] getContentBlob(String tableName, String columnName, int rowNo) {");
      bw.newLine();
      bw.append("    byte[] result = super.getContentBlob(tableName,");
      bw.newLine();
      bw.append("                                         columnName,");
      bw.newLine();
      bw.append("                                         rowNo);");
      bw.newLine();
      bw.newLine();
      bw.append("    return result;");
      bw.newLine();
      bw.append("  }");
      bw.newLine();
      bw.newLine();
      bw.append("  /**");
      bw.newLine();
      bw.append("   * Creates a content value of type CLOB.");
      bw.newLine();
      bw.append("   *");
      bw.newLine();
      bw.append("   * @param tableName         the table name");
      bw.newLine();
      bw.append("   * @param columnName        the column name");
      bw.newLine();
      bw.append("   * @param rowNo             the current row number");
      bw.newLine();
      bw.append("   */");
      bw.newLine();
      bw.append("  @Override");
      bw.newLine();
      bw.append("  protected final String getContentClob(String tableName, String columnName, int rowNo) {");
      bw.newLine();
      bw.append("    String result = super.getContentClob(tableName,");
      bw.newLine();
      bw.append("                                         columnName,");
      bw.newLine();
      bw.append("                                         rowNo);");
      bw.newLine();
      bw.newLine();
      bw.append("    return result;");
      bw.newLine();
      bw.append("  }");
      bw.newLine();
      bw.newLine();
      bw.append("  /**");
      bw.newLine();
      bw.append("   * Creates a content value of type foreign key value.");
      bw.newLine();
      bw.append("   *");
      bw.newLine();
      bw.append("   * @param tableName         the table name");
      bw.newLine();
      bw.append("   * @param columnName        the column name");
      bw.newLine();
      bw.append("   * @param rowNo             the current row number");
      bw.newLine();
      bw.append("   * @param fkList            the existing foreign keys");
      bw.newLine();
      bw.append("   */");
      bw.newLine();
      bw.append("  @Override");
      bw.newLine();
      bw.append("  protected final Object getContentFk(String tableName, String columnName, int rowNo, ArrayList<Object> fkList) {");
      bw.newLine();
      bw.append("    Object result = super.getContentFk(tableName,");
      bw.newLine();
      bw.append("                                       columnName,");
      bw.newLine();
      bw.append("                                       rowNo,");
      bw.newLine();
      bw.append("                                       fkList);");
      bw.newLine();
      bw.newLine();
      bw.append("    return result;");
      bw.newLine();
      bw.append("  }");
      bw.newLine();
      bw.newLine();
      bw.append("  /**");
      bw.newLine();
      bw.append("   * Creates a content value of type TIMESTAMP.");
      bw.newLine();
      bw.append("   *");
      bw.newLine();
      bw.append("   * @param tableName         the table name");
      bw.newLine();
      bw.append("   * @param columnName        the column name");
      bw.newLine();
      bw.append("   * @param rowNo             the current row number");
      bw.newLine();
      bw.append("   */");
      bw.newLine();
      bw.append("  @Override");
      bw.newLine();
      bw.append("  protected final Timestamp getContentTimestamp(String tableName, String columnName, int rowNo) {");
      bw.newLine();
      bw.append("    Timestamp result = super.getContentTimestamp(tableName,");
      bw.newLine();
      bw.append("                                                 columnName,");
      bw.newLine();
      bw.append("                                                 rowNo);");
      bw.newLine();
      bw.newLine();
      bw.append("    return result;");
      bw.newLine();
      bw.append("  }");
      bw.newLine();
      bw.newLine();
      bw.append("  /**");
      bw.newLine();
      bw.append("   * Creates a content value of type VARCHAR.");
      bw.newLine();
      bw.append("   *");
      bw.newLine();
      bw.append("   * @param tableName         the table name");
      bw.newLine();
      bw.append("   * @param columnName        the column name");
      bw.newLine();
      bw.append("   * @param rowNo             the current row number");
      bw.newLine();
      bw.append("   * @param size              the column size");
      bw.newLine();
      bw.append("   * @param lowerRange        the lower range");
      bw.newLine();
      bw.append("   * @param upperRange        the upper range");
      bw.newLine();
      bw.append("   * @param validValues       the valid values");
      bw.newLine();
      bw.append("   */");
      bw.newLine();
      bw.append("  @Override");
      bw.newLine();
      bw.append("  protected final String getContentVarchar(String tableName, String columnName, int rowNo, int size, String lowerRange, String upperRange, List<String> validValues) {");
      bw.newLine();
      bw.append("    String result = super.getContentVarchar(tableName,");
      bw.newLine();
      bw.append("                                            columnName,");
      bw.newLine();
      bw.append("                                            rowNo,");
      bw.newLine();
      bw.append("                                            size,");
      bw.newLine();
      bw.append("                                            lowerRange,");
      bw.newLine();
      bw.append("                                            upperRange,");
      bw.newLine();
      bw.append("                                            validValues);");
      bw.newLine();
      bw.newLine();
      bw.append("    return result;");
      bw.newLine();
      bw.append("  }");
      bw.newLine();
      bw.newLine();
      bw.append("  protected final void insertTable(PreparedStatement preparedStatement,");
      bw.newLine();
      bw.append("                                   final String tableName,");
      bw.newLine();
      bw.append("                                   final int rowNo) {");
      bw.newLine();
      bw.append("    if (isDebug) {");
      bw.newLine();
      bw.append("      logger.debug(\"Start\");");
      bw.newLine();
      bw.append("    }");
      bw.newLine();
      bw.newLine();
      bw.append("    switch (tableName) {");
      bw.newLine();

      for (String tableName : genTableNames) {
        bw.append("    case TABLE_NAME_").append(tableName).append(" -> prepDmlStmntInsert").append(getTableNamePascalCase(tableName)).append(
                                                                                                                                              "(preparedStatement,");
        bw.newLine();
        bw.append("                                                   rowNo);");
        bw.newLine();
      }

      bw.append("    default -> throw new RuntimeException(\"Not yet implemented - database table : \" + String.format(FORMAT_TABLE_NAME,");
      bw.newLine();
      bw.append("                                                                                                    tableName));");
      bw.newLine();
      bw.append("    }");
      bw.newLine();
      bw.newLine();
      bw.append("    if (isDebug) {");
      bw.newLine();
      bw.append("      logger.debug(\"End\");");
      bw.newLine();
      bw.append("    }");
      bw.newLine();
      bw.append("  }");
      bw.newLine();
      bw.newLine();

      for (String tableName : genTableNames) {
        bw.append("  private void prepDmlStmntInsert").append(getTableNamePascalCase(tableName)).append("(PreparedStatement preparedStatement, int rowNo) {");
        bw.newLine();
        bw.append("    if (isDebug) {");
        bw.newLine();
        bw.append("      logger.debug(\"Start\");");
        bw.newLine();
        bw.append("    }");
        bw.newLine();
        bw.newLine();
        bw.append("    int i = 0;");
        bw.newLine();
        bw.newLine();

        ArrayList<Column> columns = genTablesColumns.get(tableName);

        for (Column column : columns) {
          String                      columnName        = column.getColumnName().toUpperCase();
          String                      dataType          = column.getDataType().toUpperCase();

          ArrayList<ColumnConstraint> columnConstraints = column.getColumnConstraints();

          String                      referenceTable    = getColumnConstraintForeign(columnConstraints);
          boolean                     isNotNull         = getColumnConstraintNotNull(columnConstraints);
          boolean                     isPrimaryKey      = getColumnConstraintPrimary(columnConstraints);

          if (isPrimaryKey) {
            isNotNull = true;
          }

          switch (dataType) {
          case "BIGINT":
            if ("".equals(referenceTable)) {
              bw.append("    prepStmntColBigint").append(isNotNull
                  ? ""
                  : "Opt").append("(preparedStatement,");
              bw.newLine();
              bw.append("                              \"").append(tableName).append("\",");
              bw.newLine();
              bw.append("                              \"").append(columnName).append("\",");
              bw.newLine();
              bw.append("                              ++i,");
              bw.newLine();
              bw.append("                              rowNo);");
              bw.newLine();
            } else {
              bw.append("    prepStmntColFk").append(isNotNull
                  ? ""
                  : "Opt").append("(preparedStatement,");
              bw.newLine();
              bw.append("                            \"").append(tableName).append("\",");
              bw.newLine();
              bw.append("                            \"").append(columnName).append("\",");
              bw.newLine();
              bw.append("                            ++i,");
              bw.newLine();
              bw.append("                            rowNo,");
              bw.newLine();
              bw.append("                            pkLists.get(TABLE_NAME_").append(referenceTable).append("));");
              bw.newLine();
            }

            break;
          case "BLOB":
            bw.append("    prepStmntColBlob").append(isNotNull
                ? ""
                : "Opt").append("(preparedStatement,");
            bw.newLine();
            bw.append("                              \"").append(tableName).append("\",");
            bw.newLine();
            bw.append("                              \"").append(columnName).append("\",");
            bw.newLine();
            bw.append("                              ++i,");
            bw.newLine();
            bw.append("                              rowNo);");
            bw.newLine();

            break;
          case "CLOB":
            bw.append("      prepStmntColClob").append(isNotNull
                ? ""
                : "Opt").append("(preparedStatement,");
            bw.newLine();
            bw.append("                                \"").append(tableName).append("\",");
            bw.newLine();
            bw.append("                                \"").append(columnName).append("\",");
            bw.newLine();
            bw.append("                                ++i,");
            bw.newLine();
            bw.append("                                rowNo);");
            bw.newLine();

            break;
          case "TIMESTAMP":
            bw.append("    prepStmntColTimestamp").append(isNotNull
                ? ""
                : "Opt").append("(preparedStatement,");
            bw.newLine();
            bw.append("                               \"").append(tableName).append("\",");
            bw.newLine();
            bw.append("                               \"").append(columnName).append("\",");
            bw.newLine();
            bw.append("                               ++i,");
            bw.newLine();
            bw.append("                               rowNo);");
            bw.newLine();
            break;
          case "VARCHAR":
            bw.append("    prepStmntColVarchar").append(isNotNull
                ? ""
                : "Opt").append("(preparedStatement,");
            bw.newLine();
            bw.append("                             \"").append(tableName).append("\",");
            bw.newLine();
            bw.append("                             \"").append(columnName).append("\",");
            bw.newLine();
            bw.append("                             ++i,");
            bw.newLine();
            bw.append("                             rowNo,");
            bw.newLine();
            bw.append("                             ").append(String.valueOf(column.getSize())).append(",");
            bw.newLine();

            if (column.getLowerRangeString() == null && column.getUpperRangeString() == null) {
              bw.append("                             null,");
              bw.newLine();
              bw.append("                             null,");
              bw.newLine();
            } else {
              bw.append("                             \"").append(column.getLowerRangeString()).append("\",");
              bw.newLine();
              bw.append("                             \"").append(column.getUpperRangeString()).append("\",");
              bw.newLine();
            }

            if (column.getValidValuesString() == null) {
              bw.append("                             null);");
              bw.newLine();
            } else {
              bw.append("                             Arrays.asList(\"").append(String.join(",",
                                                                                            column.getValidValuesString()).replace(",",
                                                                                                                                   "\",\"")).append("\"));");
              bw.newLine();
            }

            break;
          default:
            MessageHandling.abortProgram(logger,
                                         "Database table: '" + tableName + "' column: '" + columnName + "' - Unknown data type '" + dataType + "'");
          }
        }

        bw.append("    if (isDebug) {");
        bw.newLine();
        bw.append("      logger.debug(\"End\");");
        bw.newLine();
        bw.append("    }");
        bw.newLine();
        bw.append("  }");
        bw.newLine();
        bw.newLine();
      }

      bw.append("}");
      bw.newLine();
      bw.newLine();
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
    }

    if (isDebug) {
      logger.debug("End");
    }
  }

  /**
  * Test Data Generator for a Database - Generate a complete schema.
   *
   * @param release the release identification
   * @param fileJsonName the name of the db_seeder schema definition file
   */
  public final void generateSchema(String release, String fileJsonName) {
    if (isDebug) {
      logger.debug("Start");
    }

    String     fileSchemaName = "src/main/resources/db_seeder_schema.schema.json";

    JSONObject jsonSchema     = null;

    try {
      jsonSchema = new JSONObject(new JSONTokener(new FileInputStream(fileSchemaName)));
    } catch (JSONException | FileNotFoundException e) {
      e.printStackTrace();
      System.exit(1);
    }

    JSONObject jsonSubject = null;

    try {
      jsonSubject = new JSONObject(new JSONTokener(new FileInputStream(fileJsonName)));
    } catch (JSONException | FileNotFoundException e) {
      e.printStackTrace();
      System.exit(1);
    }

    validateJson(jsonSchema,
                 jsonSubject);

    SchemaPojo schemaPojo = transformJson2Pojo(fileJsonName);

    validateSchema(schemaPojo);

    generateClasses(release,
                    schemaPojo);

    if (isDebug) {
      logger.debug("End");
    }
  }

  private String getColumnConstraintForeign(ArrayList<ColumnConstraint> columnConstraints) {
    if (columnConstraints != null) {
      for (ColumnConstraint columnConstraint : columnConstraints) {
        if ("FOREIGN".equals(columnConstraint.getConstraintType().toUpperCase())) {
          return columnConstraint.getReferenceTable().toUpperCase();
        }
      }
    }

    return "";
  }

  private boolean getColumnConstraintNotNull(ArrayList<ColumnConstraint> columnConstraints) {
    if (columnConstraints != null) {
      for (ColumnConstraint columnConstraint : columnConstraints) {
        if ("NOT NULL".equals(columnConstraint.getConstraintType().toUpperCase())) {
          return true;
        }
      }
    }

    return false;
  }

  private boolean getColumnConstraintPrimary(ArrayList<ColumnConstraint> columnConstraints) {
    if (columnConstraints != null) {
      for (ColumnConstraint columnConstraint : columnConstraints) {
        if ("PRIMARY".equals(columnConstraint.getConstraintType().toUpperCase())) {
          return true;
        }
      }
    }

    return false;
  }

  private String getTableNamePascalCase(String tableName) {
    StringBuilder tableNamePascalCase = new StringBuilder();

    boolean       isUnderline         = true;

    for (String ch : tableName.split("")) {
      if ("_".equals(ch)) {
        isUnderline = true;
        continue;
      }

      if (isUnderline) {
        tableNamePascalCase.append(ch);
        isUnderline = false;
      } else {
        tableNamePascalCase.append(ch.toLowerCase());
      }
    }

    return tableNamePascalCase.toString();
  }

  /**
   * Transform the schema in JSON format to POJO.
   *
   * @param fileJsonName the file name of the JSON schema file
   * @return the schema POJO
   */
  private SchemaPojo transformJson2Pojo(String fileJsonName) {
    if (isDebug) {
      logger.debug("Start");
    }

    MessageHandling.startProgress(logger,
                                  "Start transforming JSON to a POJO");

    Gson       gson       = new GsonBuilder().serializeNulls().setPrettyPrinting().create();

    SchemaPojo schemaPojo = null;

    try {
      schemaPojo = gson.fromJson(Files.newBufferedReader(Paths.get(fileJsonName)),
                                 SchemaPojo.class);
    } catch (JsonSyntaxException | IOException | JsonIOException e) {
      MessageHandling.abortProgram(logger,
                                   e.getMessage());
    }

    logger.info("===> The JSON to POJO transformation was successful.");
    logger.info("-".repeat(74));

    if (isDebug) {
      logger.debug("End");
    }

    return schemaPojo;
  }

  /**
   * Validate the JSON format of the schema definition.
   * 
   * @param jsonSchema the JSON schema definition
   * @param jsonSubject the db_seeder schema definition
   */
  private void validateJson(JSONObject jsonSchema, JSONObject jsonSubject) {
    if (isDebug) {
      logger.debug("Start");
    }

    MessageHandling.startProgress(logger,
                                  "Start JSON validation");

    Schema schema = SchemaLoader.load(jsonSchema);

    try {
      schema.validate(jsonSubject);
    } catch (ValidationException e) {
      MessageHandling.abortProgram(logger,
                                   e.getMessage());
    }

    logger.info("===> The JSON validation was successful.");
    logger.info("-".repeat(74));

    if (isDebug) {
      logger.debug("End");
    }
  }

  /**
   * Validate the POJO format of the schema definition.
   *
   * @param schemaPojo the schema POJO
   */
  private void validateSchema(SchemaPojo schemaPojo) {
    if (isDebug) {
      logger.debug("Start");
    }

    MessageHandling.startProgress(logger,
                                  "Start validating the schema definition");

    // -------------------------------------------------------------------------
    // defaultNumberOfRows
    // -------------------------------------------------------------------------

    int valDefaultNumberOfRows = 0;

    if (schemaPojo.getGlobals().getDefaultNumberOfRows() == null) {
      logger.error("'defaultNumberOfRows' missing (null)");
      errors++;
    } else {
      valDefaultNumberOfRows = schemaPojo.getGlobals().getDefaultNumberOfRows();

      if (valDefaultNumberOfRows <= 0) {
        logger.error("'defaultNumberOfRows' must be greater than zero");
        errors++;
      }
    }

    // -------------------------------------------------------------------------
    // tables
    // -------------------------------------------------------------------------

    if (schemaPojo.getTables() == null) {
      logger.error("Definition 'tables' missing (null)");
      errors++;
    } else {
      valTables        = schemaPojo.getTables();

      valTablesColumns = new HashMap<>();

      if (valTables.size() == 0) {
        logger.error("No definition 'tables' found");
        errors++;
      } else {

        // ---------------------------------------------------------------------
        // Definition database table
        // ---------------------------------------------------------------------

        int    numberOfRows;
        String tableName;

        for (Table table : valTables) {
          if (table.getTableName() == null) {
            logger.error("Definition 'tableName' missing (null)");
            errors++;
            continue;
          }

          tableName = table.getTableName().toUpperCase();

          if (valTablesColumns.containsKey(tableName)) {
            logger.error("'tableName': '" + tableName + "' - 'tableName' is not unique");
            errors++;
            continue;
          }

          if (table.getNumberOfRows() == null) {
            numberOfRows = valDefaultNumberOfRows;
          } else {
            numberOfRows = table.getNumberOfRows();

            if (numberOfRows < 0) {
              logger.error("'tableName': '" + tableName + " - 'numberOfRows' must not be negative");
              errors++;
            } else if (numberOfRows == 0) {
              numberOfRows = valDefaultNumberOfRows;
            }
          }

          // -------------------------------------------------------------------
          // columns
          // -------------------------------------------------------------------

          if (table.getColumns() == null) {
            logger.error("'tableName': '" + tableName + " - definition 'columns' missing (null)");
            errors++;
            continue;
          }

          ArrayList<Column> columns = table.getColumns();

          if (columns.size() == 0) {
            logger.error("'tableName': '" + tableName + " - no definition 'columns' found");
            errors++;
            continue;
          }

          HashSet<String> valColumnNames = validateSchemaColumns(tableName,
                                                                 columns);

          if (errors == 0) {
            genTablesColumns.put(tableName,
                                 columns);
            genTableNames.add(tableName);
            genTableNumberOfRows.put(tableName,
                                     numberOfRows);

            valTablesColumns.put(tableName,
                                 valColumnNames);
          }
        }

        // ---------------------------------------------------------------------
        // Column and table constraints
        // ---------------------------------------------------------------------

        if (errors == 0) {
          Collections.sort(genTableNames);

          for (String tableNameSorted : genTableNames) {
            valTableNameForeignKeys.put(tableNameSorted,
                                        new HashSet<>());
          }

          validateSchemaTableConstraints();

          validateSchemaColumnConstraints();

          if (errors == 0) {
            validateSchemaForeignKeys();
          }
        }
      }
    }

    if (errors > 0) {
      MessageHandling.abortProgram(logger,
                                   "Program abort: " + errors + " error(s) occurred during the schema validation.");
    }

    logger.info("===> The schema validation was successful.");
    logger.info("-".repeat(74));

    if (isDebug) {
      logger.debug("End");
    }
  }

  /**
   * Validate the column constraint definitions.
   */
  private void validateSchemaColumnConstraints() {
    if (isDebug) {
      logger.debug("Start");
    }

    ArrayList<Column>           columns;
    ArrayList<ColumnConstraint> columnConstraints;
    String                      columnName;
    String                      constraintType;
    String                      referenceColumn;
    String                      referenceTable;
    String                      tableName;

    HashSet<String>             valRefrenceColumnNames;

    for (Table table : valTables) {
      tableName = table.getTableName().toUpperCase();

      columns   = table.getColumns();

      for (Column column : columns) {
        columnConstraints = column.getColumnConstraints();

        if (columnConstraints == null || columnConstraints.size() == 0) {
          continue;
        }

        columnName = column.getColumnName();

        for (ColumnConstraint columnConstraint : columnConstraints) {
          if (columnConstraint.getConstraintType() == null) {
            logger.error("'tableName': '" + tableName + " 'columnName': '" + columnName + " - 'constraintType' missing (null)");
            errors++;
            continue;
          }

          constraintType = columnConstraint.getConstraintType().toUpperCase();

          switch (constraintType) {
          case "FOREIGN" -> {
            if (columnConstraint.getReferenceTable() == null) {
              logger.error("'tableName': '" + tableName + " 'columnName': '" + columnName + "' 'constraintType': '" + constraintType
                  + "' - 'referenceTable' is missing");
              errors++;
              continue;
            }

            referenceTable = columnConstraint.getReferenceTable().toUpperCase();

            if (tableName.equals(referenceTable)) {
              logger.error("'tableName': '" + tableName + " 'columnName': '" + columnName + "' 'constraintType': '" + constraintType + "' - 'referenceTable': '"
                  + referenceTable + "' must be a different database table");
              errors++;
              continue;
            }

            if (!(valTablesColumns.containsKey(referenceTable))) {
              logger.error("'tableName': '" + tableName + " 'columnName': '" + columnName + "' 'constraintType': '" + constraintType + "' - 'referenceTable': '"
                  + referenceTable + "' is not defined");
              errors++;
              continue;
            }

            if (columnConstraint.getReferenceColumn() == null) {
              logger.error("'tableName': '" + tableName + " 'columnName': '" + columnName + "' 'constraintType': '" + constraintType
                  + "' - 'referenceColumn' is missing");
              errors++;
              continue;
            }

            referenceColumn        = columnConstraint.getReferenceColumn().toUpperCase();

            valRefrenceColumnNames = valTablesColumns.get(referenceTable);

            if (!valRefrenceColumnNames.contains(referenceColumn)) {
              logger.error("'tableName': '" + tableName + " 'columnName': '" + columnName + "' 'constraintType': '" + constraintType
                  + "' - 'referenceColumn': '" + referenceColumn + "' is not defined in 'referenceTable': '" + referenceTable + "'");
              errors++;
              continue;
            }

            if (errors == 0) {
              HashSet<String> referenceTables = valTableNameForeignKeys.get(tableName);
              referenceTables.add(referenceTable);
              valTableNameForeignKeys.put(tableName,
                                          referenceTables);
            }
          }
          case "NOT NULL", "PRIMARY", "UNIQUE" -> {
            if (columnConstraint.getReferenceTable() != null) {
              logger.error("'tableName': '" + tableName + " 'columnName': '" + columnName + "' 'constraintType': '" + constraintType
                  + "' - 'referenceTable' not allowed");
              errors++;
            }
            if (columnConstraint.getReferenceColumn() != null) {
              logger.error("'tableName': '" + tableName + " 'columnName': '" + columnName + "' 'constraintType': '" + constraintType
                  + "' - 'referenceColumn' not allowed");
              errors++;
            }
          }
          default -> {
            logger.error("'tableName': '" + tableName + " 'columnName': '" + columnName + "' - unknown 'constraintType': '" + constraintType + "'");
            errors++;
          }
          }
        }
      }
    }

    if (isDebug) {
      logger.debug("End");
    }
  }

  /**
   * Validate the column definitions of a specific database table.
   *
   * @param tableName the name of the database table
   * @param columns the database table columns columns
   * 
   * @return the hash set containing the column names
   */
  private HashSet<String> validateSchemaColumns(String tableName, ArrayList<Column> columns) {
    if (isDebug) {
      logger.debug("Start");
    }

    HashSet<String>   columnNames      = new HashSet<>();

    String            columnName;
    ArrayList<String> tableColumnNames = new ArrayList<>();
    String            dataType;
    Integer           size;

    // -------------------------------------------------------------------------
    // Definition database table column
    // -------------------------------------------------------------------------

    for (Column column : columns) {

      if (column.getColumnName() == null) {
        logger.error("'tableName': '" + tableName + " - definition 'columnName' missing (null)");
        errors++;
        continue;
      }

      columnName = column.getColumnName().toUpperCase();

      if (!columnNames.add(columnName)) {
        logger.error("'tableName': '" + tableName + " 'columnName': '" + columnName + "' - 'columnName' is not unique");
        errors++;
        continue;
      }

      // -----------------------------------------------------------------------
      // dataType
      // -----------------------------------------------------------------------

      if (column.getDataType() == null) {
        logger.error("'tableName': '" + tableName + " 'columnName': '" + columnName + "' - 'dataType' is missing (null)");
        errors++;
        continue;
      }

      dataType = column.getDataType().toUpperCase();

      switch (dataType) {
      case "BIGINT", "BLOB", "CLOB", "TIMESTAMP" -> {
        if (column.getSize() != null) {
          logger.error("'tableName': '" + tableName + " 'columnName': '" + columnName + "' 'dataType': '" + dataType + "' - 'size' not allowed");
          errors++;
        }

        if (column.getPrecision() != null) {
          logger.error("'tableName': '" + tableName + " 'columnName': '" + columnName + "' 'dataType': '" + dataType + "' - 'precision' not allowed");
          errors++;
        }
      }
      case "VARCHAR" -> {
        if (column.getSize() == null) {
          logger.error("'tableName': '" + tableName + " 'columnName': '" + columnName + "' 'dataType': '" + dataType + "' - 'size' missing (null)");
          errors++;
          continue;
        }

        size = column.getSize();

        if (size < 1 || size > 4000) {
          logger.error("'tableName': '" + tableName + " 'columnName': '" + columnName + "' 'dataType': '" + dataType
              + "' - 'size' must have a value between 1 and 4000");
          errors++;
        }

        if (column.getPrecision() != null) {
          logger.error("'tableName': '" + tableName + " 'columnName': '" + columnName + "' 'dataType': '" + dataType + "' - 'precision' not allowed");
          errors++;
        }
      }
      default -> {
        logger.error("'tableName': '" + tableName + " 'columnName': '" + columnName + "' - unknown 'dataType' '" + dataType + "'");
        errors++;
      }
      }

      // -----------------------------------------------------------------------
      // lowerRange, upperRange and validValues
      // -----------------------------------------------------------------------

      if ("BIGINT".equals(dataType)) {
        if (column.getLowerRangeInteger() != null || column.getUpperRangeInteger() != null) {
          if (column.getLowerRangeInteger() == null) {
            logger.error("'tableName': '" + tableName + " 'columnName': '" + columnName + "' 'dataType': '" + dataType + "' - 'lowerRangeInteger' is missing");
            errors++;
          }

          if (column.getUpperRangeInteger() == null) {
            logger.error("'tableName': '" + tableName + " 'columnName': '" + columnName + "' 'dataType': '" + dataType + "' - 'upperRangeInteger' is missing");
            errors++;
          }

          if (column.getLowerRangeInteger() != null && column.getUpperRangeInteger() != null) {
            if (column.getLowerRangeInteger() > column.getUpperRangeInteger()) {
              logger.error("'tableName': '" + tableName + " 'columnName': '" + columnName + "' 'dataType': '" + dataType
                  + "' - 'lowerRangeInteger' greater 'upperRangeInteger'");
              errors++;
            }
          }

          if (column.getValidValuesInteger() != null) {
            logger.error("'tableName': '" + tableName + " 'columnName': '" + columnName + "' 'dataType': '" + dataType
                + "' - 'lowerRangeInteger', 'upperRangeInteger' and 'validValuesInteger' are not allowed at the same time");
            errors++;
          }
        }
      }

      if (!"BIGINT".equals(dataType)) {
        if (column.getDefaultValueInteger() != null) {
          logger.error("'tableName': '" + tableName + " 'columnName': '" + columnName + "' 'dataType': '" + dataType
              + "' - 'defaultValueInteger' is not allowed");
          errors++;
        }

        if (column.getLowerRangeInteger() != null) {
          logger.error("'tableName': '" + tableName + " 'columnName': '" + columnName + "' 'dataType': '" + dataType
              + "' - 'lowerRangeInteger' is not allowed");
          errors++;
        }

        if (column.getUpperRangeInteger() != null) {
          logger.error("'tableName': '" + tableName + " 'columnName': '" + columnName + "' 'dataType': '" + dataType
              + "' - 'upperRangeInteger' is not allowed");
          errors++;
        }

        if (column.getValidValuesInteger() != null) {
          logger.error("'tableName': '" + tableName + " 'columnName': '" + columnName + "' 'dataType': '" + dataType
              + "' - 'validValuesInteger' is not allowed");
          errors++;
        }
      }

      if ("VARCHAR".equals(dataType)) {
        if (column.getLowerRangeString() != null || column.getUpperRangeString() != null) {
          if (column.getLowerRangeString() == null) {
            logger.error("'tableName': '" + tableName + " 'columnName': '" + columnName + "' 'dataType': '" + dataType + "' - 'lowerRangeString' is missing");
            errors++;
          }

          if (column.getUpperRangeString() == null) {
            logger.error("'tableName': '" + tableName + " 'columnName': '" + columnName + "' 'dataType': '" + dataType + "' - 'upperRangeString' is missing");
            errors++;
          }

          if (column.getLowerRangeString() != null && column.getUpperRangeString() != null) {
            if (column.getLowerRangeString().compareTo(column.getUpperRangeString()) > 0) {
              logger.error("'tableName': '" + tableName + " 'columnName': '" + columnName + "' 'dataType': '" + dataType
                  + "' - 'lowerRangeString' greater 'upperRangeString'");
              errors++;
            }
          }

          if (column.getValidValuesString() != null) {
            logger.error("'tableName': '" + tableName + " 'columnName': '" + columnName + "' 'dataType': '" + dataType
                + "' - 'lowerRangeString', 'upperRangeString' and 'validValuesString' are not allowed at the same time");
            errors++;
          }
        }
      }

      if (!"VARCHAR".equals(dataType)) {
        if (column.getDefaultValueString() != null) {
          logger.error("'tableName': '" + tableName + " 'columnName': '" + columnName + "' 'dataType': '" + dataType
              + "' - 'defaultValueString' is not allowed");
          errors++;
        }

        if (column.getLowerRangeString() != null) {
          logger.error("'tableName': '" + tableName + " 'columnName': '" + columnName + "' 'dataType': '" + dataType + "' - 'lowerRangeString' is not allowed");
          errors++;
        }

        if (column.getUpperRangeString() != null) {
          logger.error("'tableName': '" + tableName + " 'columnName': '" + columnName + "' 'dataType': '" + dataType + "' - 'upperRangeString' is not allowed");
          errors++;
        }

        if (column.getValidValuesString() != null) {
          logger.error("'tableName': '" + tableName + " 'columnName': '" + columnName + "' 'dataType': '" + dataType
              + "' - 'validValuesString' is not allowed");
          errors++;
        }
      }

      if ("VARCHAR".equals(dataType)) {
        genVarcharColumnNames.add(columnName);
      }

      tableColumnNames.add(columnName);
    }

    genTableNameColumnNames.put(tableName,
                                tableColumnNames);

    if (isDebug) {
      logger.debug("End");
    }

    return columnNames;
  }

  /**
   * Validate foreign key hierarchy.
   */
  private void validateSchemaForeignKeys() {
    if (isDebug) {
      logger.debug("Start");
    }

    while (valTableNameForeignKeys.size() > 0) {
      ArrayList<String> currentLevel = new ArrayList<>();

      for (String tableName : genTableNames) {
        if (valTableNameForeignKeys.containsKey(tableName)) {
          if (valTableNameForeignKeys.get(tableName).size() == 0) {
            currentLevel.add(tableName);
            genTableHierarchy.add(tableName);
            valTableNameForeignKeys.remove(tableName);
          }
        }
      }

      if (currentLevel.size() == 0) {
        logger.error("Foreign key hierarchy is wrong");
        errors++;
        break;
      }

      for (String referenceTable : currentLevel) {
        for (String tableName : genTableNames) {
          HashSet<String> referenceTables = valTableNameForeignKeys.get(tableName);
          if (referenceTables != null) {
            if (referenceTables.contains(referenceTable)) {
              referenceTables.remove(referenceTable);
              valTableNameForeignKeys.put(tableName,
                                          referenceTables);
            }
          }
        }
      }
    }

    if (isDebug) {
      logger.debug("End");
    }
  }

  /**
   * Validate table constraint definitions.
   */
  private void validateSchemaTableConstraints() {
    if (isDebug) {
      logger.debug("Start");
    }

    ArrayList<String>          columns;
    String                     constraintType;

    ArrayList<String>          referenceColumns;
    String                     referenceTable;
    HashSet<String>            valRefrenceColumnNames;

    HashSet<String>            valColumnNames;
    ArrayList<TableConstraint> tableConstraints;
    String                     tableName;

    for (Table table : valTables) {
      if (table.getTableConstraints() == null || table.getTableConstraints().size() == 0) {
        continue;
      }

      tableName        = table.getTableName().toUpperCase();

      tableConstraints = table.getTableConstraints();

      for (TableConstraint tableConstraint : tableConstraints) {
        if (tableConstraint.getConstraintType() == null) {
          logger.error("'tableName': '" + tableName + "' - 'constraintType' missing (null)");
          errors++;
          continue;
        }

        constraintType = tableConstraint.getConstraintType().toUpperCase();

        if (tableConstraint.getColumns() == null) {
          logger.error("'tableName': '" + tableName + "' 'constraintType': '" + constraintType + "' - 'columns' missing (null)");
          errors++;
          continue;
        }

        if (tableConstraint.getColumns().size() == 0) {
          logger.error("'tableName': '" + tableName + "' 'constraintType': '" + constraintType + "' - 'columns' missing");
          errors++;
          continue;
        }

        columns        = tableConstraint.getColumns();

        valColumnNames = valTablesColumns.get(tableName);

        for (String column : columns) {
          if (!valColumnNames.contains(column.toUpperCase())) {
            logger.error("'tableName': '" + tableName + "' 'constraintType': '" + constraintType + "' - 'column': '" + column.toUpperCase()
                + "' is not defined in database table '" + tableName + "'");
            errors++;
          }
        }

        switch (constraintType) {
        case "FOREIGN" -> {
          if (columns.size() == 1) {
            logger.error("'tableName': '" + tableName + "' 'constraintType': '" + constraintType
                + "' - with a single column, a constraint must be defined as a column constraint");
            errors++;
          }

          if (tableConstraint.getReferenceTable() == null) {
            logger.error("'tableName': '" + tableName + "' 'constraintType': '" + constraintType + "' - 'referenceTable' is missing");
            errors++;
          }

          referenceTable = tableConstraint.getReferenceTable().toUpperCase();

          if (tableName.equals(referenceTable)) {
            logger.error("'tableName': '" + tableName + "' 'constraintType': '" + constraintType + "' - 'referenceTable': '" + referenceTable
                + "' must be a different database table");
            errors++;
          }

          if (!(valTablesColumns.containsKey(referenceTable))) {
            logger.error("'tableName': '" + tableName + "' 'constraintType': '" + constraintType + "' - 'referenceTable': '" + referenceTable
                + "' is not defined");
            errors++;
          }

          if (tableConstraint.getReferenceColumns() == null) {
            logger.error("'tableName': '" + tableName + "' 'constraintType': '" + constraintType + "' - 'referenceColumns' missing (null)");
            errors++;
          }

          if (tableConstraint.getReferenceColumns().size() == 0) {
            logger.error("'tableName': '" + tableName + "' 'constraintType': '" + constraintType + "' - 'referenceColumns' missing");
            errors++;
          }

          referenceColumns       = tableConstraint.getReferenceColumns();

          valRefrenceColumnNames = valTablesColumns.get(referenceTable);

          for (String referenceColumn : referenceColumns) {
            if (!valRefrenceColumnNames.contains(referenceColumn.toUpperCase())) {
              logger.error("'tableName': '" + tableName + "' 'constraintType': '" + constraintType + "' - 'referenceColumn': '" + referenceColumn.toUpperCase()
                  + "' is not defined in database table '" + referenceTable + "'");
              errors++;
            }
          }

          if (errors == 0) {
            HashSet<String> referenceTables = valTableNameForeignKeys.get(tableName);
            referenceTables.add(referenceTable);
            valTableNameForeignKeys.put(tableName,
                                        referenceTables);
          }
        }
        case "PRIMARY", "UNIQUE" -> {
          if (columns.size() == 1) {
            logger.error("'tableName': '" + tableName + "' 'constraintType': '" + constraintType
                + "' - with a single column, a constraint must be defined as a column constraint");
            errors++;
          }

          if (tableConstraint.getReferenceTable() != null) {
            logger.error("'tableName': '" + tableName + "' 'constraintType': '" + constraintType + "' - 'referenceTable' not allowed");
            errors++;
          }

          if (tableConstraint.getReferenceColumns() != null) {
            logger.error("'tableName': '" + tableName + "' 'constraintType': '" + constraintType + "' - 'referenceColumns' not allowed");
            errors++;
          }
        }
        default -> {
          logger.error("'tableName': '" + tableName + "' - unknown 'constraintType': '" + constraintType + "'");
          errors++;
        }
        }
      }

      if (errors == 0) {
        genTablesTableConstraints.put(tableName,
                                      tableConstraints);
      }
    }

    if (isDebug) {
      logger.debug("End");
    }
  }
}
