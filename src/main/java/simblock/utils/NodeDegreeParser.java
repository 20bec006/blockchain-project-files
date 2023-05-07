package simblock.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

public class NodeDegreeParser implements Parser<Double[]> {

  public Double[] parse(InputStream is) throws IOException {
    return parse(new InputStreamReader(is));
  }

  private Double[] parse(Reader reader) throws IOException {
    ArrayList<Double> values = new ArrayList<>(125);

    // read csv file
    Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader()
        .withIgnoreHeaderCase()
        .withTrim()
        .parse(reader);

    for (CSVRecord record : records) {
      //System.out.println("Record #: " + record.getRecordNumber());
      //System.out.println("NodeDegree: " + record.get("NodeDegree"));
      values.add(Double.parseDouble(record.get("CumulativeProbability")));
    }

    // close the reader
    reader.close();
    return values.toArray(new Double[125]);

  }

}
