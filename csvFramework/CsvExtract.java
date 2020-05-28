package com.custom.java.preconfig.policy.group.services.starmount.csvFramework;

import au.com.bytecode.opencsv.CSVWriter;
import com.custom.java.preconfig.policy.group.services.starmount.services.ColumnMetadata;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

/**
 * create csv file by {@link #createCsvFile}
 * add data to file by {@link #add(T)}
 * close the file by {@link #close()}
 */
public abstract class CsvExtract<T> {
    private static final Logger LOG = LoggerFactory.getLogger(CsvExtract.class);
    private final CSVWriter csvWriter;
    private String[] line;
    private Path fileName;
    private String recordCount;

    private static final String RECORD_TYPE_H = "H";
    private static final String FILE_TYPE_TEST = "TEST";
    private static final String COMPANY_NAME = "Colonial Life & Accident Insurance Company";
    private static final String COMPANY_ADDRESS_1 = "1200 Colonial Life Boulevard";
    private static final String COMPANY_ADDRESS_2 = "Columbia, SC 29210";
    private static final String RECORD_TYPE_T = "T";
    private static final String ZERO = "0";

    public CsvExtract(Path fileName, int size) throws IOException {
        this.fileName = fileName;
        this.csvWriter = createCsvFile(fileName, getHeaders(), size);
    }

    /**
     * @param fileName The file Name
     * @param headers  The Headers
     * @param size     The Size
     * @return CSVWriter
     */
    private CSVWriter createCsvFile(Path fileName, ColumnMetadata[] headers, int size) throws IOException {
        CSVWriter csvWriter = new CSVWriter(new FileWriter(fileName.toFile(), true),
                ' ', CSVWriter.NO_QUOTE_CHARACTER);

        String[] line = new String[1];

        line[0] = StringUtils.join(CsvExtract.RECORD_TYPE_H,CsvExtract.FILE_TYPE_TEST,
                UnmDateUtils.getCurrentDate(),
                StringUtils.rightPad(CsvExtract.COMPANY_NAME, 50, StringUtils.EMPTY),
                StringUtils.rightPad(CsvExtract.COMPANY_ADDRESS_1, 30, StringUtils.EMPTY),
                StringUtils.rightPad(CsvExtract.COMPANY_ADDRESS_2, 357, StringUtils.EMPTY));

        csvWriter.writeNext(line);
        return csvWriter;
    }

    public abstract ColumnMetadata[] getHeaders();

    public boolean close() {
        String[] tail = new String[1];
        tail[0] = StringUtils.join(CsvExtract.RECORD_TYPE_T,
                StringUtils.leftPad(this.recordCount, 8, CsvExtract.ZERO),
                StringUtils.rightPad(StringUtils.EMPTY,441));

        csvWriter.writeNext(tail);

        try {
            csvWriter.close();
            return true;
        } catch (IOException e) {
            LOG.error("Error occurred while closing CSVWriter for File: " + fileName, e);
            return false;
        }
    }

    /**
     * Creates new line filled with item data
     */
    public abstract void add(T item) throws IOException;

    protected void setCellValue(@Nonnull ColumnMetadata column, @Nullable Object value) {
        getLine()[column.getColumnNumber()] = Objects.toString(value, StringUtils.EMPTY);
    }

    private String[] getLine() {
        return line = Optional.ofNullable(line).orElseGet(() -> new String[getHeaders().length]);
    }

    protected void writeLine(int count) {
        this.recordCount = String.valueOf(count);
        if (line != null) {
            csvWriter.writeNext(line);
            line = null;
        }
    }

    public Path getFileName() {
        return fileName;
    }
}
